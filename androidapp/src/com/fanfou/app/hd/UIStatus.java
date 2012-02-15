package com.fanfou.app.hd;

import java.io.File;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fanfou.app.hd.R;
import com.fanfou.app.hd.App.ApnType;
import com.fanfou.app.hd.api.Status;
import com.fanfou.app.hd.cache.CacheManager;
import com.fanfou.app.hd.cache.IImageLoader;
import com.fanfou.app.hd.cache.ImageLoader;
import com.fanfou.app.hd.dialog.ConfirmDialog;
import com.fanfou.app.hd.service.Constants;
import com.fanfou.app.hd.service.FanFouService;
import com.fanfou.app.hd.ui.widget.ActionManager;
import com.fanfou.app.hd.util.DateTimeHelper;
import com.fanfou.app.hd.util.IOHelper;
import com.fanfou.app.hd.util.OptionHelper;
import com.fanfou.app.hd.util.StatusHelper;
import com.fanfou.app.hd.util.StringHelper;
import com.fanfou.app.hd.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.01
 * @version 1.2 2011.10.24
 * @version 2.0 2011.10.25
 * @version 2.1 2011.10.26
 * @version 2.2 2011.10.28
 * @version 2.3 2011.10.29
 * @version 2.4 2011.11.04
 * @version 2.5 2011.11.07
 * @version 2.6 2011.11.17
 * @version 2.7 2011.11.22
 * @version 2.8 2011.11.28
 * @version 2.9 2011.12.08
 * @version 3.0 2011.12.21
 * @version 3.1 2012.02.01
 * 
 */
public class UIStatus extends UIBaseSupport {

	private static final int PHOTO_LOADING = -1;
	private static final int PHOTO_ICON = 0;
	private static final int PHOTO_SMALL = 1;
	private static final int PHOTO_LARGE = 2;

	private int mPhotoState = PHOTO_ICON;

	private ScrollView mScrollView;

	private IImageLoader mLoader;

	private String statusId;
	private Status status;

	private View vUser;

	private ImageView iUserHead;
	private TextView tUserName;

	private TextView tContent;
	private ImageView iPhoto;

	private TextView tDate;
	private TextView tSource;

	private ImageView bReply;
	private ImageView bRepost;
	private ImageView bFavorite;
	private ImageView bShare;

	private TextView vThread;

	private TextView vConversation;

	private boolean isMe;

	private String mPhotoUrl;

	// private GestureDetector mDetector;

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private static final String TAG = UIStatus.class.getSimpleName();

	private void log(String message) {
		Log.d(TAG, message);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		parseIntent();
		updateUI();

	}

	private void updateFavoriteButton(boolean favorited) {
		if (favorited) {
			bFavorite.setImageResource(R.drawable.i_bar2_unfavorite);
		} else {
			bFavorite.setImageResource(R.drawable.i_bar2_favorite);
		}
	}

	private void parseIntent() {
		Intent intent = getIntent();
		statusId = intent.getStringExtra(Constants.EXTRA_ID);
		status = (Status) intent.getParcelableExtra(Constants.EXTRA_DATA);

		if (status == null && statusId != null) {
			status = CacheManager.getStatus(this, statusId);
		} else {
			statusId = status.id;
		}
		isMe = status.userId.equals(App.getUserId());
	}

	@Override
	protected void initialize() {
		mLoader = App.getImageLoader();
		// mDetector=new GestureDetector(new SwipeGestureListener(this));

		parseIntent();
	}

	@Override
	protected void setLayout() {

		setContentView(R.layout.status);

		mScrollView = (ScrollView) findViewById(R.id.status_content);

		vUser = findViewById(R.id.status_top);
		vUser.setOnClickListener(this);
		iUserHead = (ImageView) findViewById(R.id.user_head);
		tUserName = (TextView) findViewById(R.id.user_name);
		TextPaint tp = tUserName.getPaint();
		tp.setFakeBoldText(true);

		tContent = (TextView) findViewById(R.id.status_text);
		iPhoto = (ImageView) findViewById(R.id.status_photo);
		tDate = (TextView) findViewById(R.id.status_date);
		tSource = (TextView) findViewById(R.id.status_source);
		vThread = (TextView) findViewById(R.id.status_thread);

		vConversation = (TextView) findViewById(R.id.status_conversation);
		vConversation.setVisibility(View.GONE);

		bReply = (ImageView) findViewById(R.id.status_action_reply);
		bRepost = (ImageView) findViewById(R.id.status_action_retweet);
		bFavorite = (ImageView) findViewById(R.id.status_action_favorite);
		bShare = (ImageView) findViewById(R.id.status_action_share);

		bReply.setOnClickListener(this);
		bRepost.setOnClickListener(this);
		bFavorite.setOnClickListener(this);
		bShare.setOnClickListener(this);
		vThread.setOnClickListener(this);

		registerForContextMenu(tContent);

		updateUI();
	}

