package cn.master.horde.model.dto.api;

import cn.master.horde.model.dto.api.request.http.body.Body;
import cn.master.horde.model.dto.api.request.http.body.JsonBody;
import cn.master.horde.model.dto.api.request.http.body.RawBody;
import cn.master.horde.model.dto.api.request.http.body.XmlBody;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : 11's papa
 * @since : 2026/2/9, 星期一
 **/
@Data
public class ResponseBody implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank
    @Size(max = 20)
    private String bodyType = Body.BodyType.NONE.name();

    @Valid
    private JsonBody jsonBody = new JsonBody();

    @Valid
    private XmlBody xmlBody = new XmlBody("");

    @Valid
    private RawBody rawBody = new RawBody("");

    @Valid
    private ResponseBinaryBody binaryBody = new ResponseBinaryBody();

}
