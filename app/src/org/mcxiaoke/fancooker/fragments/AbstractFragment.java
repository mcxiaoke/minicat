package org.mcxiaoke.fancooker.fragments;

import org.mcxiaoke.fancooker.UIBaseSupport;

import android.app.Activity;
import android.app.Fragment;

/**
 * @author mcxiaoke
 * @version 1.0 2012.01.31
 * 
 */
public abstract class AbstractFragment extends Fragment {

	private UIBaseSupport mBaseSupport;

	protected final UIBaseSupport getBaseSupport() {
		return mBaseSupport;
	}

	@Override
	public void onAttach(Activity activity) {
		mBaseSupport = (UIBaseSupport) activity;
		super.onAttach(activity);
	}

	public abstract String getTitle();

	public abstract void updateUI();

}