	private void updateUI() {
		if (status != null) {

			boolean textMode = OptionHelper.readBoolean(mContext,
					R.string.option_text_mode, false);
			if (textMode) {
				iUserHead.setVisibility(View.GONE);
			} else {
				iUserHead.setTag(status.userProfileImageUrl);
				mLoader.displayImage(status.userProfileImageUrl, iUserHead,
						R.drawable.default_head);
			}

			tUserName.setText(status.userScreenName);

			StatusHelper.setStatus(tContent, status.text);
			checkPhoto(textMode, status);

			tDate.setText(DateTimeHelper.getInterval(status.createdAt));
			tSource.setText("通过" + status.source);

			if (isMe) {
				bReply.setImageResource(R.drawable.i_bar2_delete);
			} else {
				bReply.setImageResource(R.drawable.i_bar2_reply);
			}

			updateFavoriteButton(status.favorited);

			if (status.isThread) {
				vThread.setVisibility(View.VISIBLE);
			} else {
				vThread.setVisibility(View.GONE);
			}
		}
	}

	private void checkPhoto(boolean textMode, Status s) {
		if (!s.hasPhoto) {
			iPhoto.setVisibility(View.GONE);
			return;
		}

		mPhotoState = PHOTO_ICON;
		iPhoto.setVisibility(View.VISIBLE);
		iPhoto.setOnClickListener(this);

		// 先检查本地是否有大图缓存
		Bitmap bitmap = mLoader.getImage(s.photoLargeUrl, null);
		mPhotoUrl = s.photoLargeUrl;
		if (bitmap != null) {
			iPhoto.setImageBitmap(bitmap);
			mPhotoState = PHOTO_LARGE;
			return;
		}

		// 再检查本地是否有缩略图缓存
		bitmap = mLoader.getImage(s.photoImageUrl, null);
		mPhotoUrl = s.photoImageUrl;
		if (bitmap != null) {
			iPhoto.setImageBitmap(bitmap);
			mPhotoState = PHOTO_SMALL;
			return;
		}

		// 是否需要显示图片
		if (textMode) {
			iPhoto.setImageResource(R.drawable.photo_icon);
		} else {
			if (App.getApnType() == ApnType.WIFI) {
				loadPhoto(PHOTO_LARGE);
			} else {
				iPhoto.setImageResource(R.drawable.photo_icon);
			}
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
		case R.id.status_thread:
			Intent intent = new Intent(mContext, UIConversationList.class);
			intent.putExtra(Constants.EXTRA_DATA, status);
			mContext.startActivity(intent);
			// testAnimation();
			break;
		default:
			break;
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		doCopy(status.simpleText);
	}

	private String getPhotoPath(String key) {
		if (TextUtils.isEmpty(key)) {
			return null;
		}
		File file = new File(IOHelper.getImageCacheDir(mContext),
				StringHelper.md5(key) + ".jpg");
		if (App.DEBUG) {
			log("loadFile path=" + file);
		}
		if (file.exists()) {
			return file.getAbsolutePath();
		} else {
			return null;
		}

	}

	private void onClickPhoto() {
		if (App.DEBUG) {
			Log.d(TAG, "onClickPhoto() mPhotoState=" + mPhotoState);
		}
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

	private void goPhotoViewer() {
		if (!TextUtils.isEmpty(mPhotoUrl)) {
			String filePath = getPhotoPath(mPhotoUrl);
			if (App.DEBUG) {
				Log.d(TAG, "goPhotoViewer() url=" + filePath);
			}
			Intent intent = new Intent(mContext, UIPhoto.class);
			intent.putExtra(Constants.EXTRA_URL, filePath);
			mContext.startActivity(intent);
			overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_enter);
		}
	}

	private void loadPhoto(final int type) {
		if (type == PHOTO_ICON) {
			iPhoto.setImageResource(R.drawable.photo_icon);
			if (App.DEBUG) {
				Log.d(TAG, "loadPhoto mPhotoState=" + mPhotoState + " type="
						+ type);
			}
			return;
		}
		mPhotoState = PHOTO_LOADING;
		iPhoto.setImageResource(R.drawable.photo_loading);
		// clear queue before load big photos;
		ImageLoader.getInstance().clearQueue();
		if (App.DEBUG) {
			Log.d(TAG, "loadPhoto mPhotoState=" + mPhotoState + " type=" + type);
		}
		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				int what = msg.what;
				if (what == ImageLoader.MESSAGE_FINISH) {
					Bitmap bitmap = (Bitmap) msg.obj;
					if (App.DEBUG) {
						Log.d(TAG, "handler onfinish bitmap=" + bitmap);
					}
					if (bitmap != null) {
						iPhoto.setImageBitmap(bitmap);
						mPhotoState = type;
					} else {
						iPhoto.setImageResource(R.drawable.photo_icon);
						mPhotoState = PHOTO_ICON;
					}
				} else if (what == ImageLoader.MESSAGE_ERROR) {
					iPhoto.setImageResource(R.drawable.photo_icon);
					mPhotoState = PHOTO_ICON;
				}
			}
		};
		// final ImageLoaderCallback callback = new ImageLoaderCallback() {
		//
		// @Override
		// public void onFinish(String key, Bitmap bitmap) {
		// if (App.DEBUG) {
		// Log.d(TAG, "callback onfinish bitmap=" + bitmap);
		// }
		// if (bitmap != null) {
		// iPhoto.setImageBitmap(bitmap);
		// mPhotoState = type;
		// } else {
		// iPhoto.setImageResource(R.drawable.photo_icon);
		// mPhotoState = PHOTO_ICON;
		// }
		// }
		//
		// @Override
		// public void onError(String url, String message) {
		// iPhoto.setImageResource(R.drawable.photo_icon);
		// mPhotoState = PHOTO_ICON;
		// }
		//
		// @Override
		// public String toString(){
		// return "ImageLoaderCallback:"+this.hashCode();
		// }
		// };

