package com.fanfou.app.hd;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.fanfou.app.hd.controller.EmptyViewController;
import com.fanfou.app.hd.controller.UIController;
import com.fanfou.app.hd.dao.model.StatusModel;
import com.fanfou.app.hd.dialog.ConfirmDialog;
import com.fanfou.app.hd.service.FanFouService;
import com.fanfou.app.hd.task.BetterAsyncTask;
import com.fanfou.app.hd.util.DateTimeHelper;
import com.fanfou.app.hd.util.IOHelper;
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
 * @version 4.0 2012.02.22
 * @version 4.1 2012.03.01
 * @version 4.2 2012.03.02
 * @version 5.0 2012.03.13
 * 
 */
public class UIStatus extends UIBaseSupport {

	private View vContainer;

	private String statusId;
	private StatusModel status;

	private View vHeader;

	private ImageView headerImage;
	private TextView headerName;

	private TextView contentText;
	private ImageView contentPhoto;

	private TextView contentMetaInfo;

	private ImageButton imReply;
	private ImageButton imRepost;
	private ImageButton imFavorite;
	private ImageButton imShare;

	private View vEmpty;

	private EmptyViewController emptyController;

	private boolean isMe;

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

	private void updateFavoriteAction(boolean favorited) {
		imFavorite.setImageLevel(favorited ? 1 : 0);
	}

	private void updateReplyAction() {
		imReply.setImageResource(isMe ? R.drawable.ic_delete
				: R.drawable.ic_reply);
	}

	private void parseIntent() {
		Intent intent = getIntent();
		statusId = intent.getStringExtra("id");
		status = (StatusModel) intent.getParcelableExtra("data");

		if (status != null) {
			statusId = status.getId();
		}

	}

	@Override
	protected void initialize() {
		parseIntent();
	}

	@Override
	protected void setLayout() {

		setContentView(R.layout.ui_status);

		findViews();
		setEmptyView();
		setListeners();

		//
		// registerForContextMenu(tContent);

		updateUI();
	}

	private void findViews() {
		vContainer = findViewById(R.id.container);
		vEmpty = findViewById(android.R.id.empty);

		vHeader = findViewById(R.id.header);
		headerImage = (ImageView) findViewById(R.id.header_image);
		headerName = (TextView) findViewById(R.id.header_name);
		TextPaint tp = headerName.getPaint();
		tp.setFakeBoldText(true);

		contentText = (TextView) findViewById(R.id.content_text);
		contentPhoto = (ImageView) findViewById(R.id.content_photo);
		contentMetaInfo = (TextView) findViewById(R.id.content_metainfo);

		imReply = (ImageButton) findViewById(R.id.action_reply);
		imRepost = (ImageButton) findViewById(R.id.action_retweet);
		imFavorite = (ImageButton) findViewById(R.id.action_favorite);
		imShare = (ImageButton) findViewById(R.id.action_share);
	}

	private void setEmptyView() {
		emptyController = new EmptyViewController(vEmpty);
		if (status == null) {
			fetchStatus();
			showProgress();
		} else {
			showContent();
		}
	}

	private void showEmptyView(String text) {
		vContainer.setVisibility(View.GONE);
		emptyController.showEmpty(text);
	}

	private void showProgress() {
		vContainer.setVisibility(View.GONE);
		emptyController.showProgress();
		if (App.DEBUG) {
			Log.d(TAG, "showProgress");
		}
	}

	private void showContent() {
		emptyController.hideProgress();
		vContainer.setVisibility(View.VISIBLE);
		if (App.DEBUG) {
			Log.d(TAG, "showContent");
		}
	}

	private void setListeners() {
		vHeader.setOnClickListener(this);
		
		contentPhoto.setOnClickListener(this);

		imReply.setOnClickListener(this);
		imRepost.setOnClickListener(this);
		imFavorite.setOnClickListener(this);
		imShare.setOnClickListener(this);
	}

	private void updateUI(StatusModel model) {
		if (model != null) {
			status = model;
		}
		updateUI();
	}

	private void updateUI() {
		if (status == null) {
			return;
		}

		isMe = status.getUserId().equals(App.getAccount());

		showContent();
		updateHeader();
		updateContent();
		updateActions();
		updatePhoto();
	}

	private void updateHeader() {
		headerName.setText(status.getUserScreenName());
		String headerImageUrl = status.getUserProfileImageUrl();
		headerImage.setTag(headerImageUrl);
		App.getImageLoader().displayImage(headerImageUrl, headerImage,
				R.drawable.ic_head);

	}

	private void updateContent() {
		StatusHelper.setStatus(contentText, status.getText());
		StringBuilder sb = new StringBuilder();
		sb.append(DateTimeHelper.formatDate(status.getTime())).append(" 通过")
				.append(status.getSource());
		contentMetaInfo.setText(sb.toString());
	}

