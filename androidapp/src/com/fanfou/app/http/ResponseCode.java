/**
 * 
 */
package com.fanfou.app.http;

/**
 * @author mcxiaoke
 * @version 1.0 2011.05.02
 * @version 1.1 2011.05.05
 * @version 1.2 2011.05.17
 * @version 1.3 2011.10.28
 * @version 1.4 2011.11.21
 * 
 */
public interface ResponseCode {
	public static final int HTTP_OK = 200;// OK
	public static final int HTTP_BAD_REQUEST = 400;// Bad Request
	public static final int HTTP_UNAUTHORIZED = 401;// Not Authorized
	public static final int HTTP_FORBIDDEN = 403;// Forbidden
	public static final int HTTP_NOT_FOUND = 404;// Not Found
	public static final int HTTP_INTERNAL_SERVER_ERROR = 500;// Internal Server
	public static final int HTTP_BAD_GATEWAY = 502;// Bad Gateway
	public static final int HTTP_SERVICE_UNAVAILABLE = 503;// Service

	public static final int ERROR_NORMAL = 0;
	public static final int ERROR_NOT_CONNECTED = -1;
	public static final int ERROR_NULL_TOKEN = -2;
	public static final int ERROR_AUTH_FAILED = -3;
	public static final int ERROR_AUTH_EMPTY = -4;
	public static final int ERROR_PARSE_FAILED = -5;
	public static final int ERROR_DUPLICATE = -6;

	// 200 OK: 成功
	// 202 Accepted: 发送消息时未提供source的请求会暂时放到队列中，并返回状态码202
	// 400 Bad Request： 无效的请求，返回值中可以看到错误的详细信息
	// 401 Unauthorized： 用户需要登录或者认证失败
	// 403 Forbidden： 用户无访问权限，例如访问了设置隐私的用户、消息等
	// 404 Not Found： 请求的资源已经不存在，例如访问了不存在的用户、消息等

}
