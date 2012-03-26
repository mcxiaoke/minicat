package com.fanfou.app.hd.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.R;
import com.fanfou.app.hd.controller.EmptyViewController;
import com.fanfou.app.hd.controller.SimpleDialogListener;
import com.fanfou.app.hd.controller.UIController;
import com.fanfou.app.hd.dao.model.UserModel;
import com.fanfou.app.hd.dialog.ConfirmDialog;
import com.fanfou.app.hd.service.FanFouService;
import com.fanfou.app.hd.util.DateTimeHelper;
import com.fanfou.app.hd.util.StringHelper;
import com.fanfou.app.hd.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.07
 * @version 1.1 2012.02.22
 * @version 1.2 2012.02.24
 * @version 1.3 2012.03.02
 * @version 2.0 2012.03.06
 * @version 2.1 2012.03.07
 * @version 2.2 2012.03.08
 * @version 2.3 2012.03.13
 * @version 2.4 2012.03.14
 * @version 3.0 2012.03.20
 * 
 */
public class ProfileFragment extends AbstractFragment implements
		OnClickListener {
	private static final String TAG = ProfileFragment.class.getSimpleName();

	public static ProfileFragment newInstance(String userId) {
		Bundle args = new Bundle();
		args.putString("id", userId);
		ProfileFragment fragment = new ProfileFragment();
		fragment.setArguments(args);
		if (App.DEBUG) {
			Log.d(TAG, "newInstance() " + fragment);
		}
		return fragment;
	}

	private String userId;
	private UserModel user;

	private boolean noPermission = false;

	private EmptyViewController emptyController;

	private ViewGroup vContent;

	private View vEmpty;

	private ImageView headerImage;

	private TextView headerName;

	private TextView headerRelation;

	private TextView tvStatuses;

	private TextView tvFavorites;

	private TextView tvFriends;

	private TextView tvFollowers;

	private View vStatuses;
	private View vFavorites;
	private View vFriends;
	private View vFollowers;

	private TextView infoTitle;

	private TextView infoContent;

	private TextView descTitle;

	private TextView descContent;

	private ImageButton actionFollow;
	private ImageButton actionMention;
	private ImageButton actionDM;
	private ImageButton actionBlock;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		parseArguments(args);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fm_profile, container, false);
		findViews(root);
		return root;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setEmptyView();
		setListeners();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initResources();
		updateUI();

	}

	private void parseArguments(Bundle data) {
		user = data.getParcelable("data");
		if (user == null) {
			userId = data.getString("id");
			user = App.getUser(userId);
		} else {
			userId = user.getId();
		}
	}

	private void initResources() {
	}

	private void findViews(View root) {
		vContent = (ViewGroup) root.findViewById(R.id.container);
		vEmpty = root.findViewById(android.R.id.empty);

		headerImage = (ImageView) root.findViewById(R.id.header_image);
		headerName = (TextView) root.findViewById(R.id.header_name);
		headerRelation = (TextView) root.findViewById(R.id.header_relation);

		tvStatuses = (TextView) root.findViewById(R.id.count_statuses);
		tvFavorites = (TextView) root.findViewById(R.id.count_favorites);
		tvFriends = (TextView) root.findViewById(R.id.count_friends);
		tvFollowers = (TextView) root.findViewById(R.id.count_followers);

		vStatuses = root.findViewById(R.id.box_statuses);
		vFavorites = root.findViewById(R.id.box_favorites);
		vFriends = root.findViewById(R.id.box_friends);
		vFollowers = root.findViewById(R.id.box_followers);

		infoTitle = (TextView) root.findViewById(R.id.info_title);
		Utils.setBoldText(infoTitle);
		infoContent = (TextView) root.findViewById(R.id.info_content);

		descTitle = (TextView) root.findViewById(R.id.desc_title);
		Utils.setBoldText(descTitle);
		descContent = (TextView) root.findViewById(R.id.desc_content);

		actionFollow = (ImageButton) root.findViewById(R.id.action_follow);
		actionMention = (ImageButton) root.findViewById(R.id.action_mention);
		actionDM = (ImageButton) root.findViewById(R.id.action_dm);
		actionBlock = (ImageButton) root.findViewById(R.id.action_block);

	}

	private void setEmptyView() {
		emptyController = new EmptyViewController(vEmpty);

		if (user == null) {
			fetchUser();
			showProgress();
		} else {
			showContent();
		}
	}

	private void showEmptyView(String text) {
		vContent.setVisibility(View.GONE);
		emptyController.showEmpty(text);
	}

	private void showProgress() {
		vContent.setVisibility(View.GONE);
		emptyController.showProgress();
		if (App.DEBUG) {
			Log.d(TAG, "showProgress userId=" + userId);
		}
	}

	private void showContent() {
		emptyController.hideProgress();
		vContent.setVisibility(View.VISIBLE);
		if (App.DEBUG) {
			Log.d(TAG, "showContent userId=" + userId);
		}
	}

	private void setListeners() {
		vStatuses.setOnClickListener(this);
		vFavorites.setOnClickListener(this);
		vFriends.setOnClickListener(this);
		vFollowers.setOnClickListener(this);

		actionFollow.setOnClickListener(this);
		actionMention.setOnClickListener(this);
		actionDM.setOnClickListener(this);
		actionBlock.setOnClickListener(this);
	}

	private void updateUI(final UserModel user) {
		this.user = user;
		updateUI();
	}

	public void updateUI() {
		if (user == null) {
			return;
		}

		if (App.DEBUG) {
			Log.d(TAG, "updateUI() userid=" + userId);
			Log.d(TAG, "updateUI() user.following=" + user.isFollowing());
		}

		showContent();
		updatePermission();
		updateHeader();
		updateAction();
		updateStatistics();
		updateInfo();
		updateDescription();

		showRelation();

	}

	private void updatePermission() {
		if (user.getId().equals(App.getAccount())) {
			noPermission = false;
			return;
		}
		noPermission = user.isProtect() && !user.isFollowing();
	}

	private void updateHeader() {
		headerName.setText(user.getScreenName());

		String headerImageUrl = user.getProfileImageUrl();
		headerImage.setTag(headerImageUrl);
		App.getImageLoader().displayImage(headerImageUrl, headerImage,
				R.drawable.ic_head);

	}

	private void updateAction() {
		actionFollow.setImageLevel(user.isFollowing() ? 1 : 0);
	}

	private void updateStatistics() {
		tvStatuses.setText("" + user.getStatusesCount());
		tvFavorites.setText("" + user.getFavouritesCount());
		tvFriends.setText("" + user.getFriendsCount());
		tvFollowers.setText("" + user.getFollowersCount());
	}

	private void updateInfo() {

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

		infoContent.setText(sb.toString());

	}

	private void updateDescription() {
		descContent.setText(user.getDescription());
	}

	private void updateRelation(String relation) {
		headerRelation.setText(relation);
	}

	private void fetchUser() {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case FanFouService.RESULT_SUCCESS:
					UserModel result = msg.getData().getParcelable("data");
					if (result != null) {
						updateUI(result);
					}
					break;
				case FanFouService.RESULT_ERROR:
					String errorMessage = msg.getData().getString(
							"error_message");
					showEmptyView(errorMessage);
					break;
				default:
					break;
				}
			}
		};
		if (App.DEBUG) {
			Log.d(TAG, "showUser userId=" + userId);
		}
		FanFouService.showUser(getActivity(), userId, handler);
	}

	private void showRelation() {

		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case FanFouService.RESULT_SUCCESS:
					boolean follow = msg.getData().getBoolean("boolean");
					updateRelation(follow ? "正在关注你" : "没有关注你");
					break;
				case FanFouService.RESULT_ERROR:
					break;
				default:
					break;
				}
			}
		};
		FanFouService.showRelation(getActivity(), user.getId(),
				App.getAccount(), handler);
	}

	private void doFollow() {
		if (user == null) {
			return;
		}

		if (user.isFollowing()) {
			unfollow();
		} else {
			follow();
		}

	}

	private void follow() {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case FanFouService.RESULT_SUCCESS:
					if (App.DEBUG) {
						Log.d(TAG, "follow success");
					}
					user.setFollowing(true);
					updateAction();
					Utils.notify(getActivity(), "关注成功");
					break;
				case FanFouService.RESULT_ERROR:
					if (App.DEBUG) {
						Log.d(TAG, "follow error");
					}
					String errorMessage = msg.getData().getString(
							"error_message");
					Utils.notify(getActivity(), errorMessage);
					updateAction();
					break;
				default:
					break;
				}
			}
		};
		FanFouService.follow(getActivity(), user.getId(), handler);
	}

	private void unfollow() {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case FanFouService.RESULT_SUCCESS:
					if (App.DEBUG) {
						Log.d(TAG, "unfollow success");
					}
					user.setFollowing(false);
					updateAction();
					Utils.notify(getActivity(), "已取消关注");
					break;
				case FanFouService.RESULT_ERROR:
					if (App.DEBUG) {
						Log.d(TAG, "unfollow error");
					}
					String errorMessage = msg.getData().getString(
							"error_message");
					Utils.notify(getActivity(), errorMessage);
					updateAction();
					break;
				default:
					break;
				}
			}
		};
		
		
		final ConfirmDialog dialog = new ConfirmDialog(getActivity());
		dialog.setTitle("提示");
		dialog.setMessage("要取消关注" + user.getScreenName() + "吗？");
		dialog.setClickListener(new SimpleDialogListener() {

			@Override
			public void onPositiveClick() {
				super.onPositiveClick();
				FanFouService.unFollow(getActivity(), user.getId(), handler);
			}
		});
		dialog.show();
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
		if (App.DEBUG) {
			Log.d(TAG,
					"OnClick() view=" + v.getId() + " user.id=" + user.getId()
							+ " noPermission=" + noPermission);
		}
		switch (v.getId()) {
		case R.id.action_follow:
			doFollow();
			break;
		case R.id.action_mention:
			UIController.showWrite(getActivity(), "@" + user.getScreenName()
					+ " ");
			break;
		case R.id.action_dm:
			UIController.showConversation(getActivity(), user);
			break;
		case R.id.action_block:
			break;
		case R.id.box_statuses:
			// if (hasPermission()) {
			// UIController.showTimeline(getActivity(), user.getId());
			// }
			break;
		case R.id.box_favorites:
			if (hasPermission()) {
				UIController.showFavorites(getActivity(), user.getId());
			}
			break;
		case R.id.box_friends:
			if (hasPermission()) {
				UIController.showFriends(getActivity(), user.getId());
			}
			break;
		case R.id.box_followers:
			if (hasPermission()) {
				UIController.showFollowers(getActivity(), user.getId());
			}
			break;
		default:
			break;
		}
	}

	@Override
	public String getTitle() {
		return "资料";
	}

}
