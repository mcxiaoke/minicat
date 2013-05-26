package com.mcxiaoke.fanfouapp.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import com.mcxiaoke.fanfouapp.api.Api;
import com.mcxiaoke.fanfouapp.api.ApiFactory;
import com.mcxiaoke.fanfouapp.config.AccountInfo;
import com.mcxiaoke.fanfouapp.config.AccountStore;
import com.mcxiaoke.fanfouapp.controller.DataController;
import com.mcxiaoke.fanfouapp.controller.UIController;
import com.mcxiaoke.fanfouapp.dao.model.UserModel;
import com.mcxiaoke.fanfouapp.util.DateTimeHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.mcxiaoke.fanfouapp.R;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import org.oauthsimple.model.OAuthToken;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * @author mcxiaoke
 * @version 7.5 2012.02.27
 */

public class AppContext extends Application {

    private static final String TAG = "Application";

    public static final boolean DEBUG = true;
    private static HashMap<String, WeakReference<Context>> contexts = new HashMap<String, WeakReference<Context>>();

    public static int versionCode;
    public static String versionName;
    public static String packageName;
    public static PackageInfo info;
    public static boolean active;
    private static AccountInfo accountInfo;

    private static SharedPreferences sPreferences;

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
        sPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        ImageLoader.getInstance().init(getDefaultImageLoaderConfigureation());
        DateTimeHelper.FANFOU_DATE_FORMAT.setTimeZone(TimeZone
                .getTimeZone("GMT"));
//        AlarmHelper.setAlarmsIfNot(this);
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

    public static void updateAccessToken(Context context, OAuthToken token) {
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
        updateAccessToken(context, new OAuthToken(token, tokenSecret));
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

    public static Api getApi() {
        return api;
    }

    public static void setApi(Api api) {
        AppContext.api = api;
    }

    public static OAuthToken getAccessToken() {
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

    private DisplayImageOptions getDefaultDisplayImageOptions() {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.cacheInMemory().cacheOnDisc();
        builder.showStubImage(R.drawable.ic_head);
        builder.showImageOnFail(R.drawable.ic_head);
        builder.displayer(new RoundedBitmapDisplayer(getResources().getDimensionPixelSize(R.dimen.image_round_corner)));
        return builder.build();
    }

    private ImageLoaderConfiguration getDefaultImageLoaderConfigureation() {
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
                this);
        builder.defaultDisplayImageOptions(getDefaultDisplayImageOptions());
        builder.denyCacheImageMultipleSizesInMemory();
        return builder.build();
    }

    public static enum ApnType {
        WIFI, NET, WAP, CTWAP;
    }

}
