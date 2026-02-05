package cn.master.horde.model.entity;

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
 * 项目 实体类。
 *
 * @author 11's papa
 * @since 2026-01-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("system_project")
public class SystemProject implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目ID
     */
    @Id
    private String id;

    /**
     * 项目编号
     */
    private String num;

    /**
     * 项目名称
     */
    private String name;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 创建时间（毫秒精度）
     */
    @Column(onInsertValue = "now()")
    private LocalDateTime createTime;

    /**
     * 更新时间（毫秒精度）
     */
    @Column(onInsertValue = "now()", onUpdateValue = "now()")
    private LocalDateTime updateTime;

    /**
     * 修改人
     */
    private String updateUser;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 逻辑删除（0-未删，1-已删）
     */
    private Boolean deleted;

    /**
     * 是否启用（0-禁用，1-启用）
     */
    private Boolean enable;

}
