package com.fanfou.app;

import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Selection;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.fanfou.app.ui.ActionBar;
import com.fanfou.app.ui.TextChangeListener;
import com.fanfou.app.ui.ActionBar.AbstractAction;
import com.fanfou.app.util.DeviceHelper;
import com.fanfou.app.util.IntentHelper;
import com.fanfou.app.util.OptionHelper;
import com.fanfou.app.util.StringHelper;
import com.fanfou.app.util.Utils;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.10
 * @version 2.0 2011.10.17
 * @version 2.5 2011.10.25
 * 
 */
public final class LoginPage extends Activity implements OnClickListener{
	
	private static final int REQUEST_CODE_REGISTER = 0;

	public static final String TAG = LoginPage.class.getSimpleName();
	
	private LoginPage mContext;

	private GoogleAnalyticsTracker g;
	private int page;

	public void log(String message) {
		Log.i(TAG, message);
	}

	private static final String USERNAME = "a";
	private static final String PASSWORD = "b";

	private EditText editUsername;
	private EditText editPassword;
	private ImageView buttonLogin;
	private ActionBar mActionBar;

	private String username;
	private String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		setLayout();
	}

	private void init() {
		mContext=this;
		Utils.initScreenConfig(this);
		g = GoogleAnalyticsTracker.getInstance();
		g.startNewSession(getString(R.string.config_google_analytics_code),
				this);
		g.trackPageView("LoginPage");
	}

	private void setLayout() {
		setContentView(R.layout.login);

		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setLeftAction(new LogoAction());
		mActionBar.setRightAction(new RegisterAction(this));
		mActionBar.setTitle("登录饭否");

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

		buttonLogin = (ImageView) findViewById(R.id.login_signin);
		buttonLogin.setOnClickListener(this);

	}

	private static class RegisterAction extends AbstractAction {
		Activity mContext;

		public RegisterAction(Activity context) {
			super(R.drawable.i_register);
			mContext = context;
		}

		@Override
		public void performAction(View view) {
			try {
				Intent intent = new Intent(mContext, RegisterPage.class);
				mContext.startActivityForResult(intent, REQUEST_CODE_REGISTER);
			} catch (ActivityNotFoundException e) {
				Utils.notify(mContext, R.string.actionbar_activity_not_found);
			}
		}
	}

	private static class LogoAction extends ActionBar.AbstractAction {

		public LogoAction() {
			super(R.drawable.i_logo);
		}

		@Override
		public void performAction(View view) {
		}
	}

	private LoginTask mLoginTask;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_signin:
			if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
				Utils.notify(mContext,"密码和帐号不能为空");
			} else {
				g.setCustomVar(1, "username", username);
				g.trackEvent("Action", "onClick", "Login", 1);
				mLoginTask = new LoginTask();
				mLoginTask.execute();
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_REGISTER) {
			// editUsername.setText(data.getStringExtra("userid"));
			editUsername.setText(data.getStringExtra("email"));
			editPassword.setText(data.getStringExtra("password"));
			page = data.getIntExtra(Commons.EXTRA_PAGE, 0);
			mLoginTask = new LoginTask();
			mLoginTask.execute();
		}
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
	protected void onResume() {
		super.onResume();
		App.active = true;
	}

	@Override
	protected void onPause() {
		App.active = false;
		super.onPause();
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

	private void clearDB() {
		if (App.DEBUG)
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

	private class LoginTask extends AsyncTask<Void, Integer, ResultInfo> {

		static final int LOGIN_IO_ERROR = 0; // 网络错误
		static final int LOGIN_AUTH_FAILED = 1; // 验证失败
		static final int LOGIN_NEW_AUTH_SUCCESS = 2; // 首次验证成功
		static final int LOGIN_RE_AUTH_SUCCESS = 3; // 重新验证成功
		static final int LOGIN_CANCELLED_BY_USER = 4;

		private ProgressDialog progressDialog;
		private boolean isCancelled;

		@Override
		protected ResultInfo doInBackground(Void... params) {

			String savedUserId = OptionHelper.readString(mContext,
					R.string.option_userid, null);
			try {
				OAuth oauth = new OAuth();
				OAuthToken token = oauth
						.getOAuthAccessToken(username, password);
				if (App.DEBUG)
					log("xauth token=" + token);

				if (isCancelled) {
					if (App.DEBUG) {
						log("login cancelled after xauth process.");
					}
					return new ResultInfo(LOGIN_CANCELLED_BY_USER,
							"user cancel login process.");
				}

				if (token != null) {
					publishProgress(1);

					App.me.oauthAccessToken = token.getToken();
					App.me.oauthAccessTokenSecret = token.getTokenSecret();
					User u = App.me.api.verifyAccount();

					if (isCancelled) {
						if (App.DEBUG) {
							log("login cancelled after verifyAccount process.");
						}
						return new ResultInfo(LOGIN_CANCELLED_BY_USER,
								"user cancel login process.");
					}

					if (u != null && !u.isNull()) {
						App.me.updateAccountInfo(u, password, token.getToken(),
								token.getTokenSecret());
						if (App.DEBUG)
							log("xauth successful! ");

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
						if (App.DEBUG)
							log("xauth failed.");
						return new ResultInfo(LOGIN_AUTH_FAILED,
								"XAuth successful, but verifyAccount failed. ");
					}
				} else {
					return new ResultInfo(LOGIN_AUTH_FAILED,
							"username or password is incorrect, XAuth failed.");
				}

			} catch (IOException e) {
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
			progressDialog = new ProgressDialog(mContext);
			progressDialog.setMessage("正在进行登录认证...");
			progressDialog.setIndeterminate(true);
			progressDialog
					.setOnCancelListener(new DialogInterface.OnCancelListener() {

						@Override
						public void onCancel(DialogInterface dialog) {
							isCancelled = true;
							cancel(true);
						}
					});
			progressDialog.show();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			if (values.length > 0) {
				int value = values[0];
				if (value == 1) {
					progressDialog.setMessage("正在验证帐号信息...");
				}
			}
		}

		@Override
		protected void onPostExecute(ResultInfo result) {
			try {
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
			} catch (Exception e) {
			}

			switch (result.code) {
			case LOGIN_IO_ERROR:
				Utils.notify(mContext,result.message);
				break;
			case LOGIN_AUTH_FAILED:
				Utils.notify(mContext,result.message);
				break;
			case LOGIN_CANCELLED_BY_USER:
				break;
			case LOGIN_NEW_AUTH_SUCCESS:
			case LOGIN_RE_AUTH_SUCCESS:
				g.setCustomVar(2, "username", username);
				g.setCustomVar(2, "api", String.valueOf(Build.VERSION.SDK_INT));
				g.setCustomVar(2, "device", Build.MODEL);
				g.setCustomVar(2, "uuid", DeviceHelper.uuid(mContext));
				if (g != null) {
					g.dispatch();
				}
				IntentHelper.goHomePage(mContext, page);
				finish();
				break;
			default:
				break;
			}
		}

	}

}
