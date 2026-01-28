package cn.master.horde.common.constants;

import lombok.Getter;

/**
 * @author : 11's papa
 * @since : 2026/1/26, 星期一
 **/
@Getter
public enum InternalUserRole {
    ADMIN("admin"),
    MEMBER("member"),
    ORG_ADMIN("org_admin"),
    ORG_MEMBER("org_member"),
    PROJECT_ADMIN("project_admin"),
    PROJECT_MEMBER("project_member");

    private final String value;

    InternalUserRole(String value) {
        this.value = value;
    }

}
