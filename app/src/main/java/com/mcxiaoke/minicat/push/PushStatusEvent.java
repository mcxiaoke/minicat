package com.mcxiaoke.minicat.push;

import com.mcxiaoke.minicat.dao.model.StatusModel;

/**
 * User: mcxiaoke
 * Date: 16/3/10
 * Time: 11:36
 */
public class PushStatusEvent {
    private StatusModel status;

    public PushStatusEvent(final StatusModel s) {
        status = s;
    }

    public StatusModel getStatus() {
        return status;
    }
}
