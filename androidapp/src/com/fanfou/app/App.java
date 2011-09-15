package com.fanfou.app;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.app.AlarmManager;
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
import com.fanfou.app.config.Commons;
import com.fanfou.app.http.GzipResponseInterceptor;
import com.fanfou.app.http.NetworkState;
import com.fanfou.app.http.NetworkState.Type;
import com.fanfou.app.service.FetchService;
import com.fanfou.app.update.AutoUpdateManager;
import com.fanfou.app.util.OptionHelper;
import com.fanfou.app.util.StringHelper;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.05.25
 * @version 2.0 2011.07.28
 * @version 3.0 20110829
 * 
 */

@ReportsCrashes(formKey = "", // will not be used
// mailTo = "android@fanfou.com",
formUri = "http://apps.fanfou.com/andstat/cr/", mode = ReportingInteractionMode.TOAST, resToastText = R.string.crash_toast_text)
public class App extends Application {
	public static final boolean DEBUG = true;

	public static final int CORE_POOL_SIZE = 5;
	public static final int SOCKET_BUFFER_SIZE = 8192;
	public static final int CONNECTION_TIMEOUT_MS = 20000;
	public static final int SOCKET_TIMEOUT_MS = 20000;

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

	public ImageLoader imageLoader;
	public DefaultHttpClient client;
	public float density;

	public boolean verified;
	public boolean isLogin;
	public boolean autoComplete;
	public boolean connected = true;

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
		initDensity();
		initNetworkState();
		initHttpClient();
		initAlarm();
		initAppInfo();

		if (isLogin && networkState.isWIFI()) {
			initUserInfo();
			initAutoUpdate();
			initAutoComplete();
		}

		ACRA.getErrorReporter().putCustomData("USERNAME", userId);
		ACRA.getErrorReporter().putCustomData("TIMESTAMP",
				new Date().toString());

	}

	private void init() {
		App.me = this;
		ACRA.init(this);
		this.imageLoader = new ImageLoader(this);
		this.api = new ApiImpl(this);
		// this.api=new ApiImpl2(this);
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

		if (DEBUG) {
			java.util.logging.Logger.getLogger("org.apache.http").setLevel(
					java.util.logging.Level.FINEST);
			java.util.logging.Logger.getLogger("org.apache.http.wire")
					.setLevel(java.util.logging.Level.FINER);
			java.util.logging.Logger.getLogger("org.apache.http.headers")
					.setLevel(java.util.logging.Level.OFF);
		}
		// debuggable = (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) > 0;
	}

	private void initDensity() {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		if (DEBUG) {
			Log.e("App", dm.toString());
		}
		density = dm.density;
	}

	private void initAlarm() {
		boolean noAlarmSet = OptionHelper.readBoolean(this,
				R.string.option_cleandb, false);
		if (!noAlarmSet) {
			OptionHelper.saveBoolean(this, R.string.option_cleandb, true);
			Utils.addCleanTask(this);
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
			pi.versionCode = 1;
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
		if (!autoUpdate) {
			return;
		}
		Thread task = new Thread() {
			@Override
			public void run() {
				AutoUpdateManager.checkUpdate(App.me);
			}
		};
		executor.submit(task);
	}

	public void initAutoComplete() {
		Intent intent = new Intent(this, FetchService.class);
		intent.putExtra(Commons.EXTRA_TYPE, User.AUTO_COMPLETE);
		startService(intent);
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
		ConnPerRoute connPerRoute = new ConnPerRoute() {
			@Override
			public int getMaxForRoute(HttpRoute route) {
				return 10;
			}
		};
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		HttpConnectionParams
				.setConnectionTimeout(params, CONNECTION_TIMEOUT_MS);
		HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT_MS);
		HttpConnectionParams.setSocketBufferSize(params, SOCKET_BUFFER_SIZE);

		if (networkState.getApnType() == Type.CTWAP) {
			params.setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(
					"10.0.0.200", 80));
		} else if (networkState.getApnType() == Type.WAP) {
			params.setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(
					"10.0.0.172", 80));
		}

		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		ClientConnectionManager manager = new ThreadSafeClientConnManager(
				params, schReg);
		client = new DefaultHttpClient(manager, params);
		client.addResponseInterceptor(new GzipResponseInterceptor());

	}

}
