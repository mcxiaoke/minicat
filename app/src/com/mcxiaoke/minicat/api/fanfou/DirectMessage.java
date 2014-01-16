package com.mcxiaoke.minicat.api.fanfou;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Project: fanfouapp
 * User: mcxiaoke
 * Date: 13-5-28
 * Time: 下午5:54
 */
public class DirectMessage {
    @Expose
    public String id;
    @Expose
    public String text;
    @Expose
    @SerializedName("created_at")
    public String createdAt;
    @Expose
    @SerializedName("sender_id")
    public String senderId;
    @Expose
    @SerializedName("recipient_id")
    public String recipientId;
    @Expose
    @SerializedName("sender_screen_name")
    public String senderScreenName;
    @Expose
    @SerializedName("recipient_screen_name")
    public String recipientScreenName;
    @Expose
    public User sender;
    @Expose
    public User recipient;
}
