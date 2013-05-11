/**
 * 
 */
package org.mcxiaoke.fancooker;

import org.mcxiaoke.fancooker.menu.MenuCallback;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.slidingmenu.lib.SlidingMenu;

/**
 * @author mcxiaoke
 * 
 */
public abstract class UIBaseSlidingSupport extends UIBaseSupport implements
		SlidingMenu.OnCloseListener, SlidingMenu.OnOpenListener, MenuCallback {
	private SlidingMenu mSlidingMenu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// if (isMenuShowing()) {
		// getSlidingMenu().showContent(false);
		// }
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

	protected void toggle(boolean animate) {
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
		return super.onOptionsItemSelected(item);
	}

}
