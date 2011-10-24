package com.fanfou.app;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Selection;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.fanfou.app.adapter.AtTokenizer;
import com.fanfou.app.adapter.AutoCompleteCursorAdapter;
import com.fanfou.app.api.Status;
import com.fanfou.app.api.User;
import com.fanfou.app.config.Commons;
import com.fanfou.app.db.Contents.BasicColumns;
import com.fanfou.app.db.Contents.UserInfo;
import com.fanfou.app.service.PostStatusService;
import com.fanfou.app.ui.ActionBar;
import com.fanfou.app.ui.ActionBar.AbstractAction;
import com.fanfou.app.ui.TextChangeListener;
import com.fanfou.app.ui.widget.MyAutoCompleteTextView;
import com.fanfou.app.util.IOHelper;
import com.fanfou.app.util.ImageHelper;
import com.fanfou.app.util.OptionHelper;
import com.fanfou.app.util.StatusHelper;
import com.fanfou.app.util.StringHelper;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.20
 * @version 2.0 2011.10.24
 * 
 */
public class WritePage extends BaseActivity {

	private static final String tag = WritePage.class.getSimpleName();
	private static final int REQUEST_PHOTO_CAPTURE = 0;
	private static final int REQUEST_PHOTO_LIBRARY = 1;
	private static final int REQUEST_LOCATION_ADD = 2;
	private static final int REQUEST_USERNAME_ADD = 3;
	private static final int REQUEST_ADD_USER=4;

	private void log(String message) {
		Log.i(tag, message);
	}

	private ActionBar mActionBar;
	private MyAutoCompleteTextView mAutoCompleteTextView;
	CursorAdapter mAdapter;
	// private ViewGroup vExtra;
	private ImageView iPicturePrieview;
	private ImageView iPictureRemove;
	private TextView tWordsCount;

	private ViewGroup vActions;
	private ImageView iAtIcon;
	private ImageView iLocationIcon;
	private ImageView iGalleryIcon;
	private ImageView iCameraIcon;

	// private ViewGroup vButtons;
	// private ImageView bCancel;
	// private ImageView bOK;

	private Uri photoUri;
	private File photo;
	private String content;
	private int wordsCount;

	private String mLocationString;

	private LocationManager mLocationManager;
	private LocationMonitor mLocationMonitor;

	private boolean enableLocation;

