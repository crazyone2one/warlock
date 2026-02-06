package cn.master.horde.service.log;

import cn.master.horde.common.constants.HttpMethodConstants;
import cn.master.horde.common.constants.OperationLogModule;
import cn.master.horde.common.constants.OperationLogType;
import cn.master.horde.common.util.JsonHelper;
import cn.master.horde.common.util.Translator;
import cn.master.horde.model.dto.BaseTreeNode;
import cn.master.horde.model.dto.LogDTO;
import cn.master.horde.model.dto.LogDTOBuilder;
import cn.master.horde.model.entity.ApiDefinitionModule;
import cn.master.horde.model.mapper.SystemProjectMapper;
import cn.master.horde.service.OperationLogService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/2/5, 星期四
 **/
@Component
@Transactional(rollbackFor = Exception.class)
public class ApiDefinitionModuleLogService {
    private static final String API_DEBUG_MODULE = "/api/definition/module";
    private static final String ADD = API_DEBUG_MODULE + "/add";
    private static final String UPDATE = API_DEBUG_MODULE + "/update";
    private static final String DELETE = API_DEBUG_MODULE + "/delete";
    private static final String MOVE = API_DEBUG_MODULE + "/move";
    private static final String MOVE_TO = "file.log.move_to";

    @Resource
    private SystemProjectMapper projectMapper;
    @Resource
    private OperationLogService operationLogService;

    public void saveAddLog(ApiDefinitionModule module, String operator) {
        // SystemProject project = projectMapper.selectOneById(module.getProjectId());
        LogDTO dto = LogDTOBuilder.builder()
                .projectId(module.getProjectId())
                .type(OperationLogType.ADD.name())
                .module(OperationLogModule.API_TEST_MANAGEMENT_MODULE)
                .method(HttpMethodConstants.POST.name())
                .path(ADD)
                .sourceId(module.getId())
                .content(module.getName())
                .originalValue(JsonHelper.toJSONBytes(module))
                .createUser(operator)
                .build().getLogDTO();
        operationLogService.add(dto);
    }

    public void saveUpdateLog(ApiDefinitionModule module, String operator) {
        // SystemProject project = projectMapper.selectByPrimaryKey(module.getProjectId());
        LogDTO dto = LogDTOBuilder.builder()
                .projectId(module.getProjectId())
                .type(OperationLogType.UPDATE.name())
                .module(OperationLogModule.API_TEST_MANAGEMENT_MODULE)
                .method(HttpMethodConstants.POST.name())
                .path(UPDATE)
                .sourceId(module.getId())
                .content(module.getName())
                .originalValue(JsonHelper.toJSONBytes(module))
                .createUser(operator)
                .build().getLogDTO();

        operationLogService.add(dto);
    }

    public void saveDeleteModuleLog(List<BaseTreeNode> deleteModule, String operator, String projectId) {
        List<LogDTO> dtoList = new ArrayList<>();
        // Project project = projectMapper.selectByPrimaryKey(projectId);
        deleteModule.forEach(item -> {
            LogDTO dto = LogDTOBuilder.builder()
                    .projectId(projectId)
                    .type(OperationLogType.DELETE.name())
                    .module(OperationLogModule.API_TEST_MANAGEMENT_MODULE)
                    .method(HttpMethodConstants.GET.name())
                    .path(DELETE + "/%s")
                    .sourceId(item.getId())
                    .content(item.getName() + " " + Translator.get("log.delete_module"))
                    .createUser(operator)
                    .build().getLogDTO();
            dtoList.add(dto);
        });
        operationLogService.batchAdd(dtoList);
    }

    // public void saveDeleteDataLog(List<ApiDefinition> deleteData, String operator, String projectId) {
    //     // Project project = projectMapper.selectByPrimaryKey(projectId);
    //     List<LogDTO> logs = new ArrayList<>();
    //     deleteData.forEach(item -> {
    //                 LogDTO dto = LogDTOBuilder.builder()
    //                         .projectId(projectId)
    //                         .type(OperationLogType.DELETE.name())
    //                         .module(OperationLogModule.API_TEST_MANAGEMENT_MODULE)
    //                         .method(HttpMethodConstants.GET.name())
    //                         .path(DELETE + "/%s")
    //                         .sourceId(item.getId())
    //                         .content(item.getName())
    //                         .createUser(operator)
    //                         .build().getLogDTO();
    //                 logs.add(dto);
    //             }
    //     );
    //     operationLogService.batchAdd(logs);
    // }

    // public void saveDeleteCaseLog(List<ApiTestCase> apiTestCases, String operator, String projectId) {
    //     Project project = projectMapper.selectByPrimaryKey(projectId);
    //     List<LogDTO> logs = new ArrayList<>();
    //     apiTestCases.forEach(item -> {
    //                 LogDTO dto = LogDTOBuilder.builder()
    //                         .projectId(project.getId())
    //                         .organizationId(project.getOrganizationId())
    //                         .type(OperationLogType.DELETE.name())
    //                         .module(OperationLogModule.API_TEST_MANAGEMENT_MODULE)
    //                         .method(HttpMethodConstants.GET.name())
    //                         .path(DELETE + "/%s")
    //                         .sourceId(item.getId())
    //                         .content(item.getName())
    //                         .createUser(operator)
    //                         .build().getLogDTO();
    //                 logs.add(dto);
    //             }
    //     );
    //     operationLogService.batchAdd(logs);
    // }

    // public void saveMoveLog(@Validated NodeSortDTO request, String operator) {
    //     BaseModule moveNode = request.getNode();
    //     BaseModule previousNode = request.getPreviousNode();
    //     BaseModule nextNode = request.getNextNode();
    //     BaseModule parentModule = request.getParent();
    //
    //     Project project = projectMapper.selectByPrimaryKey(moveNode.getProjectId());
    //     String logContent;
    //     if (nextNode == null && previousNode == null) {
    //         logContent = moveNode.getName() + " " + Translator.get(MOVE_TO) + parentModule.getName();
    //     } else if (nextNode == null) {
    //         logContent = moveNode.getName() + " " + Translator.get(MOVE_TO) + parentModule.getName() + " " + previousNode.getName() + Translator.get("file.log.next");
    //     } else if (previousNode == null) {
    //         logContent = moveNode.getName() + " " + Translator.get(MOVE_TO) + parentModule.getName() + " " + nextNode.getName() + Translator.get("file.log.previous");
    //     } else {
    //         logContent = moveNode.getName() + " " + Translator.get(MOVE_TO) + parentModule.getName() + " " +
    //                 previousNode.getName() + Translator.get("file.log.next") + " " + nextNode.getName() + Translator.get("file.log.previous");
    //     }
    //     LogDTO dto = LogDTOBuilder.builder()
    //             .projectId(moveNode.getProjectId())
    //             .organizationId(project.getOrganizationId())
    //             .type(OperationLogType.UPDATE.name())
    //             .module(OperationLogModule.API_TEST_MANAGEMENT_MODULE)
    //             .method(HttpMethodConstants.POST.name())
    //             .path(MOVE)
    //             .sourceId(moveNode.getId())
    //             .content(logContent)
    //             .originalValue(JsonHelper.toJSONBytes(moveNode))
    //             .createUser(operator)
    //             .build().getLogDTO();
    //     operationLogService.add(dto);
    // }
}
