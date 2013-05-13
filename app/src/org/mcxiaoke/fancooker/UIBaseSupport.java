package org.mcxiaoke.fancooker;

import org.mcxiaoke.fancooker.controller.SimpleDialogListener;
import org.mcxiaoke.fancooker.controller.UIController;
import org.mcxiaoke.fancooker.dialog.ConfirmDialog;
import org.mcxiaoke.fancooker.util.Utils;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;

/**
 * @author mcxiaoke
 * @version 1.0 2011.05.30
 * @version 2.0 2011.09.25
 * @version 2.1 2011.10.19
 * @version 2.1 2011.10.25
 * @version 2.2 2011.10.27
 * @version 2.3 2011.11.07
 * @version 2.4 2011.11.11
 * @version 2.5 2011.11.15
 * @version 2.6 2011.11.22
 * @version 2.7 2011.12.07
 * @version 2.8 2012.02.01
 * @version 3.0 2012.02.06
 * @version 3.1 2012.02.07
 * @version 3.2 2012.02.09
 * @version 3.3 2012.02.10
 * @version 3.4 2012.03.09
 * @version 3.5 2012.03.16
 * @version 4.0 2013.05.07
 * 
 */
public abstract class UIBaseSupport extends Activity implements OnClickListener {

	public static final int STATE_INIT = 0;
	public static final int STATE_NORMAL = 1;
	public static final int STATE_EMPTY = 2;

	protected UIBaseSupport mContext;
	protected LayoutInflater mInflater;
	protected Resources mResources;

	protected ActionBar mActionBar;
	protected DisplayMetrics mDisplayMetrics;
	private boolean mRefreshing;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		Utils.initScreenConfig(this);
		AppContext.setActiveContext(getClass().getCanonicalName(), this);
		this.mContext = this;
		this.mInflater = LayoutInflater.from(this);
		this.mResources = getResources();
		this.mActionBar = getActionBar();
		this.mActionBar.setDisplayHomeAsUpEnabled(true);
		this.mActionBar.setDisplayUseLogoEnabled(true);
		this.mDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
	}

	protected int getMenuResourceId() {
		return R.menu.menu;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		int id = getMenuResourceId();
		if (id > 0) {
			getMenuInflater().inflate(id, menu);
		}
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onMenuHomeClick();
			return true;
		case R.id.menu_write:
			onMenuWriteClick();
			return true;
//		case R.id.menu_refresh:
//			onMenuRefreshClick();
//			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void onMenuWriteClick() {
		UIController.showWrite(mContext);
	}

	protected void onMenuHomeClick() {
		finish();
	}

	protected void onMenuRefreshClick() {
		showProgressIndicator();
	}

	protected void onMenuSearchClick() {
		onSearchRequested();
	}

	protected void onMenuLogoutClick() {
		final ConfirmDialog dialog = new ConfirmDialog(this);
		dialog.setTitle("提示");
		dialog.setMessage("确定注销当前登录帐号吗？");
		dialog.setClickListener(new SimpleDialogListener() {

			@Override
			public void onPositiveClick() {
				super.onPositiveClick();
				AppContext.doLogin(mContext);
				finish();
			}
		});
		dialog.show();
	}

	public void showProgressIndicator() {
		if (!mRefreshing) {
			mRefreshing = true;
			setProgressBarIndeterminateVisibility(true);
//			invalidateOptionsMenu();
		}
	}

	public void hideProgressIndicator() {
		if (mRefreshing) {
			mRefreshing = false;
			setProgressBarIndeterminateVisibility(false);
//			invalidateOptionsMenu();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		AppContext.active = true;
	}

	@Override
	protected void onPause() {
		AppContext.active = false;
		super.onPause();
	}

	protected int getPxInt(int dpi) {
		return (int) (dpi * mDisplayMetrics.density);
	}

	protected int getPxInt(float dpi) {
		return (int) (dpi * mDisplayMetrics.density);
	}

	protected float getPx(int dpi) {
		return (dpi * mDisplayMetrics.density);
	}

	protected float getPx(float dpi) {
		return (dpi * mDisplayMetrics.density);
	}

	@Override
	public void onClick(View v) {
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

	@Override
	public void finish() {
		super.finish();
	}

}