		if (type == PHOTO_LARGE) {
			mPhotoUrl = status.photoLargeUrl;
		} else if (type == PHOTO_SMALL) {
			mPhotoUrl = status.photoThumbUrl;
		}

		if (App.DEBUG) {
			Log.d(TAG, "loadPhoto mPhotoState=" + mPhotoState + " type=" + type
					+ " url=" + mPhotoUrl);
		}

		iPhoto.setTag(mPhotoUrl);
		Bitmap bitmap = mLoader.getImage(mPhotoUrl, handler);
		if (bitmap != null) {
			iPhoto.setImageBitmap(bitmap);
			mPhotoState = type;
			if (App.DEBUG) {
				Log.d(TAG, "loadPhoto has cache url=" + mPhotoUrl + " type="
						+ type);
			}
		}
	}

	private void doDelete() {
		final ConfirmDialog dialog = new ConfirmDialog(this, "删除消息",
				"要删除这条消息吗？");
		dialog.setClickListener(new ConfirmDialog.AbstractClickHandler() {
			@Override
			public void onButton1Click() {
				FanFouService.doStatusDelete(mContext, status.id, true);
			}
		});
		dialog.show();

	}

	private void doFavorite() {
		ActionManager.ResultListener li = new ActionManager.ResultListener() {

			@Override
			public void onActionSuccess(int type, String message) {
				if (App.DEBUG)
					log("type="
							+ (type == Constants.TYPE_FAVORITES_CREATE ? "收藏"
									: "取消收藏") + " message=" + message);
				if (type == Constants.TYPE_FAVORITES_CREATE) {
					status.favorited = true;
				} else {
					status.favorited = false;
				}
				updateFavoriteButton(status.favorited);
			}

			@Override
			public void onActionFailed(int type, String message) {
			}
		};
		updateFavoriteButton(!status.favorited);
		FanFouService.doFavorite(this, status, li);
	}

	private void doCopy(String content) {
		IOHelper.copyToClipBoard(this, content);
		Utils.notify(this, "消息内容已复制到剪贴板");
	}

}
