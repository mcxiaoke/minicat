package com.mcxiaoke.minicat.app;

import android.os.Bundle;
import com.mcxiaoke.minicat.dao.model.StatusModel;
import com.mcxiaoke.minicat.fragment.BaseTimlineFragment;
import com.mcxiaoke.minicat.fragment.UserTimelineFragment;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.08
 */
public class UITimeline extends UIBaseTimeline {

    @Override
    protected int getType() {
        return StatusModel.TYPE_USER;
    }

    @Override
    protected BaseTimlineFragment getFragment(String userId) {
        return UserTimelineFragment.newInstance(userId, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected String getTitleSuffix() {
        return "消息";
    }
}
