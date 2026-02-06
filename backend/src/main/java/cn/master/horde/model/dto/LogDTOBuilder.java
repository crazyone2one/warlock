package cn.master.horde.model.dto;

import lombok.Builder;

/**
 * @author : 11's papa
 * @since : 2026/2/5, 星期四
 **/
@Builder
public class LogDTOBuilder {
    private String projectId;
    private String sourceId;
    private String createUser;
    private String type;
    private String method;
    private String module;
    private String content;
    private String path;
    private byte[] originalValue;
    private byte[] modifiedValue;

    public LogDTO getLogDTO() {
        LogDTO logDTO = new LogDTO(projectId, sourceId, createUser, type, module, content);
        logDTO.setUri(path);
        logDTO.setMethodName(method);
        logDTO.setOriginalValue(originalValue);
        logDTO.setModifiedValue(modifiedValue);
        return logDTO;
    }
}
