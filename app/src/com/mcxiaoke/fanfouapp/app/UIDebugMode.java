package com.mcxiaoke.fanfouapp.app;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.mcxiaoke.fanfouapp.ui.widget.ProfileHeaderView;
import com.mcxiaoke.fanfouapp.ui.widget.ProfileHeaderView.OnCountClickListener;
import com.mcxiaoke.fanfouapp.R;

public class UIDebugMode extends UIBaseSupport implements OnCountClickListener {

	private ViewGroup mContainer;
	private ProfileHeaderView mHeaderView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_debug);
		mContainer = (ViewGroup) findViewById(R.id.container);
		mHeaderView = (ProfileHeaderView) findViewById(R.id.profile);
		mHeaderView.setOnCountClickListener(this);
		mHeaderView.setNameText("巫猫子");
		mHeaderView.setStateText("正在关注");
		mHeaderView
				.setIntroText("gfdddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddg g费德勒国际法大括号就发放的放的  看看看过， ddddddddddddddddddddg g费德勒国际法大括号就发放的放的  看看看过， dddg g费德勒国际法大括号就发放的放的  看看看过， dddddddddddddg g费德勒国际法大括号就发放的放的  看看看过， ddddddddddg g费德勒国际法大括号就发放的放的  看看看过， 放的三个三");
		mHeaderView.setStatusCountText("消息数", "9672");
		mHeaderView.setFollowingCountText("关注", "129");
		mHeaderView.setFollowersCountText("被关注", "764");
	}

	@Override
	protected int getMenuResourceId() {
		return super.getMenuResourceId();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void onCountClick(int position) {

	}

}
