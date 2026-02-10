package cn.master.horde.service.log;

import cn.master.horde.common.constants.HttpMethodConstants;
import cn.master.horde.common.constants.OperationLogModule;
import cn.master.horde.common.constants.OperationLogType;
import cn.master.horde.common.util.JsonHelper;
import cn.master.horde.model.dto.LogDTO;
import cn.master.horde.model.dto.api.request.ApiDefinitionAddRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : 11's papa
 * @since : 2026/2/9, 星期一
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class ApiDefinitionLogService {
    public LogDTO addLog(ApiDefinitionAddRequest request) {
        LogDTO dto = new LogDTO(
                request.getProjectId(),
                null,
                null,
                OperationLogType.ADD.name(),
                OperationLogModule.API_TEST_MANAGEMENT_DEFINITION,
                request.getName());

        dto.setHistory(true);
        dto.setMethod(HttpMethodConstants.POST.name());
        dto.setOriginalValue(JsonHelper.toJSONBytes(request));
        return dto;
    }
    // public LogDTO updateLog(ApiDefinitionUpdateRequest request) {
    //     ApiDefinitionDTO apiDefinition = getOriginalValue(request.getId());
    //     if (apiDefinition.getId() != null) {
    //         LogDTO dto = new LogDTO(
    //                 apiDefinition.getProjectId(),
    //                 request.getId(),
    //                 null,
    //                 OperationLogType.UPDATE.name(),
    //                 OperationLogModule.API_TEST_MANAGEMENT_DEFINITION,
    //                 request.getName());
    //         dto.setHistory(true);
    //         dto.setMethod(HttpMethodConstants.POST.name());
    //         dto.setOriginalValue(JsonHelper.toJSONBytes(apiDefinition));
    //         return dto;
    //     }
    //     return null;
    // }
}
