package cn.master.horde.service;

import cn.master.horde.model.dto.request.CustomFieldOptionRequest;
import com.mybatisflex.core.service.IService;
import cn.master.horde.model.entity.CustomFieldOption;

import java.util.List;

/**
 * 自定义字段选项 服务层。
 *
 * @author 11's papa
 * @since 2026-02-05
 */
public interface CustomFieldOptionService extends IService<CustomFieldOption> {

    void addByFieldId(String fieldId, List<CustomFieldOption> customFieldOptions);

    void updateByFieldId(String fieldId, List<CustomFieldOptionRequest> options);

    void deleteByFieldId(String fieldId);

    List<CustomFieldOption> getByFieldId(String fieldId);

    List<CustomFieldOption> getByFieldIds(List<String> fieldIds);
}
