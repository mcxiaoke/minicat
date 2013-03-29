package org.mcxiaoke.fancooker;

import org.mcxiaoke.fancooker.dao.model.StatusModel;
import org.mcxiaoke.fancooker.fragments.BaseTimlineFragment;
import org.mcxiaoke.fancooker.fragments.UserTimelineFragment;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.08
 *
 */
public class UITimeline extends UIBaseTimeline {

	@Override
	protected int getType() {
		return StatusModel.TYPE_USER;
	}

	@Override
	protected BaseTimlineFragment getFragment(String userId) {
		return UserTimelineFragment.newInstance(userId,true);
	}

}
