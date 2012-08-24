package com.fanfou.app.hd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import com.fanfou.app.hd.api.ResultInfo;
import com.fanfou.app.hd.controller.SimpleDialogListener;
import com.fanfou.app.hd.dao.model.UserModel;
import com.fanfou.app.hd.dialog.InfoDialog;
import com.fanfou.app.hd.http.RestResponse;
import com.fanfou.app.hd.http.Parameter;
import com.fanfou.app.hd.http.RestClient;
import com.fanfou.app.hd.service.Constants;
import com.fanfou.app.hd.ui.widget.TextChangeListener;
import com.fanfou.app.hd.util.DeviceHelper;
import com.fanfou.app.hd.util.StringHelper;
import com.fanfou.app.hd.util.Utils;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.08
 * @version 1.1 2011.10.17
 * @version 1.2 2011.10.25
 * @version 1.3 2011.10.26
 * @version 1.4 2011.11.18
 * @version 1.5 2011.11.28
 * @version 1.6 2011.12.07
 * @version 1.7 2011.12.14
 * @version 2.0 2012.02.21
 * 
 */
public class UISignup extends Activity implements OnClickListener {

	private static final String TAG = UISignup.class.getSimpleName();

	private GoogleAnalyticsTracker g;

	private UISignup mContext;

	private String mNickName;
	private String mPassword;
	private String mPasswordConfirm;
	private String mEmail;

	private EditText eNickName;
	private EditText eEmail;
	private EditText ePassword;
	private EditText ePasswordConfirm;
	private CheckBox cFollowPushed;
	private Button mButtonRegister;

	public void log(String message) {
		Log.i(TAG, message);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		setLayout();
		setTextChangeListener();
	}

	private void init() {
		mContext = this;
		Utils.initScreenConfig(this);

		g = GoogleAnalyticsTracker.getInstance();
		g.startNewSession(getString(R.string.config_google_analytics_code),
				this);
		g.trackPageView("RegisterPage");
	}

