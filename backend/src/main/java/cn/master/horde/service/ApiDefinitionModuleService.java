package cn.master.horde.service;

import cn.master.horde.model.dto.BaseTreeNode;
import cn.master.horde.model.dto.api.request.ApiModuleRequest;
import cn.master.horde.model.dto.api.request.ModuleCreateRequest;
import cn.master.horde.model.dto.api.request.ModuleUpdateRequest;
import cn.master.horde.model.entity.ApiDefinitionModule;
import com.mybatisflex.core.service.IService;

import java.util.List;

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
}
