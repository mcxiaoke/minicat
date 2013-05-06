package org.mcxiaoke.fancooker;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.logging.Logger;

import org.mcxiaoke.fancooker.api.Api;
import org.mcxiaoke.fancooker.api.ApiFactory;
import org.mcxiaoke.fancooker.auth.AccessToken;
import org.mcxiaoke.fancooker.cache.ImageLoader;
import org.mcxiaoke.fancooker.config.AccountInfo;
import org.mcxiaoke.fancooker.config.AccountStore;
import org.mcxiaoke.fancooker.controller.DataController;
import org.mcxiaoke.fancooker.controller.UIController;
import org.mcxiaoke.fancooker.dao.model.UserModel;
import org.mcxiaoke.fancooker.util.AlarmHelper;
import org.mcxiaoke.fancooker.util.DateTimeHelper;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

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
 * @version 6.2 2012.02.20
 * @version 7.0 2012.02.23
 * @version 7.1 2012.02.24
 * @version 7.5 2012.02.27
 * 
 */

public class AppContext extends Application {

	private static final String TAG = "Application";

	public static final boolean DEBUG = BuildConfig.DEBUG;
	private static HashMap<String, WeakReference<Context>> contexts = new HashMap<String, WeakReference<Context>>();

	public static int versionCode;
	public static String versionName;
	public static String packageName;
	public static PackageInfo info;
	public static boolean active;

	public static int themeId;

	private static AccountInfo accountInfo;

	private static SharedPreferences sPreferences;
	private static ImageLoader imageLoader;

	private static Api api;
	private static AppContext instance;

	private volatile static boolean disConnected;

	@Override
	public void onCreate() {
		super.onCreate();
		initAppInfo();
		initialize();
		initAccountInfo();
	}

	private void initialize() {
		// if (DEBUG) {
		// StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		// .detectAll().penaltyLog().build());
		// StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
		// .detectAll().penaltyLog().build());
		// }
		instance = this;

		themeId = R.style.AppTheme;

		sPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		imageLoader = ImageLoader.getInstance();

		DateTimeHelper.FANFOU_DATE_FORMAT.setTimeZone(TimeZone
				.getTimeZone("GMT"));

		AlarmHelper.setAlarmsIfNot(this);

		if (DEBUG) {
			Log.d(TAG, "initialize()");

			Logger.getLogger("org.apache.http.wire").setLevel(
					java.util.logging.Level.FINE);
		}
	}

	private void initAccountInfo() {
		AccountStore store = new AccountStore(this);
		accountInfo = store.read();

		api = ApiFactory.getDefaultApi();
		if (accountInfo.isVerified()) {
			api.setAccessToken(accountInfo.getAccessToken());
			api.setAccount(accountInfo.getAccount());
		}

		if (DEBUG) {
			Log.d(TAG, "initAccountInfo() accountInfo: " + accountInfo);
		}
	}

	private void initAppInfo() {
		PackageManager pm = getPackageManager();
		try {
			packageName = getPackageName();
			info = pm.getPackageInfo(packageName, 0);
			versionCode = info.versionCode;
			versionName = info.versionName;
			if (DEBUG) {
				Log.d(TAG, "initAppInfo() versionCode: " + versionCode
						+ " versionName: " + versionName);
			}
		} catch (NameNotFoundException e) {
		}

	}

	public static void doLogin(Context context) {
		// AlarmHelper.unsetScheduledTasks(context);
		clearAccountInfo(context);
		DataController.clearDatabase(context);

		if (DEBUG) {
			Log.d(TAG, "doLogin()");
		}
		// App.getImageLoader().clearQueue();
		UIController.showLogin(context);
	}

	public static void clearAccountInfo(Context context) {
		accountInfo.clear();
		api.setAccessToken(null);
		api.setAccount(null);
		AccountStore store = new AccountStore(context);
		store.clear();

		if (DEBUG) {
			Log.d(TAG, "clearAccountInfo()");
		}
	}

	public static void updateLoginInfo(Context context, String loginName,
			String loginPassword) {

		accountInfo.setLoginInfo(loginName, loginPassword);

		AccountStore store = new AccountStore(context);
		store.saveLoginInfo(loginName, loginPassword);
		if (DEBUG) {
			Log.d("App", "loginPassword loginName: " + loginName);
			Log.d("App", "loginPassword loginPassword: " + loginPassword);
		}

	}

	public static void updateUserInfo(Context context, final UserModel u) {

		String account = u.getId();
		String screenName = u.getScreenName();
		String profileImage = u.getProfileImageUrl();

		accountInfo.setAccount(account);
		accountInfo.setScreenName(screenName);
		accountInfo.setProfileImage(profileImage);
		api.setAccount(account);

		AccountStore store = new AccountStore(context);
		store.saveUserInfo(account, screenName, profileImage);
		if (DEBUG) {
			Log.d("App", "updateUserInfo UserModel: " + u);
			Log.d("App", "updateUserInfo accountInfo: " + accountInfo);
		}

	}

	public static void updateAccessToken(Context context, AccessToken token) {
		accountInfo.setAccessToken(token);
		api.setAccessToken(token);
		AccountStore store = new AccountStore(context);
		store.saveAccessToken(token);
		if (DEBUG) {
			Log.d("App", "updateAccessToken AccessToken: " + token);
			Log.d("App", "updateAccessToken accountInfo: " + accountInfo);
		}
	}

	public static void updateAccessToken(Context context, String token,
			String tokenSecret) {
		updateAccessToken(context, new AccessToken(token, tokenSecret));
	}

	public static String getAccount() {
		return accountInfo.getAccount();
	}

	public static void setAccount(String account) {
		accountInfo.setAccount(account);
	}

	public static String getScreenName() {
		return accountInfo.getScreenName();
	}

	public static void setScreenName(String screenName) {
		accountInfo.setScreenName(screenName);
	}

	public static SharedPreferences getPreferences() {
		return sPreferences;
	}

	public static void setPreferences(SharedPreferences sPreferences) {
		AppContext.sPreferences = sPreferences;
	}

	public static ImageLoader getImageLoader() {
		return imageLoader;
	}

	public static Api getApi() {
		return api;
	}

	public static void setApi(Api api) {
		AppContext.api = api;
	}

	public static AccessToken getAccessToken() {
		return accountInfo.getAccessToken();
	}

	public static boolean isDisconnected() {
		return disConnected;
	}

	public static void setDisconnected(boolean state) {
		disConnected = state;
	}

	public static AppContext getApp() {
		return instance;
	}

	public static AccountInfo getAccountInfo() {
		return accountInfo;
	}

	public static boolean isVerified() {
		return accountInfo.isVerified();
	}

	public static synchronized void setActiveContext(String className,
			Context context) {
		WeakReference<Context> reference = new WeakReference<Context>(context);
		contexts.put(className, reference);
	}

	public static synchronized void removeActiveContext(String className) {
		contexts.remove(className);
	}

	public static synchronized Context getActiveContext(String className) {
		WeakReference<Context> reference = contexts.get(className);
		if (reference == null) {
			return null;
		}

		final Context context = reference.get();

		if (context == null) {
			contexts.remove(className);
		}

		return context;
	}

	private static String userAgent;

	public static String getUserAgent() {
		if (userAgent == null) {

		}
		StringBuilder sb = new StringBuilder();
		sb.append(info.packageName).append(" ").append(info.versionName)
				.append("(").append(info.versionCode).append("").append("/")
				.append("Android ").append(Build.MODEL).append(" ");

		return sb.toString();
	}

	public static enum ApnType {
		WIFI, NET, WAP, CTWAP;
	}

}
