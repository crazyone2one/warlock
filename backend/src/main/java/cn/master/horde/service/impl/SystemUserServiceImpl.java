package cn.master.horde.service.impl;

import cn.idev.excel.FastExcelFactory;
import cn.master.horde.common.constants.UserRoleType;
import cn.master.horde.common.listener.ExcelParseDTO;
import cn.master.horde.common.listener.UserImportEventListener;
import cn.master.horde.common.result.BizException;
import cn.master.horde.common.result.SystemResultCode;
import cn.master.horde.common.service.SessionUtils;
import cn.master.horde.dto.*;
import cn.master.horde.dto.permission.UserRolePermissionDTO;
import cn.master.horde.dto.permission.UserRoleResourceDTO;
import cn.master.horde.dto.request.PersonalUpdatePasswordRequest;
import cn.master.horde.dto.request.UserBatchCreateRequest;
import cn.master.horde.dto.request.UserChangeEnableRequest;
import cn.master.horde.dto.request.UserEditRequest;
import cn.master.horde.entity.*;
import cn.master.horde.mapper.SystemUserMapper;
import cn.master.horde.service.SystemUserService;
import cn.master.horde.service.UserRoleRelationService;
import cn.master.horde.service.UserRoleService;
import cn.master.horde.util.JsonHelper;
import cn.master.horde.util.Translator;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static cn.master.horde.entity.table.SystemProjectTableDef.SYSTEM_PROJECT;
import static cn.master.horde.entity.table.SystemUserTableDef.SYSTEM_USER;
import static cn.master.horde.entity.table.UserRolePermissionTableDef.USER_ROLE_PERMISSION;
import static cn.master.horde.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;
import static cn.master.horde.entity.table.UserRoleTableDef.USER_ROLE;

