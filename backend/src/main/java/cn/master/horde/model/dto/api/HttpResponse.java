package cn.master.horde.model.dto.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/2/9, 星期一
 **/
@Data
public class HttpResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "唯一ID")
    private String id;

    @Schema(description = "响应码")
    private String statusCode;

    @Schema(description = "默认响应标识")
    private boolean defaultFlag;

    @Schema(description = "响应名称")
    private String name;

    @Schema(description = "响应请求头")
    @Valid
    private List<KeyValueEnableParam> headers = new ArrayList<>();

    @Schema(description = "响应请求体")
    @Valid
    private ResponseBody body = new ResponseBody();

}
