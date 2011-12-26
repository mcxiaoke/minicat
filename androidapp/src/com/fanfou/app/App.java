package com.fanfou.app;

import java.util.TimeZone;
import java.util.logging.Logger;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import android.util.Log;

import com.fanfou.app.api.Api;
import com.fanfou.app.api.FanFouApi;
import com.fanfou.app.api.User;
import com.fanfou.app.auth.OAuthToken;
import com.fanfou.app.cache.IImageLoader;
import com.fanfou.app.cache.ImageLoader;
import com.fanfou.app.util.AlarmHelper;
import com.fanfou.app.util.DateTimeHelper;
import com.fanfou.app.util.NetworkHelper;
import com.fanfou.app.util.OptionHelper;
import com.fanfou.app.util.StringHelper;

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
 * @version 5.3 2011.11.24
 * @version 5.4 2011.11.25
 * @version 5.5 2011.11.28
 * @version 5.6 2011.12.01
 * @version 5.7 2011.12.05
 * @version 6.0 2011.12.06
 * @version 6.1 2011.12.26
 * 
 */

@ReportsCrashes(formKey = "", formUri = "http://apps.fanfou.com/andstat/cr/")
public class App extends Application {

	public static final boolean DEBUG = true;
	public static final boolean TEST = true;

	public static boolean active = false;
	public static boolean noConnection = false;
	public static boolean verified;
	public static boolean mounted;

	public static int appVersionCode;
	public static String appVersionName;

	private static String sUserId;
	private static String sUserScreenName;

	private static App sInstance;

	private static SharedPreferences sPreferences;
	private static OAuthToken sToken;
	private static ApnType sApnType;
	private static IImageLoader sLoader;

	@Override
	public void onCreate() {

		super.onCreate();
		init();
		initAppInfo();
		initPreferences();
		versionCheck();
		ACRA.init(this);
	}

	private void init() {
		// if (DEBUG) {
		// StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		// .detectAll().penaltyLog().build());
		// StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
		// .detectAll().penaltyLog().build());
		// }

		App.sInstance = this;
		App.sApnType = NetworkHelper.getApnType(this);
		App.sPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		DateTimeHelper.FANFOU_DATE_FORMAT.setTimeZone(TimeZone
				.getTimeZone("GMT"));

		if (DEBUG) {
			Logger.getLogger("org.apache.http.wire").setLevel(
					java.util.logging.Level.FINE);
			// Logger.getLogger("org.apache.http.headers")
			// .setLevel(java.util.logging.Level.FINE);

			// and shell command
			// adb shell setprop log.tag.org.apache.http VERBOSE
			// adb shell setprop log.tag.org.apache.http.wire VERBOSE
			// adb shell setprop log.tag.org.apache.http.headers VERBOSE
		}
	}

	private void initPreferences() {
		PreferenceManager.setDefaultValues(this, R.xml.options, false);
		sUserId = OptionHelper.readString(this, R.string.option_userid, null);
		sUserScreenName = OptionHelper.readString(this,
				R.string.option_username, null);
		String oauthAccessToken = OptionHelper.readString(this,
				R.string.option_oauth_token, null);
		String oauthAccessTokenSecret = OptionHelper.readString(this,
				R.string.option_oauth_token_secret, null);
		App.verified = !StringHelper.isEmpty(oauthAccessTokenSecret);
		if (App.verified) {
			sToken = new OAuthToken(oauthAccessToken, oauthAccessTokenSecret);
		}
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

	private void versionCheck() {
		int oldVersionCode = OptionHelper.readInt(this,
				R.string.option_old_version_code, 0);
		if (oldVersionCode < appVersionCode) {
			OptionHelper.saveInt(this, R.string.option_old_version_code,
					appVersionCode);
			AlarmHelper.cleanAlarmFlags(this);
		}
		if (DEBUG) {
			Log.d("App", "versionCheck old=" + oldVersionCode + " current="
					+ appVersionCode);
		}
		AlarmHelper.checkScheduledTasks(this);
	}

	public static void updateAccountInfo(Context context, final User u,
			final OAuthToken otoken) {
		if (DEBUG) {
			Log.d("App", "updateAccountInfo");
		}
		sUserId = u.id;
		sUserScreenName = u.screenName;
		setOAuthToken(otoken);
		OptionHelper.updateAccountInfo(context, u, otoken);

	}

	public void updateUserInfo(final User u) {
		if (DEBUG) {
			Log.d("App", "updateAccountInfo u");
		}
		sUserId = u.id;
		sUserScreenName = u.screenName;
		OptionHelper.updateUserInfo(this, u);
	}

	public static void removeAccountInfo(Context context) {
		if (DEBUG) {
			Log.d("App", "removeAccountInfo");
		}
		setOAuthToken(null);
		sUserId = null;
		sUserScreenName = null;
		OptionHelper.removeAccountInfo(context);
	}

	public synchronized static void setOAuthToken(final OAuthToken otoken) {
		if (otoken == null) {
			verified = false;
			sToken = null;
		} else {
			verified = true;
			sToken = otoken;
		}
	}

	public static App getApp() {
		return sInstance;
	}

	public static String getUserId() {
		return sUserId;
	}

	public static String getUserName() {
		return sUserScreenName;
	}

	public static OAuthToken getOAuthToken() {
		return sToken;
	}

	public static ApnType getApnType() {
		return sApnType;
	}

	public static void setToken(OAuthToken sToken) {
		App.sToken = sToken;
	}

	public static void setApnType(ApnType sApnType) {
		App.sApnType = sApnType;
	}

	public static SharedPreferences getPreferences() {
		return sPreferences;
	}

	public static IImageLoader getImageLoader() {
		if (sLoader == null) {
			sLoader = ImageLoader.getInstance();
		}
		return sLoader;
	}

	public static enum ApnType {
		WIFI, HSDPA, NET, WAP;
	}

}
