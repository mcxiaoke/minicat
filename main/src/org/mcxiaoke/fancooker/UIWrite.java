package org.mcxiaoke.fancooker;

import java.io.File;

import org.mcxiaoke.fancooker.adapter.AtTokenizer;
import org.mcxiaoke.fancooker.adapter.AutoCompleteCursorAdapter;
import org.mcxiaoke.fancooker.controller.DataController;
import org.mcxiaoke.fancooker.controller.SimpleDialogListener;
import org.mcxiaoke.fancooker.controller.UIController;
import org.mcxiaoke.fancooker.dao.model.RecordColumns;
import org.mcxiaoke.fancooker.dao.model.RecordModel;
import org.mcxiaoke.fancooker.dialog.ConfirmDialog;
import org.mcxiaoke.fancooker.service.Constants;
import org.mcxiaoke.fancooker.service.PostStatusService;
import org.mcxiaoke.fancooker.ui.widget.MyAutoCompleteTextView;
import org.mcxiaoke.fancooker.ui.widget.TextChangeListener;
import org.mcxiaoke.fancooker.util.IOHelper;
import org.mcxiaoke.fancooker.util.ImageHelper;
import org.mcxiaoke.fancooker.util.OptionHelper;
import org.mcxiaoke.fancooker.util.StringHelper;
import org.mcxiaoke.fancooker.util.Utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.Selection;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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
 * @version 5.0 2012.02.13
 * @version 5.5 2012.02.22
 * @version 5.6 2012.02.28
 * @version 5.7 2012.03.09
 * @version 6.0 2012.03.14
 * @version 7.0 2012.03.15
 * @version 7.1 2012.03.16
 * 
 */
public class UIWrite extends UIBaseSupport implements LoaderCallbacks<Cursor> {

	private static final String TAG = UIWrite.class.getSimpleName();

	private static final int LOADER_ID = 1;

	private static final int REQUEST_PHOTO_CAPTURE = 0;
	private static final int REQUEST_PHOTO_LIBRARY = 1;
	private static final int REQUEST_LOCATION_ADD = 2;
	private static final int REQUEST_USERNAME_ADD = 3;

	private void log(String message) {
		Log.d(TAG, message);
	}

	private MyAutoCompleteTextView mAutoCompleteTextView;
	private AutoCompleteCursorAdapter mAutoCompleteCursorAdapter;

	private View vPhoto;
	private ImageView vPhotoPreview;
	private ImageButton vPhotoRemove;
	private TextView tCount;