/**
 * 用户 服务层实现。
 *
 * @author 11's papa
 * @since 2026-01-14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUser> implements SystemUserService {
    private final UserRoleRelationService userRoleRelationService;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDTO getUserInfoById(String id) {
        UserDTO user = getUserDTO(id);
        autoSwitch(user);
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserBatchCreateResponse saveUser(UserBatchCreateRequest request) {
        userRoleService.checkRoleIsGlobalAndHaveMember(request.getUserRoleIdList(), true);
        UserBatchCreateResponse response = new UserBatchCreateResponse();
        Map<String, String> errorEmails = validateUserInfo(request.getUserInfoList().stream().map(UserCreateInfo::getEmail).toList());
        if (MapUtils.isNotEmpty(errorEmails)) {
            response.setErrorEmails(errorEmails);
            throw new BizException(SystemResultCode.INVITE_EMAIL_EXIST, JsonHelper.objectToString(errorEmails.keySet()));
        } else {
            response.setSuccessList(saveUserAndRole(request, SessionUtils.getCurrentUsername()));
        }
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserEditRequest updateUser(UserEditRequest request) {
        userRoleService.checkRoleIsGlobalAndHaveMember(request.getUserRoleIdList(), true);
        checkUserEmail(request.getId(), request.getEmail());
        SystemUser user = new SystemUser();
        BeanUtils.copyProperties(request, user);
        user.setUpdateUser(SessionUtils.getCurrentUsername());
        mapper.update(user);
        userRoleRelationService.updateUserSystemGlobalRole(user, user.getUpdateUser(), request.getUserRoleIdList());
        return request;
    }

    private void checkUserEmail(String id, String email) {
        boolean exists = queryChain().where(SYSTEM_USER.EMAIL.eq(email).and(SYSTEM_USER.ID.ne(id))).exists();
        if (exists) {
            throw new BizException(Translator.get("user_email_already_exists"));
        }
    }

    @Override
    public UserDTO getUserDTOByKeyword(String keyword) {
        UserDTO userDTO = queryChain().where(SYSTEM_USER.EMAIL.eq(keyword).or(SYSTEM_USER.ID.eq(keyword))).oneAs(UserDTO.class);
        if (userDTO != null) {
            userDTO.setUserRoleRelations(userRoleRelationService.selectByUserId(userDTO.getId()));
            userDTO.setUserRoles(userRoleService.selectByUserRoleRelations(userDTO.getUserRoleRelations()));
        }
        return userDTO;
    }

    @Override
    public Page<UserTableResponse> pageUser(BasePageRequest request) {
        Page<UserTableResponse> responsePage = queryChain()
                .where(SYSTEM_USER.EMAIL.like(request.getKeyword())
                        .or(SYSTEM_USER.USER_NAME.like(request.getKeyword()))
                        .or(SYSTEM_USER.NICK_NAME.like(request.getKeyword()))
                        .or(SYSTEM_USER.PHONE.like(request.getKeyword()))
                        .or(SYSTEM_USER.ID.like(request.getKeyword())))
                .orderBy(SYSTEM_USER.CREATE_TIME.desc(), SYSTEM_USER.ID.desc())
                .pageAs(new Page<>(request.getPage(), request.getPageSize()), UserTableResponse.class);
        if (CollectionUtils.isNotEmpty(responsePage.getRecords())) {
            List<String> userIdList = responsePage.getRecords().stream().map(SystemUser::getId).toList();
            Map<String, UserTableResponse> roleAndOrganizationMap = userRoleRelationService.selectGlobalUserRole(userIdList);
            responsePage.getRecords().forEach(user -> {
                UserTableResponse roleAndOrganization = roleAndOrganizationMap.get(user.getId());
                if (roleAndOrganization != null) {
                    user.setUserRoleList(roleAndOrganization.getUserRoleList());
                }
            });
        }
        return responsePage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TableBatchProcessResponse updateUserEnable(UserChangeEnableRequest request, String operatorId, String operatorName) {
        request.setSelectIds(getBatchUserIds(request));
        checkUserInDb(request.getSelectIds());
        if (!request.isEnable()) {
            // 不能禁用当前用户和admin
            checkProcessUserAndThrowException(request.getSelectIds(), operatorId, operatorName, Translator.get("user.not.disable"));
        }
        boolean update = updateChain()
                .set(SYSTEM_USER.ENABLE, request.isEnable())
                .where(SYSTEM_USER.ID.in(request.getSelectIds()))
                .update();
        if (update) {
            TableBatchProcessResponse response = new TableBatchProcessResponse();
            response.setTotalCount(request.getSelectIds().size());
            response.setSuccessCount(request.getSelectIds().size());
            return response;
        }
        return new TableBatchProcessResponse();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TableBatchProcessResponse deleteUser(UserChangeEnableRequest request, String operatorId, String operatorName) {
        List<String> userIdList = getBatchUserIds(request);
        checkUserInDb(userIdList);
        checkProcessUserAndThrowException(userIdList, operatorId, operatorName, Translator.get("user.not.delete"));
        TableBatchProcessResponse response = new TableBatchProcessResponse();
        response.setTotalCount(userIdList.size());
        response.setSuccessCount(mapper.deleteBatchByIds(userIdList));
        // 删除用户角色关系
        userRoleRelationService.deleteByUserIdList(userIdList);
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TableBatchProcessResponse resetPassword(TableBatchProcessDTO request, String operatorId) {
        request.setSelectIds(getBatchUserIds(request));
        this.checkUserInDb(request.getSelectIds());
        List<SystemUser> userList = mapper.selectListByIds(request.getSelectIds());
        userList.forEach(user -> {
            user.setPassword(passwordEncoder.encode(user.getEmail()));
            user.setUpdateUser(operatorId);
            mapper.update(user);
        });
        TableBatchProcessResponse response = new TableBatchProcessResponse();
        response.setTotalCount(request.getSelectIds().size());
        response.setSuccessCount(request.getSelectIds().size());
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserImportResponse importByExcel(MultipartFile excelFile, String operatorId) {
        UserImportResponse importResponse = new UserImportResponse();
        ExcelParseDTO<UserExcelRowDTO> excelParseDTO = new ExcelParseDTO<>();
        try {
            excelParseDTO = getUserExcelParseDTO(excelFile);
        } catch (Exception e) {
            log.info("import user  error", e);
        }
        if (CollectionUtils.isNotEmpty(excelParseDTO.getDataList())) {
            saveUserByExcelData(excelParseDTO.getDataList(), operatorId);
        }
        importResponse.generateResponse(excelParseDTO);
        return importResponse;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePassword(PersonalUpdatePasswordRequest request) {
        // checkOldPassword(request.getId(), request.getOldPassword());
        return updateChain().set(SYSTEM_USER.PASSWORD, passwordEncoder.encode(request.getNewPassword()))
                .where(SYSTEM_USER.ID.eq(request.getId())).update();
    }

    private void checkOldPassword(@NotBlank(message = "{user.id.not_blank}") String id, String oldPassword) {
        boolean exists = queryChain().where(SYSTEM_USER.ID.eq(id))
                .and(SYSTEM_USER.PASSWORD.eq(passwordEncoder.encode(oldPassword)))
                .exists();
        if (!exists) {
            throw new BizException(Translator.get("password_modification_failed"));
        }
    }

    private ExcelParseDTO<UserExcelRowDTO> getUserExcelParseDTO(MultipartFile excelFile) throws IOException {
        UserImportEventListener userImportEventListener = new UserImportEventListener();
        FastExcelFactory.read(excelFile.getInputStream(), UserExcel.class, userImportEventListener).sheet().doRead();
        return validateExcelUserInfo(userImportEventListener.getExcelParseDTO());
    }

    private ExcelParseDTO<UserExcelRowDTO> validateExcelUserInfo(ExcelParseDTO<UserExcelRowDTO> excelParseDTO) {
        List<UserExcelRowDTO> prepareSaveList = excelParseDTO.getDataList();
        if (CollectionUtils.isNotEmpty(prepareSaveList)) {
            var userInDbMap = queryChain().where(SYSTEM_USER.EMAIL.in(prepareSaveList.stream().map(UserExcelRowDTO::getEmail).toList()))
                    .list()
                    .stream().collect(Collectors.toMap(SystemUser::getEmail, SystemUser::getId));
            for (UserExcelRowDTO userExcelRow : prepareSaveList) {
                // 判断邮箱是否已存在数据库中
                if (userInDbMap.containsKey(userExcelRow.getEmail())) {
                    userExcelRow.setErrorMessage(Translator.get("user.email.import.in_system") + ": " + userExcelRow.getEmail());
                    excelParseDTO.addErrorRowData(userExcelRow.getDataIndex(), userExcelRow);
                }
            }
            excelParseDTO.getDataList().removeAll(excelParseDTO.getErrRowData().values());
        }
        return excelParseDTO;
    }

    private void saveUserByExcelData(List<UserExcelRowDTO> dataList, String operatorId) {
        UserBatchCreateRequest userBatchCreateDTO = new UserBatchCreateRequest();
        userBatchCreateDTO.setUserRoleIdList(new ArrayList<>() {{
            add("member");
        }});
        List<UserCreateInfo> userCreateInfoList = new ArrayList<>();
        dataList.forEach(userExcelRowDTO -> {
            UserCreateInfo userCreateInfo = new UserCreateInfo();
            BeanUtils.copyProperties(userExcelRowDTO, userCreateInfo);
            userCreateInfoList.add(userCreateInfo);
        });
        userBatchCreateDTO.setUserInfoList(userCreateInfoList);
        saveUserAndRole(userBatchCreateDTO, operatorId);
    }

    private List<String> getBatchUserIds(TableBatchProcessDTO request) {
        if (request.isSelectAll()) {
            List<SystemUser> users = queryChain().where(SYSTEM_USER.EMAIL.like(request.getCondition().getKeyword())
                            .or(SYSTEM_USER.USER_NAME.like(request.getCondition().getKeyword()))
                            .or(SYSTEM_USER.NICK_NAME.like(request.getCondition().getKeyword()))
                            .or(SYSTEM_USER.PHONE.like(request.getCondition().getKeyword()))
                            .or(SYSTEM_USER.ID.like(request.getCondition().getKeyword())))
                    .list();
            List<String> userIdList = new ArrayList<>(users.stream().map(SystemUser::getId).toList());
            if (CollectionUtils.isNotEmpty(request.getExcludeIds())) {
                userIdList.removeAll(request.getExcludeIds());
            }
            return userIdList;
        }
        return request.getSelectIds();
    }

    private void checkProcessUserAndThrowException(List<String> userIdList, String operatorId, String operatorName, String exceptionMessage) {
        for (String userId : userIdList) {
            // 当前用户或admin不能被操作
            if (userId.equals(operatorId)) {
                throw new BizException(exceptionMessage + ":" + operatorName);
            } else if ("admin".equals(userId)) {
                throw new BizException(exceptionMessage + ": admin");
            }
        }
    }

    private void checkUserInDb(@Valid List<String> userIdList) {
        if (CollectionUtils.isEmpty(userIdList)) {
            throw new BizException(Translator.get("user.not.exist"));
        }
        if (queryChain().where(SYSTEM_USER.ID.in(userIdList)).count() != userIdList.size()) {
            throw new BizException(Translator.get("user.not.exist"));
        }
    }

    private List<UserCreateInfo> saveUserAndRole(UserBatchCreateRequest request, String username) {
        List<String> userIds = new ArrayList<>();
        request.getUserInfoList().forEach(user -> {
            SystemUser built = SystemUser.builder().build();
            built.setUserName(user.getName());
            built.setNickName(user.getNickName());
            built.setEmail(user.getEmail());
            built.setPassword(passwordEncoder.encode(user.getEmail()));
            built.setPhone(user.getPhone());
            built.setCreateUser(username);
            built.setUpdateUser(username);
            save(built);
            userIds.add(built.getId());
        });
        if (CollectionUtils.isNotEmpty(request.getUserRoleIdList())) {
            request.getUserRoleIdList().forEach(roleId -> {
                List<UserRoleRelation> userRoleRelations = new ArrayList<>();
                userIds.forEach(userId -> {
                    UserRoleRelation userRoleRelation = new UserRoleRelation();
                    userRoleRelation.setUserId(userId);
                    userRoleRelation.setRoleId(roleId);
                    userRoleRelation.setSourceId("system");
                    userRoleRelation.setCreateUser(username);
                    userRoleRelations.add(userRoleRelation);
                });
                userRoleRelationService.saveBatch(userRoleRelations);
            });
        }
        return request.getUserInfoList();
    }

    private Map<String, String> validateUserInfo(Collection<String> createEmails) {
        Map<String, String> errorMessage = new HashMap<>();
        String userEmailRepeatError = Translator.get("user.email.repeat");
        // 判断参数内是否含有重复邮箱
        List<String> emailList = new ArrayList<>();
        Map<String, String> userInDbMap = queryChain().where(SystemUser::getEmail).in(createEmails).list()
                .stream().collect(Collectors.toMap(SystemUser::getEmail, SystemUser::getId));
        for (String createEmail : createEmails) {
            if (emailList.contains(createEmail)) {
                errorMessage.put(createEmail, userEmailRepeatError);
            } else {
                // 判断邮箱是否已存在数据库中
                if (userInDbMap.containsKey(createEmail)) {
                    errorMessage.put(createEmail, userEmailRepeatError);
                } else {
                    emailList.add(createEmail);
                }
            }
        }
        return errorMessage;
    }

    private void autoSwitch(UserDTO user) {
        // 判断是否是系统管理员
        if (isSystemAdmin(user)) {
            return;
        }
        // 用户有 last_project_id 权限
        if (hasLastProjectPermission(user)) {
            return;
        }
        // 判断其他权限
        checkNewOrganizationAndProject(user);
    }

    private void checkNewOrganizationAndProject(UserDTO user) {
        List<UserRoleRelation> userRoleRelations = user.getUserRoleRelations();
        List<String> projectRoleIds = user.getUserRoles()
                .stream().filter(ug -> Objects.equals(ug.getType(), UserRoleType.PROJECT.name()))
                .map(UserRole::getCode)
                .toList();
        List<UserRoleRelation> project = userRoleRelations.stream().filter(ug -> projectRoleIds.contains(ug.getRoleId()))
                .toList();
        if (CollectionUtils.isNotEmpty(project)) {
            UserRoleRelation userRoleRelation = project.stream().filter(p -> StringUtils.isNotBlank(p.getSourceId()))
                    .toList().getFirst();
            String projectId = userRoleRelation.getSourceId();
            SystemProject p = QueryChain.of(SystemProject.class).where(SYSTEM_PROJECT.ID.eq(projectId)).one();
            updateChain().set(SystemUser::getId, user.getId()).set(SystemUser::getLastProjectId, p.getId()).update();
        }
    }

    private boolean hasLastProjectPermission(UserDTO user) {
        if (StringUtils.isNotBlank(user.getLastProjectId())) {
            List<UserRoleRelation> userRoleRelations = user.getUserRoleRelations().stream()
                    .filter(ug -> Objects.equals(user.getLastProjectId(), ug.getSourceId()))
                    .toList();
            if (CollectionUtils.isNotEmpty(userRoleRelations)) {
                List<SystemProject> systemProjects = QueryChain.of(SystemProject.class).where(SystemProject::getId).eq(user.getLastProjectId())
                        .and(SystemProject::getEnable).eq(true)
                        .list();
                return CollectionUtils.isNotEmpty(systemProjects);
            }
        }
        return false;
    }

    private boolean isSystemAdmin(UserDTO user) {
        return isSuperUser(user.getId());
    }

    private boolean isSuperUser(String id) {
        return QueryChain.of(UserRoleRelation.class)
                .innerJoin(UserRole.class).on(USER_ROLE_RELATION.ROLE_ID.eq(USER_ROLE.ID)
                        .and(USER_ROLE.CODE.eq("admin")))
                .where(USER_ROLE_RELATION.USER_ID.eq(id))
                .exists();
    }

    private UserDTO getUserDTO(String id) {
        UserDTO user = mapper.selectOneWithRelationsByIdAs(id, UserDTO.class);
        UserRolePermissionDTO dto = getUserRolePermission(id);
        user.setUserRoleRelations(dto.getUserRoleRelations());
        user.setUserRoles(dto.getUserRoles());
        user.setUserRolePermissions(dto.getList());
        return user;
    }

    private UserRolePermissionDTO getUserRolePermission(String userId) {
        UserRolePermissionDTO permissionDTO = new UserRolePermissionDTO();
        List<UserRoleResourceDTO> list = new ArrayList<>();
        List<UserRoleRelation> userRoleRelations = userRoleRelationService.selectByUserId(userId);
        if (CollectionUtils.isEmpty(userRoleRelations)) {
            return permissionDTO;
        }
        permissionDTO.setUserRoleRelations(userRoleRelations);
        List<String> roleList = userRoleRelations.stream().map(UserRoleRelation::getRoleId).toList();
        List<UserRole> userRoles = QueryChain.of(UserRole.class).where(UserRole::getId).in(roleList).list();
        permissionDTO.setUserRoles(userRoles);
        for (UserRole gp : userRoles) {
            UserRoleResourceDTO dto = new UserRoleResourceDTO();
            dto.setUserRole(gp);
            List<UserRolePermission> userRolePermissions = QueryChain.of(UserRolePermission.class)
                    .where(USER_ROLE_PERMISSION.ROLE_ID.eq(gp.getId()))
                    .list();
            dto.setUserRolePermissions(userRolePermissions);
            list.add(dto);
        }
        permissionDTO.setList(list);
        return permissionDTO;
    }
}
