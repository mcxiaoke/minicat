package com.mcxiaoke.fanfouapp.adapter;

import android.app.FragmentManager;
import com.mcxiaoke.fanfouapp.fragment.AbstractListFragment;
import com.mcxiaoke.fanfouapp.fragment.HomeTimelineFragment;
import com.mcxiaoke.fanfouapp.fragment.MentionTimelineFragment;
import com.mcxiaoke.fanfouapp.fragment.PublicTimelineFragment;

import java.util.ArrayList;
import java.util.List;

public class HomePagesAdapter extends FragmentPagerAdapter {

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

}
