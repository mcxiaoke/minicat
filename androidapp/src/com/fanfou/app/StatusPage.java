package com.fanfou.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfou.app.api.Status;
import com.fanfou.app.cache.IImageLoader;
import com.fanfou.app.cache.IImageLoader.ImageLoaderCallback;
import com.fanfou.app.config.Commons;
import com.fanfou.app.service.ActionService;
import com.fanfou.app.ui.ActionBar;
import com.fanfou.app.ui.ActionBar.Action;
import com.fanfou.app.ui.ActionManager;
import com.fanfou.app.util.DateTimeHelper;
import com.fanfou.app.util.IOHelper;
import com.fanfou.app.util.OptionHelper;
import com.fanfou.app.util.StatusHelper;
import com.fanfou.app.util.StringHelper;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * 
 */
public class StatusPage extends BaseActivity implements
		ActionManager.ResultListener {

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private static final String TAG = StatusPage.class.getSimpleName();

	private void log(String message) {
		Log.e(TAG, message);
	}

	private ActionBar mActionBar;

	private IImageLoader mLoader;

	private Status status;
	private Status thread;

	private View vUser;

	private ImageView iUserHead;
	// private TextView tUserId;
	private TextView tUserName;

	private ImageView iShowUser;

	private TextView tContent;
	private ImageView iPhoto;

	private TextView tDate;
	private TextView tSource;

	private ImageView bReply;
	private ImageView bRepost;
	private ImageView bFavorite;
	private ImageView bShare;

	private View vThread;
	private TextView tThreadName;
	private TextView tThreadText;

	private Handler mHandler;

	private boolean isMe;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parseIntent();

		mLoader = App.me.getImageLoader();
		mHandler = new Handler();

		setContentView(R.layout.status);
		setActionBar();
		setLayout();
		updateUI();

	}

	private void updateFavoriteButton() {
		if (status.favorited) {
			bFavorite.setImageResource(R.drawable.i_bar2_unfavorite);
		} else {
			bFavorite.setImageResource(R.drawable.i_bar2_favorite);
		}
	}

	private void parseIntent() {
		Intent intent = getIntent();
		status = (Status) intent.getSerializableExtra(Commons.EXTRA_STATUS);

		isMe = status.userId.equals(App.me.userId);
	}

	/**
	 * 初始化和设置ActionBar
	 */
	private void setActionBar() {
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setTitle("消息");

		Intent intent = new Intent(this, WritePage.class);
		intent.putExtra(Commons.EXTRA_TYPE, WritePage.TYPE_REPLY);
		intent.putExtra(Commons.EXTRA_STATUS, status);
		Action action = new ActionBar.IntentAction(mContext, intent,
				R.drawable.i_write);
		mActionBar.setRightAction(action);
		mActionBar.setLeftAction(new ActionBar.BackAction(mContext));
	}

	private void setLayout() {

		vUser = findViewById(R.id.status_top);
		vUser.setOnClickListener(this);
		iUserHead = (ImageView) findViewById(R.id.user_head);
		tUserName = (TextView) findViewById(R.id.user_name);
		TextPaint tp = tUserName.getPaint();
		tp.setFakeBoldText(true);
		tUserName.setText(status.userScreenName);
		iShowUser = (ImageView) findViewById(R.id.status_action_user);
		tContent = (TextView) findViewById(R.id.status_text);
		iPhoto = (ImageView) findViewById(R.id.status_photo);
		tDate = (TextView) findViewById(R.id.status_date);
		tSource = (TextView) findViewById(R.id.status_source);
		vThread = findViewById(R.id.status_thread);
		vThread.setVisibility(View.GONE);
		tThreadName = (TextView) findViewById(R.id.status_thread_user);
		TextPaint tp2 = tThreadName.getPaint();
		tp2.setFakeBoldText(true);
		tThreadText = (TextView) findViewById(R.id.status_thread_text);

		bReply = (ImageView) findViewById(R.id.status_action_reply);
		bRepost = (ImageView) findViewById(R.id.status_action_retweet);
		bFavorite = (ImageView) findViewById(R.id.status_action_favorite);
		bShare = (ImageView) findViewById(R.id.status_action_share);

		bReply.setOnClickListener(this);
		bRepost.setOnClickListener(this);
		bFavorite.setOnClickListener(this);
		bShare.setOnClickListener(this);

		tContent.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				doCopy(status.simpleText);
				return true;
			}
		});
	}

	private void updateUI() {
		if (status != null) {
			mLoader.set(status.userProfileImageUrl, iUserHead,
					R.drawable.default_head);

			StatusHelper.setStatus(tContent, status.text);
			checkPhoto(status);

			tDate.setText("时间：" + DateTimeHelper.getInterval(status.createdAt));
			tSource.setText("来源：" + status.source);

			if (isMe) {
				bReply.setImageResource(R.drawable.i_bar2_delete);
			} else {
				bReply.setImageResource(R.drawable.i_bar2_reply);
			}

			updateFavoriteButton();

			if (status.isThread) {
				showThreadLoading();
				doFetchThread();
			}
		}
	}

	private int mPhotoState=PHOTO_ICON;
	private static final int PHOTO_LOADING=-1;
	private static final int PHOTO_ICON=0;
	private static final int PHOTO_SMALL=1;
	private static final int PHOTO_LARGE=2;

	private void checkPhoto(Status s) {
		if (!s.hasPhoto) {
			iPhoto.setVisibility(View.GONE);
			return;
		}
		
		iPhoto.setOnClickListener(this);

		// 先检查本地是否有大图缓存
		Bitmap bitmap = mLoader.load(s.photoLargeUrl);
		if (bitmap != null) {
			iPhoto.setTag(s.photoLargeUrl);
			iPhoto.setImageBitmap(bitmap);
			mPhotoState = PHOTO_LARGE;
			return;
		}

		// 再检查本地是否有缩略图缓存
		bitmap = mLoader.load(s.photoImageUrl);
		if (bitmap != null) {
			iPhoto.setTag(s.photoImageUrl);
			iPhoto.setImageBitmap(bitmap);
			mPhotoState = PHOTO_SMALL;
			return;
		}

		// 再根据系统设置处理
		int set = OptionHelper.parseInt(this, R.string.option_pic_level);
		if (set == 2) {
			// 如果设置为大图
			loadPhoto(PHOTO_LARGE);
		}else if (set == 1) {
			// 如果设置为缩略图
			loadPhoto(PHOTO_SMALL);
		}else{
			iPhoto.setImageResource(R.drawable.photo_icon);
		}

		
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.status_action_reply:
			if (isMe) {
				doDelete();
			} else {
				ActionManager.doReply(this, status);
			}
			break;
		case R.id.status_action_retweet:
			ActionManager.doRetweet(this, status);
			break;
		case R.id.status_action_favorite:
			doFavorite();
			break;
		case R.id.status_action_share:
			ActionManager.doShare(this, status);
			break;
		case R.id.status_top:
			ActionManager.doProfile(this, status);
			break;
		// case R.id.status_text:
		// break;
		case R.id.status_photo:
			onClickPhoto();
			break;
		default:
			break;
		}
	}

	private String getPhotoPath(String key) {
		File file = new File(IOHelper.getCacheDir(mContext),
				StringHelper.md5(key) + ".jpg");
		if (App.DEBUG) {
			log("loadFile path=" + file.getAbsolutePath());
		}
		if (file.exists()) {
			return file.getAbsolutePath();
		} else {
			return null;
		}

	}
	
	private void onClickPhoto(){
		switch (mPhotoState) {
		case PHOTO_ICON:
			loadPhoto(PHOTO_LARGE);
			break;
		case PHOTO_SMALL:
			loadPhoto(PHOTO_LARGE);
			break;
		case PHOTO_LARGE:
			goPhotoViewer();
			break;
		case PHOTO_LOADING:
			break;
		default:
			break;
		}
	}
	
	private void goPhotoViewer(){
		Intent intent = new Intent(mContext, PhotoViewPage.class);
		 intent.putExtra(Commons.EXTRA_URL,getPhotoPath((String)iPhoto.getTag()));
		mContext.startActivity(intent);
		overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_enter);
	}

	private void loadPhoto(final int type) {
		iPhoto.setImageResource(R.drawable.photo_loading);
		mPhotoState=PHOTO_LOADING;
		ImageLoaderCallback callback = new ImageLoaderCallback() {

			@Override
			public void onFinish(String key, Bitmap bitmap) {
				if (bitmap != null) {
					iPhoto.setImageBitmap(bitmap);
					iPhoto.invalidate();
					mPhotoState=type;
				}
			}
			@Override
			public void onError(String message) {
				iPhoto.setImageResource(R.drawable.photo_icon);
				mPhotoState=PHOTO_ICON;
			}
		};
		String photoUrl=null;
		if(type==PHOTO_LARGE){
			photoUrl=status.photoLargeUrl;
		}else if(type==PHOTO_SMALL){
			photoUrl=status.photoImageUrl;
		}
		iPhoto.setTag(photoUrl);
		Bitmap bitmap=mLoader.load(photoUrl, callback);
		if(bitmap!=null){
			iPhoto.setImageBitmap(bitmap);
			iPhoto.invalidate();
			mPhotoState=type;
		}
	}

	private void doDelete() {
		ActionManager.doStatusDelete(this, status.id, true);
	}

	private void doFavorite() {
		ActionManager.ResultListener li = new ActionManager.ResultListener() {

			@Override
			public void onActionSuccess(int type, String message) {
				if (App.DEBUG)
					log("type="
							+ (type == Commons.ACTION_STATUS_FAVORITE ? "收藏"
									: "取消收藏") + " message=" + message);
				if (type == Commons.ACTION_STATUS_FAVORITE) {
					status.favorited = true;
				} else {
					status.favorited = false;
				}
				updateFavoriteButton();
			}

			@Override
			public void onActionFailed(int type, String message) {
			}
		};
		ActionManager.doFavorite(this, status, li);
	}

	private void doCopy(String content) {
		IOHelper.copyToClipBoard(this, content);
		Utils.notify(this, "消息内容已复制到剪贴板");
	}

	private void showThread(Status result) {
		if (App.DEBUG)
			log("showThread() status.text=" + result.text);
		thread = result;
		tThreadName.setText(thread.userScreenName);
		tThreadText.setText(thread.text);
		tThreadText.setGravity(Gravity.LEFT);
		tThreadName.setVisibility(View.VISIBLE);
		vThread.setVisibility(View.VISIBLE);
		vThread.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Utils.goStatusPage(mContext, thread);
			}
		});

	}

	private void showThreadLoading() {
		vThread.setVisibility(View.VISIBLE);
		tThreadName.setVisibility(View.GONE);
		tThreadText.setGravity(Gravity.CENTER);
		tThreadText.setText("正在加载对话消息...");
	}

	private void showThreadError(String text) {
		if (status == null) {
			return;
		}
		if (App.DEBUG)
			log("showThreadError() " + text);
		tThreadText.setText("加载消息失败：" + text);
		vThread.setVisibility(View.VISIBLE);

	}

	private void doFetchThread() {
		ResultReceiver receiver = new ResultReceiver(mHandler) {

			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				switch (resultCode) {
				case Commons.RESULT_CODE_START:
					break;
				case Commons.RESULT_CODE_FINISH:
					Status result = (Status) resultData
							.getSerializable(Commons.EXTRA_STATUS);
					if (result != null) {
						showThread(result);
					}
					break;
				case Commons.RESULT_CODE_ERROR:
					String errorMessage = resultData
							.getString(Commons.EXTRA_ERROR_MESSAGE);
					showThreadError(errorMessage);
					break;
				default:
					break;
				}
			}
		};
		Intent intent = new Intent(this, ActionService.class);
		intent.putExtra(Commons.EXTRA_TYPE, Commons.ACTION_STATUS_SHOW);
		intent.putExtra(Commons.EXTRA_ID, status.inReplyToStatusId);
		intent.putExtra(Commons.EXTRA_RECEIVER, receiver);
		startService(intent);
	}

	@Override
	public void onActionSuccess(int type, String message) {
	}

	@Override
	public void onActionFailed(int type, String message) {
	}

}
