package cn.master.horde.service.impl;

import cn.master.horde.common.constants.ModuleConstants;
import cn.master.horde.common.result.BizException;
import cn.master.horde.common.service.ModuleTreeService;
import cn.master.horde.common.util.CommonBeanFactory;
import cn.master.horde.common.util.NodeSortUtils;
import cn.master.horde.common.util.Translator;
import cn.master.horde.model.dto.BaseTreeNode;
import cn.master.horde.model.dto.api.ModuleCountDTO;
import cn.master.horde.model.dto.api.request.ApiModuleRequest;
import cn.master.horde.model.dto.api.request.ModuleCreateRequest;
import cn.master.horde.model.dto.api.request.ModuleUpdateRequest;
import cn.master.horde.model.entity.ApiDefinition;
import cn.master.horde.model.entity.ApiDefinitionModule;
import cn.master.horde.model.mapper.ApiDefinitionModuleMapper;
import cn.master.horde.service.ApiDefinitionModuleService;
import cn.master.horde.service.ApiDefinitionService;
import cn.master.horde.service.log.ApiDefinitionModuleLogService;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.master.horde.model.entity.table.ApiDefinitionModuleTableDef.API_DEFINITION_MODULE;
import static cn.master.horde.model.entity.table.ApiDefinitionTableDef.API_DEFINITION;
import static com.mybatisflex.core.query.QueryMethods.string;

/**
 * 接口模块 服务层实现。
 *
 * @author 11's papa
 * @since 2026-02-05
 */
@Service
@RequiredArgsConstructor
public class ApiDefinitionModuleServiceImpl extends ServiceImpl<ApiDefinitionModuleMapper, ApiDefinitionModule> implements ApiDefinitionModuleService {
    private final ApiDefinitionModuleLogService apiDefinitionModuleLogService;
    private final ModuleTreeService moduleTreeService;
    protected static final long LIMIT_POS = NodeSortUtils.DEFAULT_NODE_INTERVAL_POS;
    private static final String MODULE_NO_EXIST = "api_module.not.exist";
    private static final String UNPLANNED_API = "api_unplanned_request";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String add(ModuleCreateRequest request, String operator) {
        ApiDefinitionModule module = new ApiDefinitionModule();
        module.setName(request.getName());
        module.setParentId(request.getParentId());
        module.setProjectId(request.getProjectId());
        module.setCreateUser(operator);
        checkDataValidity(module);
        module.setPos(getNextOrder(request.getParentId()));
        module.setUpdateUser(operator);
        mapper.insert(module);
        apiDefinitionModuleLogService.saveAddLog(module, operator);
        return module.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ModuleUpdateRequest request, String operator) {
        ApiDefinitionModule module = checkModuleExist(request.id());
        ApiDefinitionModule updateModule = new ApiDefinitionModule();
        updateModule.setId(request.id());
        updateModule.setName(request.name());
        updateModule.setParentId(module.getParentId());
        updateModule.setProjectId(module.getProjectId());
        checkDataValidity(updateModule);
        mapper.update(updateModule);
        apiDefinitionModuleLogService.saveUpdateLog(updateModule, operator);
    }

