package cn.master.horde.service.impl;

import cn.master.horde.model.entity.JwtBlacklist;
import cn.master.horde.model.mapper.JwtBlacklistMapper;
import cn.master.horde.service.JwtBlacklistService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static cn.master.horde.model.entity.table.JwtBlacklistTableDef.JWT_BLACKLIST;


/**
 * JWT 黑名单服务实现
 *
 * @author Qoder
 * @since 2026/2/2
 */
@Service
@RequiredArgsConstructor
public class JwtBlacklistServiceImpl extends ServiceImpl<JwtBlacklistMapper, JwtBlacklist> implements JwtBlacklistService {

    @Override
    public void addToBlacklist(String token, LocalDateTime expireTime, String reason, String userName) {
        // 检查是否已存在
        QueryWrapper query = QueryWrapper.create()
                .select()
                .from(JWT_BLACKLIST)
                .where(JWT_BLACKLIST.TOKEN.eq(token));

        JwtBlacklist existing = this.getOne(query);
        if (existing != null) {
            return; // 已经在黑名单中
        }

        JwtBlacklist blacklist = new JwtBlacklist();
        blacklist.setToken(token);
        blacklist.setExpireTime(expireTime);
        blacklist.setReason(reason);
        blacklist.setUserName(userName);
        this.save(blacklist);
    }

    @Override
    public boolean isInBlacklist(String token) {
        QueryWrapper query = QueryWrapper.create()
                .select()
                .from(JWT_BLACKLIST)
                .where(JWT_BLACKLIST.TOKEN.eq(token))
                .and(JWT_BLACKLIST.EXPIRE_TIME.ge(LocalDateTime.now())); // 只检查未过期的

        return this.count(query) > 0;
    }

    @Override
    public int cleanupExpired() {
        // 先统计待删除的记录数
        QueryWrapper countQuery = QueryWrapper.create()
                .from(JWT_BLACKLIST)
                .where(JWT_BLACKLIST.EXPIRE_TIME.lt(LocalDateTime.now()));
        int count = (int) this.count(countQuery);

        // 删除过期记录
        QueryWrapper deleteQuery = QueryWrapper.create()
                .from(JWT_BLACKLIST)
                .where(JWT_BLACKLIST.EXPIRE_TIME.lt(LocalDateTime.now()));
        this.remove(deleteQuery);

        return count;
    }

    @Override
    public void removeByUsername(String username) {
        QueryWrapper countQuery = QueryWrapper.create()
                .from(JWT_BLACKLIST)
                .where(JWT_BLACKLIST.USER_NAME.eq(username));
        this.remove(countQuery);
    }
}