	private void updateActions() {
		updateReplyAction();
		updateFavoriteAction(status.isFavorited());
	}

	private void updatePhoto() {
		if (!status.isPhoto()) {
			contentPhoto.setVisibility(View.GONE);
			return;
		}

		contentPhoto.setVisibility(View.VISIBLE);
		String photoUrl = status.getPhotoImageUrl();
		contentPhoto.setTag(photoUrl);
		App.getImageLoader().displayImage(photoUrl, contentPhoto, 0);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.action_reply:
			if (isMe) {
				doDelete();
			} else {
				UIController.doReply(mContext, status);
			}
			break;
		case R.id.action_retweet:
			UIController.doRetweet(mContext, status);
			break;
		case R.id.action_favorite:
			doFavorite();
			break;
		case R.id.action_share:
			UIController.doShare(mContext, status);
			break;
		case R.id.header:
			UIController.showProfile(mContext, status.getUserId());
			break;
		case R.id.content_photo:
			goPhotoViewer();
			break;
		// case R.id.thread:
		// Intent intent = new Intent(mContext, UIThread.class);
		// intent.putExtra("data", status);
		// mContext.startActivity(intent);
		// break;
		default:
			break;
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		doCopy(status.getSimpleText());
	}

	private void goPhotoViewer() {
		// TODO

		String photoUrl = status.getPhotoLargeUrl();
		if (!TextUtils.isEmpty(photoUrl)) {
			if (App.DEBUG) {
				Log.d(TAG, "goPhotoViewer() url=" + photoUrl);
			}
			Intent intent = new Intent(mContext, UIPhoto.class);
			intent.putExtra("url", photoUrl);
			mContext.startActivity(intent);
			overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_enter);
		}
	}

	private void fetchStatus() {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case FanFouService.RESULT_SUCCESS:
					StatusModel result = msg.getData().getParcelable("data");
					onFetchStatusComplete(result);
					break;
				case FanFouService.RESULT_ERROR:
					String errorMessage = msg.getData().getString(
							"error_message");
					onFetchStatusError(errorMessage);
					break;
				default:
					break;
				}
			}
		};
		if (App.DEBUG) {
			Log.d(TAG, "fetchStatus");
		}
		FanFouService.showStatus(mContext, statusId, handler);
	}

	private void onDeleteComplete() {
		finish();
	}

	private void onDeleteError(String message) {
		Utils.notify(mContext, message);
	}

	private void doDelete() {
		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				int what = msg.what;
				switch (what) {
				case FanFouService.RESULT_SUCCESS:
					onDeleteComplete();
					break;
				case FanFouService.RESULT_ERROR:
					int code = msg.getData().getInt("error_code");
					String message = msg.getData().getString("error_message");
					onDeleteError(message);
					break;
				default:
					break;
				}
			}

		};
		final ConfirmDialog dialog = new ConfirmDialog(this, "删除消息",
				"要删除这条消息吗？");
		dialog.setClickListener(new ConfirmDialog.AbstractClickHandler() {
			@Override
			public void onButton1Click() {
				FanFouService.deleteStatus(mContext, status.getId(), handler);
			}
		});
		dialog.show();

	}

	private void onFavoriteComplete(boolean favorited) {
		status.setFavorited(favorited);
		updateFavoriteAction(status.isFavorited());
		Utils.notify(mContext, favorited ? "收藏成功" : "取消收藏成功");
	}

	private void onFavoriteError(String message) {

	}

	private void doFavorite() {

		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case FanFouService.RESULT_SUCCESS:
					boolean favorited = msg.getData().getBoolean("boolean");
					onFavoriteComplete(favorited);
					break;
				case FanFouService.RESULT_ERROR:
					onFavoriteError(null);
					break;
				default:
					break;
				}
			}
		};
		updateFavoriteAction(!status.isFavorited());
		if (status.isFavorited()) {
			FanFouService.unfavorite(mContext, status.getId(), handler);
		} else {
			FanFouService.favorite(mContext, status.getId(), handler);
		}
	}

	private void doCopy(String content) {
		IOHelper.copyToClipBoard(this, content);
		Utils.notify(this, "消息内容已复制到剪贴板");
	}

	private void onFetchStatusComplete(StatusModel model) {
		updateUI(model);
	}

	private void onFetchStatusError(String message) {
		showEmptyView(message);
	}

	private class FetchStatusTask extends
			BetterAsyncTask<String, Void, StatusModel> {

		public FetchStatusTask(Context context) {
			super(context);
		}

		@Override
		protected void onPrepare(Context context) {
		}

		@Override
		protected void onPost(Context context, StatusModel result) {
			onFetchStatusComplete(result);
		}

		@Override
		protected void onError(Context context, Exception exception) {
			onFetchStatusError(exception.getMessage());
		}

		@Override
		protected StatusModel run(String... params) throws Exception {
			return App.getApi().showStatus(params[0]);
		}

	}

}
