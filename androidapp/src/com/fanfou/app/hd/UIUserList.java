package com.fanfou.app.hd;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Filter;
import com.fanfou.app.hd.R;
import com.fanfou.app.hd.App.ApnType;
import com.fanfou.app.hd.api.User;
import com.fanfou.app.hd.fragments.FollowersListFragment;
import com.fanfou.app.hd.fragments.FriendsListFragment;
import com.fanfou.app.hd.fragments.OnInitCompleteListener;
import com.fanfou.app.hd.fragments.UserListFragment;
import com.fanfou.app.hd.fragments.widget.TextChangeListener;
import com.fanfou.app.hd.service.Constants;
import com.fanfou.app.hd.util.Assert;
import com.fanfou.app.hd.util.StringHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.10
 * @version 1.5 2011.10.29
 * @version 1.6 2011.11.07
 * @version 2.0 2011.11.07
 * @version 2.1 2011.11.09
 * @version 2.2 2011.11.18
 * @version 2.3 2011.11.21
 * @version 2.4 2011.12.13
 * @version 2.5 2011.12.23
 * @version 3.0 2012.01.30
 * @version 3.1 2012.01.31
 * @version 4.0 2012.02.08
 * 
 */
public class UIUserList extends UIBaseSupport implements OnInitCompleteListener {
	private static final String TAG = UIUserList.class.getSimpleName();

	private UserListFragment mFragment;
	private CursorAdapter mAdapter;
	private Filter mFilter;

	private EditText mEditText;

	private String userId;
	private String userName;
	private User user;
	private int type;

	private static final String tag = UIUserList.class.getSimpleName();

	private void log(String message) {
		Log.d(tag, message);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (App.DEBUG)
			log("onCreate");
	}

	@Override
	protected void initialize() {
		if (!parseIntent()) {
			finish();
			return;
		}
	}

	@Override
	protected void setLayout() {
		setContentView(R.layout.ui_users);
		mEditText = (EditText) findViewById(R.id.choose_input);
		mEditText.addTextChangedListener(new MyTextWatcher(this));
		setFragment();

	}

	private void filter(String text) {
		if (App.DEBUG) {
			Log.d(TAG, "filter() text=" + text);
			Assert.notNull(mAdapter, "adaper is null.");
			Assert.notNull(mAdapter.getFilter(), "adaper.filter is null.");
		}
		if (StringHelper.isEmpty(text)) {
			return;
		}
		if (mFilter != null) {
			mFilter.filter(text);
		}
	}

	private void showSearchBox() {
		mEditText.setVisibility(View.VISIBLE);
	}

	private void hideSearchBox() {
		mEditText.setVisibility(View.GONE);
	}

	private void setFragment() {
		if (App.DEBUG) {
			Log.d(TAG, "setFragment()");
		}
		if (type == Constants.TYPE_USERS_FRIENDS) {
			mFragment = FriendsListFragment.newInstance(userId);
		} else {
			mFragment = FollowersListFragment.newInstance(userId);
		}

		mFragment.setOnInitCompleteListener(this);

		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.add(R.id.container, mFragment);
		transaction.commit();
	}

	private static class MyTextWatcher extends TextChangeListener {
		private UIUserList mUiUserList;

		public MyTextWatcher(UIUserList ui) {
			this.mUiUserList = ui;
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (App.DEBUG) {
				Log.d(TAG, "onTextChanged() text=" + s);
			}
			mUiUserList.filter(s.toString().trim());
		}
	}

	private boolean parseIntent() {
		Intent intent = getIntent();
		type = intent.getIntExtra(Constants.EXTRA_TYPE,
				Constants.TYPE_USERS_FRIENDS);
		user = (User) intent.getParcelableExtra(Constants.EXTRA_DATA);
		if (user == null) {
			userId = intent.getStringExtra(Constants.EXTRA_ID);
		} else {
			userId = user.id;
			userName = user.screenName;
		}
		return !StringHelper.isEmpty(userId);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (App.getApnType() != ApnType.WIFI) {
			App.getImageLoader().clearQueue();
		}
	}

	@Override
	public void onInitComplete(Bundle data) {
		if (App.DEBUG) {
			Assert.notNull(mFragment.getAdapter(), "adaper is null.");
		}
		mAdapter = mFragment.getAdapter();
		mFilter = mAdapter.getFilter();
		showSearchBox();
	}

}
