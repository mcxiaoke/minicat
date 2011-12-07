package com.fanfou.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
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
import com.fanfou.app.api.DirectMessage;
import com.fanfou.app.api.FanFouApiConfig;
import com.fanfou.app.api.Status;
import com.fanfou.app.cache.ImageLoader;
import com.fanfou.app.config.Actions;
import com.fanfou.app.config.Commons;
import com.fanfou.app.db.FanFouProvider;
import com.fanfou.app.db.Contents.BasicColumns;
import com.fanfou.app.db.Contents.DirectMessageInfo;
import com.fanfou.app.db.Contents.StatusInfo;
import com.fanfou.app.dialog.ConfirmDialog;
import com.fanfou.app.service.FetchService;
import com.fanfou.app.service.NotificationService;
import com.fanfou.app.ui.ActionBar;
import com.fanfou.app.ui.UIManager;
import com.fanfou.app.ui.viewpager.TitlePageIndicator;
import com.fanfou.app.ui.viewpager.TitleProvider;
import com.fanfou.app.ui.widget.EndlessListViewNoHeader;
import com.fanfou.app.ui.widget.EndlessListViewNoHeader.OnLoadDataListener;
import com.fanfou.app.util.IntentHelper;
import com.fanfou.app.util.OptionHelper;
import com.fanfou.app.util.SoundManager;
import com.fanfou.app.util.ThemeHelper;
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

	private OptionHelper mOptionHelper;

	public static final String TAG = "HomePage";

	BroadcastReceiver mSendSuccessReceiver;
	IntentFilter mSendSuccessFilter;

	private void log(String message) {
		Log.d(TAG, message);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (App.DEBUG)
			log("onCreate()");
		// if (!App.me.isLogin) {
		// IntentHelper.goLoginPage(this);
		// finish();
		// return;
		// }

		init();
		setContentView(R.layout.home);
		
		View root=findViewById(R.id.root);
		ThemeHelper.setBackgroundColor(root);
		
		setActionBar();
		setBottom();
		setListViews();
		setViewPager();
		setCursors();
		setAdapters();
		checkRefresh();
	}

	private void init() {
		initPage = getIntent().getIntExtra(Commons.EXTRA_PAGE, 0);
		endlessScroll = OptionHelper.readBoolean(
				R.string.option_page_scroll_endless, false);
		soundEffect = OptionHelper.readBoolean(
				R.string.option_play_sound_effect, true);

		ImageLoader.getInstance(this);
		mHandler = new Handler();
		initSendSuccessReceiver();
		initSoundManager();
	}

	private void initSoundManager() {
		SoundManager.getInstance();
		SoundManager.initSounds(this);
		SoundManager.loadSounds();
	}

	private void initSendSuccessReceiver() {
		mSendSuccessReceiver = new SendSuccessReceiver();
		mSendSuccessFilter = new IntentFilter(Actions.ACTION_STATUS_SENT);
		// mSendSuccessFilter.addAction(Actions.ACTION_MESSAGE_SEND);
		mSendSuccessFilter.setPriority(1000);
	}

	private class SendSuccessReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (App.DEBUG) {
				log("SendSuccessReceiver.received");
				IntentHelper.logIntent(TAG, intent);
			}
			if (onSendSuccess(intent)) {
				abortBroadcast();
			}

		}

	}

	private boolean onSendSuccess(Intent intent) {
		// String action=intent.getAction();
		boolean result = true;

		// if(action.equals(Actions.ACTION_STATUS_SEND)){
		if (cursors[0] != null) {
			cursors[0].requery();
		}
		// }

		// else if(action.equals(Actions.ACTION_MESSAGE_SEND)){
		// boolean success=intent.getBooleanExtra(Commons.EXTRA_BOOLEAN, true);
		// String text=intent.getStringExtra(Commons.EXTRA_TEXT);
		// if(!success){
		// Utils.notify(this, text);
		// }else{
		// Utils.notify(this, "私信未发送："+text);
		// }
		// }
		return result;
	}

	/**
	 * 初始化和设置ActionBar
	 */
	private void setActionBar() {
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		 mActionBar.setLeftAction(new HomeLogoAction());
		mActionBar.setRightAction(new ActionBar.WriteAction(this, null));
		mActionBar.setRefreshEnabled(this);
		
		if(App.TEST){
			mActionBar.setTitle("测试版 "+App.appVersionName);
		}
		if (App.DEBUG) {
			mActionBar.setTitle("开发版 "+App.appVersionName);
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
		
		ThemeHelper.setBackgroundColor(mPageIndicator);
		
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
		cursors[0] = initStatusCursor(Status.TYPE_HOME);
		cursors[1] = initStatusCursor(Status.TYPE_MENTION);
		cursors[2] = initMessageCursor();
		cursors[3] = initStatusCursor(Status.TYPE_PUBLIC);
	}

	private void initAdapters() {
		adapters[0] = new StatusCursorAdapter(true,this, cursors[0]);
		adapters[1] = new StatusCursorAdapter(true,this, cursors[1]);
		adapters[2] = new MessageCursorAdapter(this, cursors[2]);
		adapters[3] = new StatusCursorAdapter(true,this, cursors[3]);
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
		if (!App.verified) {
			Utils.notify(this, "未通过验证，请登录");
			return;
		}
		if (App.getApnType() != ApnType.WIFI) {
			ImageLoader.getInstance(this).clearQueue();
		}
		Bundle b = new Bundle();
		b.putInt(Commons.EXTRA_COUNT, FanFouApiConfig.DEFAULT_TIMELINE_COUNT);
		b.putBoolean(Commons.EXTRA_FORMAT, false);
		ResultReceiver receiver = new HomeResultReceiver(page, doGetMore);
		;
		String sinceId = null;
		String maxId = null;
		Cursor cursor = cursors[page];
		switch (page) {
		case 0:
			if (doGetMore) {
				maxId = Utils.getMaxId(cursor);
				b.putString(Commons.EXTRA_MAX_ID, maxId);
			} else {
				sinceId = Utils.getSinceId(cursor);
				b.putString(Commons.EXTRA_SINCE_ID, sinceId);
			}
			FetchService.start(this, Status.TYPE_HOME, receiver, b);
			break;
		case 1:
			if (doGetMore) {
				maxId = Utils.getMaxId(cursor);
				b.putString(Commons.EXTRA_MAX_ID, maxId);
			} else {
				sinceId = Utils.getSinceId(cursor);
				b.putString(Commons.EXTRA_SINCE_ID, sinceId);
			}
			b.putString(Commons.EXTRA_SINCE_ID, sinceId);
			FetchService.start(this, Status.TYPE_MENTION, receiver, b);
			break;
		case 2:
			b.putBoolean(Commons.EXTRA_BOOLEAN, doGetMore);
			FetchService.start(this, DirectMessage.TYPE_ALL, receiver, b);
			break;
		case 3:
			if (!doGetMore) {
				FetchService.start(this, Status.TYPE_PUBLIC, receiver, b);
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
		doRetrieve(mCurrentPage, false);
	}

	@Override
	protected IntentFilter getIntentFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Actions.ACTION_STATUS_SENT);
		filter.addAction(Actions.ACTION_NOTIFICATION);
		return filter;
	}

	@Override
	protected boolean onBroadcastReceived(Intent intent) {
		String action = intent.getAction();
		if (action.equals(Actions.ACTION_STATUS_SENT)) {
			if (App.DEBUG) {
				log("onBroadcastReceived ACTION_STATUS_SENT");
			}

			cursors[0].requery();
			if (mCurrentPage == 0) {
				boolean needRefresh = OptionHelper.readBoolean(
						R.string.option_refresh_after_send, false);
				if (needRefresh) {
					onRefreshClick();
				}
			}

		} else if (action.equals(Actions.ACTION_NOTIFICATION)) {
			if (App.DEBUG) {
				log("onBroadcastReceived ACTION_NOTIFICATION");
			}
			int type = intent.getIntExtra(Commons.EXTRA_TYPE, -1);
			int count = intent.getIntExtra(Commons.EXTRA_COUNT, 0);
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
		registerReceiver(mSendSuccessReceiver, mSendSuccessFilter);
		for (int i = 0; i < views.length; i++) {
			if (views[i] != null && states[i] != null) {
				views[i].onRestoreInstanceState(states[i]);
				states[i] = null;
			}
		}
	}

	@Override
	protected void onPause() {
		unregisterReceiver(mSendSuccessReceiver);
		super.onPause();
		if (App.DEBUG)
			log("onPause");
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (App.getApnType() != ApnType.WIFI) {
			ImageLoader.getInstance(this).clearQueue();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ImageLoader.getInstance(this).shutdown();
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
		initPage = getIntent().getIntExtra(Commons.EXTRA_PAGE, 0);
		if (App.DEBUG) {
			log("onNewIntent page=" + initPage);
		}

		if (initPage >= 0) {
			mViewPager.setCurrentItem(initPage);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (App.DEBUG)
			log("onConfigurationChanged() ");
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem option = menu.add(0, MENU_ID_OPTION, MENU_ID_OPTION, "功能设置");
		option.setIcon(R.drawable.ic_menu_option);

		MenuItem profile = menu
				.add(0, MENU_ID_PROFILE, MENU_ID_PROFILE, "我的空间");
		profile.setIcon(R.drawable.ic_menu_profile);

		MenuItem search = menu.add(0, MENU_ID_SEARCH, MENU_ID_SEARCH, "热词搜索");
		search.setIcon(R.drawable.ic_menu_search);

		MenuItem logout = menu.add(0, MENU_ID_LOGOUT, MENU_ID_LOGOUT, "注销登录");
		logout.setIcon(R.drawable.ic_menu_logout);

		MenuItem about = menu.add(0, MENU_ID_ABOUT, MENU_ID_ABOUT, "关于饭否");
		about.setIcon(R.drawable.ic_menu_about);

		MenuItem feedback = menu.add(0, MENU_ID_FEEDBACK, MENU_ID_FEEDBACK,
				"意见反馈");
		feedback.setIcon(R.drawable.ic_menu_feedback);
		return true;
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
			case Commons.RESULT_CODE_FINISH:
				int type = resultData.getInt(Commons.EXTRA_TYPE,
						Status.TYPE_HOME);
				int count = resultData.getInt(Commons.EXTRA_COUNT);
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
						if (type == DirectMessage.TYPE_ALL) {
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
			case Commons.RESULT_CODE_ERROR:
				String errorMessage = resultData
						.getString(Commons.EXTRA_ERROR_MESSAGE);
				Utils.notify(mContext, errorMessage);
				if (doGetMore) {
					views[i].onLoadMoreComplete();
				} else {
					stopRefreshAnimation();
				}
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
			Intent intent = new Intent(this, WritePage.class);
			intent.putExtra(Commons.EXTRA_TYPE, WritePage.TYPE_NORMAL);
			startActivity(intent);
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
	public void onBackPressed() {
		boolean needConfirm = OptionHelper.readBoolean(
				R.string.option_confirm_on_exit, false);
		if (needConfirm) {
			final ConfirmDialog dialog = new ConfirmDialog(this, "提示",
					"确认退出饭否吗？");
			dialog.setClickListener(new ConfirmDialog.AbstractClickHandler() {

				@Override
				public void onButton1Click() {
					mContext.finish();
				}
			});
			dialog.show();
		} else {
			finish();
		}
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
			log("onRefreshClick page=" + mCurrentPage);
		}
		if (isBusy) {
			return;
		}
		startRefreshAnimation();
		doRefresh();
	}

}
