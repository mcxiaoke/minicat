package org.mcxiaoke.fancooker;

import org.mcxiaoke.fancooker.fragments.BaseTimlineFragment;
import org.mcxiaoke.fancooker.ui.widget.GestureManager.SwipeListener;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

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
 * @version 5.1 2012.02.09
 * @version 5.2 2012.03.19
 * 
 */
abstract class UIBaseTimeline extends UIBaseSupport implements SwipeListener {

	private static final String TAG = UIBaseTimeline.class.getSimpleName();

	private BaseTimlineFragment mFragment;

	private String userId;

	protected abstract int getType();

	protected abstract BaseTimlineFragment getFragment(String userId);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parseIntent();
		setLayout();

	}

	protected void setLayout() {
		if (userId != null) {
			setTitle("@" + userId);
		} else {
			setTitle("时间线");
		}
		setContentView(R.layout.ui_container);
		setFragment();
	}

	private void setFragment() {
		if (AppContext.DEBUG) {
			Log.d(TAG, "setFragment()");
		}

		mFragment = getFragment(userId);
		android.app.FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		transaction.add(R.id.container, mFragment);
		transaction.commit();
	}

	private void parseIntent() {
		Intent intent = getIntent();
		userId = intent.getStringExtra("id");
		if (TextUtils.isEmpty(userId)) {
			userId = AppContext.getAccount();
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
