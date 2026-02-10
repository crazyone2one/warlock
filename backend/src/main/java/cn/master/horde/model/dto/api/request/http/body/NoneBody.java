package cn.master.horde.model.dto.api.request.http.body;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @author : 11's papa
 * @since : 2026/2/9, 星期一
 **/
@JsonIgnoreProperties(value = {"handler","hibernateLazyInitializer","fieldHandler"})
@Data
public class NoneBody {
}