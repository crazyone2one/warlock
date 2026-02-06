package cn.master.horde.common.result;

/**
 * @author : 11's papa
 * @since : 2026/1/26, 星期一
 **/
public enum SystemResultCode implements IResultCode{
    GLOBAL_USER_ROLE_PERMISSION(101001, "global_user_role_permission_error"),
    GLOBAL_USER_ROLE_EXIST(101002, "global_user_role_exist_error"),
    ADMIN_USER_ROLE_PERMISSION(100019, "internal_admin_user_role_permission_error"),
    INVITE_EMAIL_EXIST(101513, "user_email_already_exists"),
    USER_ROLE_RELATION_EXIST(100002, "user_role_relation_exist_error"),
    GLOBAL_USER_ROLE_RELATION_SYSTEM_PERMISSION(101003, "global_user_role_relation_system_permission_error"),
    GLOBAL_USER_ROLE_LIMIT(101004, "global_user_role_limit_error"),
    CUSTOM_FIELD_EXIST(100012, "custom_field.exist"),
    INTERNAL_CUSTOM_FIELD_PERMISSION(100008, "internal_custom_field_permission_error"),
    ;
    private final int code;
    private final String message;
    SystemResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return getTranslationMessage(this.message);
    }
}
