package cn.master.horde.model.dto.api.request.http.body;

import cn.master.horde.model.dto.api.ApiFile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/2/9, 星期一
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class FormDataKV extends WWWFormKV {

    /**
     * 参数的文件列表
     * 当 paramType 为 FILE 时，参数值使用该字段
     * 其他类型使用 value字段
     */
    @Valid
    private List<ApiFile> files;
    /**
     * 参数的 contentType
     */
    private String contentType;

    @JsonIgnore
    public boolean isFile() {
        return StringUtils.equalsIgnoreCase(getParamType(), BodyParamType.FILE.getValue());
    }
}
