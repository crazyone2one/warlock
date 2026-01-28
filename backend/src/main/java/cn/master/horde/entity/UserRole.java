package cn.master.horde.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

import java.io.Serial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户角色表 实体类。
 *
 * @author 11's papa
 * @since 2026-01-14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("user_role")
public class UserRole implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    @Id
    private String id;

    /**
     * 角色名称
     */
    private String name;
    private String code;

    /**
     * 描述
     */
    private String description;

    /**
     * 是否是内置角色
     */
    private Boolean internal;

    /**
     * 角色类型: SYSTEM, PROJECT
     */
    private String type;

    /**
     * 创建时间
     */
    @Column(onInsertValue = "now()")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(onInsertValue = "now()", onUpdateValue = "now()")
    private LocalDateTime updateTime;

    /**
     * 创建人ID
     */
    private String createUser;

    /**
     * 作用域ID（系统/组织/项目ID）
     */
    private String scopeId;

}
