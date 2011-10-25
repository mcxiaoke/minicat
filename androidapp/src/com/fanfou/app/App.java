package com.fanfou.app;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.fanfou.app.api.Api;
import com.fanfou.app.api.ApiImpl;
import com.fanfou.app.api.User;
import com.fanfou.app.cache.IImageLoader;
import com.fanfou.app.cache.ImageLoader;
import com.fanfou.app.http.ApnType;
import com.fanfou.app.http.NetworkState;
import com.fanfou.app.service.CleanService;
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
 * 
 */

@ReportsCrashes(formKey = "", formUri = "http://apps.fanfou.com/andstat/cr/", mode = ReportingInteractionMode.TOAST, resToastText = R.string.crash_toast_text)
public class App extends Application {

	// TODO direct message chat ui
	// TODO direct message send entry
	// TODO item delete confirm
	// TODO common confirm dialog ui
	// TODO register and ui
	// TODO draft feature and ui
	// TODO status conversation threads and ui
	// TODO c2dm activities feature and ui
	// TODO home page new status notification and ui
	// TODO contacts scan and invite friends and ui
	// TODO new image cache and loader
	// TODO new common status/messages/users info cache
	// TODO new page indicator ui
	// TODO new list fragment for home page
	// TODO new tabs ui for home page
	// TODO new layout and ui for pad
	// TODO username auto complete feature and ui
	// TODO new image view ui in status page
	// TODO standalone image viewer
	// TODO user photo album and ui
	// TODO timeline filter and local search
	// TODO keywords highlight in searsh page
	// TODO standalone trends page
	// TODO edit profile feature and ui
	// TODO widgets support
	// TODO standalone camera shot and share feature, ui
	// TODO timeline: read and unread flag and ui
	// TODO timeline gap and load
	// TODO contentprovider need modify use sqlite
	// TODO add some flags to status model in db
	// TODO cache and store user info data

	public static final boolean DEBUG = true;

	public static App me;
	public static boolean active = false;

	private IImageLoader imageLoader;
	public Api api;
	public float density;

	public boolean verified;
	public boolean isLogin;

	public User user;
	public String userId;
	public String password;
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
		initDensity();
		if (isLogin) {
			initAlarm();
		}

		if (!DEBUG) {
			ACRA.init(this);
		}

	}

	private void init() {
		App.me = this;
		NetworkState state = new NetworkState(this);
		apnType = state.getApnType();
		this.imageLoader = new ImageLoader(this);
		this.api = new ApiImpl(this);

		if (DEBUG) {
			java.util.logging.Logger.getLogger("org.apache.http").setLevel(
					java.util.logging.Level.FINEST);
			java.util.logging.Logger.getLogger("org.apache.http.wire")
					.setLevel(java.util.logging.Level.FINER);
			java.util.logging.Logger.getLogger("org.apache.http.headers")
					.setLevel(java.util.logging.Level.OFF);
		}
	}

	private void initPreferences() {
		this.sp = PreferenceManager.getDefaultSharedPreferences(this);
		this.userId = OptionHelper.readString(this, R.string.option_userid,
				null);
		this.password = OptionHelper.readString(this, R.string.option_password,
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

	public void initDensity() {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		density = dm.density;
		if (DEBUG) {
			Log.i("App", dm.toString());
		}
	}

	private void initAppInfo() {
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

	private void initAlarm() {
		Utils.setAutoClean(this);
		Utils.setAutoUpdate(this);
		Utils.setAutoComplete(this);
		Utils.setAutoNotification(this);
	}

	private void versionCheck() {
		if (OptionHelper.readInt(this, R.string.option_old_version_code, 0) < appVersionCode) {
			OptionHelper.saveInt(this, R.string.option_old_version_code,
					appVersionCode);
			cleanSettings();
		}
	}

	private void cleanSettings() {
		OptionHelper.remove(this, R.string.option_set_auto_clean);
		OptionHelper.remove(this, R.string.option_set_auto_complete);
		OptionHelper.remove(this, R.string.option_set_notification);
		OptionHelper.remove(this, R.string.option_set_auto_update);
	}

	public synchronized void updateAccountInfo(User u,String password, String token, String tokenSecret) {
		user = u;
		userId = u.id;
		userScreenName = u.screenName;
		userProfileImage = u.profileImageUrl;
		
		OptionHelper.saveString(this, R.string.option_userid, u.id);
		OptionHelper.saveString(this, R.string.option_username, u.screenName);
		OptionHelper.saveString(this, R.string.option_profile_image,
				u.profileImageUrl);
		
		if(!TextUtils.isEmpty(password)){
			OptionHelper.saveString(this, R.string.option_password, password);
			
		}
		if(!TextUtils.isEmpty(token)){
			OptionHelper.saveString(this, R.string.option_oauth_token, token);
			OptionHelper.saveString(this, R.string.option_oauth_token_secret, tokenSecret);
		}
		isLogin=true;
	}
	
	public synchronized void updateUserInfo(User u) {
		user = u;
		userId = u.id;
		userScreenName = u.screenName;
		userProfileImage = u.profileImageUrl;
		OptionHelper.saveString(this, R.string.option_userid, u.id);
		OptionHelper.saveString(this, R.string.option_username, u.screenName);
		OptionHelper.saveString(this, R.string.option_profile_image,
				u.profileImageUrl);
	}
	
	public synchronized void removeAccountInfo(){
		isLogin=false;
		user=null;
		userId=null;
		userScreenName=null;
		userProfileImage=null;
		oauthAccessToken=null;
		oauthAccessTokenSecret=null;
		OptionHelper.remove(this, R.string.option_userid);
		OptionHelper.remove(this, R.string.option_password);
		OptionHelper.remove(this, R.string.option_username);
		OptionHelper.remove(this, R.string.option_profile_image);
		OptionHelper.remove(this,R.string.option_oauth_token);
		OptionHelper.remove(this, R.string.option_oauth_token_secret);
	}

	public IImageLoader getImageLoader() {
		if (imageLoader == null) {
			imageLoader = new ImageLoader(this);
		}
		return imageLoader;
	}
	
	public void clearImageTasks(){
		if (imageLoader != null) {
			imageLoader.clearQueue();
		}
	}

	public void shutdownImageLoader() {
		if (imageLoader != null) {
			imageLoader.shutdown();
		}
	}

}
