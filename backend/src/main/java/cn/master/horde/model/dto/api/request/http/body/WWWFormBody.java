package cn.master.horde.model.dto.api.request.http.body;

import jakarta.validation.Valid;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/2/9, 星期一
 **/
@Data
public class WWWFormBody {
    @Valid
    private List<WWWFormKV> formValues = new ArrayList<>();
}
