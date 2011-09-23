/**
 * 
 */
package com.fanfou.app.http;

/**
 * @author mcxiaoke
 * @version 1.0 2011.05.02
 * @version 1.1 2011.05.05
 * @version 1.2 2011.05.17
 * 
 */
public interface ResponseCode {
    public static final int HTTP_OK = 200;// OK
    public static final int HTTP_201 = 201;
    public static final int HTTP_MULTIPLE_CHOICES = 300;//
    public static final int HTTP_FOUND = 302;//
    public static final int HTTP_NOT_MODIFIED = 304;// Not Modified
    public static final int HTTP_BAD_REQUEST = 400;// Bad Request
    public static final int HTTP_UNAUTHORIZED = 401;// Not Authorized
    public static final int HTTP_FORBIDDEN = 403;// Forbidden
    public static final int HTTP_NOT_FOUND = 404;// Not Found
    public static final int HTTP_NOT_ACCEPTABLE = 406;// Not Acceptable
    public static final int HTTP_TOO_LONG = 413;// Not Acceptable
    public static final int HTTP_ENHANCE_YOUR_CLAIM = 420;// Enhance Your Calm
    public static final int HTTP_INTERNAL_SERVER_ERROR = 500;// Internal Server
    public static final int HTTP_BAD_GATEWAY = 502;// Bad Gateway
    public static final int HTTP_SERVICE_UNAVAILABLE = 503;// Service

    
    public static final int ERROR_NORMAL=0;
    public static final int ERROR_NOT_CONNECTED=-1;
    public static final int ERROR_NULL_TOKEN=-2;
    public static final int ERROR_AUTH_FAILED=-3;
    public static final int ERROR_AUTH_EMPTY=-4;
    public static final int ERROR_PARSE_FAILED=-5;
    public static final int ERROR_DUPLICATE=-6;
    
//	int statusCode=response.getStatusCode();
//	switch (statusCode) {
//	case HTTP_OK:
//		break;
//	case HTTP_BAD_REQUEST:
//		break;
//	case HTTP_UNAUTHORIZED:
//		break;
//	case HTTP_FORBIDDEN:
//		break;
//	case HTTP_NOT_FOUND:
//		break;
//	case HTTP_INTERNAL_SERVER_ERROR:
//	case HTTP_BAD_GATEWAY:
//	case HTTP_SERVICE_UNAVAILABLE:
//	default:
//		break;
//	}
    
}
