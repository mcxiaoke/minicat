package com.fanfou.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import com.fanfou.app.api.ApiConfig;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.api.Parser;
import com.fanfou.app.api.ResultInfo;
import com.fanfou.app.api.User;
import com.fanfou.app.config.Commons;
import com.fanfou.app.dialog.AlertInfoDialog;
import com.fanfou.app.http.ConnectionManager;
import com.fanfou.app.http.ConnectionRequest;
import com.fanfou.app.http.Parameter;
import com.fanfou.app.http.Response;
import com.fanfou.app.http.ResponseCode;
import com.fanfou.app.ui.ActionBar;
import com.fanfou.app.ui.TextChangeListener;
import com.fanfou.app.util.DeviceHelper;
import com.fanfou.app.util.StringHelper;
import com.fanfou.app.util.Utils;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.08
 * @version 1.1 2011.10.17
 * @version 1.2 2011.10.25
 * @version 1.3 2011.10.26
 * 
 */
public class RegisterPage extends Activity implements OnClickListener {

	/** @see http://code.fanfouapps.com/issues/2691 */

	private static final String TAG = RegisterPage.class.getSimpleName();

	private GoogleAnalyticsTracker g;

	private RegisterPage mContext;

	private String mNickName;
	private String mPassword;
	private String mPasswordConfirm;
	private String mEmail;

	private ActionBar mActionBar;
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
		setContentView(R.layout.register);
		setActionBar();
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
		eNickName = (EditText) findViewById(R.id.register_nickname);
		eEmail = (EditText) findViewById(R.id.register_email);
		ePassword = (EditText) findViewById(R.id.register_password);
		ePasswordConfirm = (EditText) findViewById(R.id.register_password_confirm);
		cFollowPushed = (CheckBox) findViewById(R.id.register_follow_random);
		mButtonRegister = (Button) findViewById(R.id.button_register);
		mButtonRegister.setOnClickListener(this);
	}

	private void setActionBar() {
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setLeftAction(new ActionBar.BackAction(this));
//		mActionBar.setTitle("注册饭否帐号");
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
		App.active = true;
	}

	@Override
	protected void onPause() {
		App.active = false;
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
				User user = register(mEmail, mNickName, mPassword, DeviceHelper.uuid(mContext));
				if (isCancelled) {
					return new ResultInfo(REGISTER_CANCELLED);
				}
				if (user != null && !user.isNull()) {
					return new ResultInfo(REGISTER_SUCCESS, "注册成功", user);
				} else {
					return new ResultInfo(REGISTER_FAILED, "注册失败");
				}
			} catch (ApiException e) {
				if (App.DEBUG) {
					e.printStackTrace();
				}
				return new ResultInfo(REGISTER_FAILED, e.getMessage());
			} catch (IOException e) {
				if (App.DEBUG) {
					e.printStackTrace();
				}
				return new ResultInfo(REGISTER_IO_ERROR,getString(R.string.connection_error_msg));
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
			final User u = (User) result.content;
			g.setCustomVar(2, "onRegisterSuccessEmail", mEmail);
			if (g != null) {
				g.dispatch();
			}
			final String message="你的临时ID是["+u.id+"]，你的昵称是["+u.screenName+"]，请访问饭否网站修改个人网址，完善你的个人介绍，点击确定直接登录";
			final AlertInfoDialog dialog=new AlertInfoDialog(mContext, "注册成功", message);
			dialog.setOnClickListener(new AlertInfoDialog.OnOKClickListener() {
				
				@Override
				public void onOKClick() {
					Intent data = new Intent();
					data.putExtra("userid", u.id);
					data.putExtra("password", mPassword);
					data.putExtra("email", mEmail);
					if (!cFollowPushed.isChecked()) {
						data.putExtra(Commons.EXTRA_PAGE, 3);
					}
					setResult(Activity.RESULT_OK, data);
					finish();
				}
			});
			dialog.show();
		}

		private User register(String email, String nickname, String password,
				String deviceId) throws IOException, ApiException {
			List<Parameter> params = new ArrayList<Parameter>();
			params.add(new Parameter("email", email));
			params.add(new Parameter("realname", nickname));
			params.add(new Parameter("loginpass", password));
			params.add(new Parameter("devicetype", "android"));
			params.add(new Parameter("deviceid", deviceId));
			params.add(new Parameter("follow_pushed", String
					.valueOf(cFollowPushed.isChecked())));

			for (Parameter parameter : params) {
				Log.d("RegisterTask", parameter.toString());
			}
//			HttpClient client = NetworkHelper.newHttpClient();
//			HttpPost request = new HttpPost(ApiConfig.URL_REGISTER);
//			request.setEntity(ConnectionRequest.encodeForPost(params));

//			Log.d("RegisterTask", request.getURI().toString());

//			HttpResponse response = client.execute(request);
			HttpResponse response=ConnectionManager.post(ApiConfig.URL_REGISTER, params);
			Response res = new Response(response);
			if (App.DEBUG) {
				Log.d("RegisterTask", res.getContent());
			}
			if (res.statusCode == ResponseCode.HTTP_OK) {
				return User.parse(res);
			} else {
				throw new ApiException(res.statusCode, Parser.error(res
						.getContent()));
			}
		}
	}

}
