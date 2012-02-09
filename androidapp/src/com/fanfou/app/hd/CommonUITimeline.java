package com.fanfou.app.hd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.fanfou.app.hd.R;
import com.fanfou.app.hd.api.User;
import com.fanfou.app.hd.service.Constants;
import com.fanfou.app.hd.ui.widget.ActionBar;
import com.fanfou.app.hd.ui.widget.ActionManager;
import com.fanfou.app.hd.ui.BaseTimlineFragment;
import com.fanfou.app.hd.ui.FollowersListFragment;
import com.fanfou.app.hd.ui.FriendsListFragment;
import com.fanfou.app.hd.ui.UserFavoritesFragment;
import com.fanfou.app.hd.ui.UserTimelineFragment;
import com.fanfou.app.hd.ui.widget.ActionBar.AbstractAction;
import com.fanfou.app.hd.ui.widget.GestureManager.SwipeListener;
import com.fanfou.app.hd.util.StringHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.07.10
 * @version 2.0 2011.10.19
 * @version 3.0 2011.10.21
 * @version 3.1 2011.10.24
 * @version 3.2 2011.10.29
 * @version 3.3 2011.11.18
 * @version 3.4 2011.12.13
 * @version 3.5 2011.12.23
 * @version 4.0 2012.01.30
 * @version 4.1 2012.01.31
 * @version 5.0 2012.02.08
 * 
 */
abstract class CommonUITimeline extends CommonUIBase implements SwipeListener {

	private static final String TAG = CommonUITimeline.class.getSimpleName();
	
	private BaseTimlineFragment mFragment;

	private ActionBar mActionBar;

	private String userId;
	private String userName;
	private User user;
	
	protected abstract int getType();
	
	protected abstract BaseTimlineFragment getFragment(String userId);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	protected void initialize() {
		parseIntent();
	}

	@Override
	protected void setLayout() {
		setContentView(R.layout.ui_container);
		setActionBar();
		setFragment();
	}
	
	private void setFragment() {
		if (App.DEBUG) {
			Log.d(TAG, "setFragment()");
		}
		
		mFragment=getFragment(userId);
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.add(R.id.container, mFragment);
		transaction.commit();
	}

	/**
	 * 初始化和设置ActionBar
	 */
	private void setActionBar() {
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setTitleClickListener(this);
		mActionBar.setRightAction(new WriteAction(this));
		if (user != null) {
			mActionBar.setTitle(user.screenName);
		}
		setActionBarSwipe(mActionBar);
	}

	public class WriteAction extends AbstractAction {

		public WriteAction(Context context) {
			super(R.drawable.i_write);
		}

		@Override
		public void performAction(View view) {
			String text = null;
			if (user != null) {
				text = "@" + user.screenName + " ";
			}
			ActionManager.doWrite(mContext, text);
		}
	}

	private void parseIntent() {
		Intent intent = getIntent();
		user = (User) intent.getParcelableExtra(Constants.EXTRA_DATA);
		if (user == null) {
			userId = intent.getStringExtra(Constants.EXTRA_ID);
		} else {
			userId = user.id;
			userName = user.screenName;
		}
		
		if(StringHelper.isEmpty(userId)){
			userId=App.getUserId();
		}
	}

	@Override
	public void onClick(View v) {
	}

	@Override
	public boolean onSwipeLeft() {
		finish();
		return true;
	}

	@Override
	public boolean onSwipeRight() {
		return true;
	}

}
