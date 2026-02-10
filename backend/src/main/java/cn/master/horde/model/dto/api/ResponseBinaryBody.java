package cn.master.horde.model.dto.api;

import jakarta.validation.Valid;
import lombok.Data;

/**
 * @author : 11's papa
 * @since : 2026/2/9, 星期一
 **/
@Data
public class ResponseBinaryBody {
    private boolean sendAsBody;
    private String description;
    /**
     * 文件对象
     */
    @Valid
    private ApiFile file;
}
