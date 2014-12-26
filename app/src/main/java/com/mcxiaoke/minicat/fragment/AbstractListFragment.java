/**
 *
 */
package com.mcxiaoke.minicat.fragment;

import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

/**
 * @author mcxiaoke
 * @version 1.0 2012-2-29 上午11:39:49
 */
public abstract class AbstractListFragment extends AbstractFragment implements
        OnItemClickListener {

    public abstract BaseAdapter getAdapter();

    public abstract ListView getListView();

}
