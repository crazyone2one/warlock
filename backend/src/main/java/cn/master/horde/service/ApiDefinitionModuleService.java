package cn.master.horde.service;

import cn.master.horde.model.dto.BaseTreeNode;
import cn.master.horde.model.dto.api.ModuleCountDTO;
import cn.master.horde.model.dto.api.request.ApiModuleRequest;
import cn.master.horde.model.dto.api.request.ModuleCreateRequest;
import cn.master.horde.model.dto.api.request.ModuleUpdateRequest;
import cn.master.horde.model.entity.ApiDefinitionModule;
import com.mybatisflex.core.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 接口模块 服务层。
 *
 * @author 11's papa
 * @since 2026-02-05
 */
public interface ApiDefinitionModuleService extends IService<ApiDefinitionModule> {
    String add(ModuleCreateRequest request, String operator);

    void update(ModuleUpdateRequest request, String operator);

    List<BaseTreeNode> getTree(ApiModuleRequest request, boolean deleted, boolean containRequest);

    void deleteModule(String deleteId, String currentUser);

    List<BaseTreeNode> selectNodeByIds(List<String> ids);

    List<String> selectChildrenIdsByParentIds(List<String> ids);

    Map<String, Long> moduleCount(ApiModuleRequest request, boolean deleted);

    Map<String, Long> getModuleCountMap(ApiModuleRequest request, List<ModuleCountDTO> moduleCountDTOList);
}
