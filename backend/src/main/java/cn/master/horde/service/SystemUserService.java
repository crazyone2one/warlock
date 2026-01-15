package cn.master.horde.service;

import com.mybatisflex.core.service.IService;
import cn.master.horde.entity.SystemUser;

/**
 * 用户 服务层。
 *
 * @author 11's papa
 * @since 2026-01-14
 */
public interface SystemUserService extends IService<SystemUser> {

    SystemUser getUserInfoById(String id);
}