	private ImageButton actionMention;
	private ImageButton actionRecord;
	private ImageButton actionLocation;
	private ImageButton actionGallery;
	private ImageButton actionCamera;

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

	}

	@Override
	protected int getMenuResourceId() {
		return R.menu.write_menu;
	}

	@Override
	protected void initialize() {
		enableLocation = OptionHelper.readBoolean(mContext,
				R.string.option_location_enable, true);
		mLocationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		mLocationMonitor = new LocationMonitor();
		size = new Float(getResources().getDimension(R.dimen.write_photo_width))
				.intValue();
		for (String provider : mLocationManager.getProviders(true)) {
			if (LocationManager.NETWORK_PROVIDER.equals(provider)
					|| LocationManager.GPS_PROVIDER.equals(provider)) {
				mLocationProvider = provider;
				break;
			}
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
				onCameraShot();
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

	private void onCameraShot() {
		if (App.DEBUG) {
			log("doCameraShot() from camera uri=" + photoUri);
			log("doCameraShot() from camera filename="
					+ photo.getAbsolutePath());
			log("doCameraShot() file.size=" + photo.length());
		}
		showPhoto();
	}

	private void showPhoto() {
		vPhoto.setVisibility(View.VISIBLE);
		try {
			vPhotoPreview.setImageBitmap(ImageHelper.getRoundedCornerBitmap(
					ImageHelper.resampleImage(photo, size), 6));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void hidePhoto() {
		vPhoto.setVisibility(View.GONE);
	}

	private void showCount(int count) {
		int num = 140 - count;
		tCount.setText(String.valueOf(num));
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
			showPhoto();
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
				type = intent.getIntExtra("type", TYPE_NORMAL);
				text = intent.getStringExtra("text");
				inReplyToStatusId = intent.getStringExtra("id");
				File file = (File) intent.getSerializableExtra("data");
				long draftId = intent.getIntExtra("record_id", -1);
				parsePhoto(file);
				updateUI();
				deleteRecord(draftId);
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
				pickPhotoFromGallery();
			} else if (action.equals(Constants.ACTION_SEND_FROM_CAMERA)) {
				type = TYPE_CAMERA;
				pickPhotoFromCamera();
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

		showCount(mAutoCompleteTextView.getText().length());

		if (photoUri != null) {
			showPhoto();
		}
	}

	private void deleteRecord(long id) {
		if (id > 0) {
			DataController.deleteRecord(mContext, id);
		}
	}

	private void setAutoComplete() {
		mAutoCompleteTextView = (MyAutoCompleteTextView) findViewById(R.id.input);

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
		mAutoCompleteCursorAdapter = new AutoCompleteCursorAdapter(mContext,
				null);
		mAutoCompleteTextView.setAdapter(mAutoCompleteCursorAdapter);
	}

	@Override
	protected void setLayout() {

		setContentView(R.layout.ui_write);

		actionMention = (ImageButton) findViewById(R.id.action_mention);
		actionRecord = (ImageButton) findViewById(R.id.action_record);
		actionLocation = (ImageButton) findViewById(R.id.action_location);
		actionGallery = (ImageButton) findViewById(R.id.action_gallery);
		actionCamera = (ImageButton) findViewById(R.id.action_camera);

		actionMention.setOnClickListener(this);
		actionRecord.setOnClickListener(this);
		actionLocation.setOnClickListener(this);
		actionGallery.setOnClickListener(this);
		actionCamera.setOnClickListener(this);

		actionLocation.setImageLevel(enableLocation ? 1 : 0);

		vPhoto = findViewById(R.id.photo);
		vPhotoPreview = (ImageView) findViewById(R.id.photo_show);
		vPhotoRemove = (ImageButton) findViewById(R.id.photo_remove);
		vPhotoRemove.setOnClickListener(this);

		tCount = (TextView) findViewById(R.id.count);

		setTitle("写消息");

		setAutoComplete();
		parseIntent();

		getSupportLoaderManager().initLoader(LOADER_ID, null, this);

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mLocationProvider != null) {
			mLocationManager.requestLocationUpdates(mLocationProvider, 0, 0,
					mLocationMonitor);
		}
	}

	@Override
	protected void onPause() {
		mLocationManager.removeUpdates(mLocationMonitor);
		super.onPause();
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
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.action_mention:
			pickMentions();
			break;
		case R.id.action_record:
			UIController.showRecords(mContext);
			break;
		case R.id.action_location:
			toggleLocation();
			break;
		case R.id.action_gallery:
			pickPhotoFromGallery();
			break;
		case R.id.action_camera:
			pickPhotoFromCamera();
			break;
		case R.id.photo_remove:
			removePhoto();
			break;
		default:
			break;
		}

	}

	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_send:
			doSend();
			return true;
			// break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void checkSave() {

		final ConfirmDialog dialog = new ConfirmDialog(this);
		dialog.setMessage("要保存未发送内容为草稿吗？");
		dialog.setTitle("保存草稿");
		dialog.setClickListener(new SimpleDialogListener() {

			@Override
			public void onPositiveClick() {
				super.onPositiveClick();
				doSaveRecord();
				finish();
			}

			@Override
			public void onNegativeClick() {
				super.onNegativeClick();
				finish();
			}
		});
		dialog.show();
	}

	private void doSaveRecord() {
		RecordModel rm = new RecordModel();
		rm.setType(type);
		rm.setText(content);
		rm.setFile(photo == null ? "" : photo.toString());
		rm.setReply(inReplyToStatusId);
		getContentResolver().insert(RecordColumns.CONTENT_URI, rm.values());
	}

	private void removePhoto() {
		hidePhoto();
		photo = null;
		photoUri = null;
	}

	private void pickPhotoFromCamera() {
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

	private void pickPhotoFromGallery() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		// startActivityForResult(intent, REQUEST_PHOTO_LIBRARY);
		startActivityForResult(Intent.createChooser(intent, "选择照片"),
				REQUEST_PHOTO_LIBRARY);
	}

	private void toggleLocation() {
		enableLocation = !enableLocation;
		OptionHelper.saveBoolean(mContext, R.string.option_location_enable,
				enableLocation);
		if (App.DEBUG)
			log("location enable status=" + enableLocation);
		actionLocation.setImageLevel(enableLocation ? 1 : 0);
	}

	private void pickMentions() {
		Intent intent = new Intent(this, UIUserChoose.class);
		startActivityForResult(intent, REQUEST_USERNAME_ADD);
	}

	private void insertNames(Intent intent) {
		String names = intent.getStringExtra("text");
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
		i.putExtra("type", type);
		i.putExtra("text", content);
		i.putExtra("data", photo);
		i.putExtra("location", mLocationString);
		i.putExtra("id", inReplyToStatusId);
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

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return DataController.getAutoCompleteCursorLoader(mContext,
				App.getAccount());
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {
		mAutoCompleteCursorAdapter.swapCursor(newCursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAutoCompleteCursorAdapter.swapCursor(null);
	}

}
