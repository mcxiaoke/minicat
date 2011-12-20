package com.fanfou.app;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfou.app.api.Api;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.api.ResultInfo;
import com.fanfou.app.api.User;
import com.fanfou.app.cache.IImageLoader;
import com.fanfou.app.db.FanFouProvider;
import com.fanfou.app.service.Constants;
import com.fanfou.app.service.FanFouService;
import com.fanfou.app.ui.ActionBar;
import com.fanfou.app.ui.TextChangeListener;
import com.fanfou.app.util.IOHelper;
import com.fanfou.app.util.ImageHelper;
import com.fanfou.app.util.StringHelper;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.11.07
 * @version 1.5 2011.11.08
 * @version 1.6 2011.11.09
 * @version 1.7 2011.11.18
 * 
 */
public class EditProfilePage extends BaseActivity {

	private static final String TAG = EditProfilePage.class.getSimpleName();

	private void log(String message) {
		Log.d(TAG, message);
	}

	private ActionBar mBar;

	private ImageView mHeadView;
	private ImageView mHeadEdit;

	private Button mButtonOK;
	private Button mButtonCancel;

	private EditText mNameEdit;
	private EditText mDescriptionEdit;
	private EditText mUrlEdit;
	private EditText mLocationEdit;

	private TextView mNameLabel;
	private TextView mDescriptionLabel;
	private TextView mUrlLabel;
	private TextView mLocationLabel;

	private String mName;
	private String mDescription;
	private String mUrl;
	private String mLocation;

	private User user;

