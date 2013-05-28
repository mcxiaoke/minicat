package com.mcxiaoke.fanfouapp.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.mcxiaoke.fanfouapp.R;
import com.mcxiaoke.fanfouapp.dao.model.UserModel;
import com.mcxiaoke.fanfouapp.util.DateTimeHelper;
import com.mcxiaoke.fanfouapp.util.StringHelper;
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

    public static final int TYPE_PHOTOS = 5;
    public static final int TYPE_FAVORATIES = 6;
    public static final int TYPE_STATUSES = 7;
    public static final int TYPE_FOLLOWING = 8;
    public static final int TYPE_FOLLOWERS = 9;

    public interface ProfileClickListener {
        public void onProfileItemClick(int type);
    }

    private static final String TAG = ProfileView.class.getSimpleName();

    private ScrollView vContent;

    private ViewGroup header;
    private ImageView headerImage;
    private TextView headerName;
    private TextView headerId;
    private TextView headerState;
    private ImageView headerLock;

    private View vFollowingItem;
    private View vFollowersItem;
    private View vStatusesItem;

    private TextView tvFollowingItemTitle;
    private TextView tvFollowersItemTitle;
    private TextView tvStatusesItemTitle;

    private TextView tvFollowingItemValue;
    private TextView tvFollowersItemValue;
    private TextView tvStatusesItemValue;

    private TextView tvFavoritesRowTitle;
    private TextView tvPhotosRowTitle;

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
        vContent = (ScrollView) findViewById(R.id.container);
        header = (ViewGroup) findViewById(R.id.header);
        headerImage = (ImageView) findViewById(R.id.header_image);
        headerName = (TextView) findViewById(R.id.header_name);
        headerId = (TextView) findViewById(R.id.header_id);
        headerState = (TextView) findViewById(R.id.header_state);
        headerLock = (ImageView) findViewById(R.id.header_lock);

        vFollowingItem = findViewById(R.id.item1);
        vFollowersItem = findViewById(R.id.item2);
        vStatusesItem = findViewById(R.id.item3);

        tvStatusesItemTitle = (TextView) findViewById(R.id.text_title1);
        tvFollowingItemTitle = (TextView) findViewById(R.id.text_title2);
        tvFollowersItemTitle = (TextView) findViewById(R.id.text_title3);

        tvStatusesItemValue = (TextView) findViewById(R.id.text_value1);
        tvFollowingItemValue = (TextView) findViewById(R.id.text_value2);
        tvFollowersItemValue = (TextView) findViewById(R.id.text_value3);

        tvFollowingItemValue.setText("正在关注");
        tvFollowersItemValue.setText("被关注");
        tvStatusesItemValue.setText("消息");

        tvPhotosRowTitle = (TextView) findViewById(R.id.text_left);
        tvFavoritesRowTitle = (TextView) findViewById(R.id.text_right);

        tvPhotosRowTitle.setText("查看相册");
        tvFavoritesRowTitle.setText("查看收藏");

        tvInfo = (TextView) findViewById(R.id.info);

    }

    private void setListeners() {

        vFollowingItem.setOnClickListener(this);
        vFollowersItem.setOnClickListener(this);
        vStatusesItem.setOnClickListener(this);

        tvFavoritesRowTitle.setOnClickListener(this);
        tvPhotosRowTitle.setOnClickListener(this);
    }

    private void updateHeader(final UserModel user) {
        headerName.setText(user.getScreenName());
        headerId.setText("@" + user.getId());
        headerLock.setVisibility(user.isProtect() ? View.VISIBLE : View.INVISIBLE);
        String headerImageUrl = user.getProfileImageUrl();
        ImageLoader.getInstance().displayImage(headerImageUrl, headerImage);
    }

    private void updateStatistics(final UserModel user) {
        tvFollowingItemTitle.setText("" + user.getFriendsCount());
        tvFollowersItemTitle.setText("" + user.getFollowersCount());
        tvStatusesItemTitle.setText("" + user.getStatusesCount());
    }

    public void updateFollowState(boolean following) {
//        btnFollowState.setVisibility(View.VISIBLE);
//        btnFollowState.setBackgroundResource(following ? R.drawable.button_follow_on : R.drawable.button_follow_off);
//        btnFollowState.setTextColor(following ? mFollowOnColor : mFollowOffColor);
//        btnFollowState.setText(following ? "正在关注" : "添加关注");
    }

    private void updateDescription(final UserModel user) {
        String gender = user.getGender();
        String birthday = user.getBirthday();
        String location = user.getLocation();
        String url = user.getUrl();
        String time = DateTimeHelper.formatDateOnly(user.getTime());
        String description = user.getDescription();

        StringBuffer sb = new StringBuffer();

        sb.append("自述：").append("\n");
        sb.append(StringHelper.isEmpty(description) ? "这家伙很懒，什么都没写" : description).append("\n\n");

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

//        headerIntro.setText(simpleBuilder.toString());
        tvInfo.setText(sb.toString());

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
        int id = v.getId();
        if (v == vFollowingItem) {
            onProfileItemClick(TYPE_TOP_FOLLOWING);
        } else if (v == vFollowersItem) {
            onProfileItemClick(TYPE_TOP_FOLLOWERS);
        } else if (v == vStatusesItem) {
            onProfileItemClick(TYPE_TOP_STATUSES);
        } else if (v == tvPhotosRowTitle) {
            onProfileItemClick(TYPE_PHOTOS);
        } else if (v == tvFavoritesRowTitle) {
            onProfileItemClick(TYPE_FAVORATIES);
        }
    }

}
