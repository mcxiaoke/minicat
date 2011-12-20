package com.fanfou.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.fanfou.app.App.ApnType;
import com.fanfou.app.adapter.BaseCursorAdapter;
import com.fanfou.app.adapter.MessageCursorAdapter;
import com.fanfou.app.adapter.StatusCursorAdapter;
import com.fanfou.app.adapter.ViewsAdapter;
import com.fanfou.app.api.Status;
import com.fanfou.app.cache.ImageLoader;
import com.fanfou.app.db.Contents.BasicColumns;
import com.fanfou.app.db.Contents.DirectMessageInfo;
import com.fanfou.app.db.Contents.StatusInfo;
import com.fanfou.app.db.FanFouProvider;
import com.fanfou.app.dialog.ConfirmDialog;
import com.fanfou.app.service.Constants;
import com.fanfou.app.service.FanFouService;
import com.fanfou.app.service.NotificationService;
import com.fanfou.app.ui.ActionBar;
import com.fanfou.app.ui.ActionManager;
import com.fanfou.app.ui.UIManager;
import com.fanfou.app.ui.viewpager.TitlePageIndicator;
import com.fanfou.app.ui.viewpager.TitleProvider;
import com.fanfou.app.ui.widget.EndlessListViewNoHeader;
import com.fanfou.app.ui.widget.EndlessListViewNoHeader.OnLoadDataListener;
import com.fanfou.app.util.IntentHelper;
import com.fanfou.app.util.OptionHelper;
import com.fanfou.app.util.SoundManager;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.05.31
 * @version 2.0 2011.07.16
 * @version 3.0 2011.09.24
 * @version 3.2 2011.10.25
 * @version 3.3 2011.10.27
 * @version 3.5 2011.10.29
 * @version 3.6 2011.11.02
 * @version 3.7 2011.11.04
 * @version 4.0 2011.11.04
 * @version 4.1 2011.11.07
 * @version 4.2 2011.11.08
 * @version 4.3 2011.11.09
 * @version 4.4 2011.11.11
 * @version 4.5 2011.11.16
 * @version 4.6 2011.11.21
 * @version 4.7 2011.11.22
 * @version 4.8 2011.11.30
 * @version 4.9 2011.12.02
 * @version 5.0 2011.12.05
 * @version 5.1 2011.12.06
 * @version 5.2 2011.12.09
 * @version 5.3 2011.12.13
 * @version 6.0 2011.12.19
 * 
 */
