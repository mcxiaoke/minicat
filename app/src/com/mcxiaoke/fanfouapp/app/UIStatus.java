package com.mcxiaoke.fanfouapp.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.mcxiaoke.fanfouapp.R;
import com.mcxiaoke.fanfouapp.controller.CacheController;
import com.mcxiaoke.fanfouapp.controller.EmptyViewController;
import com.mcxiaoke.fanfouapp.controller.SimpleDialogListener;
import com.mcxiaoke.fanfouapp.controller.UIController;
import com.mcxiaoke.fanfouapp.dao.model.StatusModel;
import com.mcxiaoke.fanfouapp.dialog.ConfirmDialog;
import com.mcxiaoke.fanfouapp.service.SyncService;
import com.mcxiaoke.fanfouapp.task.BetterAsyncTask;
import com.mcxiaoke.fanfouapp.util.DateTimeHelper;
import com.mcxiaoke.fanfouapp.util.IOHelper;
import com.mcxiaoke.fanfouapp.util.ImageHelper;
import com.mcxiaoke.fanfouapp.util.IntentHelper;
import com.mcxiaoke.fanfouapp.util.StatusHelper;
import com.mcxiaoke.fanfouapp.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

/**
 * @author mcxiaoke
 * @version 5.0 2012.03.13
 */
public class UIStatus extends UIBaseSupport {

    private View vContainer;

    private String statusId;
    private StatusModel status;

    private View vHeader;

    private ImageView headerImage;
    private TextView headerName;
    private TextView headerId;
    private TextView headerFollow;

    private TextView contentText;
    private ImageView contentPhoto;

    private TextView contentMetaInfo;

    private ImageButton imThread;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseIntent();
        setLayout();
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
        status = intent.getParcelableExtra("data");

        if (status != null) {
            statusId = status.getId();
        } else {
            status = CacheController.getStatusAndCache(statusId, mContext);
        }

    }

    protected void setLayout() {

        setContentView(R.layout.ui_status);
        setProgressBarIndeterminateVisibility(false);

        setTitle("消息");

        findViews();
        setEmptyView();
        setListeners();
        updateUI();
    }

    private void findViews() {
        vContainer = findViewById(R.id.container);
        vEmpty = findViewById(android.R.id.empty);

        vHeader = findViewById(R.id.header);
        headerImage = (ImageView) findViewById(R.id.header_image);
        headerName = (TextView) findViewById(R.id.header_name);
        headerId = (TextView) findViewById(R.id.header_id);
        headerFollow = (TextView) findViewById(R.id.header_follow);
        headerFollow.setVisibility(View.GONE);

        contentText = (TextView) findViewById(R.id.content_text);
        contentPhoto = (ImageView) findViewById(R.id.content_photo);
        contentMetaInfo = (TextView) findViewById(R.id.content_metainfo);

        imThread = (ImageButton) findViewById(R.id.thread_title);

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
        if (AppContext.DEBUG) {
            Log.d(TAG, "showProgress");
        }
    }

    private void showContent() {
        emptyController.hideProgress();
        vContainer.setVisibility(View.VISIBLE);
        if (AppContext.DEBUG) {
            Log.d(TAG, "showContent");
        }
    }

    private void setListeners() {
        vHeader.setOnClickListener(this);

        contentPhoto.setOnClickListener(this);

        imThread.setOnClickListener(this);

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

        isMe = status.getUserId().equals(AppContext.getAccount());

        showContent();
        updateHeader();
        updateContent();
        updateActions();
        updatePhoto();
        updateThread();
    }

    private void updateHeader() {
        headerName.setText(status.getUserScreenName());
        headerId.setText("@" + status.getUserId());
        String headerImageUrl = status.getUserProfileImageUrl();
        ImageLoader.getInstance().displayImage(headerImageUrl, headerImage);

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
        String photoUrl = status.getPhotoLargeUrl();
        displayPhoto(photoUrl);
    }

    private void updateThread() {
        imThread.setVisibility(status.isThread() ? View.VISIBLE : View.GONE);
    }

    private void displayPhoto(String url) {
        final ImageLoadingListener listener = new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                contentPhoto.setImageResource(R.drawable.photo_loading);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view,
                                        FailReason failReason) {
                contentPhoto.setImageResource(R.drawable.photo_error);
                contentPhoto.setClickable(false);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view,
                                          Bitmap loadedImage) {
                contentPhoto.setImageBitmap(ImageHelper.getRoundedCornerBitmap(loadedImage, getResources().getDimensionPixelSize(R.dimen.image_round_corner)));
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                contentPhoto.setImageResource(R.drawable.photo_error);
            }
        };
        ImageLoader.getInstance().loadImage(url, listener);
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
            case R.id.thread_title:
                UIController.showThread(mContext, status.getId());
                break;
            default:
                break;
        }
    }

    @Override
    protected int getMenuResourceId() {
        return R.menu.menu_status;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_copy) {
            doCopy();
            return true;
        } else if (item.getItemId() == R.id.menu_web) {
            IntentHelper.startWebIntent(this, "http://fanfou.com/statuses/" + status.getId());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goPhotoViewer() {
        String photoUrl = status.getPhotoLargeUrl();
        if (!TextUtils.isEmpty(photoUrl)) {
            if (AppContext.DEBUG) {
                Log.d(TAG, "goPhotoViewer() url=" + photoUrl);
            }
            UIController.showPhoto(mContext, photoUrl);
        }
    }

    private void fetchStatus() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SyncService.RESULT_SUCCESS:
                        StatusModel result = msg.getData().getParcelable("data");
                        onFetchStatusComplete(result);
                        break;
                    case SyncService.RESULT_ERROR:
                        String errorMessage = msg.getData().getString(
                                "error_message");
                        onFetchStatusError(errorMessage);
                        break;
                    default:
                        break;
                }
            }
        };
        if (AppContext.DEBUG) {
            Log.d(TAG, "fetchStatus");
        }
        SyncService.showStatus(mContext, statusId, handler);
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
                    case SyncService.RESULT_SUCCESS:
                        onDeleteComplete();
                        break;
                    case SyncService.RESULT_ERROR:
                        int code = msg.getData().getInt("error_code");
                        String message = msg.getData().getString("error_message");
                        onDeleteError(message);
                        break;
                    default:
                        break;
                }
            }

        };
        final ConfirmDialog dialog = new ConfirmDialog(this);
        dialog.setMessage("确定要删除这条消息吗？");
        dialog.setClickListener(new SimpleDialogListener() {

            @Override
            public void onPositiveClick() {
                super.onPositiveClick();
                SyncService.deleteStatus(mContext, status.getId(), handler);
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
                    case SyncService.RESULT_SUCCESS:
                        boolean favorited = msg.getData().getBoolean("boolean");
                        onFavoriteComplete(favorited);
                        break;
                    case SyncService.RESULT_ERROR:
                        onFavoriteError(null);
                        break;
                    default:
                        break;
                }
            }
        };
        updateFavoriteAction(!status.isFavorited());
        if (status.isFavorited()) {
            SyncService.unfavorite(mContext, status.getId(), handler);
        } else {
            SyncService.favorite(mContext, status.getId(), handler);
        }
    }

    private void doCopy() {
        if (status != null) {
            IOHelper.copyToClipBoard(this, status.getText());
            Utils.notify(this, "消息内容已复制到剪贴板");
        }

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
            return AppContext.getApi().showStatus(params[0]);
        }

    }

}
