package com.fanfou.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fanfou.app.api.User;
import com.fanfou.app.cache.CacheManager;
import com.fanfou.app.cache.IImageLoader;
import com.fanfou.app.config.Actions;
import com.fanfou.app.config.Commons;
import com.fanfou.app.dialog.ConfirmDialog;
import com.fanfou.app.service.ActionService;
import com.fanfou.app.ui.ActionBar;
import com.fanfou.app.ui.ActionBar.AbstractAction;
import com.fanfou.app.ui.ActionManager;
import com.fanfou.app.util.DateTimeHelper;
import com.fanfou.app.util.OptionHelper;
import com.fanfou.app.util.StringHelper;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.07.18
 * @version 1.1 2011.10.25
 * @version 1.2 2011.10.27
 * @version 1.3 2011.10.28
 * @version 1.4 2011.10.29
 * @version 1.5 2011.11.11
 * 
 */
public class ProfilePage extends BaseActivity {

	private ScrollView mScrollView;
	private View mEmptyView;

	private ActionBar mActionBar;

	private RelativeLayout mHeader;
	private ImageView mHead;
	private TextView mName;

	private ImageView mProtected;
	private TextView mRelationship;

	private LinearLayout mActions;
	private ImageView mReplyAction;
	private ImageView mMessageAction;
	private ImageView mFollowAction;

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
	private boolean noPermission = false;
	private boolean isBusy = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parseIntent();
		initialize();
		setLayout();
		initCheckState();
	}

	private void parseIntent() {
		Intent intent = getIntent();
		String action = intent.getAction();
		if (action == null) {
			userId = intent.getStringExtra(Commons.EXTRA_ID);
			user = (User) intent.getParcelableExtra(Commons.EXTRA_USER);
			if (user != null) {
				userId = user.id;
			}
		} else if (action.equals(Actions.ACTION_PROFILE)) {
			Bundle extras = intent.getExtras();
			if (extras != null) {
				userId = extras.getString(Commons.EXTRA_ID);
				user = (User) extras.getParcelable(Commons.EXTRA_USER);
				if (user != null) {
					userId = user.id;
				}
			}
		} else if (action.equals(Intent.ACTION_VIEW)) {
			Uri data = intent.getData();
			if (data != null) {
				userId = data.getLastPathSegment();
			}
		}
		if (user == null && userId != null) {
			user = CacheManager.getUser(this, userId);
		}
		if (StringHelper.isEmpty(userId)) {
			if (App.DEBUG)
				log("用户ID不能为空");
			finish();
		}

	}

	private void initialize() {
		mHandler = new Handler();
		mLoader = App.me.getImageLoader();
	}

	private void setLayout() {
		setContentView(R.layout.profile);

		setActionBar();

		mEmptyView = findViewById(R.id.empty);
		mScrollView = (ScrollView) findViewById(R.id.user_profile);

		mHeader = (RelativeLayout) findViewById(R.id.user_headview);
		mHead = (ImageView) findViewById(R.id.user_head);
		mName = (TextView) findViewById(R.id.user_name);
		TextPaint tp = mName.getPaint();
		tp.setFakeBoldText(true);

		mExtraInfo = (TextView) findViewById(R.id.user_extrainfo);

		mProtected = (ImageView) findViewById(R.id.user_protected);

		mRelationship = (TextView) findViewById(R.id.user_relationship);

		mDescription = (TextView) findViewById(R.id.user_description);

		mActions = (LinearLayout) findViewById(R.id.user_actionview);
		mReplyAction = (ImageView) findViewById(R.id.user_action_reply);
		mMessageAction = (ImageView) findViewById(R.id.user_action_message);
		mFollowAction = (ImageView) findViewById(R.id.user_action_follow);

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

		mReplyAction.setOnClickListener(this);
		mMessageAction.setOnClickListener(this);
		mFollowAction.setOnClickListener(this);

		mScrollView.setVisibility(View.GONE);
	}

	/**
	 * 初始化和设置ActionBar
	 */
	private void setActionBar() {
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setTitle("我的空间");
		mActionBar.setRightAction(new WriteAction());
	}

	private class WriteAction extends AbstractAction {

		public WriteAction() {
			super(R.drawable.i_write);
		}

		@Override
		public void performAction(View view) {
			ActionManager.doWrite(mContext);

		}
	}

	protected void initCheckState() {
		if (user != null) {
			showContent();
			updateUI();
		} else {
			doRefresh();
			mEmptyView.setVisibility(View.VISIBLE);
		}
	}

	private void showContent() {
		if (App.DEBUG)
			log("showContent()");
		isInitialized = true;
		mEmptyView.setVisibility(View.GONE);
		mScrollView.setVisibility(View.VISIBLE);

	}

	private void updateUI() {
		if (user == null) {
			return;
		}
		noPermission = !user.following && user.protect;

		if (App.DEBUG)
			log("updateUI user.name=" + user.screenName);

		boolean textMode = OptionHelper.readBoolean(this,
				R.string.option_text_mode, false);
		if (textMode) {
			mHead.setVisibility(View.GONE);
		} else {
			mLoader.set(user.profileImageUrl, mHead, R.drawable.default_head);
		}

		mName.setText(user.screenName);

		mStatusesInfo.setText("" + user.statusesCount);
		mFavoritesInfo.setText("" + user.favouritesCount);
		mFriendsInfo.setText("" + user.friendsCount);
		mFollowersInfo.setText("" + user.followersCount);
		if (App.DEBUG)
			log("updateUI user.description=" + user.description);

		if (StringHelper.isEmpty(user.description)) {
			mDescription.setText("这家伙什么也没留下");
			mDescription.setGravity(Gravity.CENTER);
		} else {
			mDescription.setText(user.description);
		}

		mProtected.setVisibility(user.protect ? View.VISIBLE : View.GONE);

		setExtraInfo(user);
		updateFollowButton(user);

		if (!noPermission) {
			boolean need = OptionHelper.readBoolean(this,
					R.string.option_fetch_relationships, false);
			if (need) {
				doFetchRelationshipInfo();
			}
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

	private void doFetchRelationshipInfo() {
		ResultReceiver receiver = new MyResultReceiver();
		Intent intent = new Intent(this, ActionService.class);
		intent.putExtra(Commons.EXTRA_TYPE, Commons.ACTION_USER_RELATION);
		if (App.DEBUG)
			log("App.userId=" + App.me.userId);
		intent.putExtra("user_a", user.id);
		intent.putExtra("user_b", App.me.userId);
		intent.putExtra(Commons.EXTRA_RECEIVER, receiver);
		startService(intent);
	}

	private void updateFollowButton(User u) {
		mFollowAction.setImageResource(u.following ? R.drawable.btn_unfollow
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
			final ConfirmDialog dialog = new ConfirmDialog(this, "取消关注",
					"要取消关注" + user.screenName + "吗？");
			dialog.setClickListener(new ConfirmDialog.AbstractClickHandler() {

				@Override
				public void onButton1Click() {
					ActionManager.doFollow(mContext, user,
							new MyResultReceiver());
				}
			});
			dialog.show();
		} else {
			ActionManager.doFollow(mContext, user, new MyResultReceiver());
		}

	}

	@Override
	public void onClick(View v) {
		if (user == null || user.isNull()) {
			return;
		}
		switch (v.getId()) {
		case R.id.user_action_reply:
			ActionManager.doWrite(this, "@" + user.screenName + " ");
			break;
		case R.id.user_action_message:
			ActionManager.doMessage(this, user);
			break;
		case R.id.user_action_follow:
			doFollow();
			break;
		case R.id.user_statuses_view:
			if (hasPermission()) {
				ActionManager.doShowTimeline(this, user);
			}
			break;
		case R.id.user_favorites_view:
			if (hasPermission()) {
				ActionManager.doShowFavorites(this, user);
			}
			break;
		case R.id.user_friends_view:
			if (hasPermission()) {
				ActionManager.doShowFriends(this, user);
			}
			break;
		case R.id.user_followers_view:
			if (hasPermission()) {
				ActionManager.doShowFollowers(this, user);
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

	private boolean hasPermission() {
		if (noPermission) {
			Utils.notify(this, "你没有通过这个用户的验证");
			return false;
		}
		return true;
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
					if (App.DEBUG)
						log("result ok, update ui");
					int type = resultData.getInt(Commons.EXTRA_TYPE);
					User result = (User) resultData
							.getParcelable(Commons.EXTRA_USER);
					if (result != null) {
						user = result;
					}
					if (!isInitialized) {
						showContent();
					}
					if (type == Commons.ACTION_USER_RELATION) {
						boolean follow = resultData
								.getBoolean(Commons.EXTRA_BOOLEAN);
						if (App.DEBUG)
							log("user relationship result=" + follow);
						updateRelationshipState(follow);
					} else if (type == Commons.ACTION_USER_SHOW) {
						if (App.DEBUG)
							log("show result=" + user.id);
						updateUI();
						if (isInitialized) {
							stopRefreshAnimation();
						}
					} else if (type == Commons.ACTION_USER_FOLLOW
							|| type == Commons.ACTION_USER_UNFOLLOW) {
						if (App.DEBUG)
							log("user.following=" + user.following);
						updateFollowButton(user);
						Utils.notify(mContext, user.following ? "关注成功"
								: "取消关注成功");
					}
				}
				break;
			case Commons.RESULT_CODE_ERROR:
				if (App.DEBUG)
					log("result error");
				if (!isInitialized) {
					mEmptyView.setVisibility(View.GONE);
				}
				int type = resultData.getInt(Commons.EXTRA_TYPE);
				if (type == Commons.ACTION_USER_RELATION) {
					return;
				}
				if (type == Commons.ACTION_USER_SHOW) {
					stopRefreshAnimation();
				}
				String msg = resultData.getString(Commons.EXTRA_ERROR_MESSAGE);
				Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}

	}

	private static final String tag = ProfilePage.class.getSimpleName();

	private void log(String message) {
		Log.d(tag, message);
	}

}
