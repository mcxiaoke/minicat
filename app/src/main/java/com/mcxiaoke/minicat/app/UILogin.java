package com.mcxiaoke.minicat.app;

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
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.api.Api;
import com.mcxiaoke.minicat.api.ApiException;
import com.mcxiaoke.minicat.controller.DataController;
import com.mcxiaoke.minicat.controller.UIController;
import com.mcxiaoke.minicat.dao.model.UserModel;
import com.mcxiaoke.minicat.ui.widget.TextChangeListener;
import com.mcxiaoke.minicat.util.UmengHelper;
import com.mcxiaoke.minicat.util.Utils;
import org.oauthsimple.model.OAuthToken;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author mcxiaoke
 * @version 4.1 2012.03.13
 */
public final class UILogin extends UIBaseSupport implements OnClickListener {

    public static final String TAG = UILogin.class.getSimpleName();
    private static final int REQUEST_CODE_REGISTER = 0;
    private static final boolean DEBUG = AppContext.DEBUG;
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final int DIALOG_PROGRESS = -99;
    private EditText editUsername;
    private EditText editPassword;
    private Button mButtonSignin;
    private String username;
    private String password;

    public void log(String message) {
        Log.i(TAG, message);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(false);
        getActionBar().setTitle(R.string.app_name);
        setLayout();
    }

    @Override
    protected int getMenuResourceId() {
        return -1;
    }

    @Override
    protected void onMenuHomeClick() {
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

    protected void setLayout() {
        setContentView(R.layout.ui_login);
        setProgressBarIndeterminateVisibility(false);

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
                if (AppContext.DEBUG) {
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
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_PROGRESS:
                ProgressDialog dialog = new ProgressDialog(mContext);
                dialog.setMessage("正在登录中...");
                dialog.setIndeterminate(true);
                return dialog;
            default:
                return null;
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

    private void onLoginComplete() {
        UIController.showHome(mContext);
        finish();
    }

    static final class ResultInfo implements Serializable {
        public static final int CODE_ERROR = -1;
        public static final int CODE_SUCCESS = 0;
        public static final int CODE_FAILED = 1;
        public static final int CODE_CANCELED = 2;
        ;

        private static final long serialVersionUID = 4195237447592568873L;
        public final int code;
        public final String message;
        public final Object content;

        public ResultInfo(int code) {
            this(code, null, null);
        }

        public ResultInfo(int code, String message) {
            this(code, message, null);
        }

        public ResultInfo(int code, String message, Object content) {
            this.code = code;
            this.message = message;
            this.content = content;
        }

    }

    private class LoginTask extends AsyncTask<Void, Integer, ResultInfo> {

        static final int LOGIN_IO_ERROR = 0; // 网络错误
        static final int LOGIN_AUTH_FAILED = 1; // 验证失败
        static final int LOGIN_AUTH_SUCCESS = 2; // 首次验证成功
        static final int LOGIN_CANCELLED_BY_USER = 3;

        private boolean isCancelled = false;

        @Override
        protected ResultInfo doInBackground(Void... params) {
            try {

                if (DEBUG) {
                    Log.d(TAG, "LoginTask.doInBackground()");
                }
                final Api api = AppContext.getApi();
                OAuthToken token = api.getOAuthAccessToken(username, password);
                if (AppContext.DEBUG)
                    log("xauth token=" + token);

                if (token != null) {
                    if (isCancelled) {
                        return new ResultInfo(LOGIN_CANCELLED_BY_USER,
                                "user cancel login process.");
                    }

                    publishProgress(1);
                    AppContext.updateAccessToken(mContext, token);

                    final UserModel u = api.verifyCredentials();

                    log("verifyCredentials user=" + u);

                    if (u != null) {
                        AppContext.updateUserInfo(mContext, u);
                        AppContext
                                .updateLoginInfo(mContext, username, password);
                        if (AppContext.DEBUG) {
                            log("xauth successful! ");
                        }
                        DataController.clearDatabase(getApplication());
                        UmengHelper.onLoginEvent(mContext, u.getId());
                        return new ResultInfo(LOGIN_AUTH_SUCCESS);
                    } else {
                        if (AppContext.DEBUG) {
                            log("xauth failed.");
                        }
                        AppContext.clearAccountInfo(mContext);
                        UmengHelper.onLoginError(mContext, username, 0, "verifyCredentials user is null", "");
                        return new ResultInfo(LOGIN_AUTH_FAILED,
                                "XAuth successful, but verifyAccount failed. ");
                    }
                } else {
                    UmengHelper.onLoginError(mContext, username, 0, "xauth token is null", "");
                    return new ResultInfo(LOGIN_AUTH_FAILED,
                            "username or password is incorrect, XAuth failed.");
                }

            } catch (IOException e) {
                if (AppContext.DEBUG) {
                    e.printStackTrace();
                }
                AppContext.clearAccountInfo(mContext);
                UmengHelper.onLoginError(mContext, username, -1, e.getMessage(), e.toString());
                return new ResultInfo(LOGIN_IO_ERROR, e.toString());
            } catch (ApiException e) {
                UmengHelper.onLoginError(mContext, username, e.statusCode, e.errorMessage, e.getCause() + "");
                return new ResultInfo(LOGIN_IO_ERROR, e.toString());
            } catch (Exception e) {
                UmengHelper.onLoginError(mContext, username, 0, e.getMessage(), e.toString());
                return new ResultInfo(LOGIN_IO_ERROR, e.toString());
            }
        }

        @Override
        protected void onPreExecute() {
            showDialog(DIALOG_PROGRESS);
        }

        @Override
        protected void onPostExecute(ResultInfo result) {
            try {
                dismissDialog(DIALOG_PROGRESS);
            } catch (Exception ignored) {
            }
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

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (values.length > 0) {
                int value = values[0];
            }
        }

    }

}
