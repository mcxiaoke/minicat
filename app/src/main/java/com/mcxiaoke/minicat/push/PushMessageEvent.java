package com.mcxiaoke.minicat.push;

import com.mcxiaoke.minicat.dao.model.DirectMessageModel;

/**
 * User: mcxiaoke
 * Date: 16/3/10
 * Time: 11:36
 */
public class PushMessageEvent {
    private DirectMessageModel message;

    public PushMessageEvent(final DirectMessageModel s) {
        message = s;
    }

    public DirectMessageModel getMessage() {
        return message;
    }
}
