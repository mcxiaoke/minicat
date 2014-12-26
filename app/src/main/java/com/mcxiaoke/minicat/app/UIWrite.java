package com.mcxiaoke.minicat.app;

import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.res.ColorStateList;
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
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.adapter.AtTokenizer;
import com.mcxiaoke.minicat.adapter.AutoCompleteCursorAdapter;
import com.mcxiaoke.minicat.controller.DataController;
import com.mcxiaoke.minicat.controller.UIController;
import com.mcxiaoke.minicat.dao.model.StatusUpdateInfo;
import com.mcxiaoke.minicat.dao.model.StatusUpdateInfoColumns;
import com.mcxiaoke.minicat.service.Constants;
import com.mcxiaoke.minicat.service.SyncService;
import com.mcxiaoke.minicat.ui.widget.MyAutoCompleteTextView;
import com.mcxiaoke.minicat.ui.widget.TextChangeListener;
import com.mcxiaoke.minicat.util.CompatUtils;
import com.mcxiaoke.minicat.util.IOHelper;
import com.mcxiaoke.minicat.util.ImageHelper;
import com.mcxiaoke.minicat.util.StringHelper;
import com.mcxiaoke.minicat.util.Utils;

import java.io.File;

/**
 * @author mcxiaoke
 * @version 7.1 2012.03.16
 */
public class UIWrite extends UIBaseSupport implements LoaderCallbacks<Cursor> {

    private static final String TAG = UIWrite.class.getSimpleName();

    private static final int LOADER_ID = 1;

    private static final int REQUEST_PHOTO_CAPTURE = 0;
    private static final int REQUEST_PHOTO_LIBRARY = 1;
    private static final int REQUEST_LOCATION_ADD = 2;
    private static final int REQUEST_USERNAME_ADD = 3;
    private MyAutoCompleteTextView mAutoCompleteTextView;
    private AutoCompleteCursorAdapter mAutoCompleteCursorAdapter;
    private ViewGroup vPhoto;
    private ImageView vPhotoPreview;
    private TextView tvWriteInfo;
    private ImageButton actionMention;
    private ImageButton actionRecord;
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
    private StatusUpdateInfo info;
    private String inReplyToStatusId;
    private String text;
    private int type;
    private int size;
    private ColorStateList mNormalTextColor;
    private ColorStateList mAlertTextColor;

