package com.fanfou.app;

import java.io.File;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextPaint;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfou.app.api.Status;
import com.fanfou.app.cache.CacheManager;
import com.fanfou.app.cache.IImageLoader;
import com.fanfou.app.cache.IImageLoader.ImageLoaderCallback;
import com.fanfou.app.config.Commons;
import com.fanfou.app.dialog.ConfirmDialog;
import com.fanfou.app.ui.ActionBar;
import com.fanfou.app.ui.ActionManager;
import com.fanfou.app.util.DateTimeHelper;
import com.fanfou.app.util.IOHelper;
import com.fanfou.app.util.OptionHelper;
import com.fanfou.app.util.StatusHelper;
import com.fanfou.app.util.StringHelper;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.01
 * @version 1.2 2011.10.24
 * @version 2.0 2011.10.25
 * @version 2.1 2011.10.26
 * @version 2.2 2011.10.28
 * @version 2.3 2011.10.29
 * 
 */
public class StatusPage extends BaseActivity {

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
		Log.d(TAG, message);
	}

	private ActionBar mActionBar;

	private IImageLoader mLoader;

	private String statusId;
	private Status status;
	private Status thread;

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
		statusId = intent.getStringExtra(Commons.EXTRA_STATUS_ID);
		status = (Status) intent.getSerializableExtra(Commons.EXTRA_STATUS);

		if (status == null && statusId != null) {
			status = CacheManager.getStatus(this, statusId);
		} else {
			statusId = status.id;
		}
		isMe = status.userId.equals(App.me.userId);
	}

	/**
	 * 初始化和设置ActionBar
	 */
	private void setActionBar() {
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setTitle("消息");
		mActionBar.setRightAction(new ActionBar.WriteAction(this, status));
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
	}

	private void updateUI() {
		if (status != null) {
			boolean textMode = OptionHelper.readBoolean(this,
					R.string.option_text_mode, false);
			if (textMode) {
				iUserHead.setVisibility(View.GONE);
			} else {
				mLoader.set(status.userProfileImageUrl, iUserHead,
						R.drawable.default_head);
			}

			StatusHelper.setStatus(tContent, status.text);
			checkPhoto(textMode, status);

			tDate.setText(DateTimeHelper.getInterval(status.createdAt));
			tSource.setText("通过" + status.source);

			if (isMe) {
				bReply.setImageResource(R.drawable.i_bar2_delete);
			} else {
				bReply.setImageResource(R.drawable.i_bar2_reply);
			}

			updateFavoriteButton();

			if (status.isThread) {
				vThread.setVisibility(View.VISIBLE);
			} else {
				vThread.setVisibility(View.GONE);
			}
		}
	}

	private int mPhotoState = PHOTO_ICON;
	private static final int PHOTO_LOADING = -1;
	private static final int PHOTO_ICON = 0;
	private static final int PHOTO_SMALL = 1;
	private static final int PHOTO_LARGE = 2;

	private void checkPhoto(boolean textMode, Status s) {
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

		// 是否需要显示图片
		if (textMode) {
			iPhoto.setImageResource(R.drawable.photo_icon);
		} else {
			// 再根据系统设置处理
			int set = OptionHelper.parseInt(this, R.string.option_pic_level);
			if (set == 2) {
				// 如果设置为大图
				loadPhoto(PHOTO_LARGE);
			} else if (set == 1) {
				// 如果设置为缩略图
				loadPhoto(PHOTO_SMALL);
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
			 Intent intent = new Intent(mContext, ConversationPage.class);
			 intent.putExtra(Commons.EXTRA_STATUS, status);
			 mContext.startActivity(intent);
//			testAnimation();
			break;
		default:
			break;
		}
	}

	private void testAnimation() {
		vConversation.setVisibility(View.VISIBLE);
		vConversation
				.setText("梵蒂冈范德萨更多撒第四个第四个第四个第四个第四个第四个第四个听歌个第四个第四个人人后台很热替换各位特务特务独德萨更多撒第四个第四个第四个第四个第四个第四个第四个听歌个第四个第四个人人后台很热替换各位特务特务独特维特维特我郭特维特维特我郭德纲德国队三个");
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.scroll_from_top);
		vConversation.setAnimation(animation);
		animation.start();
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		doCopy(status.simpleText);
	}

	private String getPhotoPath(String key) {
		File file = new File(IOHelper.getImageCacheDir(mContext),
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

	private void onClickPhoto() {
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
		Intent intent = new Intent(mContext, PhotoViewPage.class);
		intent.putExtra(Commons.EXTRA_URL,
				getPhotoPath((String) iPhoto.getTag()));
		mContext.startActivity(intent);
		overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_enter);
	}

	private void loadPhoto(final int type) {
		iPhoto.setImageResource(R.drawable.photo_loading);
		mPhotoState = PHOTO_LOADING;
		ImageLoaderCallback callback = new ImageLoaderCallback() {

			@Override
			public void onFinish(String key, Bitmap bitmap) {
				if (bitmap != null) {
					iPhoto.setImageBitmap(bitmap);
					iPhoto.invalidate();
					mPhotoState = type;
				}
			}

			@Override
			public void onError(String message) {
				iPhoto.setImageResource(R.drawable.photo_icon);
				mPhotoState = PHOTO_ICON;
			}
		};
		String photoUrl = null;
		if (type == PHOTO_LARGE) {
			photoUrl = status.photoLargeUrl;
		} else if (type == PHOTO_SMALL) {
			photoUrl = status.photoImageUrl;
		}
		iPhoto.setTag(photoUrl);
		Bitmap bitmap = mLoader.load(photoUrl, callback);
		if (bitmap != null) {
			iPhoto.setImageBitmap(bitmap);
			iPhoto.invalidate();
			mPhotoState = type;
		}
	}

	private void doDelete() {
		final ConfirmDialog dialog = new ConfirmDialog(this, "删除消息",
				"要删除这条消息吗？");
		dialog.setClickListener(new ConfirmDialog.AbstractClickHandler() {
			@Override
			public void onButton1Click() {
				ActionManager.doStatusDelete(mContext, status.id, true);
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

}
