package com.mcxiaoke.minicat.service;

import com.mcxiaoke.minicat.dao.model.StatusModel;

/**
 * User: mcxiaoke
 * Date: 16/3/11
 * Time: 11:29
 */
public class StatusUpdateEvent {
    private StatusModel model;

    public StatusUpdateEvent(final StatusModel m) {
        this.model = m;
    }

    public StatusModel getModel() {
        return model;
    }
}
