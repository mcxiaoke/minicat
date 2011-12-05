package com.fanfou.app;

import java.util.TimeZone;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;

import com.fanfou.app.api.Api;
import com.fanfou.app.api.FanFouApi;
import com.fanfou.app.api.User;
import com.fanfou.app.auth.OAuthToken;
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
 * 
 */

@ReportsCrashes(formKey = "", formUri = "http://apps.fanfou.com/andstat/cr/")
public class App extends Application {

	public static final boolean DEBUG = true;

	public static App me;

	public static Api api;

	public static boolean active = false;

	public volatile static boolean noConnection;
	public volatile static boolean verified;
	public volatile static boolean mounted;

	public String userId;
	public String userScreenName;

	public OAuthToken token;

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
		ACRA.init(this);
		api = FanFouApi.newInstance();
	}

	private void init() {
		if(DEBUG){
		         StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll()
		                 .penaltyLog()
		                 .build());
		         StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
		                 .detectLeakedSqlLiteObjects()
		                 .detectLeakedClosableObjects()
		                 .penaltyLog()
		                 .penaltyDeath()
		                 .build());
		}
		
		App.me = this;
		apnType = NetworkHelper.getApnType(this);

		DateTimeHelper.FANFOU_DATE_FORMAT.setTimeZone(TimeZone
				.getTimeZone("GMT"));

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
		String oauthAccessToken = OptionHelper.readString(this,
				R.string.option_oauth_token, null);
		String oauthAccessTokenSecret = OptionHelper.readString(this,
				R.string.option_oauth_token_secret, null);
		App.verified = !StringHelper.isEmpty(oauthAccessTokenSecret);
		if (App.verified) {
			this.token = new OAuthToken(oauthAccessToken,
					oauthAccessTokenSecret);
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
		if (DEBUG) {
			Log.d("App", "versionCheck");
		}
		if (OptionHelper.readInt(this, R.string.option_old_version_code, 0) < appVersionCode) {
			OptionHelper.saveInt(this, R.string.option_old_version_code,
					appVersionCode);
			AlarmHelper.cleanAlarmFlags(this);
			AlarmHelper.setScheduledTasks(this);
		}
	}



	public synchronized void updateAccountInfo(final User u,
			final OAuthToken otoken) {
		if (DEBUG) {
			Log.d("App", "updateAccountInfo");
		}
		setOAuthToken(otoken);
		userId = u.id;
		userScreenName = u.screenName;
		Editor editor = sp.edit();
		editor.putString(getString(R.string.option_userid), u.id);
		editor.putString(getString(R.string.option_username), u.screenName);
		editor.putString(getString(R.string.option_profile_image),
				u.profileImageUrl);
		editor.putString(getString(R.string.option_oauth_token),
				token.getToken());
		editor.putString(getString(R.string.option_oauth_token_secret),
				token.getTokenSecret());
		editor.commit();

	}

	public synchronized void updateUserInfo(final User u) {
		if (DEBUG) {
			Log.d("App", "updateAccountInfo u");
		}
		userId = u.id;
		userScreenName = u.screenName;
		OptionHelper.saveString(this, R.string.option_userid, u.id);
		OptionHelper.saveString(this, R.string.option_username, u.screenName);
		OptionHelper.saveString(this, R.string.option_profile_image,
				u.profileImageUrl);
	}

	public synchronized void removeAccountInfo() {
		if (DEBUG) {
			Log.d("App", "removeAccountInfo");
		}
		setOAuthToken(null);
		userId = null;
		userScreenName = null;
		Editor editor = sp.edit();
		editor.remove(getString(R.string.option_userid));
		editor.remove(getString(R.string.option_username));
		editor.remove(getString(R.string.option_profile_image));
		editor.remove(getString(R.string.option_oauth_token));
		editor.remove(getString(R.string.option_oauth_token_secret));
		editor.commit();
	}

	public void setOAuthToken(final OAuthToken otoken) {
		if (otoken == null) {
			verified = false;
			this.token = null;
			((FanFouApi) App.api).setOAuthToken(null);
		} else {
			verified = true;
			this.token = otoken;
			((FanFouApi) App.api).setOAuthToken(token);
		}
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
