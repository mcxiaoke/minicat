package org.mcxiaoke.fancooker.fragments;

import org.mcxiaoke.fancooker.AppContext;
import org.mcxiaoke.fancooker.R;
import org.mcxiaoke.fancooker.controller.CacheController;
import org.mcxiaoke.fancooker.controller.EmptyViewController;
import org.mcxiaoke.fancooker.controller.SimpleDialogListener;
import org.mcxiaoke.fancooker.controller.UIController;
import org.mcxiaoke.fancooker.dao.model.UserModel;
import org.mcxiaoke.fancooker.dialog.ConfirmDialog;
import org.mcxiaoke.fancooker.service.FanFouService;
import org.mcxiaoke.fancooker.ui.widget.OnActionClickListener;
import org.mcxiaoke.fancooker.util.DateTimeHelper;
import org.mcxiaoke.fancooker.util.StringHelper;
import org.mcxiaoke.fancooker.util.Utils;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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
		if (AppContext.DEBUG) {
			Log.d(TAG, "newInstance() " + fragment);
		}
		return fragment;
	}

	private String userId;
	private UserModel user;

	private boolean noPermission = false;
	private String relationState;

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

	private Button actionFollow;
	private ImageButton actionOthers;
	private View stateView;

	private OnActionClickListener mOnActionClickListener;

	// private ImageButton actionMention;
	// private ImageButton actionDM;
	// private ImageButton actionBlock;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parseArguments();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mOnActionClickListener = (OnActionClickListener) activity;
		} catch (Exception e) {
		}
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
		setListeners();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getActivity().setTitle("个人空间");
		initResources();
		checkRefresh();

	}


	private void parseArguments() {
		Bundle data = getArguments();
		user = data.getParcelable("data");
		if (user == null) {
			userId = data.getString("id");
		} else {
			userId = user.getId();
		}
	}

	private void initResources() {
	}

	private void findViews(View root) {
		vContent = (ViewGroup) root.findViewById(R.id.container);
		vEmpty = root.findViewById(android.R.id.empty);
		emptyController = new EmptyViewController(vEmpty);

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

		stateView = root.findViewById(R.id.state);
		if (userId.equals(AppContext.getAccount())) {
			stateView.setVisibility(View.GONE);
		}
		actionOthers = (ImageButton) root.findViewById(R.id.action_others);
		registerForContextMenu(actionOthers);

		actionFollow = (Button) root.findViewById(R.id.action_follow);
		// actionMention = (ImageButton)
		// root.findViewById(R.id.action_mention);
		// actionDM = (ImageButton) root.findViewById(R.id.action_dm);
		// actionBlock = (ImageButton) root.findViewById(R.id.action_block);

	}

	private void checkRefresh() {
		if (user == null) {
			user = CacheController.getUserAndCache(userId, getActivity());
		}
		if (user == null) {
			fetchUser();
			showProgress();
		} else {
			showContent();
			updateUI();
		}
	}

	private void showEmptyView(String text) {
		vContent.setVisibility(View.GONE);
		emptyController.showEmpty(text);
	}

	private void showProgress() {
		vContent.setVisibility(View.GONE);
		emptyController.showProgress();
		if (AppContext.DEBUG) {
			Log.d(TAG, "showProgress userId=" + userId);
		}
	}

	private void showContent() {
		emptyController.hideProgress();
		vContent.setVisibility(View.VISIBLE);
		if (AppContext.DEBUG) {
			Log.d(TAG, "showContent userId=" + userId);
		}
	}

	private void setListeners() {
		vStatuses.setOnClickListener(this);
		vFavorites.setOnClickListener(this);
		vFriends.setOnClickListener(this);
		vFollowers.setOnClickListener(this);

		actionOthers.setOnClickListener(this);
		actionFollow.setOnClickListener(this);
		// actionMention.setOnClickListener(this);
		// actionDM.setOnClickListener(this);
		// actionBlock.setOnClickListener(this);
	}

	private void updateUI(final UserModel user) {
		this.user = user;
		updateUI();
	}

	@Override
	public void updateUI() {
		if (user == null) {
			return;
		}

		if (AppContext.DEBUG) {
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

		
		updateRelation();

	}

	private void updatePermission() {
		if (user.getId().equals(AppContext.getAccount())) {
			noPermission = false;
			return;
		}
		noPermission = user.isProtect() && !user.isFollowing();
	}

	private void updateHeader() {
		headerName.setText(user.getScreenName());

		String headerImageUrl = user.getProfileImageUrl();
		ImageLoader.getInstance().displayImage(headerImageUrl, headerImage);

	}

	private void updateAction() {
		boolean following = user.isFollowing();
		actionFollow
				.setBackgroundResource(following ? R.drawable.follow_state_following
						: R.drawable.follow_state_notfollowing);
		actionFollow.setText(following ? "正在关注" : "没有关注");
		// actionFollow.setImageLevel(user.isFollowing() ? 1 : 0);
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

	private void updateRelation() {
		if(TextUtils.isEmpty(relationState)){
			showRelation();
		}else{
			headerRelation.setText(relationState);
		}
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
		if (AppContext.DEBUG) {
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
					relationState=follow ? "正在关注你" : "没有关注你";
					updateRelation();
					break;
				case FanFouService.RESULT_ERROR:
					break;
				default:
					break;
				}
			}
		};
		FanFouService.showRelation(getActivity(), user.getId(),
				AppContext.getAccount(), handler);
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
					if (AppContext.DEBUG) {
						Log.d(TAG, "follow success");
					}
					user.setFollowing(true);
					updateAction();
					Utils.notify(getActivity(), "关注成功");
					break;
				case FanFouService.RESULT_ERROR:
					if (AppContext.DEBUG) {
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
					if (AppContext.DEBUG) {
						Log.d(TAG, "unfollow success");
					}
					user.setFollowing(false);
					updateAction();
					Utils.notify(getActivity(), "已取消关注");
					break;
				case FanFouService.RESULT_ERROR:
					if (AppContext.DEBUG) {
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

	private void showOthers() {
		actionOthers.showContextMenu();
	}

	private boolean hasPermission() {
		if (noPermission) {
			Utils.notify(getActivity(), "你没有通过这个用户的验证");
			return false;
		}
		return true;
	}

	private void doSendDirectMessage() {
		UIController.showConversation(getActivity(), user, false);
	}

	private void doRefreshProfile() {
		fetchUser();
	}

	private void doBlockUser() {
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		// return super.onContextItemSelected(item);
		int id = item.getItemId();
		switch (id) {
		case R.id.menu_dm:
			doSendDirectMessage();
			break;
		case R.id.menu_refresh:
			doRefreshProfile();
			break;
		case R.id.menu_block:
			doBlockUser();
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		android.view.MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.profile_context_menu, menu);
	}

	@Override
	public void onClick(View v) {
		if (user == null) {
			return;
		}
		if (AppContext.DEBUG) {
			Log.d(TAG,
					"OnClick() view=" + v.getId() + " user.id=" + user.getId()
							+ " noPermission=" + noPermission);
		}
		switch (v.getId()) {
		case R.id.action_follow:
			doFollow();
			break;
		case R.id.action_others:
			showOthers();
			break;
		case R.id.action_mention:
			UIController.showWrite(getActivity(), "@" + user.getScreenName()
					+ " ");
			break;
		case R.id.action_dm:
			UIController.showConversation(getActivity(), user, false);
			break;
		case R.id.action_block:
			break;
		case R.id.box_statuses:
			if (hasPermission()) {
				mOnActionClickListener.onActionClick(2, "timeline");
				// UIController.showTimeline(getActivity(), user.getId());
			}
			break;
		case R.id.box_favorites:
			if (hasPermission()) {
				mOnActionClickListener.onActionClick(0, "favorites");
				// UIController.showFavorites(getActivity(), user.getId());
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
