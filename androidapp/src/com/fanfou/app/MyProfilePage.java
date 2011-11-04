package com.fanfou.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fanfou.app.api.User;
import com.fanfou.app.cache.CacheManager;
import com.fanfou.app.cache.IImageLoader;
import com.fanfou.app.config.Commons;
import com.fanfou.app.service.ActionService;
import com.fanfou.app.ui.ActionBar;
import com.fanfou.app.ui.ActionManager;
import com.fanfou.app.util.DateTimeHelper;
import com.fanfou.app.util.StringHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.07.18
 * @version 1.2 2011.10.29
 * 
 */
public class MyProfilePage extends BaseActivity {

	private ScrollView mScrollView;
	private View mEmptyView;

	private ActionBar mActionBar;

	private ImageView mHead;
	private TextView mName;

	private ImageView mProtected;

	private TextView mDescription;

	private ViewGroup mStatusesView;
	private TextView mStatusesInfo;
	private ViewGroup mFavoritesView;
	private TextView mFavoritesInfo;
	private ViewGroup mFriendsView;
	private TextView mFriendsInfo;
	private ViewGroup mFollowersView;
	private TextView mFollowersInfo;
	private TextView mExtraInfo;

	private String userId;

	private User user;

	private Handler mHandler;
	private IImageLoader mLoader;

