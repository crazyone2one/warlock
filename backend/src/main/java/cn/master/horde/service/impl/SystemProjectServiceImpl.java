package cn.master.horde.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.horde.entity.SystemProject;
import cn.master.horde.mapper.SystemProjectMapper;
import cn.master.horde.service.SystemProjectService;
import org.springframework.stereotype.Service;

/**
 * 项目 服务层实现。
 *
 * @author 11's papa
 * @since 2026-01-15
 */
@Service
public class SystemProjectServiceImpl extends ServiceImpl<SystemProjectMapper, SystemProject>  implements SystemProjectService{

}
