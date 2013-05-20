package com.mcxiaoke.fanfouapp.ui;

import android.content.res.Resources;
import android.widget.ListView;
import android.widget.ScrollView;
import com.mcxiaoke.fanfouapp.R;

/**
 * Project: fanfouapp
 * Package: com.mcxiaoke.fanfouapp.ui
 * User: mcxiaoke
 * Date: 13-5-20
 * Time: 下午10:00
 */
public final class UIHelper {

    public static int getCardUIBackgroundColor(Resources res) {
        return res.getColor(R.color.list_background_color);
    }


    /**
     * // save index and top position
     int index = mList.getFirstVisiblePosition();
     View v = mList.getChildAt(0);
     int top = (v == null) ? 0 : v.getTop();

     // ...

     // restore
     mList.setSelectionFromTop(index, top);
     * @param listView
     */

    public static void setListViewCardUI(ListView listView) {
        Resources res = listView.getResources();
        int padding = res.getDimensionPixelSize(R.dimen.list_card_padding);
        int dividerHeight = res.getDimensionPixelSize(R.dimen.list_card_divider_height);
        int backgroundColor = res.getColor(R.color.list_background_color);
        listView.setSelector(res.getDrawable(R.drawable.selector_list_light));
        listView.setPadding(padding, padding, padding, padding);
        listView.setDivider(res
                .getDrawable(R.drawable.list_card_divider));
        listView.setDividerHeight(dividerHeight);
        listView.setHeaderDividersEnabled(true);
        listView.setFooterDividersEnabled(true);
        listView.setCacheColorHint(0);
        listView.setBackgroundResource(R.drawable.list_card_background);
        listView.setScrollBarStyle(ScrollView.SCROLLBARS_OUTSIDE_OVERLAY);
    }

}
