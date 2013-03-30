package org.mcxiaoke.fancooker;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.mcxiaoke.fancooker.api.Api;
import org.mcxiaoke.fancooker.api.ApiException;
import org.mcxiaoke.fancooker.api.ResultInfo;
import org.mcxiaoke.fancooker.cache.ImageLoader;
import org.mcxiaoke.fancooker.controller.DataController;
import org.mcxiaoke.fancooker.dao.model.UserModel;
import org.mcxiaoke.fancooker.ui.widget.TextChangeListener;
import org.mcxiaoke.fancooker.util.IOHelper;
import org.mcxiaoke.fancooker.util.ImageHelper;
import org.mcxiaoke.fancooker.util.StringHelper;
import org.mcxiaoke.fancooker.util.Utils;

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

/**
 * @author mcxiaoke
 * @version 1.0 2011.11.07
 * @version 1.5 2011.11.08
 * @version 1.6 2011.11.09
 * @version 1.7 2011.11.18
 * @version 1.8 2012.02.01
 * @version 2.0 2012.02.21
 * 
 */
public class UIEditProfile extends UIBaseSupport {

	private static final String TAG = UIEditProfile.class.getSimpleName();

	private void log(String message) {
		Log.d(TAG, message);
	}

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

	private UserModel user;

	private ImageLoader mLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private void parseIntent() {
		user = (UserModel) getIntent().getParcelableExtra("data");
	}

	@Override
	protected void initialize() {
		parseIntent();
		mLoader = AppContext.getImageLoader();
	}

	@Override
	protected void setLayout() {
		setContentView(R.layout.edit_profile);

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

		updateUI();

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
		mNameEdit.setText(user.getScreenName());
		mDescriptionEdit.setText(user.getDescription());
		mUrlEdit.setText(user.getUrl());
		mLocationEdit.setText(user.getLocation());

		updateProfileImagePreview();

	}

	private void updateProfileImagePreview() {
		if (AppContext.DEBUG) {
			log("updateProfileImagePreview() url=" + user.getProfileImageUrl());
		}
		mHeadView.setImageResource(R.drawable.ic_head);
		mHeadView.invalidate();
		mHeadView.setTag(user.getProfileImageUrl());
		mLoader.displayImage(user.getProfileImageUrl(), mHeadView,
				R.drawable.ic_head);

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
			if (AppContext.DEBUG)
				log("from gallery uri=" + uri);
			String path;
			if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
				path = IOHelper.getRealPathFromURI(this, uri);
			} else {
				path = uri.getPath();
			}
			File file = new File(path);
			if (AppContext.DEBUG)
				log("from gallery file=" + path);
			return file;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private void doUpdateProfile() {
		HashMap<String, String> map = new HashMap<String, String>();
		if (!StringHelper.isEmpty(mDescription)
				&& !mDescription.equals(user.getDescription())) {
			map.put("description", mDescription);
		}
		if (!StringHelper.isEmpty(mName) && !mName.equals(user.getScreenName())) {
			map.put("name", mName);
		}
		if (!StringHelper.isEmpty(mUrl) && !mUrl.equals(user.getUrl())) {
			map.put("url", mUrl);
		}
		if (!StringHelper.isEmpty(mLocation)
				&& !mLocation.equals(user.getLocation())) {
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
			api = AppContext.getApi();
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
			UserModel user = (UserModel) result.content;
			if (user != null) {
				Intent intent = new Intent();
				intent.putExtra("data", user);
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
				UserModel user = api.updateProfile(url, location, description,
						name);
				if (isCancelled) {
					return new ResultInfo(ResultInfo.CODE_CANCELED, "用户取消");
				}
				if (user == null) {
					return new ResultInfo(ResultInfo.CODE_FAILED, "更新个人资料失败");
				} else {
					// DataProvider.updateUserInfo(mContext, user);
					// TODO

					DataController.update(mContext, user, user.values());

					return new ResultInfo(ResultInfo.CODE_SUCCESS, "更新个人资料成功",
							user);
				}
			} catch (ApiException e) {
				if (AppContext.DEBUG) {
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

		private UIEditProfile mEditProfilePage;
		private ProgressDialog pd = null;
		private Api api;
		private boolean isCancelled;

		public UpdateProfileImageTask(UIEditProfile context) {
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
			api = AppContext.getApi();
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
			UserModel user = (UserModel) result.content;
			if (user != null) {
				// FanFouProvider.updateUserInfo(mEditProfilePage, user);
				// TODO
				Intent intent = new Intent();
				intent.putExtra("data", user);
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
				UserModel user = api.updateProfileImage(file);
				if (isCancelled) {
					return new ResultInfo(ResultInfo.CODE_CANCELED, "用户取消");
				}
				if (user == null) {
					return new ResultInfo(ResultInfo.CODE_FAILED, "更新个人头像失败");
				} else {
					// TODO
					// FanFouProvider.updateUserInfo(mEditProfilePage, user);
					// FanFouProvider.updateStatusProfileImageUrl(
					// mEditProfilePage, user);

					return new ResultInfo(ResultInfo.CODE_SUCCESS, "更新个人头像成功",
							user);
				}
			} catch (ApiException e) {
				if (AppContext.DEBUG) {
					e.printStackTrace();
				}
				return new ResultInfo(ResultInfo.CODE_ERROR, e.getMessage());
			}
		}

	}

}
