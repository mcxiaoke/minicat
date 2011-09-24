package com.fanfou.app;

import java.io.IOException;
import java.util.Date;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Selection;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fanfou.app.api.ApiException;
import com.fanfou.app.api.ResultInfo;
import com.fanfou.app.api.User;
import com.fanfou.app.auth.OAuth;
import com.fanfou.app.auth.OAuthToken;
import com.fanfou.app.config.Commons;
import com.fanfou.app.db.Contents.DirectMessageInfo;
import com.fanfou.app.db.Contents.StatusInfo;
import com.fanfou.app.db.Contents.UserInfo;
import com.fanfou.app.ui.TextChangeListener;
import com.fanfou.app.util.Base64;
import com.fanfou.app.util.CryptoHelper;
import com.fanfou.app.util.DateTimeHelper;
import com.fanfou.app.util.DeviceHelper;
import com.fanfou.app.util.OptionHelper;
import com.fanfou.app.util.StringHelper;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public final class LoginPage extends BaseActivity implements
		View.OnClickListener {

	private GoogleAnalyticsTracker g;

	public static final String tag = LoginPage.class.getSimpleName();

	public void log(String message) {
		Log.v(tag, message);
	}

	private static final String USERNAME = "a";
	private static final String PASSWORD = "b";

	private EditText editUsername;
	private EditText editPassword;
	private ImageView buttonLogin;
	private TextView textSignupWeb;
	private TextView textSignupSms;
	
	private ProgressDialog mProgressDialog;

	private String username;
	private String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String savedToken = OptionHelper.readString(mContext,
				Commons.KEY_OAUTH_ACCESS_TOKEN, null);
		String savedTokenSecret = OptionHelper.readString(mContext,
				Commons.KEY_OAUTH_ACCESS_TOKEN_SECRET, null);

		if (!StringHelper.isEmpty(savedToken)
				&& !StringHelper.isEmpty(savedTokenSecret)) {
			goHome();
		} else {
			g = GoogleAnalyticsTracker.getInstance();
			g.startNewSession(Commons.GOOGLE_ANALYTICS_CODE, this);
			g.trackPageView("LoginPage");
			initLayout();
		}
	}

	private void initLayout() {
		setContentView(R.layout.login);
		editUsername = (EditText) findViewById(R.id.login_username);
		editUsername.addTextChangedListener(new TextChangeListener() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				username = s.toString();
			}
		});
		editPassword = (EditText) findViewById(R.id.login_password);
		editPassword.addTextChangedListener(new TextChangeListener() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				password = s.toString();
			}
		});

		buttonLogin = (ImageView) findViewById(R.id.login_dologin);
		buttonLogin.setOnClickListener(this);

		textSignupWeb = (TextView) findViewById(R.id.login_signup_web);
		textSignupWeb.setOnClickListener(this);

		textSignupSms = (TextView) findViewById(R.id.login_signup_sms);
		textSignupSms.setOnClickListener(this);
		
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setMessage("验证中...");
		mProgressDialog.setIndeterminate(true);

	}

	

	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_dologin:
			if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
				showToast("密码和帐号不能为空");
			} else {
				g.setCustomVar(1, "username", username);
				g.trackEvent("Action", "onClick", "Login", 1);

				mProgressDialog.show();
				new LoginTask().execute();
			}
			break;
		case R.id.login_signup_web:
			goSignUpWeb();
			break;
		case R.id.login_signup_sms:
			goSignUpSms();
		default:
			break;
		}
	}

	@Override
	protected int getPageType() {
		return PAGE_LOGIN;
	}

	public static void doLogin(Context context) {
		OptionHelper.remove(context, Commons.KEY_OAUTH_ACCESS_TOKEN);
		OptionHelper.remove(context, Commons.KEY_OAUTH_ACCESS_TOKEN_SECRET);
		Intent intent = new Intent(context, LoginPage.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
	}

	private void goSignUpWeb() {
		g.setCustomVar(0, "SignupWeb", "OnClick");
		Uri uri = Uri.parse("http://m.fanfou.com/register/");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}

	private void goSignUpSms() {
		g.setCustomVar(1, "SignupSms", "OnClick");
		Uri uri = Uri.parse("smsto:106900293152");
		Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
		intent.putExtra("sms_body", "ff");
		startActivity(intent);
	}

	@Override
	protected void onRestoreInstanceState(Bundle state) {
		super.onRestoreInstanceState(state);
		editUsername.setText(state.getString(USERNAME));
		Selection.setSelection(editUsername.getText(), editUsername.getText()
				.length());
		editPassword.setText(state.getString(PASSWORD));
		Selection.setSelection(editPassword.getText(), editPassword.getText()
				.length());
	}

	@Override
	protected void onSaveInstanceState(Bundle state) {
		super.onSaveInstanceState(state);
		state.putString(USERNAME, username);
		state.putString(PASSWORD, password);
	}

	private void showToast(String message) {
		Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
	}

	private void goHome() {
		if(App.DEBUG)
		log("goHome()");
		if (g != null) {
			g.dispatch();
		}
		Intent intent = new Intent(mContext, HomePage.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	private void clearDB() {
		if(App.DEBUG)
		log("clearDB()");
		ContentResolver cr = getContentResolver();
		cr.delete(StatusInfo.CONTENT_URI, null, null);
		cr.delete(UserInfo.CONTENT_URI, null, null);
		cr.delete(DirectMessageInfo.CONTENT_URI, null, null);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (g != null) {
			g.stopSession();
		}
	}

	private class LoginTask extends AsyncTask<Void, Void, ResultInfo> {

		static final int LOGIN_IO_ERROR = 0; // 网络错误
		static final int LOGIN_AUTH_FAILED = 1; // 验证失败
		static final int LOGIN_NEW_AUTH_SUCCESS = 2; // 首次验证成功
		static final int LOGIN_RE_AUTH_SUCCESS = 3; // 重新验证成功

		

		@Override
		protected ResultInfo doInBackground(Void... params) {

			String savedUserId = OptionHelper.readString(mContext,
					Commons.KEY_USERID, null);
			try {
				OAuth oauth = new OAuth();
				OAuthToken token = oauth
						.getOAuthAccessToken(username, password);
				if(App.DEBUG)
				log("xauth token=" + token);

				if (token != null) {

					OptionHelper.saveString(mContext,
							Commons.KEY_OAUTH_ACCESS_TOKEN, token.getToken());
					OptionHelper.saveString(mContext,
							Commons.KEY_OAUTH_ACCESS_TOKEN_SECRET,
							token.getTokenSecret());
					OptionHelper.saveString(mContext, Commons.KEY_USERID,
							username);
					OptionHelper.saveString(mContext, Commons.KEY_PASSWORD,
							password);
					App.me.password = password;
					App.me.oauthAccessToken = token.getToken();
					App.me.oauthAccessTokenSecret = token.getTokenSecret();
					App.me.isLogin = true;

					User u = App.me.api.verifyAccount();
					if (u != null && !u.isNull()) {
						if(App.DEBUG)
						log("xauth successful! ");
						App.me.updateUserInfo(u);

						if (StringHelper.isEmpty(savedUserId)) {
							clearDB();
							return new ResultInfo(LOGIN_NEW_AUTH_SUCCESS);
						} else {
							if (!savedUserId.equals(u.id)) {
								clearDB();
							}
							return new ResultInfo(LOGIN_RE_AUTH_SUCCESS);
						}
					} else {
						if(App.DEBUG)
						log("xauth failed.");
						return new ResultInfo(LOGIN_AUTH_FAILED,
								"XAuth successful, but verifyAccount failed. ");
					}
				} else {
					return new ResultInfo(LOGIN_AUTH_FAILED,
							"username or password is incorrect, XAuth failed.");
				}

			}catch (IOException e) {
				if (App.DEBUG)
					e.printStackTrace();
				return new ResultInfo(LOGIN_IO_ERROR, "Connection error: "
						+ e.getMessage());
			} catch (ApiException e) {
				if (App.DEBUG)
					e.printStackTrace();
				return new ResultInfo(LOGIN_IO_ERROR, e.getMessage());
			} finally {
			}
		}

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected void onPostExecute(ResultInfo result) {
			if(mProgressDialog!=null&&mProgressDialog.isShowing()){
				mProgressDialog.dismiss();
			}
			switch (result.code) {
			case LOGIN_IO_ERROR:
				showToast(result.message);
				break;
			case LOGIN_AUTH_FAILED:
//				g.trackEvent("Action", "LoginFailed", "AUTH_FAILED", 1);
				showToast(result.message);
				break;
			case LOGIN_NEW_AUTH_SUCCESS:
			case LOGIN_RE_AUTH_SUCCESS:
				g.setCustomVar(2, "username", username);
				g.setCustomVar(2, "api", String.valueOf(Build.VERSION.SDK_INT));
				g.setCustomVar(2, "device", Build.MODEL);
				g.setCustomVar(2, "uuid", DeviceHelper.uuid(mContext));
//				g.trackEvent("Action", "LoginSuccess", "AUTH_SUCCESS", 1);
				goHome();
				break;
			default:
				break;
			}
		}

	}

}
