package com.mcxiaoke.fanfouapp.adapter;

import android.app.FragmentManager;
import com.mcxiaoke.fanfouapp.R;
import com.mcxiaoke.fanfouapp.fragment.AbstractListFragment;
import com.mcxiaoke.fanfouapp.fragment.HomeTimelineFragment;
import com.mcxiaoke.fanfouapp.fragment.MentionTimelineFragment;
import com.mcxiaoke.fanfouapp.fragment.PublicTimelineFragment;
import com.viewpagerindicator.IconPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomePagesAdapter extends FragmentPagerAdapter implements IconPagerAdapter {

    private final List<AbstractListFragment> fragments = new ArrayList<AbstractListFragment>();

    public HomePagesAdapter(FragmentManager fm) {
        super(fm);
        addFragments();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return getItem(position).getTitle();
    }

    @Override
    public AbstractListFragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    private void addFragments() {
        fragments.add(HomeTimelineFragment.newInstance(true));
        fragments.add(MentionTimelineFragment.newInstance(true));
        fragments.add(PublicTimelineFragment.newInstance());
    }

    @Override
    public int getIconResId(int index) {
        switch (index) {
            case 0:
                return R.drawable.ic_tab_home_0;
//                break;
            case 1:
                return R.drawable.ic_tab_mention_0;
//                break;
            case 2:
                return R.drawable.ic_tab_timeline_0;
//            break;
            default:
                break;
        }
        return 0;
    }
}
