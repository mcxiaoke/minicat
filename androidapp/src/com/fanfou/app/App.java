package com.fanfou.app;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.fanfou.app.api.User;
import com.fanfou.app.util.OptionHelper;
import com.fanfou.app.util.StringHelper;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.05.25
 * @version 2.0 2011.07.28
 * @version 3.0 2011.08.29
 * @version 4.0 2011.09.22
 * @version 4.5 2011.10.25
 * @version 4.6 2011.10.27
 * @version 5.0 2011.11.10
 * @version 5.1 2011.11.21
 * @version 5.2 2011.11.23
 * 
 */

@ReportsCrashes(formKey = "", formUri = "http://apps.fanfou.com/andstat/cr/", mode = ReportingInteractionMode.TOAST, resToastText = R.string.crash_toast_text)
public class App extends Application {

	// TODO c2dm activities feature and ui
	// TODO contacts scan and invite friends and ui
	// TODO new list fragment for home page
	// TODO new tabs ui for home page
	// TODO new layout and ui for pad
	// TODO timeline filter and local search
	// TODO edit profile feature and ui
	// TODO widgets support
	// TODO standalone camera shot and share feature, ui
	// TODO timeline: read and unread flag and ui
	// TODO contentprovider need modify use sqlite
	// TODO add some flags to status model in db

	public static final boolean DEBUG = true;

	public static App me;
	public static boolean active = false;
	
	public boolean noConnection;

	public boolean isLogin;
	public String userId;
	public String userScreenName;
	public String userProfileImage;
	public String oauthAccessToken;
	public String oauthAccessTokenSecret;
	public int appVersionCode;
	public String appVersionName;
	public ApnType apnType;
	public SharedPreferences sp;

	@Override
	public void onCreate() {
		super.onCreate();
		init();
		initAppInfo();
		initPreferences();
		versionCheck();
		setAlarms();
		ACRA.init(this);
	}

	private void init() {
		App.me = this;
		this.apnType = getApnType(this);

		if (DEBUG) {
			java.util.logging.Logger.getLogger("org.apache.http").setLevel(
					java.util.logging.Level.FINEST);
			java.util.logging.Logger.getLogger("org.apache.http.wire")
					.setLevel(java.util.logging.Level.FINER);
			java.util.logging.Logger.getLogger("org.apache.http.headers")
					.setLevel(java.util.logging.Level.OFF);
			java.util.logging.Logger.getLogger("httpclient.wire.header")
					.setLevel(java.util.logging.Level.FINEST);
			java.util.logging.Logger.getLogger("httpclient.wire.content")
					.setLevel(java.util.logging.Level.FINEST);

			// and shell command
			// adb shell setprop log.tag.org.apache.http VERBOSE
			// adb shell setprop log.tag.org.apache.http.wire VERBOSE
			// adb shell setprop log.tag.org.apache.http.headers VERBOSE
			// adb shell setprop log.tag.httpclient.wire.header VERBOSE
			// adb shell setprop log.tag.httpclient.wire.content VERBOSE
		}
	}

	private void initPreferences() {
		this.sp = PreferenceManager.getDefaultSharedPreferences(this);
		this.userId = OptionHelper.readString(this, R.string.option_userid,
				null);
		this.userScreenName = OptionHelper.readString(this,
				R.string.option_username, null);
		this.userProfileImage = OptionHelper.readString(this,
				R.string.option_profile_image, null);
		this.oauthAccessToken = OptionHelper.readString(this,
				R.string.option_oauth_token, null);
		this.oauthAccessTokenSecret = OptionHelper.readString(this,
				R.string.option_oauth_token_secret, null);
		this.isLogin = !StringHelper.isEmpty(oauthAccessTokenSecret);
	}