public class HomePage extends BaseActivity implements OnPageChangeListener,
		OnLoadDataListener, OnItemLongClickListener, TitleProvider {

	public static final int NUMS_OF_PAGE = 4;

	private ActionBar mActionBar;

	private Handler mHandler;
	private ViewPager mViewPager;
	private ViewsAdapter mViewAdapter;
	private TitlePageIndicator mPageIndicator;

	private ImageView iRefreshBottom;
	private ImageView iWriteBottom;

	private boolean isBusy;

	private int mCurrentPage;

	private int initPage;

	private EndlessListViewNoHeader[] views = new EndlessListViewNoHeader[NUMS_OF_PAGE];

	private Cursor[] cursors = new Cursor[NUMS_OF_PAGE];

	private BaseCursorAdapter[] adapters = new BaseCursorAdapter[NUMS_OF_PAGE];

	private Parcelable[] states = new Parcelable[NUMS_OF_PAGE];

	private boolean[] initializeState = new boolean[NUMS_OF_PAGE];

	private static final String[] PAGE_TITLES = new String[] { "我的主页", "提到我的",
			"我的私信", "随便看看" };

	private boolean endlessScroll;
	private boolean soundEffect;

	public static final String TAG = "HomePage";

	private void log(String message) {
		Log.d(TAG, message);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (App.DEBUG)
			log("onCreate()");

		init();
		setContentView(R.layout.home);

		setActionBar();
		setBottom();
		setListViews();
		setViewPager();
		setCursors();
		setAdapters();
		checkRefresh();
	}

	private void init() {
		initPage = getIntent().getIntExtra(Constants.EXTRA_PAGE, 0);
		endlessScroll = OptionHelper.readBoolean(
				R.string.option_page_scroll_endless, false);
		soundEffect = OptionHelper.readBoolean(
				R.string.option_play_sound_effect, true);
		mHandler = new Handler();
		ImageLoader.getInstance();
		initSoundManager();
	}

	private void initSoundManager() {
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		SoundManager.getInstance();
		SoundManager.initSounds(this);
		SoundManager.loadSounds();
	}

	/**
	 * 初始化和设置ActionBar
	 */
	private void setActionBar() {
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setLeftAction(new HomeLogoAction());
		mActionBar.setRightAction(new ActionBar.WriteAction(this, null));
		mActionBar.setRefreshEnabled(this);

		// if(App.TEST){
		// mActionBar.setTitle("测试版 "+App.appVersionName);
		// }
		if (App.DEBUG) {
			mActionBar.setTitle("开发版 " + App.appVersionName);
		}
	}

	private class HomeLogoAction extends ActionBar.AbstractAction {

		public HomeLogoAction() {
			super(R.drawable.i_logo);
		}

		@Override
		public void performAction(View view) {
			goTop();
		}

	}

	@Override
	protected void startRefreshAnimation() {
		setBusy(true);
		mActionBar.startAnimation();
		startBottomAnimation();
	}

	@Override
	protected void stopRefreshAnimation() {
		setBusy(false);
		mActionBar.stopAnimation();
		stopBottomAnimation();
	}

	private void startBottomAnimation() {
		iRefreshBottom.post(new Runnable() {
			@Override
			public void run() {
				iRefreshBottom.setOnClickListener(null);
				iRefreshBottom.setImageDrawable(null);
				iRefreshBottom
						.setBackgroundResource(R.drawable.animation_refresh);
				AnimationDrawable frameAnimation = (AnimationDrawable) iRefreshBottom
						.getBackground();
				frameAnimation.start();
			}
		});

	}

	private void stopBottomAnimation() {
		iRefreshBottom.post(new Runnable() {
			@Override
			public void run() {
				AnimationDrawable frameAnimation = (AnimationDrawable) iRefreshBottom
						.getBackground();
				if (frameAnimation != null) {
					frameAnimation.stop();
					iRefreshBottom.setBackgroundDrawable(null);
					iRefreshBottom
							.setImageResource(R.drawable.i_refresh_bottom);
					iRefreshBottom.setOnClickListener(HomePage.this);
				}
			}
		});

	}

	private void setViewPager() {
		if (App.DEBUG) {
			log("setViewPager initPage=" + initPage);
		}

		mViewAdapter = new ViewsAdapter(views, endlessScroll);
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPager.setOnPageChangeListener(this);
		mViewPager.setAdapter(mViewAdapter);
		mPageIndicator = (TitlePageIndicator) findViewById(R.id.viewindicator);
		mPageIndicator.setTitleProvider(this);

		if (initPage > 0) {
			mPageIndicator.setViewPager(mViewPager, initPage);
			mViewPager.setCurrentItem(initPage);
		}

	}

	private void setBottom() {

		iWriteBottom = (ImageView) findViewById(R.id.write_bottom);
		iWriteBottom.setOnClickListener(this);

		iRefreshBottom = (ImageView) findViewById(R.id.refresh_bottom);
		iRefreshBottom.setOnClickListener(this);

		final Resources res = getResources();
		final int size = new Float(res.getDimension(R.dimen.icon_width))
				.intValue();
		final int margin = new Float(res.getDimension(R.dimen.bottom_margin))
				.intValue();

		RelativeLayout.LayoutParams onLeft = new RelativeLayout.LayoutParams(
				size, size);
		onLeft.setMargins(margin, margin, margin, margin);
		onLeft.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		onLeft.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

		RelativeLayout.LayoutParams onRight = new RelativeLayout.LayoutParams(
				size, size);
		onRight.setMargins(margin, margin, margin, margin);
		onRight.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		onRight.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

		String writePostion = OptionHelper.readString(
				R.string.option_bottom_write_icon, "none");
		String refreshPostion = OptionHelper.readString(
				R.string.option_bottom_refresh_icon, "right");

		if (writePostion.equals(refreshPostion)
				&& !refreshPostion.equals("none")) {
			iRefreshBottom.setLayoutParams(onRight);
			iWriteBottom.setVisibility(View.GONE);
		} else {
			if (refreshPostion.equals("left")) {
				iRefreshBottom.setLayoutParams(onLeft);
			} else if (refreshPostion.equals("right")) {
				iRefreshBottom.setLayoutParams(onRight);

			} else {
				iRefreshBottom.setVisibility(View.GONE);
			}

			if (writePostion.equals("left")) {
				iWriteBottom.setLayoutParams(onLeft);
			} else if (writePostion.equals("right")) {
				iWriteBottom.setLayoutParams(onRight);
			} else {
				iWriteBottom.setVisibility(View.GONE);
			}
		}

	}

	/**
	 * 初始化并添加四个页面的ListView
	 */
	private void setListViews() {
		for (int i = 0; i < views.length; i++) {
			views[i] = new EndlessListViewNoHeader(this);
			views[i].setOnRefreshListener(this);
			if (i != 2) {
				views[i].setOnItemLongClickListener(this);
			}
		}
	}

	private Cursor initStatusCursor(int type) {
		String where = BasicColumns.TYPE + "=?";
		String[] whereArgs = new String[] { String.valueOf(type) };
		Uri uri = StatusInfo.CONTENT_URI;
		return managedQuery(uri, StatusInfo.COLUMNS, where, whereArgs,
				FanFouProvider.ORDERBY_DATE_DESC);
	}

	private Cursor initMessageCursor() {
		Uri uri = Uri.withAppendedPath(DirectMessageInfo.CONTENT_URI, "list");
		return managedQuery(uri, DirectMessageInfo.COLUMNS, null, null, null);
	}

	private void setCursors() {
		cursors[0] = initStatusCursor(Constants.TYPE_STATUSES_HOME_TIMELINE);
		cursors[1] = initStatusCursor(Constants.TYPE_STATUSES_MENTIONS);
		cursors[2] = initMessageCursor();
		cursors[3] = initStatusCursor(Constants.TYPE_STATUSES_PUBLIC_TIMELINE);
	}

	private void initAdapters() {
		adapters[0] = new StatusCursorAdapter(true, this, cursors[0]);
		adapters[1] = new StatusCursorAdapter(true, this, cursors[1]);
		adapters[2] = new MessageCursorAdapter(this, cursors[2]);
		adapters[3] = new StatusCursorAdapter(true, this, cursors[3]);
	}

	private void setAdapters() {
		initAdapters();
		for (int i = 0; i < adapters.length; i++) {
			views[i].setAdapter(adapters[i]);
			// views[i].setOnScrollListener(adapters[i]);
			if (cursors[i].getCount() == 0) {
				if (App.DEBUG)
					log("cursors[" + i
							+ "] is empty, remove footer and refresh.");
				views[i].removeFooter();
			}
		}
		views[3].removeFooter();

	}

	private void checkRefresh() {
		boolean refresh = OptionHelper.readBoolean(
				R.string.option_refresh_on_open, false);
		if (refresh || (cursors[0].getCount() == 0 && mCurrentPage == 0)) {
			onRefreshClick();
		}
	}

	private void setBusy(boolean busy) {
		isBusy = busy;
	}

	/**
	 * 刷新，获取最新的消息
	 * 
	 * @param type
	 *            类型参数：Home/Mention/Message/Public
	 */
	private void doRetrieve(final int page, boolean doGetMore) {
		if (App.DEBUG) {
			log("doRetrieve() page=" + page + " doGetMore=" + doGetMore);
		}
		ResultReceiver receiver = new HomeResultReceiver(page, doGetMore);
		String sinceId = null;
		String maxId = null;
		Cursor cursor = cursors[page];
		switch (page) {
		case 0:
			if (doGetMore) {
				maxId = Utils.getMaxId(cursor);
			} else {
				sinceId = Utils.getSinceId(cursor);
			}
			FanFouService.doFetchHomeTimeline(this, receiver, sinceId, maxId);
			break;
		case 1:
			if (doGetMore) {
				maxId = Utils.getMaxId(cursor);
			} else {
				sinceId = Utils.getSinceId(cursor);
			}
			FanFouService.doFetchMentions(this, receiver, sinceId, maxId);
			break;
		case 2:
			FanFouService.doFetchDirectMessagesConversationList(this, receiver,
					doGetMore);
			break;
		case 3:
			if (!doGetMore) {
				FanFouService.doFetchPublicTimeline(this, receiver);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 载入更多，获取较旧的消息
	 */
	private void doGetMore() {
		doRetrieve(mCurrentPage, true);
	}

	/**
	 * 刷新，载入更新的消息
	 */
	private void doRefresh() {
		if (App.DEBUG) {
			log("doRefresh()");
		}
		doRetrieve(mCurrentPage, false);
	}

	@Override
	protected IntentFilter getIntentFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.ACTION_STATUS_SENT);
		filter.addAction(Constants.ACTION_DRAFTS_SENT);
		filter.addAction(Constants.ACTION_NOTIFICATION);
		return filter;
	}

	@Override
	protected boolean onBroadcastReceived(Intent intent) {
		String action = intent.getAction();
		if (action.equals(Constants.ACTION_STATUS_SENT)) {
			if (App.DEBUG) {
				log("onBroadcastReceived ACTION_STATUS_SENT");
			}

			if (mCurrentPage == 0) {
				boolean needRefresh = OptionHelper.readBoolean(
						R.string.option_refresh_after_send, false);
				if (needRefresh) {
					onRefreshClick();
				}
			}

		} else if (action.equals(Constants.ACTION_DRAFTS_SENT)) {
			if (App.DEBUG) {
				log("onBroadcastReceived ACTION_DRAFTS_SENT");
			}

			if (mCurrentPage == 0) {
				boolean needRefresh = OptionHelper.readBoolean(
						R.string.option_refresh_after_send, false);
				if (needRefresh) {
					onRefreshClick();
				}
			}
		} else if (action.equals(Constants.ACTION_NOTIFICATION)) {
			if (App.DEBUG) {
				log("onBroadcastReceived ACTION_NOTIFICATION");
			}
			int type = intent.getIntExtra(Constants.EXTRA_TYPE, -1);
			int count = intent.getIntExtra(Constants.EXTRA_COUNT, 0);
			switch (type) {
			case NotificationService.NOTIFICATION_TYPE_HOME:
				if (count > 0) {
					if (cursors[0] != null) {
						cursors[0].requery();
					}
					views[0].setSelection(0);
					Utils.notify(this, count + "条新消息");
					if (soundEffect) {
						SoundManager.playSound(1, 0);
					}
				}
				break;
			case NotificationService.NOTIFICATION_TYPE_MENTION:
				if (count > 0) {
					if (cursors[1] != null) {
						cursors[1].requery();
					}
					views[1].setSelection(0);
					Utils.notify(this, count + "条新@消息");
					if (soundEffect) {
						SoundManager.playSound(1, 0);
					}
				}

				break;
			case NotificationService.NOTIFICATION_TYPE_DM:
				if (count > 0) {
					if (cursors[2] != null) {
						cursors[2].requery();
					}
					views[2].setSelection(0);
					Utils.notify(this, count + "条新私信");
					if (soundEffect) {
						SoundManager.playSound(1, 0);
					}
				}

				break;
			default:
				break;
			}
		}
		return true;
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		for (int i = 0; i < views.length; i++) {
			if (views[i] != null)
				states[i] = savedInstanceState.getParcelable(views[i]
						.toString());
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		for (int i = 0; i < views.length; i++) {
			if (views[i] != null) {
				states[i] = views[i].onSaveInstanceState();
				outState.putParcelable(views[i].toString(), states[i]);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (App.DEBUG)
			log("onResume");

		for (int i = 0; i < views.length; i++) {
			if (views[i] != null && states[i] != null) {
				views[i].onRestoreInstanceState(states[i]);
				states[i] = null;
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (App.DEBUG)
			log("onPause");
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (App.getApnType() != ApnType.WIFI) {
			App.getImageLoader().clearQueue();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		App.getImageLoader().shutdown();
		SoundManager.cleanup();
		if (App.DEBUG) {
			log("onDestroy()");
		}
	}

	@Override
	protected int getPageType() {
		return PAGE_HOME;
	}

	@Override
	protected boolean isHomeScreen() {
		return true;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		initPage = getIntent().getIntExtra(Constants.EXTRA_PAGE, 0);
		if (App.DEBUG) {
			log("onNewIntent page=" + initPage);
		}

		if (initPage >= 0) {
			mViewPager.setCurrentItem(initPage);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.menu_option:
			onMenuOptionClick();
			return true;
		case R.id.menu_profile:
			onMenuProfileClick();
			return true;
		case R.id.menu_search:
			onMenuSearchClick();
			return true;
		case R.id.menu_logout:
			onMenuLogoutClick();
			return true;
		case R.id.menu_about:
			onMenuAboutClick();
			return true;
		case R.id.menu_feedback:
			onMenuFeedbackClick();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void onMenuOptionClick() {
		Intent intent = new Intent(this, SettingsPage.class);
		startActivity(intent);
	}

	private void onMenuProfileClick() {
		ActionManager.doMyProfile(this);
	}

	private void onMenuSearchClick() {
		Intent intent = new Intent(this, SearchPage.class);
		startActivity(intent);
	}

	private void onMenuAboutClick() {
		Utils.goAboutPage(this);
	}

	private void onMenuFeedbackClick() {
		ActionManager.doWrite(this, getString(R.string.config_feedback_account)
				+ " (" + Build.MODEL + "-" + Build.VERSION.RELEASE + " "
				+ App.appVersionName + ") ");
	}

	private void onMenuLogoutClick() {
		final ConfirmDialog dialog = new ConfirmDialog(this, "注销",
				"确定注销当前登录帐号吗？");
		dialog.setClickListener(new ConfirmDialog.AbstractClickHandler() {

			@Override
			public void onButton1Click() {
				App.setOAuthToken(null);
				IntentHelper.goLoginPage(mContext);
				finish();
			}
		});
		dialog.show();
	}

	private long lastPressTime = 0;

	@SuppressWarnings("unused")
	private boolean doBackPress() {
		if (System.currentTimeMillis() - lastPressTime < 2000) {
			finish();
			// android.os.Process.killProcess(android.os.Process.myPid());
		} else {
			Utils.notify(this, "再按一次退出");
			lastPressTime = System.currentTimeMillis();
		}
		return true;
	}

	/**
	 * FetchService返回数据处理 根据resultData里面的type信息分别处理
	 */
	private class HomeResultReceiver extends ResultReceiver {
		private int i;
		private boolean doGetMore;

		public HomeResultReceiver(int page, boolean getMore) {
			super(mHandler);
			this.i = page;
			this.doGetMore = getMore;
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			setBusy(false);
			switch (resultCode) {
			case Constants.RESULT_SUCCESS:
				int type = resultData.getInt(Constants.EXTRA_TYPE,
						Constants.TYPE_STATUSES_HOME_TIMELINE);
				int count = resultData.getInt(Constants.EXTRA_COUNT);
				if (doGetMore) {
					if (i < NUMS_OF_PAGE - 1) {
						views[i].onLoadMoreComplete();
					}
					cursors[i].requery();
				} else {
					stopRefreshAnimation();
					if (i < NUMS_OF_PAGE - 1) {
						views[i].addFooter();
					}
					if (count > 0) {
						if (type == Constants.TYPE_DIRECT_MESSAGES_CONVERSTATION_LIST) {
							Utils.notify(mContext, count + "条新私信");
						} else {
							Utils.notify(mContext, count + "条新消息");
						}
						if (soundEffect) {
							SoundManager.playSound(1, 0);
						}
						cursors[i].requery();
						views[i].setSelection(0);
					}
				}
				break;
			case Constants.RESULT_ERROR:
				String errorMessage = resultData
						.getString(Constants.EXTRA_ERROR);
				int errorCode = resultData.getInt(Constants.EXTRA_CODE);
				if (doGetMore) {
					views[i].onLoadMoreComplete();
				} else {
					stopRefreshAnimation();
				}
				Utils.notify(mContext, errorMessage);
				Utils.checkAuthorization(mContext, errorCode);
				break;
			default:
				break;
			}
		}

	}

	@Override
	public void onLoadMore(EndlessListViewNoHeader view) {
		doGetMore();
	}

	@Override
	public void onItemClick(EndlessListViewNoHeader view, int position) {
		final Cursor c = (Cursor) view.getItemAtPosition(position);
		if (c == null) {
			return;
		}
		if (mCurrentPage == 2) {
			Utils.goMessageChatPage(this, c);
		} else {
			final Status s = Status.parse(c);
			if (s != null && !s.isNull()) {
				Utils.goStatusPage(this, s);
			}
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		if (mCurrentPage == 2) {
			return true;
		}
		final Cursor c = (Cursor) parent.getItemAtPosition(position);
		showPopup(view, c);
		return true;
	}

	private void showPopup(final View view, final Cursor c) {
		if (c != null) {
			final Status s = Status.parse(c);
			if (s == null) {
				return;
			}
			UIManager.showPopup(mContext, c, view, s);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_title:
			goTop();
			break;
		case R.id.refresh_bottom:
			onRefreshClick();
			break;
		case R.id.write_bottom:
			ActionManager.doWrite(this);
			break;
		default:
			break;
		}
	}

	private void goTop() {
		if (views[mCurrentPage] != null) {
			views[mCurrentPage].setSelection(0);
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		// if (!endlessScroll) {
		mPageIndicator.onPageScrolled(position, positionOffset,
				positionOffsetPixels);
		// }

	}

	@Override
	public void onPageSelected(int position) {
		mCurrentPage = position % NUMS_OF_PAGE;
		mPageIndicator.onPageSelected(mCurrentPage);
		if (cursors[mCurrentPage] != null
				&& cursors[mCurrentPage].getCount() == 0) {
			onRefreshClick();
		}

	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
		// if (mCurrentPage > 0) {
		// mViewPager.setCurrentItem(mCurrentPage - 1);
		// }
		// } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
		// if (mCurrentPage < mViewPager.getWidth()-1) {
		// mViewPager.setCurrentItem(mCurrentPage + 1);
		// }
		// }
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public String getTitle(int position) {
		return PAGE_TITLES[position % NUMS_OF_PAGE];
	}

	@Override
	public void onRefreshClick() {
		if (App.DEBUG) {
			log("onRefreshClick page=" + mCurrentPage + " isBusy" + isBusy);
		}
		if (isBusy) {
			return;
		}
		startRefreshAnimation();
		doRefresh();
	}

}
