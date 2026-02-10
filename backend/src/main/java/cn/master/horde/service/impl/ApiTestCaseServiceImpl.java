package cn.master.horde.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.horde.model.entity.ApiTestCase;
import cn.master.horde.model.mapper.ApiTestCaseMapper;
import cn.master.horde.service.ApiTestCaseService;
import org.springframework.stereotype.Service;

/**
 * 接口用例 服务层实现。
 *
 * @author 11's papa
 * @since 2026-02-09
 */
@Service
public class ApiTestCaseServiceImpl extends ServiceImpl<ApiTestCaseMapper, ApiTestCase>  implements ApiTestCaseService{

}
