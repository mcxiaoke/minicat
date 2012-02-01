package com.fanfou.app;

import java.io.File;
import java.io.IOException;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Selection;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfou.app.adapter.AtTokenizer;
import com.fanfou.app.adapter.AutoCompleteCursorAdapter;
import com.fanfou.app.api.Draft;
import com.fanfou.app.db.Contents.BasicColumns;
import com.fanfou.app.db.Contents.DraftInfo;
import com.fanfou.app.db.Contents.UserInfo;
import com.fanfou.app.dialog.ConfirmDialog;
import com.fanfou.app.service.Constants;
import com.fanfou.app.service.PostStatusService;
import com.fanfou.app.ui.ActionBar;
import com.fanfou.app.ui.ActionBar.AbstractAction;
import com.fanfou.app.ui.ActionManager;
import com.fanfou.app.ui.TextChangeListener;
import com.fanfou.app.ui.widget.MyAutoCompleteTextView;
import com.fanfou.app.util.IOHelper;
import com.fanfou.app.util.ImageHelper;
import com.fanfou.app.util.OptionHelper;
import com.fanfou.app.util.StringHelper;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.20
 * @version 2.0 2011.10.24
 * @version 2.1 2011.10.26
 * @version 3.0 2011.10.27
 * @version 3.1 2011.10.28
 * @version 3.2 2011.10.29
 * @version 3.3 2011.11.02
 * @version 3.4 2011.11.07
 * @version 4.0 2011.11.08
 * @version 4.1 2011.11.15
 * @version 4.2 2011.11.18
 * @version 4.4 2011.11.21
 * @version 4.5 2011.12.05
 * @version 4.6 2011.12.13
 * @version 4.7 2011.12.26
 * @version 4.8 2012.02.01
 * 
 */
public class WritePage extends BaseActivity {

	private static final String tag = WritePage.class.getSimpleName();
	private static final int REQUEST_PHOTO_CAPTURE = 0;
	private static final int REQUEST_PHOTO_LIBRARY = 1;
	private static final int REQUEST_LOCATION_ADD = 2;
	private static final int REQUEST_USERNAME_ADD = 3;

	private void log(String message) {
		Log.d(tag, message);
	}

	private ActionBar mActionBar;
	private MyAutoCompleteTextView mAutoCompleteTextView;

	private View mPictureView;
	private ImageView iPicturePrieview;
	private ImageView iPictureRemove;
	private TextView tWordsCount;

	private ImageView iAtIcon;
	private ImageView iDraftIcon;
	private ImageView iLocationIcon;
	private ImageView iGalleryIcon;
	private ImageView iCameraIcon;

	private Uri photoUri;
	private File photo;
	private String content;
	private int wordsCount;

	private String mLocationString;

	private LocationManager mLocationManager;
	private String mLocationProvider;
	private LocationMonitor mLocationMonitor;

	private boolean enableLocation;

