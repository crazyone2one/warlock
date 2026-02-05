package cn.master.horde.model.mapper;

import cn.master.horde.model.entity.JwtBlacklist;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * JWT 黑名单 Mapper 接口
 *
 * @author Qoder
 * @since 2026/2/2
 */
@Mapper
public interface JwtBlacklistMapper extends BaseMapper<JwtBlacklist> {
}