package com.fanfou.app.hd.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.api.User;
import com.fanfou.app.cache.CacheManager;
import com.fanfou.app.cache.IImageLoader;
import com.fanfou.app.dialog.ConfirmDialog;
import com.fanfou.app.service.Constants;
import com.fanfou.app.service.FanFouService;
import com.fanfou.app.ui.ActionManager;
import com.fanfou.app.util.DateTimeHelper;
import com.fanfou.app.util.OptionHelper;
import com.fanfou.app.util.StringHelper;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.07
 * 
 */
public class ProfileContentFragment extends AbstractFragment implements
		OnClickListener {
	private static final String TAG = ProfileContentFragment.class
			.getSimpleName();
	
	public static ProfileContentFragment newInstance(String userId) {
		Bundle args = new Bundle();
		args.putString(Constants.EXTRA_ID, userId);
		ProfileContentFragment fragment = new ProfileContentFragment();
		fragment.setArguments(args);
		if (App.DEBUG) {
			Log.d(TAG, "newInstance() "+fragment);
		}
		return fragment;
	}

	private View root;

	private ScrollView mScrollView;
	private ImageView mHead;
	private TextView mName;

	private ImageView mProtected;
	private TextView mRelationship;
	private ImageView mReplyAction;
	private ImageView mMessageAction;
	private ImageView mFollowAction;

	private TextView mDescription;

	private ViewGroup mStatusesView;
	private TextView mStatusesTitle;
	private TextView mStatusesInfo;

	private ViewGroup mFavoritesView;
	private TextView mFavoritesTitle;
	private TextView mFavoritesInfo;

	private ViewGroup mFriendsView;
	private TextView mFriendsTitle;
	private TextView mFriendsInfo;

	private ViewGroup mFollowersView;
	private TextView mFollowersTitle;
	private TextView mFollowersInfo;

	private TextView mExtraInfo;

	private String userId;

	private User user;

	private IImageLoader mLoader;

	private boolean noPermission = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		parseArguments(args);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.fm_profile, container, false);
		setLayout();
		return root;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mLoader = App.getImageLoader();
		updateUI();

	}

	private void parseArguments(Bundle data) {
		String action = data.getString(Constants.EXTRA_TYPE);
		if (action == null) {
			userId = data.getString(Constants.EXTRA_ID);
			user = (User) data.getParcelable(Constants.EXTRA_DATA);
			if (user != null) {
				userId = user.id;
			}
		} else if (action.equals(Intent.ACTION_VIEW)) {
			Uri uri = data.getParcelable(Constants.EXTRA_DATA);
			if (uri != null) {
				userId = uri.getLastPathSegment();
			}
		}
		if (user == null && userId != null) {
			user = CacheManager.getUser(getActivity(), userId);
		}

		if (user != null) {
			userId = user.id;
		}
		//
		// if (App.getUserId().equals(userId)) {
		// ActionManager.doMyProfile(getActivity());
		// }

	}

	private void setLayout() {
		mScrollView = (ScrollView) root.findViewById(R.id.user_profile);

		mHead = (ImageView) root.findViewById(R.id.user_head);
		mName = (TextView) root.findViewById(R.id.user_name);
		TextPaint tp = mName.getPaint();
		tp.setFakeBoldText(true);

		mExtraInfo = (TextView) root.findViewById(R.id.user_extrainfo);

		mProtected = (ImageView) root.findViewById(R.id.user_protected);

		mRelationship = (TextView) root.findViewById(R.id.user_relationship);

		mDescription = (TextView) root.findViewById(R.id.user_description);

		mReplyAction = (ImageView) root.findViewById(R.id.user_action_reply);
		mMessageAction = (ImageView) root
				.findViewById(R.id.user_action_message);
		mFollowAction = (ImageView) root.findViewById(R.id.user_action_follow);

		mStatusesView = (ViewGroup) root.findViewById(R.id.user_statuses_view);
		mStatusesTitle = (TextView) root.findViewById(R.id.user_statuses_title);
		mStatusesInfo = (TextView) root.findViewById(R.id.user_statuses);

		mFavoritesView = (ViewGroup) root
				.findViewById(R.id.user_favorites_view);
		mFavoritesTitle = (TextView) root
				.findViewById(R.id.user_favorites_title);
		mFavoritesInfo = (TextView) root.findViewById(R.id.user_favorites);

		mFriendsView = (ViewGroup) root.findViewById(R.id.user_friends_view);
		mFriendsTitle = (TextView) root.findViewById(R.id.user_friends_title);
		mFriendsInfo = (TextView) root.findViewById(R.id.user_friends);

		mFollowersView = (ViewGroup) root
				.findViewById(R.id.user_followers_view);
		mFollowersTitle = (TextView) root
				.findViewById(R.id.user_followers_title);
		mFollowersInfo = (TextView) root.findViewById(R.id.user_followers);

		mStatusesView.setOnClickListener(this);
		mFavoritesView.setOnClickListener(this);
		mFriendsView.setOnClickListener(this);
		mFollowersView.setOnClickListener(this);

		mReplyAction.setOnClickListener(this);
		mMessageAction.setOnClickListener(this);
		mFollowAction.setOnClickListener(this);

		mScrollView.setVisibility(View.GONE);
	}

	private void updateUI(final User user) {
		this.user = user;
		updateUI();
	}

	@Override
	public void updateUI() {
		if (user == null) {
			mScrollView.setVisibility(View.GONE);
			return;
		}

		noPermission = !user.following && user.protect;

		if (App.DEBUG)
			Log.d(TAG, "updateUI user.name=" + user.screenName);

		boolean textMode = OptionHelper.readBoolean(getActivity(),
				R.string.option_text_mode, false);
		if (textMode) {
			mHead.setVisibility(View.GONE);
		} else {
			mHead.setTag(user.profileImageUrl);
			mLoader.displayImage(user.profileImageUrl, mHead,
					R.drawable.default_head);
		}

		mName.setText(user.screenName);

		String prefix;

		if (user.gender.equals("男")) {
			prefix = "他";
		} else if (user.gender.equals("女")) {
			prefix = "她";
		} else {
			prefix = "TA";
		}

		mStatusesTitle.setText(prefix + "的消息");
		mFavoritesTitle.setText(prefix + "的收藏");
		mFriendsTitle.setText(prefix + "关注的人");
		mFollowersTitle.setText("关注" + prefix + "的人");

		mStatusesInfo.setText("" + user.statusesCount);
		mFavoritesInfo.setText("" + user.favouritesCount);
		mFriendsInfo.setText("" + user.friendsCount);
		mFollowersInfo.setText("" + user.followersCount);
		if (App.DEBUG)
			Log.d(TAG, "updateUI user.description=" + user.description);

		if (StringHelper.isEmpty(user.description)) {
			mDescription.setText("这家伙什么也没留下");
			mDescription.setGravity(Gravity.CENTER);
		} else {
			mDescription.setText(user.description);
		}

		mProtected.setVisibility(user.protect ? View.VISIBLE : View.GONE);

		setExtraInfo(user);
		updateFollowState(user.following);

		if (!noPermission) {
			doFetchRelationshipInfo();
		}

		mScrollView.setVisibility(View.VISIBLE);
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

	private void doFetchRelationshipInfo() {
		FanFouService.doFriendshipsExists(getActivity(), user.id,
				App.getUserId(), new ResultHandler(this));
	}

	private void updateFollowState(boolean following) {
		mFollowAction.setImageResource(following ? R.drawable.btn_unfollow
				: R.drawable.btn_follow);
	}

	private void updateRelationshipState(boolean follow) {
		mRelationship.setVisibility(View.VISIBLE);
		mRelationship.setText(follow ? "(此用户正在关注你)" : "(此用户没有关注你)");
	}

	private void doFollow() {
		if (user == null || user.isNull()) {
			return;
		}

		if (user.following) {
			final ConfirmDialog dialog = new ConfirmDialog(getActivity(),
					"取消关注", "要取消关注" + user.screenName + "吗？");
			dialog.setClickListener(new ConfirmDialog.AbstractClickHandler() {

				@Override
				public void onButton1Click() {
					updateFollowState(false);
					FanFouService.doFollow(getActivity(), user,
							new ResultHandler(ProfileContentFragment.this));
				}
			});

			dialog.show();
		} else {
			updateFollowState(true);
			FanFouService
					.doFollow(getActivity(), user, new ResultHandler(this));
		}

	}

	private boolean hasPermission() {
		if (noPermission) {
			Utils.notify(getActivity(), "你没有通过这个用户的验证");
			return false;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		if (user == null || user.isNull()) {
			return;
		}
		switch (v.getId()) {
		case R.id.user_action_reply:
			ActionManager.doWrite(getActivity(), "@" + user.screenName + " ");
			break;
		case R.id.user_action_message:
			ActionManager.doMessage(getActivity(), user);
			break;
		case R.id.user_action_follow:
			doFollow();
			break;
		case R.id.user_statuses_view:
			if (hasPermission()) {
				ActionManager.doShowTimeline(getActivity(), user);
			}
			break;
		case R.id.user_favorites_view:
			if (hasPermission()) {
				ActionManager.doShowFavorites(getActivity(), user);
			}
			break;
		case R.id.user_friends_view:
			if (hasPermission()) {
				ActionManager.doShowFriends(getActivity(), user);
			}
			break;
		case R.id.user_followers_view:
			if (hasPermission()) {
				ActionManager.doShowFollowers(getActivity(), user);
			}
			break;
		case R.id.user_location_view:
			break;
		case R.id.user_site_view:
			break;
		default:
			break;
		}
	}

	private static class ResultHandler extends Handler {
		private ProfileContentFragment fragment;

		public ResultHandler(ProfileContentFragment fragment) {
			this.fragment = fragment;
		}

		@Override
		public void handleMessage(Message msg) {
			int type = msg.arg1;
			Bundle bundle = msg.getData();
			User result = (User) bundle.getParcelable(Constants.EXTRA_DATA);
			switch (msg.what) {
			case Constants.RESULT_SUCCESS:
				if (bundle != null) {
					if (App.DEBUG)
						Log.d(TAG, "result ok, update ui");

					if (result != null) {
						fragment.updateUI(result);
					}

					if (type == Constants.TYPE_USERS_SHOW) {
						if (App.DEBUG)
							Log.d(TAG, "show result=" + result.id);
						fragment.updateUI();

					} else if (type == Constants.TYPE_FRIENDSHIPS_EXISTS) {
						boolean follow = bundle
								.getBoolean(Constants.EXTRA_BOOLEAN);
						if (App.DEBUG)
							Log.d(TAG, "user relationship result=" + follow);
						fragment.updateRelationshipState(follow);
					} else if (type == Constants.TYPE_FRIENDSHIPS_CREATE
							|| type == Constants.TYPE_FRIENDSHIPS_DESTROY) {
						if (App.DEBUG)
							Log.d(TAG, "user.following=" + result.following);
						fragment.updateFollowState(result.following);
						Utils.notify(fragment.getActivity(),
								result.following ? "关注成功" : "取消关注成功");
					}
				}
				break;
			case Constants.RESULT_ERROR:
				if (type == Constants.TYPE_USERS_SHOW) {
					if (App.DEBUG)
						Log.d(TAG, "show result=" + result.id);
					fragment.updateUI();
				} else if (type == Constants.TYPE_FRIENDSHIPS_EXISTS) {
					return;
				} else if (type == Constants.TYPE_FRIENDSHIPS_CREATE
						|| type == Constants.TYPE_FRIENDSHIPS_DESTROY) {
					fragment.updateFollowState(result.following);
				}

				String errorMessage = bundle.getString(Constants.EXTRA_ERROR);
				Utils.notify(fragment.getActivity(), errorMessage);
				break;
			default:
				break;
			}
		}

	}

}
