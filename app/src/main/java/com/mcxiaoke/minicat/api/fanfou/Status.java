package com.mcxiaoke.minicat.api.fanfou;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Project: fanfouapp
 * User: mcxiaoke
 * Date: 13-5-28
 * Time: 下午5:27
 */
public class Status {

    @Expose
    @SerializedName("created_at")
    public String createdAt;
    @Expose
    @SerializedName("id")
    public String idStr;
    @Expose
    @SerializedName("rawid")
    public long id;
    @Expose
    public String text;
    @Expose
    public String source;
    @Expose
    public boolean truncated;
    @Expose
    @SerializedName("in_reply_to_status_id")
    public String inReplyToStatusId;
    @Expose
    @SerializedName("in_reply_to_user_id")
    public String inReplyToUserId;
    @Expose
    @SerializedName("in_reply_to_screen_name")
    public String inReplyToScreenName;
    @Expose
    @SerializedName("in_reply_to_lastmsg_id")
    public String inReplyToLastMsgId;
    @Expose
    public boolean favorited;
    @Expose
    @SerializedName("is_self")
    public boolean isSelf;
    @Expose
    public String location;

    @Expose
    @SerializedName("repost_status_id")
    public String retweetStatusId;
    @Expose
    @SerializedName("repost_user_id")
    public String retweetUserId;
    @Expose
    @SerializedName("repost_screen_name")
    public String retweetScreenName;

    @Expose
    @SerializedName("repost_status")
    public Status retweetStatus;
    @Expose
    public User user;
    @Expose
    public Photo photo;
}
