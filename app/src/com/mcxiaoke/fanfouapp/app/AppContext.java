package com.mcxiaoke.fanfouapp.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.util.Log;
import com.mcxiaoke.fanfouapp.R;
import com.mcxiaoke.fanfouapp.api.Api;
import com.mcxiaoke.fanfouapp.api.ApiFactory;
import com.mcxiaoke.fanfouapp.config.AccountInfo;
import com.mcxiaoke.fanfouapp.config.AccountStore;
import com.mcxiaoke.fanfouapp.config.AppConfig;
import com.mcxiaoke.fanfouapp.controller.DataController;
import com.mcxiaoke.fanfouapp.controller.UIController;
import com.mcxiaoke.fanfouapp.dao.model.UserModel;
import com.mcxiaoke.fanfouapp.util.LogUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.umeng.analytics.MobclickAgent;
import org.oauthsimple.model.OAuthToken;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import static com.mcxiaoke.fanfouapp.util.DateTimeHelper.FANFOU_DATE_FORMAT;
import static java.util.TimeZone.getTimeZone;

/**
 * @author mcxiaoke
 * @version 7.5 2012.02.27
 */

public class AppContext extends Application {

    private static final String TAG = "Application";

    public static final boolean DEBUG = false;
    private static HashMap<String, WeakReference<Activity>> contexts = new HashMap<String, WeakReference<Activity>>();

    public static int versionCode;
    public static String versionName;
    public static String packageName;
    public static PackageInfo info;
    public static boolean active;
    private static AccountInfo accountInfo;

    private static Api api;
    private static AppContext instance;

    public AppContext getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initAppInfo();
        initialize();
        initAccountInfo();
    }

    private void initialize() {
        instance = this;
        MobclickAgent.setDebugMode(DEBUG);
        ImageLoader.getInstance().init(getDefaultImageLoaderConfigureation());
        FANFOU_DATE_FORMAT.setTimeZone(
                getTimeZone("GMT"));
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
            Log.v(TAG, "initAccountInfo() accountInfo: " + accountInfo);
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
                Log.v(TAG, "initAppInfo() versionCode: " + versionCode
                        + " versionName: " + versionName);
            }
        } catch (NameNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public static void doLogin(Context context) {
        clearAccountInfo(context);
        DataController.clearDatabase(context);

        if (DEBUG) {
            Log.v(TAG, "doLogin()");
        }
        UIController.showLogin(context);
    }

    public static void clearAccountInfo(Context context) {
        accountInfo.clear();
        api.setAccessToken(null);
        api.setAccount(null);
        AccountStore store = new AccountStore(context);
        store.clear();

        if (DEBUG) {
            Log.v(TAG, "clearAccountInfo()");
        }
    }

    public static void updateLoginInfo(Context context, String loginName,
                                       String loginPassword) {

        accountInfo.setLoginInfo(loginName, loginPassword);

        AccountStore store = new AccountStore(context);
        store.saveLoginInfo(loginName, loginPassword);
        if (DEBUG) {
            Log.v("App", "loginPassword loginName: " + loginName);
            Log.v("App", "loginPassword loginPassword: " + loginPassword);
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
            Log.v("App", "updateUserInfo UserModel: " + u);
            Log.v("App", "updateUserInfo accountInfo: " + accountInfo);
        }

    }

    public static void updateAccessToken(Context context, OAuthToken token) {
        accountInfo.setAccessToken(token);
        api.setAccessToken(token);
        AccountStore store = new AccountStore(context);
        store.saveAccessToken(token);
        if (DEBUG) {
            Log.v("App", "updateAccessToken AccessToken: " + token);
            Log.v("App", "updateAccessToken accountInfo: " + accountInfo);
        }
    }

    public static String getAccount() {
        return accountInfo.getAccount();
    }

    public static String getScreenName() {
        return accountInfo.getScreenName();
    }

    public static Api getApi() {
        return api;
    }

    public static AppContext getApp() {
        return instance;
    }

    public static boolean isVerified() {
        return accountInfo.isVerified();
    }

    public static synchronized void setActiveContext(Activity context) {
        WeakReference<Activity> reference = new WeakReference<Activity>(context);
        contexts.put(context.getClass().getSimpleName(), reference);
    }

    public static synchronized Activity getActiveContext(String className) {
        WeakReference<Activity> reference = contexts.get(className);
        if (reference == null) {
            return null;
        }

        final Activity context = reference.get();

        if (context == null) {
            contexts.remove(className);
        }

        return context;
    }

    private static boolean checkDebuggable(Context ctx) {
        boolean debuggable = false;
        try {
            PackageInfo pinfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature signatures[] = pinfo.signatures;
            LogUtil.e(new String(signatures[0].toChars()));
            if (!AppConfig.SIGNATURE.equals(signatures[0].toCharsString())) {
                debuggable = false;
            }
        } catch (NameNotFoundException e) {
        }

        return debuggable;
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

}
