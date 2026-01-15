package cn.master.horde.common.result;

import lombok.Getter;

/**
 * @author : 11's papa
 * @since : 2026/1/15, 星期四
 **/
public class BizException extends RuntimeException {
    @Getter
    protected IResultCode errorCode;

    public BizException(String message) {
        super(message);
    }

    public BizException(Throwable t) {
        super(t);
    }

    public BizException(IResultCode errorCode) {
        super("");
        this.errorCode = errorCode;
    }

    public BizException(IResultCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BizException(IResultCode errorCode, Throwable t) {
        super(t);
        this.errorCode = errorCode;
    }

    public BizException(String message, Throwable t) {
        super(message, t);
    }
}
