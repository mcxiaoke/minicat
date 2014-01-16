package com.mcxiaoke.minicat.api.fanfou;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Project: fanfouapp
 * User: mcxiaoke
 * Date: 13-5-28
 * Time: 下午5:49
 */
public class Trends {

    @Expose
    @SerializedName("as_of")
    public String createdAt;

    @Expose
    public List<Search> trends;
}