    private void log(String message) {
        Log.d(TAG, message);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        size = getResources().getDimensionPixelSize(R.dimen.write_image_preview_width);
        mNormalTextColor = getResources().getColorStateList(R.color.text_blue);
        mAlertTextColor = getResources().getColorStateList(R.color.text_red);
        setLayout();

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
        super.onPause();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationMonitor);
        }
    }

    @Override
    public void onBackPressed() {
        if (AppContext.DEBUG) {
            log("onBackPressed content=" + content);
        }
        if (StringHelper.isEmpty(content) || this.content.trim().equals(text == null ? null : text.trim())) {
            goBack();
        } else {
            checkSave();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

    @Override
    protected int getMenuResourceId() {
        return R.menu.write_menu;
    }

    @Override
    protected void onMenuHomeClick() {
        goBack();
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
            case R.id.action_gallery:
                pickPhotoFromGallery();
                break;
            case R.id.action_camera:
                pickPhotoFromCamera();
                break;
            case R.id.photo_show:
                showRemoveDialog();
                break;
            default:
                break;
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        parseIntent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_LOCATION_ADD:
                    break;
                case REQUEST_PHOTO_LIBRARY:
                    if (AppContext.DEBUG) {
                        log("onActivityResult requestCode=REQUEST_PHOTO_LIBRARY data="
                                + data);
                    }
                    if (data != null) {
                        parsePhoto(data.getData());
                    }
                    break;
                case REQUEST_PHOTO_CAPTURE:
                    if (AppContext.DEBUG) {
                        log("onActivityResult requestCode=REQUEST_PHOTO_CAPTURE");
                    }
                    onCameraShot();
                    break;
                case REQUEST_USERNAME_ADD:
                    if (AppContext.DEBUG) {
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

    private void onCameraShot() {
        if (AppContext.DEBUG) {
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

    private void showInfo(int count) {
        log("show count =" + count);
        if (count <= 140) {
            tvWriteInfo.setTextColor(mNormalTextColor);
            tvWriteInfo.setText(String.valueOf(count));
        } else {
            tvWriteInfo.setTextColor(mAlertTextColor);
            tvWriteInfo.setText(String.valueOf(140 - count));
        }

    }

    private void parsePhoto(Uri uri) {
        if (uri != null) {

            if (AppContext.DEBUG)
                log("from gallery uri=" + uri);

            String path = CompatUtils.getPath(this, uri);
            if (AppContext.DEBUG)
                log("from gallery path=" + path);
            photo = new File(path);
            if (photo.exists()) {
                photoUri = uri;
            }
            if (AppContext.DEBUG)
                log("from gallery file=" + path);
            showPhoto();
        }
    }

    private void parsePhoto(File file) {
        if (file != null && file.exists()) {
            photo = file;
            photoUri = Uri.fromFile(file);
            if (AppContext.DEBUG)
                log("from file=" + file);
        }
    }

    private void parseIntent() {
        type = StatusUpdateInfo.TYPE_NONE;
        Intent intent = getIntent();
        if (intent != null) {
            String action = intent.getAction();
            if (action == null) {
                File file = null;
                info = intent.getParcelableExtra(StatusUpdateInfo.TAG);
                if (info != null) {
                    type = info.type;
                    text = info.text;
                    inReplyToStatusId = info.reply;
                    if (info.fileName != null) {
                        file = new File(info.fileName);
                    }
                    deleteRecord(info.id);
                } else {
                    type = intent.getIntExtra("type", StatusUpdateInfo.TYPE_NONE);
                    text = intent.getStringExtra("text");
                    inReplyToStatusId = intent.getStringExtra("id");
                    file = (File) intent.getSerializableExtra("data");
                }
                parsePhoto(file);
                updateUI();

            } else if (action.equals(Intent.ACTION_SEND)
                    || action.equals(Constants.ACTION_SEND)) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    text = extras.getString(Intent.EXTRA_TEXT);
                    Uri uri = extras.getParcelable(Intent.EXTRA_STREAM);
                    parsePhoto(uri);
                    updateUI();
                }
            }
            if (AppContext.DEBUG) {
                log("intent type=" + type);
                log("intent text=" + text);
            }
        }
    }

    private void updateUI() {
        if (!StringHelper.isEmpty(text)) {
            mAutoCompleteTextView.setText(text);
            if (type != StatusUpdateInfo.TYPE_REPOST) {
                Selection.setSelection(mAutoCompleteTextView.getText(),
                        mAutoCompleteTextView.getText().length());
            }
        }

        showInfo(mAutoCompleteTextView.getText().length());

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
                showInfo(wordsCount);
            }
        });

        mAutoCompleteTextView.setTokenizer(new AtTokenizer());
        mAutoCompleteCursorAdapter = new AutoCompleteCursorAdapter(mContext,
                null);
        mAutoCompleteTextView.setAdapter(mAutoCompleteCursorAdapter);
    }

    protected void setLayout() {

        setContentView(R.layout.ui_write);
        setProgressBarIndeterminateVisibility(false);

        actionMention = (ImageButton) findViewById(R.id.action_mention);
        actionRecord = (ImageButton) findViewById(R.id.action_record);
        actionGallery = (ImageButton) findViewById(R.id.action_gallery);
        actionCamera = (ImageButton) findViewById(R.id.action_camera);

        actionMention.setOnClickListener(this);
        actionRecord.setOnClickListener(this);
        actionGallery.setOnClickListener(this);
        actionCamera.setOnClickListener(this);

        vPhoto = (ViewGroup) findViewById(R.id.photo);
        final GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return super.onSingleTapUp(e);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float distanceX = e2.getX() - e1.getX();

                ViewConfiguration.get(mContext).getScaledMinimumFlingVelocity();
                if (distanceX > 500) {
                    removePhoto();
                    return true;
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        };
        final GestureDetector detector = new GestureDetector(this, simpleOnGestureListener);
        vPhoto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return true;
            }
        });
        vPhotoPreview = (ImageView) findViewById(R.id.photo_show);
        vPhotoPreview.setOnClickListener(this);

        tvWriteInfo = (TextView) findViewById(R.id.write_info);

        setTitle("写消息");

        setAutoComplete();
        parseIntent();

        getLoaderManager().initLoader(LOADER_ID, null, this);

    }

    private void goBack() {
        super.onBackPressed();
//        finish();
//        overridePendingTransition(R.anim.keep, R.anim.footer_disappear);
    }

    private void checkSave() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("保存草稿");
        builder.setMessage("要保存未发送内容为草稿吗？");
        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                doSaveRecord();
                goBack();
            }
        });
        builder.setNegativeButton("不保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                goBack();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void doSaveRecord() {
        StatusUpdateInfo rm = new StatusUpdateInfo();
        rm.type = type;
        rm.text = content;
        rm.fileName = (photo == null ? null : photo.toString());
        rm.reply = inReplyToStatusId;
        rm.repost = inReplyToStatusId;
        getContentResolver().insert(StatusUpdateInfoColumns.CONTENT_URI, rm.values());
    }

    private void removePhoto() {
        hidePhoto();
        photo = null;
        photoUri = null;
    }

    private void showRemoveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("移除照片");
        builder.setMessage("是否要移除该照片？");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removePhoto();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void pickPhotoFromCamera() {
        photo = IOHelper.getPhotoFilePath(this);
        photoUri = Uri.fromFile(photo);
        if (AppContext.DEBUG) {
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

    private void pickMentions() {
        Intent intent = new Intent(this, UIUserChoose.class);
        startActivityForResult(intent, REQUEST_USERNAME_ADD);
    }

    private void insertNames(Intent intent) {
        String names = intent.getStringExtra("text");
        if (AppContext.DEBUG) {
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
        Intent i = new Intent(mContext, SyncService.class);
        StatusUpdateInfo info = new StatusUpdateInfo();
        info.type = type;
        info.userId = AppContext.getAccount();
        info.text = content;
        info.fileName = photo == null ? null : photo.toString();
        info.location = mLocationString;
        info.reply = inReplyToStatusId;
        info.repost = inReplyToStatusId;
        i.putExtra("type", SyncService.STATUS_UPDATE);
        i.putExtra(StatusUpdateInfo.TAG, info);
        if (AppContext.DEBUG) {
            log("intent=" + i);
        }
        startService(i);
    }

    private void updateLocationString(Location loc) {
        if (loc != null) {
            mLocationString = String.format("%1$.5f,%2$.5f", loc.getLatitude(),
                    loc.getLongitude());
            if (AppContext.DEBUG)
                log("Location Info: " + mLocationString);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return DataController.getAutoCompleteCursorLoader(mContext,
                AppContext.getAccount());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {
        mAutoCompleteCursorAdapter.swapCursor(newCursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAutoCompleteCursorAdapter.swapCursor(null);
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
