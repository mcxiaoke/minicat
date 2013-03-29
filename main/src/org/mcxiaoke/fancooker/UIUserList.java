package org.mcxiaoke.fancooker;

import org.mcxiaoke.fancooker.dao.model.UserModel;
import org.mcxiaoke.fancooker.fragments.FollowersListFragment;
import org.mcxiaoke.fancooker.fragments.FriendsListFragment;
import org.mcxiaoke.fancooker.fragments.OnInitCompleteListener;
import org.mcxiaoke.fancooker.fragments.UserListFragment;
import org.mcxiaoke.fancooker.ui.widget.TextChangeListener;
import org.mcxiaoke.fancooker.util.NetworkHelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

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
 * @version 4.1 2012.03.26
 * 
 */
public class UIUserList extends UIBaseSupport implements OnInitCompleteListener {
	private static final String TAG = UIUserList.class.getSimpleName();

	private UserListFragment mFragment;

	private EditText mEditText;

	private String userId;
	private UserModel user;
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
		}
	}

	@Override
	protected void setLayout() {
		setContentView(R.layout.ui_users);
		mEditText = (EditText) findViewById(R.id.input);
		mEditText.addTextChangedListener(new MyTextWatcher(this));
		setFragment();

	}

	private void filter(String text) {
		if (App.DEBUG) {
			Log.d(TAG, "filter() text=" + text);
		}
		mFragment.filter(text);
	}

	private void showSearchBox() {
		mEditText.setVisibility(View.VISIBLE);
	}

	private void setFragment() {
		if (App.DEBUG) {
			Log.d(TAG, "setFragment()");
		}
		if (type == UserModel.TYPE_FRIENDS) {
			mFragment = FriendsListFragment.newInstance(userId, true);
		} else {
			mFragment = FollowersListFragment.newInstance(userId, true);
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
		type = intent.getIntExtra("type", UserModel.TYPE_FRIENDS);
		user = (UserModel) intent.getParcelableExtra("data");
		if (user == null) {
			userId = intent.getStringExtra("id");
		} else {
			userId = user.getId();
		}
		return !TextUtils.isEmpty(userId);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (!NetworkHelper.isWifi(this)) {
			App.getImageLoader().clearQueue();
		}
	}

	@Override
	public void onInitComplete(Bundle data) {
		showSearchBox();
	}

}