	private void initAppInfo() {
		if (DEBUG) {
			Log.d("App", "initAppInfo");
		}
		
		PackageManager pm = getPackageManager();
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			pi = new PackageInfo();
			pi.versionName = "1.0";
			pi.versionCode = 20110901;
		}
		appVersionCode = pi.versionCode;
		appVersionName = pi.versionName;
	}

	public void setAlarms() {
		if (!isLogin) {
			return;
		}
		if (DEBUG) {
			Log.d("App", "initAlarm");
		}
		Utils.setAutoClean(this);
		Utils.setAutoUpdate(this);
		Utils.setAutoComplete(this);
		Utils.setAutoNotification(this);
	}

	private void versionCheck() {
		if (DEBUG) {
			Log.d("App", "versionCheck");
		}
		if (OptionHelper.readInt(this, R.string.option_old_version_code, 0) < appVersionCode) {
			OptionHelper.saveInt(this, R.string.option_old_version_code,
					appVersionCode);
			cleanSettings();
		}
	}

	private void cleanSettings() {
		if (DEBUG) {
			Log.d("App", "cleanSettings");
		}
		Editor editor = sp.edit();
		editor.remove(getString(R.string.option_set_auto_clean));
		editor.remove(getString(R.string.option_set_auto_update));
		editor.remove(getString(R.string.option_set_auto_complete));
		editor.remove(getString(R.string.option_set_notification));
		editor.remove(getString(R.string.option_fontsize));
		editor.commit();
	}
	
	public void cleanAlarmSettings() {
		if (DEBUG) {
			Log.d("App", "cleanAlarmSettings");
		}
		Editor editor = sp.edit();
		editor.remove(getString(R.string.option_set_auto_clean));
		editor.remove(getString(R.string.option_set_auto_update));
		editor.remove(getString(R.string.option_set_auto_complete));
		editor.remove(getString(R.string.option_set_notification));
		editor.commit();
	}

	public synchronized void updateAccountInfo(User u, String token,
			String tokenSecret) {
		if (DEBUG) {
			Log.d("App", "updateAccountInfo");
		}
		userId = u.id;
		userScreenName = u.screenName;
		userProfileImage = u.profileImageUrl;
		Editor editor = sp.edit();
		editor.putString(getString(R.string.option_userid), u.id);
		editor.putString(getString(R.string.option_username), u.screenName);
		editor.putString(getString(R.string.option_profile_image),
				u.profileImageUrl);
		if (!TextUtils.isEmpty(token)) {
			editor.putString(getString(R.string.option_oauth_token), token);
			editor.putString(getString(R.string.option_oauth_token_secret),
					tokenSecret);
		}
		editor.commit();
		isLogin = true;
	}

	public synchronized void updateUserInfo(User u) {
		if (DEBUG) {
			Log.d("App", "updateAccountInfo u");
		}
		userId = u.id;
		userScreenName = u.screenName;
		userProfileImage = u.profileImageUrl;
		OptionHelper.saveString(this, R.string.option_userid, u.id);
		OptionHelper.saveString(this, R.string.option_username, u.screenName);
		OptionHelper.saveString(this, R.string.option_profile_image,
				u.profileImageUrl);
	}

	public synchronized void removeAccountInfo() {
		if (DEBUG) {
			Log.d("App", "removeAccountInfo");
		}
		isLogin = false;
		userId = null;
		userScreenName = null;
		userProfileImage = null;
		oauthAccessToken = null;
		oauthAccessTokenSecret = null;
		Editor editor = sp.edit();
		editor.remove(getString(R.string.option_userid));
		editor.remove(getString(R.string.option_username));
		editor.remove(getString(R.string.option_profile_image));
		editor.remove(getString(R.string.option_oauth_token));
		editor.remove(getString(R.string.option_oauth_token_secret));
		editor.commit();
	}
	
	public static ApnType getApnType(Context context) {
		ApnType type=ApnType.NET;
		try {
			ConnectivityManager cm=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();
			if (App.DEBUG) {
				Log.d("App","NetworkInfo: "+info);
			}
			if (info != null && info.isConnectedOrConnecting()) {
				App.me.noConnection=false;
				if (info.getType() == ConnectivityManager.TYPE_WIFI) {
					type = ApnType.WIFI;
				} else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
					String apnTypeName = info.getExtraInfo();
					if (!TextUtils.isEmpty(apnTypeName)) {
						if (apnTypeName.equals("3gnet")) {
							type = ApnType.HSDPA;
						} else if (apnTypeName.equals("ctwap")) {
							type = ApnType.CTWAP;
						} else if (apnTypeName.contains("wap")) {
							type = ApnType.WAP;
						}
					}
				}
			}else{
				App.me.noConnection=true;
			}
		} catch (Exception e) {
		}
		return type;
	}
	
	public static enum ApnType {
		WIFI("wifi"), HSDPA("hsdpa"), NET("net"), WAP("wap"), CTWAP("ctwap"), ;

		private String tag;

		ApnType(String tag) {
			this.tag = tag;
		}

		@Override
		public String toString() {
			return tag;
		}
	}

}
