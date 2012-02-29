/**
 * 
 */
package com.fanfou.app.hd.fragments;

import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author mcxiaoke
 * @version 1.0 2012-2-29 上午11:39:49
 * 
 */
public abstract class AbstractListFragment extends AbstractFragment implements OnItemClickListener{

	public abstract BaseAdapter getAdapter();
	
	public abstract ListView getListView();

	public abstract void startRefresh();

}
