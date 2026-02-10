package cn.master.horde.service.impl;

import cn.master.horde.common.constants.ApiConstants;
import cn.master.horde.common.constants.ApplicationNumScope;
import cn.master.horde.common.result.ApiResultCode;
import cn.master.horde.common.result.BizException;
import cn.master.horde.common.service.NumGenerator;
import cn.master.horde.common.uid.IDGenerator;
import cn.master.horde.common.util.JsonHelper;
import cn.master.horde.model.dto.api.HttpResponse;
import cn.master.horde.model.dto.api.request.ApiDefinitionAddRequest;
import cn.master.horde.model.entity.ApiDefinition;
import cn.master.horde.model.entity.ApiDefinitionBlob;
import cn.master.horde.model.entity.ProjectVersion;
import cn.master.horde.model.mapper.ApiDefinitionBlobMapper;
import cn.master.horde.model.mapper.ApiDefinitionMapper;
import cn.master.horde.service.ApiDefinitionService;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.master.horde.common.util.NodeSortUtils.DEFAULT_NODE_INTERVAL_POS;
import static cn.master.horde.model.entity.table.ApiDefinitionTableDef.API_DEFINITION;
import static cn.master.horde.model.entity.table.ProjectVersionTableDef.PROJECT_VERSION;

/**
 * 接口定义 服务层实现。
 *
 * @author 11's papa
 * @since 2026-02-09
 */
@Service
@RequiredArgsConstructor
public class ApiDefinitionServiceImpl extends ServiceImpl<ApiDefinitionMapper, ApiDefinition> implements ApiDefinitionService {
    private final ApiDefinitionBlobMapper apiDefinitionBlobMapper;
    @Override
    public ApiDefinition create(ApiDefinitionAddRequest request, String userId) {
        ApiDefinition apiDefinition = new ApiDefinition();
        BeanUtils.copyProperties(request, apiDefinition);
        apiDefinition.setVersionId(StringUtils.defaultIfBlank(request.getVersionId(), getDefaultVersion(request.getProjectId())));
        checkAddExist(apiDefinition);
        checkResponseNameCode(request.getResponse());
        apiDefinition.setNum(getNextNum(request.getProjectId()));
        apiDefinition.setPos(getNextOrder(request.getProjectId()));
        apiDefinition.setLatest(true);
        apiDefinition.setStatus(request.getStatus());
        apiDefinition.setCreateUser(userId);
        apiDefinition.setUpdateUser(userId);
        mapper.insert(apiDefinition);
        ApiDefinitionBlob apiDefinitionBlob = new ApiDefinitionBlob();
        apiDefinitionBlob.setApiDefinitionId(apiDefinition.getId());
        apiDefinitionBlob.setRequest(JsonHelper.toJSONBytes(request.getRequest()));
        if (Objects.nonNull(request.getResponse())) {
            List<HttpResponse> wHttpResponse = request.getResponse();
            wHttpResponse.forEach(item -> item.setId(IDGenerator.nextStr()));
            apiDefinitionBlob.setResponse(JsonHelper.toJSONBytes(wHttpResponse));
        }
        apiDefinitionBlobMapper.insert(apiDefinitionBlob);
        return apiDefinition;
    }

    @Override
    public List<String> getQueryExcludeIds(Map<String, List<String>> queryFilter, String projectId, List<String> protocols) {
        return List.of();
    }

    @Override
    public List<String> getQueryIncludeIds(Map<String, List<String>> queryFilter, String projectId, List<String> protocols) {
        return List.of();
    }

    private Long getNextOrder(String projectId) {
        return queryChain().select(API_DEFINITION.POS)
                .where(API_DEFINITION.PROJECT_ID.eq(projectId))
                .orderBy(API_DEFINITION.POS.desc())
                .oneAsOpt(Integer.class)
                .map(maxPos -> maxPos + DEFAULT_NODE_INTERVAL_POS)
                .orElse(0L);
    }

    private Long getNextNum(String projectId) {
        return NumGenerator.nextNum(projectId, ApplicationNumScope.API_DEFINITION);
    }

    private void checkResponseNameCode(Object response) {
        if (response != null && !response.toString().isEmpty() && !response.toString().equals("{}")) {
            List<HttpResponse> httpResponses = JsonHelper.parseArray(JsonHelper.objectToString(response), HttpResponse.class);
            boolean isUnique = httpResponses.stream()
                    .map(httpResponse -> httpResponse.getName() + httpResponse.getStatusCode())
                    .collect(Collectors.toSet())
                    .size() == httpResponses.size();
            if (!isUnique) {
                throw new BizException(ApiResultCode.API_RESPONSE_NAME_CODE_UNIQUE);
            }
        }
    }

    private void checkAddExist(ApiDefinition apiDefinition) {
        QueryChain<ApiDefinition> queryChain;
        if (ApiConstants.HTTP_PROTOCOL.equals(apiDefinition.getProtocol())) {
            queryChain = queryChain().where(API_DEFINITION.PATH.eq(apiDefinition.getPath())
                    .and(API_DEFINITION.METHOD.eq(apiDefinition.getMethod()))
                    .and(API_DEFINITION.PROJECT_ID.eq(apiDefinition.getProjectId()))
                    .and(API_DEFINITION.PROTOCOL.eq(apiDefinition.getProtocol()))
            );
        } else {
            queryChain = queryChain().where(API_DEFINITION.NAME.eq(apiDefinition.getName())
                    .and(API_DEFINITION.PROJECT_ID.eq(apiDefinition.getProjectId()))
                    .and(API_DEFINITION.PROTOCOL.eq(apiDefinition.getProtocol()))
                    .and(API_DEFINITION.MODULE_ID.eq(apiDefinition.getModuleId()))
            );
        }
        if (queryChain.exists()) {
            throw new BizException(ApiResultCode.API_DEFINITION_EXIST);
        }
    }

    private String getDefaultVersion(String projectId) {
        return QueryChain.of(ProjectVersion.class).select(PROJECT_VERSION.ID)
                .where(PROJECT_VERSION.PROJECT_ID.eq(projectId).and(PROJECT_VERSION.LATEST.eq(true)))
                .limit(1).oneAs(String.class);
    }
}
