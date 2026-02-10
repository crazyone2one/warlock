package cn.master.horde.service.log;

import cn.master.horde.common.constants.OperationLogConstants;
import cn.master.horde.common.constants.OperationLogModule;
import cn.master.horde.common.constants.OperationLogType;
import cn.master.horde.common.util.JsonHelper;
import cn.master.horde.model.dto.LogDTO;
import cn.master.horde.model.dto.request.UserRoleUpdateRequest;
import cn.master.horde.model.entity.UserRole;
import cn.master.horde.model.mapper.UserRoleMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : 11's papa
 * @since : 2026/2/9, 星期一
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class UserRoleLogService {
    @Resource
    private UserRoleMapper userRoleMapper;

    public LogDTO addLog(UserRoleUpdateRequest request) {
        LogDTO dto = new LogDTO(
                OperationLogConstants.ORGANIZATION,
                OperationLogConstants.SYSTEM,
                null,
                OperationLogType.ADD.name(),
                OperationLogModule.SETTING_ORGANIZATION_USER_ROLE,
                request.getName());

        dto.setOriginalValue(JsonHelper.toJSONBytes(request.getName()));
        return dto;
    }

    public LogDTO updateLog(UserRoleUpdateRequest request) {
        UserRole userRole = userRoleMapper.selectOneById(request.getId());
        LogDTO dto = null;
        if (userRole != null) {
            dto = new LogDTO(
                    OperationLogConstants.SYSTEM,
                    userRole.getId(),
                    null,
                    OperationLogType.UPDATE.name(),
                    OperationLogModule.SETTING_SYSTEM_USER_GROUP,
                    userRole.getName());

            dto.setOriginalValue(JsonHelper.toJSONBytes(userRole));
        }
        return dto;
    }

    public LogDTO deleteLog(String id) {
        UserRole userRole = userRoleMapper.selectOneById(id);
        if (userRole == null) {
            return null;
        }
        LogDTO dto = new LogDTO(
                OperationLogConstants.SYSTEM,
                userRole.getId(),
                null,
                OperationLogType.DELETE.name(),
                OperationLogModule.SETTING_SYSTEM_USER_GROUP,
                userRole.getName());

        dto.setOriginalValue(JsonHelper.toJSONBytes(userRole));
        return dto;
    }
}
