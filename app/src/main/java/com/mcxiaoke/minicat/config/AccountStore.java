/**
 *
 */
package com.mcxiaoke.minicat.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;
import com.mcxiaoke.minicat.AppContext;
import org.oauthsimple.model.OAuthToken;

/**
 * @author mcxiaoke
 * @version 2.0 2012.02.27
 */
public class AccountStore {
    public static final String ACCOUNT_ENCRYPT_KEY = "~!@WER$%g&(_=<LhG54FGH{+[/j]";
    public static final String CONSUMER_ENCRYPT_KEY = "Gh%^@!4FH)(8<.:er%34Fh&%$%";
    private static final String TAG = AccountStore.class.getSimpleName();
    private static final boolean DEBUG = AppContext.DEBUG;
    private static final String STORE_NAME = "account_store";
    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_SCREEN_NAME = "screen_name";
    private static final String KEY_PROFILE_IMAGE = "profile_image";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_ACCESS_TOKEN_SECRET = "access_token_secret";
    private static final String KEY_LOGIN_NAME = "login_name";
    private static final String KEY_LOGIN_PASSWORD = "login_password";
    private Context mContext;
    private SharedPreferences mPreferences;

    public AccountStore(Context context) {
        this.mContext = context;
        this.mPreferences = mContext.getSharedPreferences(STORE_NAME,
                Context.MODE_PRIVATE);
    }

    public synchronized void saveAccessToken(String token, String tokenSecret) {
        if (token == null) {
            return;
        }
        Editor editor = mPreferences.edit();
        editor.putString(KEY_ACCESS_TOKEN, token);
        editor.putString(KEY_ACCESS_TOKEN_SECRET, tokenSecret);
        editor.commit();
        if (DEBUG) {
            Log.d(TAG, "saveAccessToken() token: " + token + " tokenSecret: "
                    + tokenSecret);
        }
    }

    public synchronized void saveAccessToken(OAuthToken token) {
        if (token == null) {
            return;
        }
        saveAccessToken(token.getToken(), token.getSecret());
    }

    public synchronized OAuthToken readAccessToken() {
        String token = mPreferences.getString(KEY_ACCESS_TOKEN, null);
        String tokenSecret = mPreferences.getString(KEY_ACCESS_TOKEN_SECRET,
                null);

        if (DEBUG) {
            Log.d(TAG, "readAccessToken() token: " + token + " tokenSecret: "
                    + tokenSecret);
        }

        if (TextUtils.isEmpty(tokenSecret)) {
            return null;
        }
        return new OAuthToken(token, tokenSecret);
    }

    public synchronized void clearAccessToken() {
        Editor editor = mPreferences.edit();
        editor.remove(KEY_ACCESS_TOKEN);
        editor.remove(KEY_ACCESS_TOKEN_SECRET);
        editor.commit();
        if (DEBUG) {
            Log.d(TAG, "clearAccessToken()");
        }
    }

    public synchronized void save(AccountInfo info) {
        if (info == null) {
            return;
        }
        Editor editor = mPreferences.edit();

        editor.putString(KEY_ACCOUNT, info.getAccount());
        editor.putString(KEY_SCREEN_NAME, info.getScreenName());
        editor.putString(KEY_PROFILE_IMAGE, info.getProfileImage());

        editor.putString(KEY_LOGIN_NAME, info.getLoginName());
        editor.putString(KEY_LOGIN_PASSWORD, info.getLoginPassword());

        editor.putString(KEY_ACCESS_TOKEN, info.getToken());
        editor.putString(KEY_ACCESS_TOKEN_SECRET, info.getTokenSecret());

        editor.commit();

        if (DEBUG) {
            Log.d(TAG, "save() AccountInfo: " + info);
        }
    }

    public AccountInfo read() {
        AccountInfo info = new AccountInfo();
        info.setAccount(mPreferences.getString(KEY_ACCOUNT, null));
        info.setScreenName(mPreferences.getString(KEY_SCREEN_NAME, null));
        info.setProfileImage(mPreferences.getString(KEY_PROFILE_IMAGE, null));
        info.setLoginName(mPreferences.getString(KEY_LOGIN_NAME, null));
        info.setLoginPassword(mPreferences.getString(KEY_LOGIN_PASSWORD, null));
        info.setTokenAndSecret(mPreferences.getString(KEY_ACCESS_TOKEN, null),
                mPreferences.getString(KEY_ACCESS_TOKEN_SECRET, null));

        if (DEBUG) {
            Log.d(TAG, "read() AccountInfo: " + info);
        }

        return info;
    }

    public synchronized void clear() {
        Editor editor = mPreferences.edit();
        editor.clear();
        editor.commit();
        if (DEBUG) {
            Log.d(TAG, "clear()");
        }
    }

    public synchronized void saveUserInfo(String account, String screenName,
                                          String profileImage) {
        Editor editor = mPreferences.edit();

        editor.putString(KEY_ACCOUNT, account);
        editor.putString(KEY_SCREEN_NAME, screenName);
        editor.putString(KEY_PROFILE_IMAGE, profileImage);

        editor.commit();

        if (DEBUG) {
            Log.d(TAG, "saveUserInfo() account: " + account + " screenName: "
                    + screenName);
        }
    }

    public String readAccount() {
        return mPreferences.getString(KEY_ACCOUNT, null);
    }

    public String readScreenName() {
        return mPreferences.getString(KEY_SCREEN_NAME, null);
    }

    public void saveLoginInfo(String loginName, String loginPassword) {
        Editor editor = mPreferences.edit();
        editor.putString(KEY_LOGIN_NAME, loginName);
        editor.putString(KEY_LOGIN_PASSWORD, loginPassword);
        editor.commit();

        if (DEBUG) {
            Log.d(TAG, "saveLoginInfo() loginName: " + loginName
                    + " loginPassword: " + loginPassword);
        }

    }

    public String readLoginName() {
        return mPreferences.getString(KEY_LOGIN_NAME, null);
    }

    public String readLoginPassword() {
        return mPreferences.getString(KEY_LOGIN_PASSWORD, null);
    }

}
