package cn.master.horde.service;

import cn.master.horde.dto.*;
import cn.master.horde.dto.request.PersonalUpdatePasswordRequest;
import cn.master.horde.dto.request.UserBatchCreateRequest;
import cn.master.horde.dto.request.UserChangeEnableRequest;
import cn.master.horde.dto.request.UserEditRequest;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import cn.master.horde.entity.SystemUser;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户 服务层。
 *
 * @author 11's papa
 * @since 2026-01-14
 */
public interface SystemUserService extends IService<SystemUser> {

    UserDTO getUserInfoById(String id);

    UserBatchCreateResponse saveUser(UserBatchCreateRequest request);

    UserEditRequest updateUser(UserEditRequest request);

    UserDTO getUserDTOByKeyword(String keyword);

    Page<UserTableResponse> pageUser(BasePageRequest request);

    TableBatchProcessResponse updateUserEnable(UserChangeEnableRequest request, String operatorId, String operatorName);

    TableBatchProcessResponse deleteUser(UserChangeEnableRequest request, String currentUserId, String currentUsername);

    TableBatchProcessResponse resetPassword(TableBatchProcessDTO request, String operatorId);

    UserImportResponse importByExcel(MultipartFile excelFile, String operatorId);

    boolean updatePassword(PersonalUpdatePasswordRequest request);
}
