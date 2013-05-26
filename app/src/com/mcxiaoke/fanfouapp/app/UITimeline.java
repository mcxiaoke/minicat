package com.mcxiaoke.fanfouapp.app;

import android.os.Bundle;
import com.mcxiaoke.fanfouapp.dao.model.StatusModel;
import com.mcxiaoke.fanfouapp.fragment.BaseTimlineFragment;
import com.mcxiaoke.fanfouapp.fragment.UserTimelineFragment;

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
