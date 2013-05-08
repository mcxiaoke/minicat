/**
 * 
 */
package org.mcxiaoke.fancooker;

import org.mcxiaoke.fancooker.controller.SimpleDialogListener;
import org.mcxiaoke.fancooker.dialog.ConfirmDialog;
import org.mcxiaoke.fancooker.menu.MenuCallback;
import org.mcxiaoke.fancooker.menu.MenuItemResource;

import com.slidingmenu.lib.SlidingMenu;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * @author mcxiaoke
 * 
 */
public class UIBaseSlidingSupport extends UIBaseSupport implements
		SlidingMenu.OnCloseListener, SlidingMenu.OnOpenListener,
		MenuCallback {
	private SlidingMenu mSlidingMenu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
//		if (isMenuShowing()) {
//			getSlidingMenu().showContent(false);
//		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	protected void setSlidingMenu(final int menuResourceId) {
		mSlidingMenu = new SlidingMenu(this);
		mSlidingMenu.setMode(SlidingMenu.LEFT);
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		mSlidingMenu.setShadowWidth(getResources().getDimensionPixelOffset(
				R.dimen.sliding_shadow));
		mSlidingMenu.setShadowDrawable(R.drawable.menu_shadow);
		mSlidingMenu.setBehindOffset(getResources().getDimensionPixelOffset(
				R.dimen.sliding_offset));
		mSlidingMenu.setFadeDegree(0.35f);
		mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		mSlidingMenu.setMenu(menuResourceId);
		mSlidingMenu.setOnOpenListener(this);
		mSlidingMenu.setOnCloseListener(this);
	}

	protected SlidingMenu getSlidingMenu() {
		return mSlidingMenu;
	}

	protected void toggle() {
		toggle(true);
	}
	
	protected void toggle(boolean animate){
		mSlidingMenu.toggle(animate);
	}

	protected boolean isMenuShowing() {
		return mSlidingMenu.isMenuShowing();
	}

	protected void setTouchModeAbove(int touchMode) {
		mSlidingMenu.setTouchModeAbove(touchMode);
	}

	protected void setTouchModeBehind(int touchMode) {
		mSlidingMenu.setTouchModeBehind(touchMode);
	}

	@Override
	public void onOpen() {
		getActionBar().setDisplayHomeAsUpEnabled(false);
	}

	@Override
	public void onClose() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public void onMenuItemSelected(int position, MenuItemResource menuItem) {

	}

	@Override
	protected void onMenuHomeClick() {
		toggle();
	}

	@Override
	public void onBackPressed() {
		if (isMenuShowing()) {
			getSlidingMenu().showContent();
		} else {
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_write:
			onMenuWriteClick();
			break;
		case R.id.menu_search:
			onMenuSearchClick();
			break;
		case R.id.menu_logout:
			onMenuLogoutClick();
			break;
		default:
			super.onOptionsItemSelected(item);
			break;
		}
		return true;
	}

	private void onMenuSearchClick() {
		Intent intent = new Intent(this, UISearch.class);
		startActivity(intent);
	}

	private void onMenuLogoutClick() {
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

}
