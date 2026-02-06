package cn.master.horde.service.impl;

import cn.master.horde.common.constants.ModuleConstants;
import cn.master.horde.common.result.BizException;
import cn.master.horde.common.service.ModuleTreeService;
import cn.master.horde.common.util.NodeSortUtils;
import cn.master.horde.common.util.Translator;
import cn.master.horde.model.dto.BaseTreeNode;
import cn.master.horde.model.dto.api.request.ApiModuleRequest;
import cn.master.horde.model.dto.api.request.ModuleCreateRequest;
import cn.master.horde.model.dto.api.request.ModuleUpdateRequest;
import cn.master.horde.model.entity.ApiDefinitionModule;
import cn.master.horde.model.mapper.ApiDefinitionModuleMapper;
import cn.master.horde.service.ApiDefinitionModuleService;
import cn.master.horde.service.log.ApiDefinitionModuleLogService;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cn.master.horde.model.entity.table.ApiDefinitionModuleTableDef.API_DEFINITION_MODULE;

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

    private List<BaseTreeNode> selectBaseByRequest(ApiModuleRequest request) {
        return queryChain()
                .select(API_DEFINITION_MODULE.ID, API_DEFINITION_MODULE.NAME, API_DEFINITION_MODULE.PARENT_ID,
                        API_DEFINITION_MODULE.PROJECT_ID, API_DEFINITION_MODULE.POS)
                .select("MODULE").as("type")
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
