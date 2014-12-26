package com.mcxiaoke.minicat.api.fanfou;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Project: fanfouapp
 * User: mcxiaoke
 * Date: 13-5-28
 * Time: 下午5:36
 */
public class Relation {
    @Expose
    public Relationship relationship;

    public static class Relationship {
        @Expose
        public Member source;
        @Expose
        public Member target;

    }


    public static class Member {
        @Expose
        public String id;
        @Expose
        @SerializedName("screen_name")
        public String screenName;
        @Expose
        public String following;
        @Expose
        @SerializedName("followed_by")
        public String followedBy;
        @Expose
        public String blocking;

    }
}
