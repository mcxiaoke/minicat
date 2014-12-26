/**
 *
 */
package com.mcxiaoke.minicat.api;

/**
 * @author mcxiaoke
 * @version 1.4 2012.02.21
 */
public class ApiException extends Exception {
    public static final int IO_ERROR = -1001;
    public static final int DATA_ERROR = -1002;
    public static final int AUTH_ERROR = -1003;

    private static final long serialVersionUID = 6673077544941712048L;
    public final int statusCode;
    public final String errorMessage;

    public ApiException(int statusCode) {
        super();
        this.statusCode = statusCode;
        this.errorMessage = "";
    }

    public ApiException(int statusCode, String detailMessage) {
        super(detailMessage);
        this.statusCode = statusCode;
        this.errorMessage = detailMessage;
    }

    public ApiException(int statusCode, String detailMessage,
                        Throwable throwable) {
        super(detailMessage, throwable);
        this.statusCode = statusCode;
        this.errorMessage = detailMessage;

    }

    public ApiException(int statusCode, Throwable throwable) {
        super(throwable);
        this.statusCode = statusCode;
        this.errorMessage = throwable.toString();

    }

    @Override
    public String toString() {
        return new StringBuilder().append("code:").append(statusCode)
                .append(" msg:").append(getMessage()).toString();
    }

}