    @Override
    public List<BaseTreeNode> getTree(ApiModuleRequest request, boolean deleted, boolean containRequest) {
        List<BaseTreeNode> fileModuleList = selectBaseByRequest(request);
        List<BaseTreeNode> baseTreeNodes = moduleTreeService.buildTreeAndCountResource(fileModuleList, true, Translator.get(UNPLANNED_API));
        if (!containRequest || CollectionUtils.isEmpty(request.getProtocols())) {
            return baseTreeNodes;
        }
        return List.of();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteModule(String deleteId, String currentUser) {
        ApiDefinitionModule module = checkModuleExist(deleteId);
        deleteModule(List.of(deleteId), currentUser, module.getProjectId());
    }

    @Override
    public List<BaseTreeNode> selectNodeByIds(List<String> ids) {
        return queryChain()
                .select(API_DEFINITION_MODULE.ID, API_DEFINITION_MODULE.NAME, API_DEFINITION_MODULE.PARENT_ID,
                        API_DEFINITION_MODULE.PROJECT_ID, API_DEFINITION_MODULE.POS)
                .select("MODULE").as("type")
                .where(API_DEFINITION_MODULE.ID.in(ids)).orderBy(API_DEFINITION_MODULE.POS.desc())
                .listAs(BaseTreeNode.class);
    }

    @Override
    public List<String> selectChildrenIdsByParentIds(List<String> ids) {
        return queryChain().select(API_DEFINITION_MODULE.ID)
                .where(API_DEFINITION_MODULE.PARENT_ID.in(ids))
                .listAs(String.class);
    }

    @Override
    public Map<String, Long> moduleCount(ApiModuleRequest request, boolean deleted) {
        if (CollectionUtils.isEmpty(request.getProtocols())) {
            return Collections.emptyMap();
        }
        boolean isRepeat = true;
        if (StringUtils.isNotEmpty(request.getTestPlanId())) {
            isRepeat = checkTestPlanRepeatCase(request);
        }
        request.setModuleIds(null);
        ApiDefinitionService apiDefinitionService = CommonBeanFactory.getBean(ApiDefinitionService.class);
        request.setExcludeIds(apiDefinitionService.getQueryExcludeIds(request.getFilter(), request.getProjectId(), request.getProtocols()));
        request.setIncludeIds(apiDefinitionService.getQueryIncludeIds(request.getFilter(), request.getProjectId(), request.getProtocols()));

        // 查找根据moduleIds查找模块下的接口数量 查非delete状态的
        List<ModuleCountDTO> moduleCountDTOList = countModuleIdByRequest(request, deleted, isRepeat);
        long allCount = moduleTreeService.getAllCount(moduleCountDTOList);
        Map<String, Long> moduleCountMap = getModuleCountMap(request, moduleCountDTOList);
        moduleCountMap.put("all", allCount);
        return moduleCountMap;
    }

    @Override
    public Map<String, Long> getModuleCountMap(ApiModuleRequest request, List<ModuleCountDTO> moduleCountDTOList) {
        List<BaseTreeNode> treeNodeList = getTreeOnlyIdsAndResourceCount(request, moduleCountDTOList);
        return moduleTreeService.getIdCountMapByBreadth(treeNodeList);
    }

    private List<BaseTreeNode> getTreeOnlyIdsAndResourceCount(ApiModuleRequest request, List<ModuleCountDTO> moduleCountDTOList) {
        request.setKeyword(null);
        request.setModuleIds(null);
        List<BaseTreeNode> fileModuleList = selectIdAndParentIdByRequest(request);
        return moduleTreeService.buildTreeAndCountResource(fileModuleList, moduleCountDTOList, true, Translator.get(UNPLANNED_API));
    }

    private List<BaseTreeNode> selectIdAndParentIdByRequest(ApiModuleRequest request) {
        return queryChain()
                .select(API_DEFINITION_MODULE.ID, API_DEFINITION_MODULE.PARENT_ID)
                .where(API_DEFINITION_MODULE.PROJECT_ID.eq(request.getProjectId())
                        .and(API_DEFINITION_MODULE.NAME.like(request.getKeyword()))
                        .and(API_DEFINITION_MODULE.ID.in(request.getModuleIds())))
                .orderBy(API_DEFINITION_MODULE.POS.desc())
                .listAs(BaseTreeNode.class);
    }

    private List<ModuleCountDTO> countModuleIdByRequest(ApiModuleRequest request, boolean deleted, boolean isRepeat) {
        return QueryChain.of(ApiDefinition.class)
                .select(API_DEFINITION.MODULE_ID, QueryMethods.count(API_DEFINITION.ID).as("dataCount"))
                .where(API_DEFINITION.DELETED.eq(deleted))
                .and(API_DEFINITION.NAME.like(request.getKeyword())
                        .or(API_DEFINITION.NUM.like(request.getKeyword()))
                        .or(API_DEFINITION.PATH.like(request.getKeyword()))
                        .or(API_DEFINITION.TAGS.like(request.getKeyword()))
                )
                .and(API_DEFINITION.PROJECT_ID.in(request.getProjectId()))
                .and(API_DEFINITION.ID.in(request.getIncludeIds()))
                .and(API_DEFINITION.ID.notIn(request.getExcludeIds()))
                .and(API_DEFINITION.PROTOCOL.in(request.getProtocols()))
                .and(API_DEFINITION.MODULE_ID.in(request.getModuleIds()))
                .groupBy(API_DEFINITION.MODULE_ID)
                .listAs(ModuleCountDTO.class);
    }

    private boolean checkTestPlanRepeatCase(ApiModuleRequest request) {
        return false;
    }

    private void deleteModule(List<String> deleteIds, String currentUser, String projectId) {
        if (CollectionUtils.isEmpty(deleteIds)) {
            return;
        }
        mapper.deleteBatchByIds(deleteIds);
        List<BaseTreeNode> baseTreeNodes = selectNodeByIds(deleteIds);
        apiDefinitionModuleLogService.saveDeleteModuleLog(baseTreeNodes, currentUser, projectId);
        batchDeleteData(deleteIds, currentUser, projectId);
        List<String> childrenIds = selectChildrenIdsByParentIds(deleteIds);
        if (CollectionUtils.isNotEmpty(childrenIds)) {
            deleteModule(childrenIds, currentUser, projectId);
        }
    }

    private void batchDeleteData(List<String> deleteIds, String currentUser, String projectId) {
        if (QueryChain.of(ApiDefinition.class).where(API_DEFINITION.MODULE_ID.in(deleteIds)).count() > 0) {
            List<ApiDefinition> apiDefinitions = QueryChain.of(ApiDefinition.class).where(API_DEFINITION.MODULE_ID.in(deleteIds)).list();
            // 提取id为新的集合
            List<String> refIds = apiDefinitions.stream().map(ApiDefinition::getId).distinct().toList();
            UpdateChain.of(ApiDefinition.class).set(API_DEFINITION.MODULE_ID, "root")
                    .set(API_DEFINITION.DELETE_TIME, LocalDateTime.now())
                    .set(API_DEFINITION.DELETE_USER, currentUser)
                    .set(API_DEFINITION.DELETED, true)
                    .where(API_DEFINITION.ID.in(refIds)).update();
            apiDefinitionModuleLogService.saveDeleteDataLog(apiDefinitions, currentUser, projectId);
        }
    }

    private List<BaseTreeNode> selectBaseByRequest(ApiModuleRequest request) {
        return queryChain()
                .select(API_DEFINITION_MODULE.ID, API_DEFINITION_MODULE.NAME, API_DEFINITION_MODULE.PARENT_ID,
                        API_DEFINITION_MODULE.PROJECT_ID, API_DEFINITION_MODULE.POS)
                .select(string("MODULE").as("type"))
                // .from(API_DEFINITION_MODULE)
                .where(API_DEFINITION_MODULE.PROJECT_ID.eq(request.getProjectId())
                        .and(API_DEFINITION_MODULE.NAME.like(request.getKeyword()))
                        .and(API_DEFINITION_MODULE.ID.in(request.getModuleIds())))
                .orderBy(API_DEFINITION_MODULE.POS.desc())
                .listAs(BaseTreeNode.class);
    }

    private ApiDefinitionModule checkModuleExist(String moduleId) {
        return queryChain().where(API_DEFINITION_MODULE.ID.eq(moduleId)).oneOpt()
                .orElseThrow(() -> new BizException(Translator.get(MODULE_NO_EXIST)));
    }

    private Long getNextOrder(String parentId) {
        return queryChain().select(QueryMethods.max(API_DEFINITION_MODULE.POS))
                .where(API_DEFINITION_MODULE.PARENT_ID.eq(parentId)).oneAsOpt(Integer.class)
                .map(maxPos -> maxPos + LIMIT_POS)
                .orElse(LIMIT_POS);
    }

    private void checkDataValidity(ApiDefinitionModule module) {
        if (!ModuleConstants.ROOT_NODE_PARENT_ID.equals(module.getParentId())) {
            boolean exists = queryChain().where(API_DEFINITION_MODULE.ID.eq(module.getParentId())
                    .and(API_DEFINITION_MODULE.PROJECT_ID.eq(module.getProjectId()))).exists();
            if (!exists) {
                throw new BizException(Translator.get("parent.node.not_blank"));
            }
        }
        if (queryChain().where(API_DEFINITION_MODULE.NAME.eq(module.getName())
                .and(API_DEFINITION_MODULE.PARENT_ID.eq(module.getParentId()))
                .and(API_DEFINITION_MODULE.PROJECT_ID.eq(module.getProjectId()))
                .and(API_DEFINITION_MODULE.ID.ne(module.getId()))).exists()) {
            throw new BizException(Translator.get("node.name.repeat"));
        }
    }
}
