package com.fanfou.app;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.fanfou.app.api.Api;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.api.ApiImpl;
import com.fanfou.app.api.User;
import com.fanfou.app.cache.IImageLoader;
import com.fanfou.app.cache.ImageLoader;
import com.fanfou.app.config.Commons;
import com.fanfou.app.http.NetworkState;
import com.fanfou.app.http.NetworkState.Type;
import com.fanfou.app.service.FetchService;
import com.fanfou.app.update.AutoUpdateManager;
import com.fanfou.app.util.AlarmHelper;
import com.fanfou.app.util.NetworkHelper;
import com.fanfou.app.util.OptionHelper;
import com.fanfou.app.util.StringHelper;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.05.25
 * @version 2.0 2011.07.28
 * @version 3.0 2011.08.29
 * @version 4.0 2011.09.22
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

	private IImageLoader imageLoader;
	private DefaultHttpClient client;
	private float density;

	public boolean verified;
	public boolean isLogin;
	public boolean autoComplete;
	public boolean connected = true;
	public boolean active;

	public static int notificationCount;

	public User user;
	public String userId;
	public String password;
	public String userScreenName;
	public String userProfileImage;

	public String oauthAccessToken;
	public String oauthAccessTokenSecret;

	public int appVersionCode;
	public String appVersionName;

	public NetworkState networkState;

	public SharedPreferences sp;

	public Api api;

	@Override
	public void onCreate() {
		super.onCreate();
		init();
		initAppInfo();
		initPreferences();
		getDensity();
		initNetworkState();
		getHttpClient();
		Utils.setAutoClean(this);
		Utils.setAutoComplete(this);
		Utils.setAutoNotification(this);
		if (isLogin) {
			if (networkState.apnType == Type.WIFI
					|| networkState.apnType == Type.HSDPA) {
				initUserInfo();
			}
		}

	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		if (imageLoader != null) {
			imageLoader.clearCache();
		}
	}

	private void init() {
		App.me = this;
		ACRA.init(this);
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
		this.userId = OptionHelper.readString(this, Commons.KEY_USERID, null);
		this.password = OptionHelper.readString(this, Commons.KEY_PASSWORD,
				null);
		this.userScreenName = OptionHelper.readString(this,
				Commons.KEY_SCREEN_NAME, null);
		this.userProfileImage = OptionHelper.readString(this,
				Commons.KEY_PROFILE_IMAGE, null);
		this.oauthAccessToken = OptionHelper.readString(this,
				Commons.KEY_OAUTH_ACCESS_TOKEN, null);
		this.oauthAccessTokenSecret = OptionHelper.readString(this,
				Commons.KEY_OAUTH_ACCESS_TOKEN_SECRET, null);
		this.isLogin = !StringHelper.isEmpty(oauthAccessTokenSecret);
	}

	public float getDensity() {
		if(density==0f){
			DisplayMetrics dm = getResources().getDisplayMetrics();
			if (DEBUG) {
				Log.i("App", dm.toString());
			}
			density = dm.density;
		}
		return density;
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

	private void initNetworkState() {
		networkState = new NetworkState(this);
	}

	private void initUserInfo() {
		Thread task = new Thread() {

			@Override
			public void run() {
				try {
					User result = api.verifyAccount();
					if (result != null) {
						updateUserInfo(result);
						verified = true;
					}
				} catch (ApiException e) {
				}
			}
		};
		task.start();
	}

//	public static App getApp(Activity activity){
//		return (App) activity.getApplication();
//	}
//	
//	public static App getApp(Context context){
//		return (App) context.getApplicationContext();
//	}


	public synchronized void updateUserInfo(User u) {
		user = u;
		userId = u.id;
		userScreenName = u.screenName;
		userProfileImage = u.profileImageUrl;
		OptionHelper.saveString(this, Commons.KEY_USERID, u.id);
		OptionHelper.saveString(this, Commons.KEY_SCREEN_NAME, u.screenName);
		OptionHelper.saveString(this, Commons.KEY_PROFILE_IMAGE,
				u.profileImageUrl);
	}

	public IImageLoader getImageLoader() {
		if (imageLoader == null) {
			imageLoader = new ImageLoader(this);
		}
		return imageLoader;
	}

	public synchronized void shutdownImageLoader() {
		if (imageLoader != null) {
			imageLoader.shutdown();
		}
	}

	public DefaultHttpClient getHttpClient() {
		if (client == null) {
			synchronized (this) {
				client = NetworkHelper.setHttpClient();
				NetworkHelper.setProxy(client.getParams(),
						networkState.getApnType());	
			}
		}
		return client;
	}

}