	private boolean isInitialized = false;
	private boolean isBusy = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (parseIntent()) {
			initialize();
			setLayout();
			initCheckState();
		} else {
			finish();
		}
	}

	private boolean parseIntent() {
		user = App.me.user;
		if (user != null && !user.isNull()) {
			userId = user.id;
		} else {
			userId = App.me.userId;
			user = CacheManager.getUser(this,userId);
		}
		if (StringHelper.isEmpty(userId)) {
			log("用户ID不能为空");
			return false;
		} else {
			return true;
		}
	}

	private void initialize() {
		mHandler = new Handler();
		mLoader = App.me.getImageLoader();
	}

	private void setLayout() {
		setContentView(R.layout.myprofile);

		setActionBar();

		mEmptyView = findViewById(R.id.empty);
		mScrollView = (ScrollView) findViewById(R.id.user_profile);

		mHead = (ImageView) findViewById(R.id.user_head);
		mName = (TextView) findViewById(R.id.user_name);
		TextPaint tp = mName.getPaint();
		tp.setFakeBoldText(true);
		mExtraInfo = (TextView) findViewById(R.id.user_extrainfo);

		mProtected = (ImageView) findViewById(R.id.user_protected);

		mDescription = (TextView) findViewById(R.id.user_description);

		mStatusesView = (ViewGroup) findViewById(R.id.user_statuses_view);
		mStatusesInfo = (TextView) findViewById(R.id.user_statuses);
		mFavoritesView = (ViewGroup) findViewById(R.id.user_favorites_view);
		mFavoritesInfo = (TextView) findViewById(R.id.user_favorites);
		mFriendsView = (ViewGroup) findViewById(R.id.user_friends_view);
		mFriendsInfo = (TextView) findViewById(R.id.user_friends);
		mFollowersView = (ViewGroup) findViewById(R.id.user_followers_view);
		mFollowersInfo = (TextView) findViewById(R.id.user_followers);

		mStatusesView.setOnClickListener(this);
		mFavoritesView.setOnClickListener(this);
		mFriendsView.setOnClickListener(this);
		mFollowersView.setOnClickListener(this);
	}

	/**
	 * 初始化和设置ActionBar
	 */
	private void setActionBar() {
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setTitle("我的空间");
		mActionBar.setRefreshEnabled(this);
		mActionBar.setLeftAction(new ActionBar.BackAction(mContext));
	}

	protected void initCheckState() {
		if (user != null) {
			showContent();
			updateUI();
		} else {
			doRefresh();
			showProgress();
		}
	}

	private void showProgress() {
		mScrollView.setVisibility(View.GONE);
		mEmptyView.setVisibility(View.VISIBLE);
	}

	private void showContent() {
		log("showContent()");
		isInitialized = true;
		mEmptyView.setVisibility(View.GONE);
		mScrollView.setVisibility(View.VISIBLE);
	}

	private void updateUI() {
		if (user == null) {
			return;
		}
		log("updateUI user.name=" + user.screenName);

		mLoader.set(user.profileImageUrl, mHead, R.drawable.default_head);
		mName.setText(user.screenName);

		mStatusesInfo.setText("" + user.statusesCount);
		mFavoritesInfo.setText("" + user.favouritesCount);
		mFriendsInfo.setText("" + user.friendsCount);
		mFollowersInfo.setText("" + user.followersCount);

		log("updateUI user.description=" + user.description);

		if (StringHelper.isEmpty(user.description)) {
			mDescription.setText("这家伙什么也没留下");
			mDescription.setGravity(Gravity.CENTER);
		} else {
			mDescription.setText(user.description);
		}

		setExtraInfo(user);

		if (user.protect) {
			mProtected.setVisibility(View.VISIBLE);
		} else {
			mProtected.setVisibility(View.GONE);
		}

	}

	private void setExtraInfo(User u) {
		if (u == null) {
			mExtraInfo.setVisibility(View.GONE);
			return;
		}

		StringBuffer sb = new StringBuffer();

		if (!StringHelper.isEmpty(user.gender)) {
			sb.append("性别：").append(user.gender).append("\n");
		}
		if (!StringHelper.isEmpty(user.birthday)) {
			sb.append("生日：").append(user.birthday).append("\n");
		}
		if (!StringHelper.isEmpty(user.location)) {
			sb.append("位置：").append(user.location).append("\n");
		}

		if (!StringHelper.isEmpty(user.url)) {
			sb.append("网站：").append(user.url).append("\n");
		}

		sb.append("注册时间：")
				.append(DateTimeHelper.formatDateOnly(user.createdAt));

		mExtraInfo.setText(sb.toString());

	}

	private void doRefresh() {
		ResultReceiver receiver = new MyResultReceiver();
		Intent intent = new Intent(this, ActionService.class);
		intent.putExtra(Commons.EXTRA_TYPE, Commons.ACTION_USER_SHOW);
		intent.putExtra(Commons.EXTRA_ID, userId);// 如果只传了ID就用这个
		intent.putExtra(Commons.EXTRA_RECEIVER, receiver);
		startService(intent);
		if (isInitialized) {
			startRefreshAnimation();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.user_statuses_view:
			ActionManager.doShowTimeline(this, user);
			break;
		case R.id.user_favorites_view:
			ActionManager.doShowFavorites(this, user);
			break;
		case R.id.user_friends_view:
			ActionManager.doShowFriends(this, user);
			break;
		case R.id.user_followers_view:
			ActionManager.doShowFollowers(this, user);
			break;
		case R.id.user_location_view:
			break;
		case R.id.user_site_view:
			break;
		default:
			break;
		}
		
	}
	
	@Override
	public void onRefreshClick() {
		if (isBusy) {
			return;
		}
		doRefresh();
	}

	private synchronized void setBusy(boolean busy) {
		isBusy = busy;
	}

	@Override
	protected void startRefreshAnimation() {
		setBusy(true);
		mActionBar.startAnimation();
	}

	@Override
	protected void stopRefreshAnimation() {
		setBusy(false);
		mActionBar.stopAnimation();
	}

	private class MyResultReceiver extends ResultReceiver {

		public MyResultReceiver() {
			super(mHandler);
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			switch (resultCode) {
			case Commons.RESULT_CODE_FINISH:
				if (resultData != null) {
					log("result ok, update ui");
					int type = resultData.getInt(Commons.EXTRA_TYPE);
					User result = (User) resultData
							.getSerializable(Commons.EXTRA_USER);
					if (result != null) {
						App.me.updateUserInfo(result);
						user = result;
					}
					if (!isInitialized) {
						showContent();
					}
					if (type == Commons.ACTION_USER_SHOW) {
						log("show result=" + user.id);
						updateUI();
						if (isInitialized) {
							stopRefreshAnimation();
						}
					}
				}
				break;
			case Commons.RESULT_CODE_ERROR:
				int type = resultData.getInt(Commons.EXTRA_TYPE);
				if (type == Commons.ACTION_USER_SHOW) {
					stopRefreshAnimation();
				}
				String msg = resultData.getString(Commons.EXTRA_ERROR_MESSAGE);
				Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
				log("result error");
				break;
			default:
				break;
			}
		}

	}

	private static final String tag = MyProfilePage.class.getSimpleName();

	private void log(String message) {
		Log.d(tag, message);
	}

}
