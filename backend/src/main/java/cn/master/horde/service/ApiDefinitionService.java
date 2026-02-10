package cn.master.horde.service;

import cn.master.horde.model.dto.api.request.ApiDefinitionAddRequest;
import cn.master.horde.model.entity.ApiDefinition;
import com.mybatisflex.core.service.IService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Map;

/**
 * 接口定义 服务层。
 *
 * @author 11's papa
 * @since 2026-02-09
 */
public interface ApiDefinitionService extends IService<ApiDefinition> {
    ApiDefinition create(ApiDefinitionAddRequest request, String userId);

    List<String> getQueryExcludeIds(Map<String, List<String>> queryFilter, String projectId, List<String> protocols);

    List<String> getQueryIncludeIds(Map<String, List<String>> queryFilter, String projectId, List<String> protocols);
}
