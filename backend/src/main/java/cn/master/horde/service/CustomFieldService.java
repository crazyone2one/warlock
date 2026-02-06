package cn.master.horde.service;

import cn.master.horde.model.dto.BasePageRequest;
import cn.master.horde.model.dto.CustomFieldDTO;
import cn.master.horde.model.dto.request.CustomFieldOptionRequest;
import cn.master.horde.model.entity.CustomField;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 自定义字段 服务层。
 *
 * @author 11's papa
 * @since 2026-02-05
 */
public interface CustomFieldService extends IService<CustomField> {
    CustomField add(CustomField customField, List<CustomFieldOptionRequest> options);

    CustomField update(CustomField customField, List<CustomFieldOptionRequest> options);

    void delete(String id);

    Page<CustomFieldDTO> getPage(BasePageRequest page);
}
