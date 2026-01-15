package cn.master.horde.common.result;

/**
 * @author : 11's papa
 * @since : 2026/1/15, 星期四
 **/
public enum ResultCode implements IResultCode {
    SUCCESS(100200, "操作成功"),
    FAILED(100500, "系统未知异常"),
    VALIDATE_FAILED(100400, "参数校验失败"),
    UNAUTHORIZED(100401, "用户认证失败"),
    FORBIDDEN(100403, "权限不足"),
    NOT_FOUND(100404, "资源未找到"),
    ;
    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
