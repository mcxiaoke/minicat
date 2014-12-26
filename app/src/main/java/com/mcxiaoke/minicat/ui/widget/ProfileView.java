package com.mcxiaoke.minicat.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.dao.model.UserModel;
import com.mcxiaoke.minicat.util.DateTimeHelper;
import com.mcxiaoke.minicat.util.LogUtil;
import com.mcxiaoke.minicat.util.StringHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author mcxiaoke
 * @version 4.0 2013.05.18
 */
public class ProfileView extends FrameLayout implements
        OnClickListener {
    public static final int TYPE_TOP_STATUSES = 0;
    public static final int TYPE_TOP_FOLLOWING = 1;
    public static final int TYPE_TOP_FOLLOWERS = 2;

    public static final int TYPE_FOLLOW_STATE = 4;

    public static final int TYPE_ALBUM = 5;
    public static final int TYPE_TOP_FAVORATIES = 6;
    public static final int TYPE_STATUSES = 7;
    public static final int TYPE_FOLLOWING = 8;
    public static final int TYPE_FOLLOWERS = 9;
    private static final String TAG = ProfileView.class.getSimpleName();
    private ViewGroup header;
    private ImageView headerImage;
    private TextView headerName;
    private TextView headerId;
    private TextView headerState;
    private ImageView headerLock;
    private TextView headerAlbum;
    private TextView headerFollow;
    private View vFollowingItem;
    private View vFollowersItem;
    private View vStatusesItem;
    private View vFavoritesItem;
    private TextView tvFollowingItemTitle;
    private TextView tvFollowersItemTitle;
    private TextView tvStatusesItemTitle;
    private TextView tvFavoritesRowTitle;
    private TextView tvFollowingItemValue;
    private TextView tvFollowersItemValue;
    private TextView tvStatusesItemValue;
    private TextView tvFavoritesRowValue;
    private TextView tvInfo;
    private boolean mExpanded;
    private ProfileClickListener mClickListener;
    private int mFollowOnColor;
    private int mFollowOffColor;
    public ProfileView(Context context) {
        super(context);
        initialize(context);
    }


    public ProfileView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public ProfileView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context);
    }

    private void initialize(Context context) {
        mFollowOnColor = getResources().getColor(R.color.solid_white);
        mFollowOffColor = getResources().getColor(R.color.text_primary);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_profile, this);
        findViews();
        setListeners();
    }

    private void findViews() {
        header = (ViewGroup) findViewById(R.id.header);
        headerImage = (ImageView) findViewById(R.id.header_image);
        headerName = (TextView) findViewById(R.id.header_name);
        headerId = (TextView) findViewById(R.id.header_id);
        headerState = (TextView) findViewById(R.id.header_state);
        headerLock = (ImageView) findViewById(R.id.header_lock);
        headerAlbum = (TextView) findViewById(R.id.header_album);
        headerFollow = (TextView) findViewById(R.id.header_follow);

        vFollowingItem = findViewById(R.id.item1);
        vFollowersItem = findViewById(R.id.item2);
        vStatusesItem = findViewById(R.id.item3);
        vFavoritesItem = findViewById(R.id.item4);

        tvFollowingItemTitle = (TextView) findViewById(R.id.text_title1);
        tvFollowersItemTitle = (TextView) findViewById(R.id.text_title2);
        tvStatusesItemTitle = (TextView) findViewById(R.id.text_title3);
        tvFavoritesRowTitle = (TextView) findViewById(R.id.text_title4);

        tvFollowingItemValue = (TextView) findViewById(R.id.text_value1);
        tvFollowersItemValue = (TextView) findViewById(R.id.text_value2);
        tvStatusesItemValue = (TextView) findViewById(R.id.text_value3);
        tvFavoritesRowValue = (TextView) findViewById(R.id.text_value4);

        tvFollowingItemValue.setText("正在关注");
        tvFollowersItemValue.setText("被关注");
        tvStatusesItemValue.setText("消息");
        tvFavoritesRowValue.setText("收藏");

        tvInfo = (TextView) findViewById(R.id.info);

    }

    private void setListeners() {
        header.setClickable(false);
        headerImage.setOnClickListener(this);
        headerAlbum.setOnClickListener(this);
        headerFollow.setOnClickListener(this);

        vFollowingItem.setOnClickListener(this);
        vFollowersItem.setOnClickListener(this);
        vStatusesItem.setOnClickListener(this);
        vFavoritesItem.setOnClickListener(this);

    }

    private void updateHeader(final UserModel user) {
        headerName.setText(user.getScreenName());
        headerId.setText("@" + user.getId());
        headerLock.setVisibility(user.isProtect() ? View.VISIBLE : View.INVISIBLE);
        String headerImageUrl = user.getProfileImageUrlLarge();
        ImageLoader.getInstance().displayImage(headerImageUrl, headerImage);
    }

    private void updateStatistics(final UserModel user) {
        tvFollowingItemTitle.setText("" + user.getFriendsCount());
        tvFollowersItemTitle.setText("" + user.getFollowersCount());
        tvStatusesItemTitle.setText("" + user.getStatusesCount());
        tvFavoritesRowTitle.setText("" + user.getFavouritesCount());
    }

    public void updateFollowState(boolean following) {
        headerFollow.setVisibility(View.VISIBLE);
        headerFollow.setBackgroundResource(following ? R.drawable.state_on : R.drawable.state_off);
        headerFollow.setTextColor(following ? mFollowOnColor : mFollowOffColor);
        headerFollow.setText(following ? "正在关注" : "添加关注");
    }

    public void hideFollowState() {
        headerFollow.setVisibility(View.GONE);
        headerState.setVisibility(View.GONE);

    }

    private void updateDescription(final UserModel user) {
        String gender = user.getGender();
        String birthday = user.getBirthday();
        String location = user.getLocation();
        String url = user.getUrl();
        String time = DateTimeHelper.formatDateOnly(user.getTime());
        String description = user.getDescription();

        StringBuilder sb = new StringBuilder();

        if (!StringHelper.isEmpty(gender)) {
            sb.append("性别：").append(gender).append("\n");
        }
        if (!StringHelper.isEmpty(birthday)) {
            sb.append("生日：").append(birthday).append("\n");
        }
        if (!StringHelper.isEmpty(location)) {
            sb.append("位置：").append(location).append("\n");
        }

        if (!StringHelper.isEmpty(url)) {
            sb.append("网站：").append(url).append("\n");
        }

        sb.append("注册时间：")
                .append(time).append("\n");

        sb.append("自述：").append("\n");
        sb.append(buildDescription(description)).append("\n");
        tvInfo.setText(sb.toString());

    }

    private String buildDescription(final String desc) {
        LogUtil.v(TAG, "buildDescription() " + desc);

        final StringBuilder builder = new StringBuilder();
        if (StringHelper.isEmpty(desc)) {
            builder.append("这家伙很懒，什么都没写");
        } else {
            builder.append(desc);
        }

        return builder.toString();
    }

    public void setProfileClickListener(ProfileClickListener listener) {
        this.mClickListener = listener;
    }

    private void onProfileItemClick(int type) {
        if (mClickListener != null) {
            mClickListener.onProfileItemClick(type);
        }
    }

    public void setContent(final UserModel user) {
        if (user == null) {
            setVisibility(View.INVISIBLE);
            return;
        }
        setVisibility(View.VISIBLE);
        updateHeader(user);
        updateFollowState(user.isFollowing());
        updateStatistics(user);
        updateDescription(user);
    }

    public void setFollowState(boolean follow) {
        headerState.setText(follow ? R.string.profile_state_on : R.string.profile_state_off);
        headerState.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        if (v == headerFollow) {
            onProfileItemClick(TYPE_FOLLOW_STATE);
        } else if (v == vFollowingItem) {
            onProfileItemClick(TYPE_TOP_FOLLOWING);
        } else if (v == vFollowersItem) {
            onProfileItemClick(TYPE_TOP_FOLLOWERS);
        } else if (v == vStatusesItem) {
            onProfileItemClick(TYPE_TOP_STATUSES);
        } else if (v == vFavoritesItem) {
            onProfileItemClick(TYPE_TOP_FAVORATIES);
        } else if (v == headerAlbum) {
            onProfileItemClick(TYPE_ALBUM);
        }
    }

    public interface ProfileClickListener {
        public void onProfileItemClick(int type);
    }

}
