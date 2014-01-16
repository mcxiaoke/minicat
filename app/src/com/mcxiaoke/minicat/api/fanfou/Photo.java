package com.mcxiaoke.minicat.api.fanfou;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Project: fanfouapp
 * User: mcxiaoke
 * Date: 13-5-28
 * Time: 下午5:25
 */
public class Photo {

    @Expose
    public String url;
    @Expose
    @SerializedName("imageurl")
    public String imageUrl;
    @Expose
    @SerializedName("thumburl")
    public String thumbUrl;
    @Expose
    @SerializedName("largeurl")
    public String largeUrl;
}
