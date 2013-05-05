package org.mcxiaoke.fancooker.fragments;

import android.app.Fragment;


/**
 * @author mcxiaoke
 * @version 1.0 2012.01.31
 * 
 */
public abstract class AbstractFragment extends Fragment {
	
	public abstract String getTitle();
	public abstract void updateUI();

}
