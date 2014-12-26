package com.mcxiaoke.minicat.util;

import android.content.Context;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * Project: fanfouapp
 * Package: com.mcxiaoke.fanfouapp.util
 * User: mcxiaoke
 * Date: 13-6-1
 * Time: 上午9:31
 */
public class UmengHelper {

    private static final String EVENT_LOGIN = "event_login";
    private static final String EVENT_LOGIN_ERROR = "event_login_erro";
    private static final String EVENT_STATUS_UPDATE = "even_status_update";
    private static final String EVENT_PHOTO_UPLOAD = "event_photo_upload";
    private static final String EVENT_SEND_DM = "event_send_dm";
    private static final String EVENT_STATUS_UPDATE_ERROR = "event_status_update_failed";

    public static void onLoginEvent(Context context, String userName) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userName", userName);
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));
        MobclickAgent.onEvent(context, EVENT_LOGIN, params);
    }

    public static void onStatusUpdateEvent(Context context, String userId, String statusId) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId", userId);
        params.put("status_id", statusId);
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));
        MobclickAgent.onEvent(context, EVENT_STATUS_UPDATE, params);
    }

    public static void onPhotoUploadEvent(Context context, String userId, String statusId) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId", userId);
        params.put("status_id", statusId);
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));
        MobclickAgent.onEvent(context, EVENT_PHOTO_UPLOAD, params);
    }

    public static void onSendDMEvent(Context context) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));
        MobclickAgent.onEvent(context, EVENT_SEND_DM, params);
    }

    public static void onStatusUpdateError(Context context, String userId, int code, String message, String extra) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId", userId);
        params.put("code", String.valueOf(code));
        params.put("message", message);
        params.put("extra", extra);
        params.put("network_info", NetworkHelper.getNetworkInfo(context));
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));
        MobclickAgent.onEvent(context, EVENT_STATUS_UPDATE_ERROR, params);
    }

    public static void onLoginError(Context context, String userId, int code, String message, String extra) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId", userId);
        params.put("code", String.valueOf(code));
        params.put("message", message);
        params.put("extra", extra);
        params.put("network_info", NetworkHelper.getNetworkInfo(context));
        params.put("timestamp", String.valueOf(System.currentTimeMillis()));
        MobclickAgent.onEvent(context, EVENT_LOGIN_ERROR, params);
    }
}
