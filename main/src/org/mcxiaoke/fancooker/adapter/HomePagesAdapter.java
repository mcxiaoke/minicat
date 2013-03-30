package org.mcxiaoke.fancooker.adapter;

import java.util.ArrayList;
import java.util.List;

import org.mcxiaoke.fancooker.fragments.AbstractListFragment;
import org.mcxiaoke.fancooker.fragments.ConversationListFragment;
import org.mcxiaoke.fancooker.fragments.HomeTimelineFragment;
import org.mcxiaoke.fancooker.fragments.MentionTimelineFragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

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
		fragments.add(ConversationListFragment.newInstance(true));
	}
}
