package cn.master.horde.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * JWT 黑名单实体
 *
 * @author Qoder
 * @since 2026/2/2
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "JWT黑名单表")
@Table(value = "jwt_blacklist")
public class JwtBlacklist implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @Schema(description = "主键")
    private String id;
    /**
     * JWT令牌
     */
    private String token;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 踢出原因
     */
    private String reason;
    private String createUser;
    private String userName;
    @Column(onInsertValue = "now()")
    @Schema(description = "创建时间（精确到毫秒）")
    private LocalDateTime createTime;
    /**
     * 逻辑删除（0-未删，1-已删）
     */
    private Boolean deleted;
}