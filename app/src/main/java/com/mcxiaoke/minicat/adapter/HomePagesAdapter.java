package com.mcxiaoke.minicat.adapter;

import android.app.FragmentManager;
import android.content.Context;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.fragment.AbstractListFragment;
import com.mcxiaoke.minicat.fragment.HomeTimelineFragment;
import com.mcxiaoke.minicat.fragment.MentionTimelineFragment;
import com.mcxiaoke.minicat.fragment.PublicTimelineFragment;

public class HomePagesAdapter extends FragmentPagerAdapter {

    private static final int[] ICONS = {
            R.drawable.ic_tab_home_1, R.drawable.ic_tab_mention_1, R.drawable.ic_tab_browse_1
    };
    private static final int[] TITLES = {
            R.string.page_title_home, R.string.page_title_mention, R.string.page_title_public
    };

    private Context mContext;

    public HomePagesAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public AbstractListFragment getItem(int position) {
        final AbstractListFragment fragment;
        switch (position) {
            case 0:
                fragment = HomeTimelineFragment.newInstance();
                break;
            case 1:
                fragment = MentionTimelineFragment.newInstance();
                break;
            case 2:
                fragment = PublicTimelineFragment.newInstance();
                break;
            default:
                fragment = null;
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getString(TITLES[position]);
    }
}