	private String inReplyToStatusId;
	private String text;
	private int type;
	private int size;

	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_REPLY = 1;
	public static final int TYPE_REPOST = 2;
	public static final int TYPE_GALLERY = 3;
	public static final int TYPE_CAMERA = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialize();
		setLayout();
		parseIntent();
	}

	private void initialize() {
		enableLocation = OptionHelper.readBoolean(mContext,
				R.string.option_location_enable, true);
		mLocationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		mLocationMonitor = new LocationMonitor();
		size = new Float(getResources().getDimension(
				R.dimen.photo_preview_width)).intValue();
		for (String provider : mLocationManager.getProviders(true)) {
			if (LocationManager.NETWORK_PROVIDER.equals(provider)
					|| LocationManager.GPS_PROVIDER.equals(provider)) {
				mLocationProvider = provider;
				break;
			}
		}

		if (mDisplayMetrics.heightPixels < 600) {
			getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_LOCATION_ADD:
				break;
			case REQUEST_PHOTO_LIBRARY:
				if (App.DEBUG) {
					log("onActivityResult requestCode=REQUEST_PHOTO_LIBRARY data="
							+ data);
				}
				if (data != null) {
					parsePhoto(data.getData());
				}
				break;
			case REQUEST_PHOTO_CAPTURE:
				if (App.DEBUG) {
					log("onActivityResult requestCode=REQUEST_PHOTO_CAPTURE");
				}
				doCameraShot();
				break;
			case REQUEST_USERNAME_ADD:
				if (App.DEBUG) {
					log("onActivityResult requestCode=REQUEST_USERNAME_ADD data="
							+ data);
				}
				if (data != null) {
					insertNames(data);
				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		parseIntent();
	}

	private void doCameraShot() {
		if (App.DEBUG) {
			log("doCameraShot() from camera uri=" + photoUri);
			log("doCameraShot() from camera filename="
					+ photo.getAbsolutePath());
			log("doCameraShot() file.size=" + photo.length());
		}
		showPreview();
	}

	private void showPreview() {
		mPictureView.setVisibility(View.VISIBLE);
		try {
			iPicturePrieview.setImageBitmap(ImageHelper.getRoundedCornerBitmap(
					ImageHelper.resampleImage(photo, size), 6));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void hidePreview() {
		mPictureView.setVisibility(View.GONE);
	}

	private void showCount(int count) {
		if (count > 140) {
			tWordsCount.setTextColor(getResources().getColorStateList(
					R.color.write_count_alert_text));
			tWordsCount.setText("字数超标：" + (count - 140));
		} else {

			tWordsCount.setTextColor(getResources().getColorStateList(
					R.color.write_count_text));
			tWordsCount.setText("剩余字数：" + (140 - count));
		}

	}

	private void parsePhoto(Uri uri) {
		if (uri != null) {

			if (App.DEBUG)
				log("from gallery uri=" + photoUri);

			String path;
			if (uri.getScheme().equals("content")) {
				path = IOHelper.getRealPathFromURI(this, uri);
			} else {
				path = uri.getPath();
			}
			photo = new File(path);
			if (photo.exists()) {
				photoUri = uri;
			}
			if (App.DEBUG)
				log("from gallery file=" + path);
			showPreview();
		}
	}

	private void parsePhoto(File file) {
		if (file != null && file.exists()) {
			photo = file;
			photoUri = Uri.fromFile(file);
			if (App.DEBUG)
				log("from file=" + file);
		}
	}

	private void parseIntent() {
		type = TYPE_NORMAL;
		Intent intent = getIntent();
		if (intent != null) {
			String action = intent.getAction();
			if (action == null) {
				type = intent.getIntExtra(Constants.EXTRA_TYPE, TYPE_NORMAL);
				text = intent.getStringExtra(Constants.EXTRA_TEXT);
				inReplyToStatusId = intent
						.getStringExtra(Constants.EXTRA_IN_REPLY_TO_ID);
				File file = (File) intent
						.getSerializableExtra(Constants.EXTRA_DATA);
				int draftId = intent.getIntExtra(Constants.EXTRA_ID, -1);
				parsePhoto(file);
				updateUI();
				deleteDraft(draftId);
			} else if (action.equals(Intent.ACTION_SEND)
					|| action.equals(Constants.ACTION_SEND)) {
				Bundle extras = intent.getExtras();
				if (extras != null) {
					text = extras.getString(Intent.EXTRA_TEXT);
					Uri uri = extras.getParcelable(Intent.EXTRA_STREAM);
					parsePhoto(uri);
					updateUI();
				}
			} else if (action.equals(Constants.ACTION_SEND_FROM_GALLERY)) {
				type = TYPE_GALLERY;
				startAddPicture();
			} else if (action.equals(Constants.ACTION_SEND_FROM_CAMERA)) {
				type = TYPE_CAMERA;
				startCameraShot();
			}

			if (App.DEBUG) {
				log("intent type=" + type);
				log("intent text=" + text);
			}
		}
	}

	private void updateUI() {
		if (!StringHelper.isEmpty(text)) {
			mAutoCompleteTextView.setText(text);
			if (type != TYPE_REPOST) {
				Selection.setSelection(mAutoCompleteTextView.getText(),
						mAutoCompleteTextView.getText().length());
			}
		}
		if (photoUri != null) {
			showPreview();
		}
	}

	private void deleteDraft(int id) {
		if (id >= 0) {
			getContentResolver().delete(
					ContentUris.withAppendedId(DraftInfo.CONTENT_URI, id),
					null, null);
		}
	}

	/**
	 * 初始化和设置ActionBar
	 */
	private void setActionBar() {
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setTitle("写消息");
		mActionBar.setRightAction(new SendAction());
		setActionBarSwipe(mActionBar);

	}

	private class SendAction extends AbstractAction {

		public SendAction() {
			super(R.drawable.ic_send);
		}

		@Override
		public void performAction(View view) {
			doSend();
		}

	}

	private void setAutoComplete() {
		mAutoCompleteTextView = (MyAutoCompleteTextView) findViewById(R.id.write_text);
		mAutoCompleteTextView.addTextChangedListener(new TextChangeListener() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				content = s.toString().trim();
				wordsCount = content.length();
				showCount(wordsCount);
			}
		});

		mAutoCompleteTextView.setTokenizer(new AtTokenizer());
		mAutoCompleteTextView.setBackgroundResource(R.drawable.input_bg);
		final String[] projection = new String[] { BaseColumns._ID,
				BasicColumns.ID, UserInfo.SCREEN_NAME, BasicColumns.TYPE,
				BasicColumns.OWNER_ID };
		String where = BasicColumns.OWNER_ID + " = '" + App.getUserId()
				+ "' AND " + BasicColumns.TYPE + " = '"
				+ Constants.TYPE_USERS_FRIENDS + "'";
		// Cursor cursor = managedQuery(UserInfo.CONTENT_URI, projection, where,
		// null,
		// null);
		Cursor cursor = getContentResolver().query(UserInfo.CONTENT_URI,
				projection, where, null, null);
		mAutoCompleteTextView.setAdapter(new AutoCompleteCursorAdapter(this,
				cursor));
	}

	private void setLayout() {

		setContentView(R.layout.write);

		setActionBar();
		setAutoComplete();

		mPictureView = findViewById(R.id.write_picture);
		iPicturePrieview = (ImageView) findViewById(R.id.write_picture_prieview);
		iPictureRemove = (ImageView) findViewById(R.id.write_picture_remove);

		tWordsCount = (TextView) findViewById(R.id.write_extra_words);

		iAtIcon = (ImageView) findViewById(R.id.write_action_at);
		iDraftIcon = (ImageView) findViewById(R.id.write_action_draft);
		iLocationIcon = (ImageView) findViewById(R.id.write_action_location);
		iGalleryIcon = (ImageView) findViewById(R.id.write_action_gallery);
		iCameraIcon = (ImageView) findViewById(R.id.write_action_camera);

		iAtIcon.setOnClickListener(this);
		iDraftIcon.setOnClickListener(this);
		iLocationIcon.setOnClickListener(this);
		iGalleryIcon.setOnClickListener(this);
		iCameraIcon.setOnClickListener(this);

		iPictureRemove.setOnClickListener(this);

		iLocationIcon.setImageResource(enableLocation ? R.drawable.ic_bar_geoon
				: R.drawable.ic_bar_geooff);

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (enableLocation && mLocationProvider != null) {
			mLocationManager.requestLocationUpdates(mLocationProvider, 0, 0,
					mLocationMonitor);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (enableLocation) {
			mLocationManager.removeUpdates(mLocationMonitor);
		}
	}

	@Override
	public void onBackPressed() {
		if (App.DEBUG) {
			log("onBackPressed content=" + content);
		}
		if (StringHelper.isEmpty(content)) {
			super.onBackPressed();
		} else {
			checkSave();
		}

	}

	@Override
	public boolean onSwipeLeft() {
		if (StringHelper.isEmpty(content)) {
			finish();
		} else {
			checkSave();
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.write_action_at:
			startAddUsername();
			break;
		case R.id.write_action_draft:
			ActionManager.doShowDrafts(this);
			break;
		case R.id.write_action_location:
			switchLocation();
			break;
		case R.id.write_action_gallery:
			startAddPicture();
			break;
		case R.id.write_action_camera:
			startCameraShot();
			break;
		case R.id.write_picture_remove:
			removePicture();
			break;
		default:
			break;
		}

	}

	private void checkSave() {

		final ConfirmDialog dialog = new ConfirmDialog(this, "保存草稿",
				"要保存未发送内容为草稿吗？");
		dialog.setButton1Text("保存");
		dialog.setButton2Text("放弃");
		dialog.setClickListener(new ConfirmDialog.ClickHandler() {

			@Override
			public void onButton1Click() {
				doSaveDrafts();
				finish();
			}

			@Override
			public void onButton2Click() {
				finish();
			}
		});
		dialog.show();
	}

	private void doSaveDrafts() {
		Draft d = new Draft();
		d.type = type;
		d.text = content;
		d.filePath = photo == null ? "" : photo.toString();
		d.replyTo = inReplyToStatusId;
		getContentResolver().insert(DraftInfo.CONTENT_URI, d.toContentValues());
	}

	private void removePicture() {
		hidePreview();
		photo = null;
		photoUri = null;
	}

	private void startCameraShot() {
		photo = IOHelper.getPhotoFilePath(this);
		photoUri = Uri.fromFile(photo);
		if (App.DEBUG) {
			log("startCameraShot() photoPath=" + photo.getAbsolutePath());
			log("startCameraShot() photoUri=" + photoUri);
		}
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
		startActivityForResult(Intent.createChooser(intent, "拍摄照片"),
				REQUEST_PHOTO_CAPTURE);

	}

	private void startAddPicture() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		// startActivityForResult(intent, REQUEST_PHOTO_LIBRARY);
		startActivityForResult(Intent.createChooser(intent, "选择照片"),
				REQUEST_PHOTO_LIBRARY);
	}

	private void switchLocation() {
		enableLocation = !enableLocation;
		OptionHelper.saveBoolean(mContext, R.string.option_location_enable,
				enableLocation);
		if (App.DEBUG)
			log("location enable status=" + enableLocation);
		if (enableLocation) {
			iLocationIcon.setImageResource(R.drawable.ic_bar_geoon);
			if (mLocationProvider != null) {
				mLocationManager.requestLocationUpdates(mLocationProvider, 0,
						0, mLocationMonitor);
			}
		} else {
			iLocationIcon.setImageResource(R.drawable.ic_bar_geooff);
			mLocationManager.removeUpdates(mLocationMonitor);
		}
	}

	private void startAddUsername() {
		Intent intent = new Intent(this, UserChoosePage.class);
		startActivityForResult(intent, REQUEST_USERNAME_ADD);
	}

	private void insertNames(Intent intent) {
		String names = intent.getStringExtra(Constants.EXTRA_TEXT);
		if (App.DEBUG) {
			log("doAddUserNames: " + names);
		}
		if (!StringHelper.isEmpty(names)) {
			Editable editable = mAutoCompleteTextView.getEditableText();
			editable.append(names);
			Selection.setSelection(editable, editable.length());
		}

	}

	private void doSend() {
		if (wordsCount < 1) {
			Utils.notify(this, "消息内容不能为空");
			return;
		}
		Utils.hideKeyboard(this, mAutoCompleteTextView);
		startSendService();
		finish();
	}

	private void startSendService() {
		Intent i = new Intent(mContext, PostStatusService.class);
		i.putExtra(Constants.EXTRA_TYPE, type);
		i.putExtra(Constants.EXTRA_TEXT, content);
		i.putExtra(Constants.EXTRA_DATA, photo);
		i.putExtra(Constants.EXTRA_LOCATION, mLocationString);
		i.putExtra(Constants.EXTRA_IN_REPLY_TO_ID, inReplyToStatusId);
		if (App.DEBUG) {
			log("intent=" + i);
		}
		startService(i);
	}

	private void updateLocationString(Location loc) {
		if (loc != null) {
			mLocationString = String.format("%1$.5f,%2$.5f", loc.getLatitude(),
					loc.getLongitude());
			if (App.DEBUG)
				log("Location Info: " + mLocationString);
		}
	}

	private class LocationMonitor implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {
				updateLocationString(location);
			}
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

	}

}
