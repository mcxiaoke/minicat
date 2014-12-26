package com.mcxiaoke.minicat.api.fanfou;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Project: fanfouapp
 * User: mcxiaoke
 * Date: 13-5-28
 * Time: 下午5:47
 */
public class Search {
    @Expose
    public int id;
    @Expose
    public String query;
    @Expose
    public String name;
    @Expose
    @SerializedName("created_at")
    public String createdAt;
    @Expose
    public String url;
}
