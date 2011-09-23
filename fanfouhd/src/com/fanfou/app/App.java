package com.fanfou.app;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.apache.http.impl.client.DefaultHttpClient;

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
import com.fanfou.app.cache.ImageLoader;
import com.fanfou.app.cache.ImageLoader2;
import com.fanfou.app.cache.ImageLoaderInterface;
import com.fanfou.app.config.Commons;
import com.fanfou.app.http.NetworkState;
import com.fanfou.app.http.NetworkState.Type;
import com.fanfou.app.service.FetchService;
import com.fanfou.app.update.AutoUpdateManager;
import com.fanfou.app.util.AlarmHelper;
import com.fanfou.app.util.NetworkHelper;
import com.fanfou.app.util.OptionHelper;
import com.fanfou.app.util.StringHelper;

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
	public static final boolean DEBUG = false;

	public static final int CORE_POOL_SIZE = 5;

	public static final ThreadFactory sThreadFactory = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "fanfou thread #" + mCount.getAndIncrement());
		}
	};

	public final ExecutorService executor = Executors.newFixedThreadPool(
			CORE_POOL_SIZE, sThreadFactory);

	public static App me;

	public ImageLoaderInterface imageLoader;
	public DefaultHttpClient client;
	public float density;

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
		initDensity();
		initNetworkState();
		initScreenMode();
		initHttpClient();
		initAutoClean(this);
		initAutoComplete(this);
		initAutoNotification(this);
		initAutoUpdate();
		if (isLogin) {
			if (networkState.apnType == Type.WIFI
					|| networkState.apnType == Type.HSDPA) {
				initUserInfo();
			}
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

	private void initScreenMode() {
	}

	private void initDensity() {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		if (DEBUG) {
			Log.i("App", dm.toString());
		}
		density = dm.density;
	}

	private static void initAutoClean(Context context) {
		boolean isSet = OptionHelper.readBoolean(context,
				Commons.KEY_SET_AUTO_CLEAN, false);
		if (!isSet) {
			AlarmHelper.setCleanTask(context);
			OptionHelper.saveBoolean(context, Commons.KEY_SET_AUTO_CLEAN, true);
		}
	}

	private static void initAutoComplete(Context context) {
		boolean isSet = OptionHelper.readBoolean(context,
				Commons.KEY_SET_AUTO_COMPLETE, false);
		if (!isSet) {
			Intent intent = new Intent(context, FetchService.class);
			intent.putExtra(Commons.EXTRA_TYPE, User.AUTO_COMPLETE);
			context.startService(intent);

			AlarmHelper.setAutoCompleteTask(context);
			OptionHelper.saveBoolean(context, Commons.KEY_SET_AUTO_COMPLETE,
					true);
		}
	}

	private static void initAutoNotification(Context context) {
		boolean isSet = OptionHelper.readBoolean(context,
				Commons.KEY_SET_NOTIFICATION, false);
		if (!isSet) {
			AlarmHelper.setNotificationTaskOn(context);
			OptionHelper.saveBoolean(context, Commons.KEY_SET_NOTIFICATION,
					true);
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
		executor.submit(task);
	}

	public void initAutoUpdate() {
		boolean autoUpdate = OptionHelper.readBoolean(this,
				R.string.option_autoupdate, true);
		if (autoUpdate) {
			Thread task = new Thread() {
				@Override
				public void run() {
					AutoUpdateManager.checkUpdate(App.me);
				}
			};
			executor.submit(task);
		}
	}

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

	private void initHttpClient() {
		client = NetworkHelper.setHttpClient();
		NetworkHelper.setProxy(client.getParams(), networkState.getApnType());
	}

}
