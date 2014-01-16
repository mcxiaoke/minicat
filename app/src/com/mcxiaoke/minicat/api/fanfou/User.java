package com.mcxiaoke.minicat.api.fanfou;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Project: fanfouapp
 * User: mcxiaoke
 * Date: 13-5-28
 * Time: 下午5:13
 */
public class User {

    @Expose
    public String id;
    @Expose
    public String name;
    @Expose
    @SerializedName("screen_name")
    public String screenName;
    @Expose
    public String location;
    @Expose
    public String gender;
    @Expose
    public String birthday;
    @Expose
    public String description;
    @Expose
    public String url;
    @Expose
    @SerializedName("created_at")
    public String createdAt;
    @Expose
    @SerializedName("followers_count")
    public int followersCount;
    @Expose
    @SerializedName("friends_count")
    public int friendsCount;
    @Expose
    @SerializedName("statuses_count")
    public int statusesCount;
    @Expose
    @SerializedName("favourites_count")
    public int favoritesCount;
    @Expose
    @SerializedName("utc_offset")
    public int utcOffset;

    @Expose
    @SerializedName("protected")
    public boolean isProtected;
    @Expose
    public boolean following;
    @Expose
    public boolean notifications;

    @Expose
    @SerializedName("profile_image_url")
    public String profileImageUrl;
    @Expose
    @SerializedName("profile_image_url_large")
    public String profileImageUrlLarge;
    @Expose
    @SerializedName("profile_background_color")
    public String profileBackgroundColor;
    @Expose
    @SerializedName("profile_text_color")
    public String profileTextColor;
    @Expose
    @SerializedName("profile_link_color")
    public String profileLinkColor;
    @Expose
    @SerializedName("profile_sidebar_fill_color")
    public String profileSideBarFillColor;
    @Expose
    @SerializedName("profile_sidebar_border_color")
    public String profileSideBarBorderColor;
    @Expose
    @SerializedName("profile_background_image_url")
    public String profileBackgroundImageUrl;
    @Expose
    @SerializedName("profile_background_tile")
    public boolean profileBackgroundTile;

    @Expose
    public Status status;


}