	private void setLayout() {
		setContentView(R.layout.register);

		eNickName = (EditText) findViewById(R.id.register_nickname);
		eEmail = (EditText) findViewById(R.id.register_email);
		ePassword = (EditText) findViewById(R.id.register_password);
		ePasswordConfirm = (EditText) findViewById(R.id.register_password_confirm);
		ePasswordConfirm
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (App.DEBUG) {
							Log.d(TAG, "actionId=" + actionId + " KeyEvent="
									+ event);
						}
						if (actionId == EditorInfo.IME_ACTION_SEND) {
							doRegister();
							return true;
						}
						return false;
					}
				});

		cFollowPushed = (CheckBox) findViewById(R.id.register_follow_suggestions);
		mButtonRegister = (Button) findViewById(R.id.button_register);
		mButtonRegister.setOnClickListener(this);
	}

	private void setTextChangeListener() {
		eNickName.addTextChangedListener(new TextChangeListener() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mNickName = s.toString();
			}
		});

		eEmail.addTextChangedListener(new TextChangeListener() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mEmail = s.toString();
			}
		});

		ePassword.addTextChangedListener(new TextChangeListener() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mPassword = s.toString();
			}
		});

		ePasswordConfirm.addTextChangedListener(new TextChangeListener() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mPasswordConfirm = s.toString();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.button_register) {
			doRegister();
		}
	}

	private void doRegister() {
		if (StringHelper.isEmpty(mNickName) || StringHelper.isEmpty(mEmail)
				|| StringHelper.isEmpty(mPassword)
				|| StringHelper.isEmpty(mPasswordConfirm)) {
			Utils.notify(this, " 注册资料未填写完整");
			return;
		}

		g.setCustomVar(1, "doRegisterEmail", mEmail);

		int nickLength = mNickName.length();
		if (nickLength < 2 || nickLength > 12) {
			Utils.notify(this, "昵称为2～12字符");
			return;
		}
		int emailLength = mEmail.length();
		if (emailLength < 8) {
			Utils.notify(this, "Email地址不正确");
			return;
		}
		if (!mPassword.equals(mPasswordConfirm)) {
			Utils.notify(this, "两次输入密码不一致");
		}
		if (mPassword.length() < 4 || mPasswordConfirm.length() < 4) {
			Utils.notify(this, "密码至少4个字符");
			return;
		}

		Utils.hideKeyboard(this, ePasswordConfirm);
		new RegisterTask().execute();
	}

	private class RegisterTask extends AsyncTask<Void, Integer, ResultInfo> {

		static final int REGISTER_IO_ERROR = 0; // 网络错误
		static final int REGISTER_FAILED = 1; // 注册失败
		static final int REGISTER_CANCELLED = 2;// 用户取消
		static final int REGISTER_SUCCESS = 3;// 注册成功

		private ProgressDialog progressDialog;
		private boolean isCancelled;

		@Override
		protected ResultInfo doInBackground(Void... params) {
			try {
				UserModel user = register(mEmail, mNickName, mPassword,
						DeviceHelper.uuid(mContext));
				if (isCancelled) {
					return new ResultInfo(REGISTER_CANCELLED);
				}
				if (user != null) {
					return new ResultInfo(REGISTER_SUCCESS, "注册成功", user);
				} else {
					return new ResultInfo(REGISTER_FAILED, "注册失败");
				}
			} catch (IOException e) {
				if (App.DEBUG) {
					Log.e(TAG, e.toString());
				}
				return new ResultInfo(REGISTER_IO_ERROR,
						getString(R.string.msg_connection_error));
			}
		}

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(mContext);
			progressDialog.setMessage("正在注册...");
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
		protected void onPostExecute(ResultInfo result) {
			try {
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
			} catch (Exception e) {
			}

			switch (result.code) {
			case REGISTER_IO_ERROR:
				Utils.notify(mContext, result.message);
				break;
			case REGISTER_FAILED:
				Utils.notify(mContext, result.message);
				break;
			case REGISTER_CANCELLED:
				break;
			case REGISTER_SUCCESS:
				onRegisterSuccess(result);
				break;
			default:
				break;
			}
		}

		private void onRegisterSuccess(ResultInfo result) {
			final UserModel u = (UserModel) result.content;
			g.setCustomVar(2, "onRegisterSuccessEmail", mEmail);
			if (g != null) {
				g.dispatch();
			}
			final String message = "你的昵称是[" + u.getScreenName()
					+ "]，请稍后通过MENU菜单进入个人空间完善你的个人资料，点击确定直接登录";
			final InfoDialog dialog = new InfoDialog(mContext);
			dialog.setTitle("注册成功");
			dialog.setMessage(message);
			dialog.setClickListener(new SimpleDialogListener() {

				@Override
				public void onPositiveClick() {
					super.onPositiveClick();
					Intent data = new Intent();
					data.putExtra("userid", u.getId());
					data.putExtra("password", mPassword);
					data.putExtra("email", mEmail);
					setResult(Activity.RESULT_OK, data);
					finish();
				}
			});
			dialog.show();
		}

		private UserModel register(String email, String nickname,
				String password, String deviceId) throws IOException {
			List<Parameter> params = new ArrayList<Parameter>();
			params.add(new Parameter("email", email));
			params.add(new Parameter("realname", nickname));
			params.add(new Parameter("loginpass", password));
			params.add(new Parameter("devicetype", "android"));
			params.add(new Parameter("deviceid", deviceId));
			params.add(new Parameter("follow_pushed", String
					.valueOf(cFollowPushed.isChecked())));
			if (App.DEBUG) {
				for (Parameter parameter : params) {
					Log.d("RegisterTask", parameter.toString());
				}
			}

			// NetClient client = new NetClient();

			RestResponse res = new RestClient().post(
					"http://api.fanfou.com/register.json", params, false);

			// HttpResponse response = client.post(FanFouApiConfig.URL_REGISTER,
			// params);
			// NetResponse res = new NetResponse(response);
			if (App.DEBUG) {
				Log.d("RegisterTask", "Response: " + res.getContent());
			}
			UserModel result = null;
			if (res.statusCode == 200) {
				// result = FanFouParser.user(res.getContent(),
				// User.TYPE_OTHER);
			}
			return result;
		}
	}

}
