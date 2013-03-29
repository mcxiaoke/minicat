package org.mcxiaoke.fancooker;

import org.mcxiaoke.fancooker.api.Api;
import org.mcxiaoke.fancooker.api.ResultInfo;
import org.mcxiaoke.fancooker.auth.AccessToken;
import org.mcxiaoke.fancooker.controller.UIController;
import org.mcxiaoke.fancooker.dao.model.UserModel;
import org.mcxiaoke.fancooker.ui.widget.TextChangeListener;
import org.mcxiaoke.fancooker.util.Utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Selection;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.10
 * @version 2.0 2011.10.17
 * @version 2.5 2011.10.25
 * @version 2.6 2011.10.26
 * @version 2.7 2011.10.27
 * @version 2.8 2011.11.26
 * @version 3.0 2011.12.01
 * @version 3.1 2011.12.06
 * @version 3.2 2011.12.13
 * @version 3.3 2011.12.14
 * @version 3.4 2012.02.20
 * @version 3.5 2012.02.22
 * @version 4.0 2012.02.27
 * @version 4.1 2012.03.13
 * 
 */
public final class UILogin extends UIBaseSupport implements OnClickListener {

	private static final int REQUEST_CODE_REGISTER = 0;

	private static final boolean DEBUG = App.DEBUG;
	public static final String TAG = UILogin.class.getSimpleName();

	public void log(String message) {
		Log.i(TAG, message);
	}

	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";

	private static final int DIALOG_PROGRESS = -99;

	private EditText editUsername;
	private EditText editPassword;

	private Button mButtonSignin;

	private String username;
	private String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//TODO when login, clear all alarms and tasks
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_PROGRESS:
			ProgressDialog dialog = new ProgressDialog(mContext);
			dialog.setMessage("正在登录中...");
			dialog.setIndeterminate(true);
			return dialog;
//			break;
		default:
			return null;
//			break;
		}
	}

	@Override
	protected void setLayout() {
		setContentView(R.layout.ui_login);

		if (DEBUG) {
			Log.d(TAG, "setLayout()");
		}

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
		editPassword.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (App.DEBUG) {
					Log.d(TAG, "actionId=" + actionId + " KeyEvent=" + event);
				}
				if (actionId == EditorInfo.IME_ACTION_SEND) {
					doLogin();
					return true;
				}
				return false;
			}
		});

		mButtonSignin = (Button) findViewById(R.id.button_signin);
		mButtonSignin.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_signin:
			doLogin();
			break;
		default:
			break;
		}
	}

	private void doLogin() {
		if (DEBUG) {
			Log.d(TAG, "doLogin()");
		}
		if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
			Utils.notify(mContext, "密码和帐号不能为空");
		} else {
			Utils.hideKeyboard(this, editPassword);
			new LoginTask().execute();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_REGISTER) {
			editUsername.setText(data.getStringExtra("email"));
			editPassword.setText(data.getStringExtra("password"));
			new LoginTask().execute();
		}
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

	@Override
	protected int getMenuResourceId() {
		return -1;
	}

	private class LoginTask extends AsyncTask<Void, Integer, ResultInfo> {

		static final int LOGIN_IO_ERROR = 0; // 网络错误
		static final int LOGIN_AUTH_FAILED = 1; // 验证失败
		static final int LOGIN_AUTH_SUCCESS = 2; // 首次验证成功
		static final int LOGIN_CANCELLED_BY_USER = 3;

		private boolean isCancelled = false;
//		private ProgressDialog dialog;

		@Override
		protected ResultInfo doInBackground(Void... params) {
			try {

				if (DEBUG) {
					Log.d(TAG, "LoginTask.doInBackground()");
				}
				final Api api = App.getApi();
				AccessToken token = api.getOAuthAccessToken(username, password);
				if (App.DEBUG)
					log("xauth token=" + token);

				if (token != null) {
					if (isCancelled) {
						return new ResultInfo(LOGIN_CANCELLED_BY_USER,
								"user cancel login process.");
					}

					publishProgress(1);
					App.updateAccessToken(mContext, token);

					final UserModel u = api.verifyCredentials();

					if (u != null) {
						App.updateUserInfo(mContext, u);
						App.updateLoginInfo(mContext, username, password);
						if (App.DEBUG) {
							log("xauth successful! ");
						}
						return new ResultInfo(LOGIN_AUTH_SUCCESS);
					} else {
						if (App.DEBUG) {
							log("xauth failed.");
						}
						App.clearAccountInfo(mContext);
						return new ResultInfo(LOGIN_AUTH_FAILED,
								"XAuth successful, but verifyAccount failed. ");
					}
				} else {
					return new ResultInfo(LOGIN_AUTH_FAILED,
							"username or password is incorrect, XAuth failed.");
				}

			} catch (Exception e) {
				if (App.DEBUG) {
					e.printStackTrace();
				}
				App.clearAccountInfo(mContext);
				return new ResultInfo(LOGIN_IO_ERROR, e.getMessage());
			} finally {
			}
		}

		@Override
		protected void onPreExecute() {
			showDialog(DIALOG_PROGRESS);
//			dialog = new ProgressDialog(mContext);
//			dialog.setMessage("正在登录...");
//			dialog.setIndeterminate(true);
//			dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//
//				@Override
//				public void onCancel(DialogInterface dialog) {
//					isCancelled = true;
//					cancel(true);
//				}
//			});
//			dialog.show();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			if (values.length > 0) {
				int value = values[0];
//				if (value == 1) {
//					dialog.setMessage("正在验证帐号...");
//				}
			}
		}

		@Override
		protected void onPostExecute(ResultInfo result) {
			dismissDialog(DIALOG_PROGRESS);
//			if (dialog != null) {
//				dialog.dismiss();
//			}
			switch (result.code) {
			case LOGIN_IO_ERROR:
			case LOGIN_AUTH_FAILED:
				Utils.notify(mContext, result.message);
				break;
			case LOGIN_CANCELLED_BY_USER:
				break;
			case LOGIN_AUTH_SUCCESS:
				onLoginComplete();
				break;
			default:
				break;
			}
		}

	}

	private void onLoginComplete() {
		// AlarmHelper.setScheduledTasks(mContext);
		UIController.showHome(mContext);
		finish();
	}

	@Override
	protected void initialize() {
		// TODO Auto-generated method stub

	}

}