	private Status status;
	private String text;
	private int type;

	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_REPLY = 1;
	public static final int TYPE_REPLY_ALL = 2;
	public static final int TYPE_REPOST = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parseIntent();
		initialize();
		setContentView(R.layout.write);
		setActionBar();
		setLayout();
		setValues();
	}

	private void initialize() {
		enableLocation = OptionHelper.readBoolean(this,
				R.string.option_location_enable, false);
		mLocationMonitor = new LocationMonitor();
		mLocationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		// TODO need test and modify
		// 修复小屏幕软键盘挡住输入框的问题
		if (App.me.density < 1.5) {
			getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
		}
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_USERNAME_ADD:
				break;
			case REQUEST_LOCATION_ADD:
				break;
			case REQUEST_PHOTO_LIBRARY:
				doAddPicture(data.getData());
				showPreview();
				break;
			case REQUEST_PHOTO_CAPTURE:
				doCameraShot(data);
				break;
			case REQUEST_ADD_USER:
				doAddUserNames(data);
				break;
			default:
				break;
			}
		}
	}

	private void doCameraShot(Intent data) {
		try {
			if (App.DEBUG) {
				log("from camera uri=" + photoUri);
				log("from camera filename=" + photo.getCanonicalPath());
				log("file.size=" + photo.length());
			}
			showPreview();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void doAddPicture(Uri uri) {
		parseUri(uri);
	}

	private void showPreview() {
		final int size = new Float(getResources().getDimension(
				R.dimen.photo_preview_width)).intValue();
		iPicturePrieview.setImageBitmap(ImageHelper.getRoundedCornerBitmap(
				ImageHelper.getThumb(this, photoUri, size, size), 6));
		iPicturePrieview.setVisibility(View.VISIBLE);
		iPictureRemove.setVisibility(View.VISIBLE);
	}

	private void hidePreview() {
		iPicturePrieview.setVisibility(View.INVISIBLE);
		iPictureRemove.setVisibility(View.INVISIBLE);
	}

	private void showCount(int count) {
		if (count >= 140) {
			tWordsCount.setTextColor(getResources().getColorStateList(
					R.color.write_count_alert_text));
		} else {
			tWordsCount.setTextColor(getResources().getColorStateList(
					R.color.write_count_text));
		}
		tWordsCount.setText("剩余字数：" + (140 - count));
	}

	private void parseUri(Uri uri) {
		if (uri != null) {
			photoUri = uri;
			if (App.DEBUG)
				log("from gallery uri=" + photoUri);

			String path;
			if (photoUri.getScheme().equals("content")) {
				path = IOHelper.getRealPathFromURI(this, photoUri);
			} else {
				path = photoUri.getPath();
			}
			photo = new File(path);
			if (App.DEBUG)
				log("from gallery file=" + path);
		}
	}

	private void parseIntent() {
		Intent intent = getIntent();
		if (intent != null) {
			String action = intent.getAction();
			if (action == null) {
				type = intent.getIntExtra(Commons.EXTRA_TYPE, TYPE_NORMAL);
				text = intent.getStringExtra(Commons.EXTRA_TEXT);
				status = (Status) intent
						.getSerializableExtra(Commons.EXTRA_STATUS);
				photo = (File) intent.getSerializableExtra(Commons.EXTRA_FILE);
				if (photo != null) {
					photoUri = Uri.parse(photo.getAbsolutePath());
				}
			} else if (action.equals(Intent.ACTION_SEND)) {
				type = TYPE_NORMAL;
				Bundle extras = intent.getExtras();
				if (extras.containsKey(Intent.EXTRA_STREAM)) {
					Uri uri = extras.getParcelable(Intent.EXTRA_STREAM);
					if (uri != null) {
						doAddPicture(uri);
						if (App.DEBUG)
							log("parseIntent ACTION_SEND EXTRA_STREAM uri="
									+ uri);
					}
				} else if (extras.containsKey(Intent.EXTRA_TEXT)) {
					type = TYPE_NORMAL;
					text = extras.getString(Intent.EXTRA_TEXT);
				}
			}
			if (App.DEBUG) {
				log("intent type=" + type);
				log("intent text=" + text);
				log("intent status=" + status);
			}
		}
	}

	/**
	 * 初始化和设置ActionBar
	 */
	private void setActionBar() {
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setTitle("写消息");
		mActionBar.setRightAction(new SendAction());
		mActionBar.setLeftAction(new ActionBar.BackAction(mContext));

	}

	private class SendAction extends AbstractAction {

		public SendAction() {
			super(R.drawable.i_send);
		}

		@Override
		public void performAction(View view) {
			send();
		}

	}

	private void setAutoComplete() {
		mAutoCompleteTextView = (MyAutoCompleteTextView) findViewById(R.id.write_text);
		mAutoCompleteTextView.addTextChangedListener(textMonitor);
		mAutoCompleteTextView.setTokenizer(new AtTokenizer());
		mAutoCompleteTextView.setDropDownBackgroundResource(R.drawable.bg);
		mAutoCompleteTextView.setDropDownAnchor(R.id.write_text);
		String[] projection = new String[] { UserInfo._ID, UserInfo.ID,
				UserInfo.SCREEN_NAME };
		String where = BasicColumns.TYPE + " = '" + User.TYPE_FRIENDS + "'";
		Cursor c = managedQuery(UserInfo.CONTENT_URI, projection, where, null,
				null);
		mAdapter = new AutoCompleteCursorAdapter(this, c);
		mAutoCompleteTextView.setAdapter(mAdapter);
	}

	private void setLayout() {
		setAutoComplete();

		// vExtra = (ViewGroup) findViewById(R.id.write_extras);
		iPicturePrieview = (ImageView) findViewById(R.id.write_extra_picture_prieview);
		iPictureRemove = (ImageView) findViewById(R.id.write_extra_picture_remove);

		tWordsCount = (TextView) findViewById(R.id.write_extra_words);

		vActions = (ViewGroup) findViewById(R.id.write_actions);
		iAtIcon = (ImageView) findViewById(R.id.write_action_at);
		iLocationIcon = (ImageView) findViewById(R.id.write_action_location);
		iGalleryIcon = (ImageView) findViewById(R.id.write_action_gallery);
		iCameraIcon = (ImageView) findViewById(R.id.write_action_camera);

		// vButtons = (ViewGroup) findViewById(R.id.write_buttons);
		// bCancel = (ImageView) findViewById(R.id.write_button_cancel);
		// bOK = (ImageView) findViewById(R.id.write_button_ok);
		// bCancel.setOnClickListener(this);
		// bOK.setOnClickListener(this);
		iAtIcon.setOnClickListener(this);
		iLocationIcon.setOnClickListener(this);
		iGalleryIcon.setOnClickListener(this);
		iCameraIcon.setOnClickListener(this);

		iPictureRemove.setOnClickListener(this);

		iLocationIcon
				.setImageResource(enableLocation ? R.drawable.i_bar_location_on
						: R.drawable.i_bar_location_off);

	}

	private void setValues() {
		if (type == TYPE_NORMAL) {
			mAutoCompleteTextView.setText(text);
			Selection.setSelection(mAutoCompleteTextView.getText(),
					mAutoCompleteTextView.getText().length());
		}
		if (status != null) {
			if (type == TYPE_REPLY) {
				mAutoCompleteTextView
						.setText("@" + status.userScreenName + " ");
				Selection.setSelection(mAutoCompleteTextView.getText(),
						mAutoCompleteTextView.getText().length());
			} else if (type == TYPE_REPOST) {
				mAutoCompleteTextView.setText(" 转@" + status.userScreenName
						+ " " + status.simpleText+ " ");
			}
		}

		if (photoUri != null) {
			showPreview();
		}
	}

	@Override
	protected void onResume() {
		if (enableLocation) {
			mLocationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0, mLocationMonitor);
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		if (enableLocation) {
			mLocationManager.removeUpdates(mLocationMonitor);
		}
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		// case R.id.write_button_cancel:
		// finish();
		// break;
		// case R.id.write_button_ok:
		// update();
		// break;
		case R.id.write_action_at:
			startAddUsername();
			break;
		case R.id.write_action_location:
			switchGeoStatus();
			break;
		case R.id.write_action_gallery:
			startAddPicture();
			break;
		case R.id.write_action_camera:
			startCameraShot();
			break;
		case R.id.write_extra_picture_remove:
			removePicture();
			break;
		default:
			break;
		}

	}

	private void removePicture() {
		hidePreview();
		photo = null;
		photoUri = null;

	}

	private void startCameraShot() {
		photo = IOHelper.getPhotoFilePath(this);
		photoUri = Uri.fromFile(photo);
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

	private void switchGeoStatus() {
		enableLocation = !enableLocation;
		OptionHelper.saveBoolean(this, R.string.option_location_enable,
				enableLocation);
		if (App.DEBUG)
			log("location enable status=" + enableLocation);
		if (enableLocation) {

			iLocationIcon.setImageResource(R.drawable.i_bar_location_on);
			mLocationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0, mLocationMonitor);
		} else {
			iLocationIcon.setImageResource(R.drawable.i_bar_location_off);
			mLocationManager.removeUpdates(mLocationMonitor);
		}
	}

	private void startAddUsername() {
		Intent intent=new Intent(this, UserChoosePage.class);
		startActivityForResult(intent, REQUEST_ADD_USER);
//		if (StringHelper.isEmpty(content)) {
//			mAutoCompleteTextView.setText("@");
//		} else {
//			mAutoCompleteTextView.setText(content + " @");
//		}
//		Selection.setSelection(mAutoCompleteTextView.getEditableText(),
//				mAutoCompleteTextView.getEditableText().length());
	}
	
	private void doAddUserNames(Intent intent){
		String names=intent.getStringExtra(Commons.EXTRA_TEXT);
		mAutoCompleteTextView.setText(content+names);
		Editable editable=mAutoCompleteTextView.getEditableText();
		Selection.setSelection(editable, editable.length());
	}

	private void send() {
		if (wordsCount < 1) {
			Utils.notify(this, "消息内容不能为空");
			return;
		}
		if (!App.me.isLogin) {
			Utils.notify(this, "未通过验证，请先登录");
			return;
		}
		finish();
		startSendService();
	}

	private void startSendService() {
		Intent i = new Intent(mContext, PostStatusService.class);
		i.putExtra(Commons.EXTRA_TYPE, type);
		i.putExtra(Commons.EXTRA_TEXT, content);
		i.putExtra(Commons.EXTRA_FILE, photo);
		i.putExtra(Commons.EXTRA_LOCATION, mLocationString);
		i.putExtra(Commons.EXTRA_STATUS, status);
		if (App.DEBUG) {
			log("intent=" + i);
		}
		startService(i);
	}

	// private ProgressDialog dialog = null;
	//
	// private void post2() {
	// if (StringHelper.isEmpty(content)) {
	// Utils.notify(mContext, "消息内容不能为空");
	// return;
	// }
	// dialog = new ProgressDialog(WritePage.this);
	// dialog.setMessage("发送中...");
	// dialog.setIndeterminate(true);
	// dialog.show();
	// Intent i = new Intent(this, UpdateService.class);
	// i.putExtra(Commons.EXTRA_RECEIVER, receiver);
	// if (status != null) {
	// if (type == TYPE_REPLY) {
	// i.putExtra(Commons.EXTRA_IN_REPLY_TO_ID, status.id);
	// } else if (type == TYPE_REPOST) {
	// i.putExtra(Commons.EXTRA_REPOST_ID, status.id);
	// }
	// }
	// if (photo != null) {
	// i.putExtra(Commons.EXTRA_FILE, photo);
	// }
	// if (enableLocation) {
	// i.putExtra(Commons.EXTRA_LOCATION, mLocationString);
	// }
	// i.putExtra(Commons.EXTRA_TEXT, content);
	// log("intent=" + i);
	// startService(i);
	// }

	// private ResultReceiver receiver = new ResultReceiver(new Handler()) {
	//
	// @Override
	// public void onReceiveResult(int resultCode, Bundle resultData) {
	// switch (resultCode) {
	// case Commons.RESULT_CODE_START:
	// break;
	// case Commons.RESULT_CODE_FINISH:
	// dialog.dismiss();
	// Toast.makeText(WritePage.this, "发送成功！", Toast.LENGTH_SHORT)
	// .show();
	// finish();
	// break;
	// case Commons.RESULT_CODE_ERROR:
	// dialog.dismiss();
	// String errorMessage = resultData
	// .getString(Commons.EXTRA_ERROR_MESSAGE);
	// Toast.makeText(WritePage.this,
	// "发送失败：" + errorMessage == null ? "" : errorMessage,
	// Toast.LENGTH_SHORT).show();
	// break;
	// default:
	// break;
	// }
	//
	// }
	// };

	private TextChangeListener textMonitor = new TextChangeListener() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			content = s.toString();
			wordsCount = content.length();
			showCount(wordsCount);
		}
	};

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
