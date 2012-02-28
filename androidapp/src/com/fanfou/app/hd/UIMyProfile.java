package com.fanfou.app.hd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.fanfou.app.hd.cache.IImageLoader;
import com.fanfou.app.hd.dao.model.UserModel;
import com.fanfou.app.hd.service.Constants;
import com.fanfou.app.hd.service.FanFouService;
import com.fanfou.app.hd.util.DateTimeHelper;
import com.fanfou.app.hd.util.StringHelper;
import com.fanfou.app.hd.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.07.18
 * @version 1.2 2011.10.29
 * @version 1.3 2011.11.07
 * @version 1.4 2011.11.08
 * @version 1.5 2011.12.06
 * @version 1.6 2011.12.19
 * @version 2.0 2012.01.31
 * @version 2.1 2012.02.02
 * 
 */
public class UIMyProfile extends UIBaseSupport {

	private static final String TAG = UIMyProfile.class.getSimpleName();

	private ScrollView mScrollView;
	private View mEmptyView;

	private ImageView mHead;
	private TextView mName;

	private ImageView mProtected;

	private TextView mDescription;

	private ViewGroup mHeadView;

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

	private UserModel user;

	private IImageLoader mLoader;

	private boolean isInitialized = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private void parseIntent() {
		userId = App.getAccount();
//		user = CacheManager.getUser(this, userId);
	}

	@Override
	protected void initialize() {
		parseIntent();
		mLoader = App.getImageLoader();
	}

	@Override
	protected void setLayout() {
		setContentView(R.layout.myprofile);

		mEmptyView = findViewById(R.id.empty);
		mScrollView = (ScrollView) findViewById(R.id.user_profile);

		mHead = (ImageView) findViewById(R.id.user_head);
		mName = (TextView) findViewById(R.id.user_name);
		TextPaint tp = mName.getPaint();
		tp.setFakeBoldText(true);
		mExtraInfo = (TextView) findViewById(R.id.user_extrainfo);

		mProtected = (ImageView) findViewById(R.id.user_protected);

		mDescription = (TextView) findViewById(R.id.user_description);

		// mHeadView = (ViewGroup) findViewById(R.id.user_headview);

		mStatusesView = (ViewGroup) findViewById(R.id.user_statuses_view);
		mStatusesInfo = (TextView) findViewById(R.id.user_statuses);
		mFavoritesView = (ViewGroup) findViewById(R.id.user_favorites_view);
		mFavoritesInfo = (TextView) findViewById(R.id.user_favorites);
		mFriendsView = (ViewGroup) findViewById(R.id.user_friends_view);
		mFriendsInfo = (TextView) findViewById(R.id.user_friends);
		mFollowersView = (ViewGroup) findViewById(R.id.user_followers_view);
		mFollowersInfo = (TextView) findViewById(R.id.user_followers);

		// mHeadView.setOnClickListener(this);
		mStatusesView.setOnClickListener(this);
		mFavoritesView.setOnClickListener(this);
		mFriendsView.setOnClickListener(this);
		mFollowersView.setOnClickListener(this);

		initCheckState();
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
		if (App.DEBUG) {
			log("showContent()");
		}
		isInitialized = true;
		mEmptyView.setVisibility(View.GONE);
		mScrollView.setVisibility(View.VISIBLE);
	}

	private void updateUI() {
		if (user == null) {
			return;
		}

		if (App.DEBUG) {
			log("updateUI user.name=" + user.getScreenName());
		}

		mHead.setTag(user.getProfileImageUrl());
		mLoader.displayImage(user.getProfileImageUrl(), mHead,
				R.drawable.default_head);
		mName.setText(user.getScreenName());

		mStatusesInfo.setText("" + user.getStatusesCount());
		mFavoritesInfo.setText("" + user.getFavouritesCount());
		mFriendsInfo.setText("" + user.getFriendsCount());
		mFollowersInfo.setText("" + user.getFollowersCount());

		if (App.DEBUG) {
			log("updateUI user.description=" + user.getDescription());
		}

		if (StringHelper.isEmpty(user.getDescription())) {
			mDescription.setText("这家伙什么也没留下");
			mDescription.setGravity(Gravity.CENTER);
		} else {
			mDescription.setText(user.getDescription());
		}

		setExtraInfo(user);

		if (user.isProtect()) {
			mProtected.setVisibility(View.VISIBLE);
		} else {
			mProtected.setVisibility(View.GONE);
		}

	}

	private void setExtraInfo(UserModel u) {
		if (u == null) {
			mExtraInfo.setVisibility(View.GONE);
			return;
		}

		StringBuffer sb = new StringBuffer();

		if (!StringHelper.isEmpty(user.getGender())) {
			sb.append("性别：").append(user.getGender()).append("\n");
		}
		if (!StringHelper.isEmpty(user.getBirthday())) {
			sb.append("生日：").append(user.getBirthday()).append("\n");
		}
		if (!StringHelper.isEmpty(user.getLocation())) {
			sb.append("位置：").append(user.getLocation()).append("\n");
		}

		if (!StringHelper.isEmpty(user.getUrl())) {
			sb.append("网站：").append(user.getUrl()).append("\n");
		}

		sb.append("注册时间：")
				.append(DateTimeHelper.formatDateOnly(user.getTime()));

		mExtraInfo.setText(sb.toString());

	}

	private void doRefresh() {
		FanFouService.doProfile(this, userId, new ResultHandler());
	}

	private static final int REQUEST_CODE_UPDATE_PROFILE = 0;

	private static void goEditProfilePage(Activity context, final UserModel user) {
		Intent intent = new Intent(context, UIEditProfile.class);
		intent.putExtra("data", user);
		context.startActivityForResult(intent, REQUEST_CODE_UPDATE_PROFILE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE_UPDATE_PROFILE) {
				UserModel result = (UserModel) data
						.getParcelableExtra("data");
				if (result != null) {
					userId = user.getId();
					updateUI();
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (App.DEBUG) {
			Log.d(TAG, "onClick id=" + v.getId());
		}
		switch (v.getId()) {
		case R.id.user_statuses_view:
//			ActionManager.doShowTimeline(this, user);
			break;
		case R.id.user_favorites_view:
//			ActionManager.doShowFavorites(this, user);
			break;
		case R.id.user_friends_view:
//			ActionManager.doShowFriends(this, user);
			break;
		case R.id.user_followers_view:
//			ActionManager.doShowFollowers(this, user);
			break;
		default:
			break;
		}

	}

	private class ResultHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			int type = msg.arg1;
			switch (msg.what) {
			case Constants.RESULT_SUCCESS:
				if (msg.getData() != null) {
					if (App.DEBUG) {
						log("result ok, update ui");
					}
					UserModel result = (UserModel) msg.getData().getParcelable("data");
					if (result != null) {
//						App.getApp().updateUserInfo(result);
					}
					if (!isInitialized) {
						showContent();
					}
					if (type == FanFouService.USER_SHOW) {
						log("show result=" + user.getId());
						updateUI();
					}
				}
				break;
			case Constants.RESULT_ERROR:
				if (!isInitialized) {
					showContent();
				}
				String errorMessage = msg.getData().getString("error_message");
				Utils.notify(mContext, errorMessage);
				if (App.DEBUG) {
					log("result error");
				}
				break;
			default:
				break;
			}
		}

	}

	private static final String tag = UIMyProfile.class.getSimpleName();

	private void log(String message) {
		Log.d(tag, message);
	}

}
