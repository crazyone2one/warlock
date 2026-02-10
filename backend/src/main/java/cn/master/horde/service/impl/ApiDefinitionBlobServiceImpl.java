package cn.master.horde.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.horde.model.entity.ApiDefinitionBlob;
import cn.master.horde.model.mapper.ApiDefinitionBlobMapper;
import cn.master.horde.service.ApiDefinitionBlobService;
import org.springframework.stereotype.Service;

/**
 * 接口定义详情内容 服务层实现。
 *
 * @author 11's papa
 * @since 2026-02-09
 */
@Service
public class ApiDefinitionBlobServiceImpl extends ServiceImpl<ApiDefinitionBlobMapper, ApiDefinitionBlob>  implements ApiDefinitionBlobService{

}
