package com.mcxiaoke.minicat.ui;

import android.content.res.Resources;
import android.widget.ListView;
import android.widget.ScrollView;
import com.mcxiaoke.minicat.R;

/**
 * Project: fanfouapp
 * Package: com.mcxiaoke.fanfouapp.ui
 * User: mcxiaoke
 * Date: 13-5-20
 * Time: 下午10:00
 */
public final class UIHelper {

    /**
     * // save index and top position
     * int index = mList.getFirstVisiblePosition();
     * View v = mList.getChildAt(0);
     * int top = (v == null) ? 0 : v.getTop();
     * <p/>
     * // ...
     * <p/>
     * // restore
     * mList.setSelectionFromTop(index, top);
     *
     * @param listView
     */

    public static void setListView(ListView listView) {
        Resources res = listView.getResources();
        int dividerHeight = res.getDimensionPixelSize(R.dimen.list_divider_height);
        listView.setSelector(res.getDrawable(R.drawable.selector));
        listView.setDivider(res
                .getDrawable(R.drawable.divider));
        listView.setDividerHeight(dividerHeight);
        listView.setHeaderDividersEnabled(true);
        listView.setFooterDividersEnabled(true);
        listView.setCacheColorHint(0);
        listView.setScrollBarStyle(ScrollView.SCROLLBARS_OUTSIDE_OVERLAY);
    }

}
