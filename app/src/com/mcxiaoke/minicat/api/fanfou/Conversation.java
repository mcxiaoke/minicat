package com.mcxiaoke.minicat.api.fanfou;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Project: fanfouapp
 * User: mcxiaoke
 * Date: 13-5-28
 * Time: 下午5:57
 */
public class Conversation {
    @Expose
    @SerializedName("otherid")
    public String id;
    @Expose
    @SerializedName("msg_num")
    public int count;
    @Expose
    @SerializedName("new_conv")
    public boolean newConversation;

    @Expose
    public DirectMessage dm;
}
