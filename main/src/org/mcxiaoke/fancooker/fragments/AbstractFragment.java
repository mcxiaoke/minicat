package org.mcxiaoke.fancooker.fragments;

import com.actionbarsherlock.app.SherlockFragment;

/**
 * @author mcxiaoke
 * @version 1.0 2012.01.31
 * 
 */
public abstract class AbstractFragment extends SherlockFragment {
	
	public abstract String getTitle();
	public abstract void updateUI();

}
