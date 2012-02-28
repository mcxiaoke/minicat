package com.fanfou.app.hd.fragments;

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

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.R;
import com.fanfou.app.hd.cache.IImageLoader;
import com.fanfou.app.hd.dao.model.UserModel;
import com.fanfou.app.hd.dialog.ConfirmDialog;
import com.fanfou.app.hd.service.Constants;
import com.fanfou.app.hd.service.FanFouService;
import com.fanfou.app.hd.util.DateTimeHelper;
import com.fanfou.app.hd.util.OptionHelper;
import com.fanfou.app.hd.util.StringHelper;
import com.fanfou.app.hd.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.07
 * @version 1.1 2012.02.22
 * @version 1.2 2012.02.24
 * 
 */
public class ProfileContentFragment extends AbstractFragment implements
		OnClickListener {
	private static final String TAG = ProfileContentFragment.class
			.getSimpleName();

	public static ProfileContentFragment newInstance(String userId) {
		Bundle args = new Bundle();
		args.putString("id", userId);
		ProfileContentFragment fragment = new ProfileContentFragment();
		fragment.setArguments(args);
		if (App.DEBUG) {
			Log.d(TAG, "newInstance() " + fragment);
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

	private UserModel user;

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
		String action = data.getString("type");
		if (action == null) {
			userId = data.getString("id");
			user = (UserModel) data.getParcelable("data");
			if (user != null) {
				userId = user.getId();
			}
		} else if (action.equals(Intent.ACTION_VIEW)) {
			Uri uri = data.getParcelable("data");
			if (uri != null) {
				userId = uri.getLastPathSegment();
			}
		}

		if (user != null) {
			userId = user.getId();
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

	private void updateUI(final UserModel user) {
		this.user = user;
		updateUI();
	}

	@Override
	public void updateUI() {
		if (user == null) {
			mScrollView.setVisibility(View.GONE);
			return;
		}

		noPermission = !user.isFollowing() && user.isProtect();

		if (App.DEBUG)
			Log.d(TAG, "updateUI user.name=" + user.getScreenName());

		boolean textMode = OptionHelper.readBoolean(getActivity(),
				R.string.option_text_mode, false);
		if (textMode) {
			mHead.setVisibility(View.GONE);
		} else {
			mHead.setTag(user.getProfileImageUrl());
			mLoader.displayImage(user.getProfileImageUrl(), mHead,
					R.drawable.default_head);
		}

		mName.setText(user.getScreenName());

		String prefix;

		if (user.getGender().equals("男")) {
			prefix = "他";
		} else if (user.getGender().equals("女")) {
			prefix = "她";
		} else {
			prefix = "TA";
		}

		mStatusesTitle.setText(prefix + "的消息");
		mFavoritesTitle.setText(prefix + "的收藏");
		mFriendsTitle.setText(prefix + "关注的人");
		mFollowersTitle.setText("关注" + prefix + "的人");

		mStatusesInfo.setText("" + user.getStatusesCount());
		mFavoritesInfo.setText("" + user.getFavouritesCount());
		mFriendsInfo.setText("" + user.getFriendsCount());
		mFollowersInfo.setText("" + user.getFollowersCount());
		if (App.DEBUG)
			Log.d(TAG, "updateUI user.description=" + user.getDescription());

		if (StringHelper.isEmpty(user.getDescription())) {
			mDescription.setText("这家伙什么也没留下");
			mDescription.setGravity(Gravity.CENTER);
		} else {
			mDescription.setText(user.getDescription());
		}

		mProtected.setVisibility(user.isProtect() ? View.VISIBLE : View.GONE);

		setExtraInfo(user);
		updateFollowState(user.isFollowing());

		if (!noPermission) {
			doFetchRelationshipInfo();
		}

		mScrollView.setVisibility(View.VISIBLE);
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

	private void doFetchRelationshipInfo() {
		FanFouService.doFriendshipsExists(getActivity(), user.getId(),
				App.getAccount(), new ResultHandler(this));
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
		if (user == null) {
			return;
		}

		if (user.isFollowing()) {
			final ConfirmDialog dialog = new ConfirmDialog(getActivity(),
					"取消关注", "要取消关注" + user.getScreenName() + "吗？");
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
		if (user == null) {
			return;
		}
		switch (v.getId()) {
		case R.id.user_action_reply:
//			ActionManager.doWrite(getActivity(), "@" + user.getScreenName()
//					+ " ");
			break;
		case R.id.user_action_message:
//			ActionManager.doMessage(getActivity(), user);
			break;
		case R.id.user_action_follow:
			doFollow();
			break;
		case R.id.user_statuses_view:
			if (hasPermission()) {
//				ActionManager.doShowTimeline(getActivity(), user);
			}
			break;
		case R.id.user_favorites_view:
			if (hasPermission()) {
//				ActionManager.doShowFavorites(getActivity(), user);
			}
			break;
		case R.id.user_friends_view:
			if (hasPermission()) {
//				ActionManager.doShowFriends(getActivity(), user);
			}
			break;
		case R.id.user_followers_view:
			if (hasPermission()) {
//				ActionManager.doShowFollowers(getActivity(), user);
			}
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
			UserModel result = (UserModel) bundle.getParcelable("data");
			switch (msg.what) {
			case Constants.RESULT_SUCCESS:
				if (bundle != null) {
					if (App.DEBUG)
						Log.d(TAG, "result ok, update ui");

					if (result != null) {
						fragment.updateUI(result);
					}

					if (type == FanFouService.USER_SHOW) {
						if (App.DEBUG)
							Log.d(TAG, "show result=" + result.getId());
						fragment.updateUI();

					} else if (type == FanFouService.FRIENDSHIPS_EXISTS) {
						boolean follow = bundle.getBoolean("boolean");
						if (App.DEBUG)
							Log.d(TAG, "user relationship result=" + follow);
						fragment.updateRelationshipState(follow);
					} else if (type == FanFouService.USER_FOLLOW
							|| type == FanFouService.USER_UNFOLLOW) {
						if (App.DEBUG)
							Log.d(TAG, "user.following=" + result.isFollowing());
						fragment.updateFollowState(result.isFollowing());
						Utils.notify(fragment.getActivity(),
								result.isFollowing() ? "关注成功" : "取消关注成功");
					}
				}
				break;
			case Constants.RESULT_ERROR:
				if (type == FanFouService.USER_SHOW) {
					if (App.DEBUG)
						Log.d(TAG, "show result=" + result.getId());
					fragment.updateUI();
				} else if (type == FanFouService.FRIENDSHIPS_EXISTS) {
					return;
				}
				String errorMessage = bundle.getString("error_message");
				Utils.notify(fragment.getActivity(), errorMessage);
				break;
			default:
				break;
			}
		}

	}

}