	private IImageLoader mLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parseIntent();
		mLoader = App.getImageLoader();
		setLayout();
		updateUI();
	}

	private void parseIntent() {
		user = (User) getIntent().getParcelableExtra(Constants.EXTRA_DATA);
	}

	private void setLayout() {
		setContentView(R.layout.edit_profile);

		mBar = (ActionBar) findViewById(R.id.actionbar);

		mButtonOK = (Button) findViewById(R.id.button_ok);
		mButtonOK.setOnClickListener(this);

		mButtonCancel = (Button) findViewById(R.id.button_cancel);
		mButtonCancel.setOnClickListener(this);

		mHeadView = (ImageView) findViewById(R.id.profile_image);
		mHeadView.setOnClickListener(this);

		mHeadEdit = (ImageView) findViewById(R.id.profile_image_edit);
		mHeadEdit.setOnClickListener(this);

		mNameEdit = (EditText) findViewById(R.id.profile_name_edit);
		mDescriptionEdit = (EditText) findViewById(R.id.profile_description_edit);
		mUrlEdit = (EditText) findViewById(R.id.profile_url_edit);
		mLocationEdit = (EditText) findViewById(R.id.profile_location_edit);

		mNameLabel = (TextView) findViewById(R.id.profile_name);
		mDescriptionLabel = (TextView) findViewById(R.id.profile_description);
		mUrlLabel = (TextView) findViewById(R.id.profile_url);
		mLocationLabel = (TextView) findViewById(R.id.profile_location);

		setFakedBold(mNameLabel);
		setFakedBold(mDescriptionLabel);
		setFakedBold(mUrlLabel);
		setFakedBold(mLocationLabel);

		setTextChangeListener();

	}

	private void setFakedBold(TextView tv) {
		TextPaint tp = tv.getPaint();
		tp.setFakeBoldText(true);
	}

	private void setTextChangeListener() {
		mNameEdit.addTextChangedListener(new TextChangeListener() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mName = s.toString();
			}
		});

		mDescriptionEdit.addTextChangedListener(new TextChangeListener() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mDescription = s.toString();
			}
		});

		mUrlEdit.addTextChangedListener(new TextChangeListener() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mUrl = s.toString();
			}
		});

		mLocationEdit.addTextChangedListener(new TextChangeListener() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mLocation = s.toString();
			}
		});

	}

	private void updateUI() {
		mBar.setTitle("编辑个人资料");
		mNameEdit.setText(user.screenName);
		mDescriptionEdit.setText(user.description);
		mUrlEdit.setText(user.url);
		mLocationEdit.setText(user.location);

		updateProfileImagePreview();

	}

	private void updateProfileImagePreview() {
		if (App.DEBUG) {
			log("updateProfileImagePreview() url=" + user.profileImageUrl);
		}
		mHeadView.setImageResource(R.drawable.default_head);
		mHeadView.invalidate();
		mHeadView.setTag(user.profileImageUrl);
		mLoader.displayImage(user.profileImageUrl, mHeadView,
				R.drawable.default_head);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE_SELECT_IMAGE) {
				File file = getPhotoFilePath(data.getData());
				doUpdateProfileImage(file);
			}
		}
	}

	private static final int REQUEST_CODE_SELECT_IMAGE = 0;

	private void startEditProfileImage() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(Intent.createChooser(intent, "选择头像"),
				REQUEST_CODE_SELECT_IMAGE);
	}

	private void doUpdateProfileImage(File file) {
		new UpdateProfileImageTask(this).execute(file);
	}

	private File getPhotoFilePath(Uri uri) {
		if (uri != null) {
			if (App.DEBUG)
				log("from gallery uri=" + uri);
			String path;
			if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
				path = IOHelper.getRealPathFromURI(this, uri);
			} else {
				path = uri.getPath();
			}
			File file = new File(path);
			if (App.DEBUG)
				log("from gallery file=" + path);
			return file;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private void doUpdateProfile() {
		HashMap<String, String> map = new HashMap<String, String>();
		if (!StringHelper.isEmpty(mDescription)
				&& !mDescription.equals(user.description)) {
			map.put("description", mDescription);
		}
		if (!StringHelper.isEmpty(mName) && !mName.equals(user.screenName)) {
			map.put("name", mName);
		}
		if (!StringHelper.isEmpty(mUrl) && !mUrl.equals(user.url)) {
			map.put("url", mUrl);
		}
		if (!StringHelper.isEmpty(mLocation)
				&& !mLocation.equals(user.location)) {
			map.put("location", mLocation);
		}
		if (map.size() > 0) {
			new UpdateProfileTask(this).execute(map);
		} else {
			// Utils.notify(this, "无任何修改");
			finish();
		}

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.profile_image:
		case R.id.profile_image_edit:
			startEditProfileImage();
			break;
		case R.id.button_ok:
			doUpdateProfile();
			break;
		case R.id.button_cancel:
			finish();
			break;
		default:
			break;
		}
	}

	/**
	 * 更新个人资料后台任务
	 */
	private static class UpdateProfileTask extends
			AsyncTask<HashMap<String, String>, Integer, ResultInfo> {

		private Activity mContext;
		private ProgressDialog pd = null;
		private Api api;
		private boolean isCancelled;

		public UpdateProfileTask(Activity context) {
			super();
			this.mContext = context;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			api = App.getApi();
			pd = new ProgressDialog(mContext);
			pd.setMessage("正在更新个人资料...");
			pd.setIndeterminate(true);
			pd.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					isCancelled = true;
					cancel(true);
				}
			});
			pd.show();
		}

		private void onSuccess(ResultInfo result) {
			User user = (User) result.content;
			if (user != null) {
				Intent intent = new Intent();
				intent.putExtra(Constants.EXTRA_DATA, user);
				mContext.setResult(RESULT_OK, intent);
				mContext.finish();
			}
		}

		@Override
		protected void onPostExecute(ResultInfo result) {
			super.onPostExecute(result);
			pd.dismiss();
			int code = result.code;
			switch (code) {
			case ResultInfo.CODE_SUCCESS:
				Utils.notify(mContext, result.message);
				onSuccess(result);
				break;
			case ResultInfo.CODE_FAILED:
				Utils.notify(mContext, result.message);
				break;
			case ResultInfo.CODE_ERROR:
				Utils.notify(mContext, result.message);
				break;
			case ResultInfo.CODE_CANCELED:
				break;
			default:
				break;
			}
		}

		@Override
		protected ResultInfo doInBackground(HashMap<String, String>... params) {
			if (params == null || params.length == 0) {
				return new ResultInfo(ResultInfo.CODE_ERROR, "参数不能为空");
			}
			Map<String, String> map = params[0];
			String description = map.get("description");
			String name = map.get("name");
			String location = map.get("location");
			String url = map.get("url");
			try {
				User user = api.updateProfile(description, name, location, url,
						Constants.MODE);
				if (isCancelled) {
					return new ResultInfo(ResultInfo.CODE_CANCELED, "用户取消");
				}
				if (user == null || user.isNull()) {
					return new ResultInfo(ResultInfo.CODE_FAILED, "更新个人资料失败");
				} else {
					FanFouProvider.updateUserInfo(mContext, user);

					return new ResultInfo(ResultInfo.CODE_SUCCESS, "更新个人资料成功",
							user);
				}
			} catch (ApiException e) {
				if (App.DEBUG) {
					e.printStackTrace();
				}
				return new ResultInfo(ResultInfo.CODE_ERROR, e.getMessage());

			}
		}

	}

	/**
	 * 更新个人头像后台任务
	 */
	private static class UpdateProfileImageTask extends
			AsyncTask<File, Integer, ResultInfo> {

		private EditProfilePage mEditProfilePage;
		private ProgressDialog pd = null;
		private Api api;
		private boolean isCancelled;

		public UpdateProfileImageTask(EditProfilePage context) {
			super();
			this.mEditProfilePage = context;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			api = App.getApi();
			pd = new ProgressDialog(mEditProfilePage);
			pd.setMessage("正在更新头像...");
			pd.setIndeterminate(true);
			pd.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					isCancelled = true;
					cancel(true);
				}
			});
			pd.show();
		}

		private void onSuccess(ResultInfo result) {
			User user = (User) result.content;
			if (user != null) {
				FanFouProvider.updateUserInfo(mEditProfilePage, user);
				Intent intent = new Intent();
				intent.putExtra(Constants.EXTRA_DATA, user);
				mEditProfilePage.setResult(RESULT_OK, intent);
				mEditProfilePage.user = user;
				mEditProfilePage.updateProfileImagePreview();
				// mEditProfilePage.finish();
			}
		}

		@Override
		protected void onPostExecute(ResultInfo result) {
			super.onPostExecute(result);
			pd.dismiss();
			int code = result.code;
			switch (code) {
			case ResultInfo.CODE_SUCCESS:
				Utils.notify(mEditProfilePage, result.message);
				onSuccess(result);
				break;
			case ResultInfo.CODE_FAILED:
				Utils.notify(mEditProfilePage, result.message);
				break;
			case ResultInfo.CODE_ERROR:
				Utils.notify(mEditProfilePage, result.message);
				break;
			case ResultInfo.CODE_CANCELED:
				break;
			default:
				break;
			}
		}

		@Override
		protected ResultInfo doInBackground(File... params) {
			if (params == null || params.length == 0) {
				return new ResultInfo(ResultInfo.CODE_ERROR, "参数不能为空");
			}
			File srcFile = params[0];
			try {
				File file = ImageHelper.prepareProfileImage(mEditProfilePage,
						srcFile);
				User user = api.updateProfileImage(file, Constants.MODE);
				if (isCancelled) {
					return new ResultInfo(ResultInfo.CODE_CANCELED, "用户取消");
				}
				if (user == null || user.isNull()) {
					return new ResultInfo(ResultInfo.CODE_FAILED, "更新个人头像失败");
				} else {

					FanFouProvider.updateUserInfo(mEditProfilePage, user);
					FanFouProvider.updateStatusProfileImageUrl(
							mEditProfilePage, user);

					return new ResultInfo(ResultInfo.CODE_SUCCESS, "更新个人头像成功",
							user);
				}
			} catch (ApiException e) {
				if (App.DEBUG) {
					e.printStackTrace();
				}
				return new ResultInfo(ResultInfo.CODE_ERROR, e.getMessage());
			}
		}

	}

}
