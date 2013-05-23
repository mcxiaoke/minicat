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
    private TextView headerState;
    private ImageView headerLock;
    private TextView headerIntro;

    private View vExtras;
    private TextView btnFollowState;

    private View vFollowingItem;
    private View vFollowersItem;
    private View vStatusesItem;

    private TextView tvFollowingItemTitle;
    private TextView tvFollowersItemTitle;
    private TextView tvStatusesItemTitle;

    private TextView tvFollowingItemValue;
    private TextView tvFollowersItemValue;
    private TextView tvStatusesItemValue;


    private View vStatusesRow;
    private View vFavoritesRow;
    private View vFollowingRow;
    private View vFollowersRow;
    private View vPhotosRow;

    private TextView tvStatusesRowTitle;
    private TextView tvFavoritesRowTitle;
    private TextView tvFollowingRowTitle;
    private TextView tvFollowersRowTitle;
    private TextView tvPhotosRowTitle;

    private TextView tvStatusesRowValue;
    private TextView tvFavoritesRowValue;
    private TextView tvFollowingRowValue;
    private TextView tvFollowersRowValue;
    private TextView tvPhotosRowValue;


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
        mFollowOnColor = getResources().getColor(R.color.text_color_primary_dark);
        mFollowOffColor = getResources().getColor(R.color.text_color_primary_light);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_profile, this);
        findViews();
        setListeners();
    }

    private void findViews() {
        vContent = (ScrollView) findViewById(R.id.container);
        header = (ViewGroup) findViewById(R.id.header);
        headerImage = (ImageView) findViewById(R.id.header_icon);
        headerName = (TextView) findViewById(R.id.header_name);
        headerState = (TextView) findViewById(R.id.header_state);
        headerState.setText("正在关注你");
        headerLock = (ImageView) findViewById(R.id.header_lock);
        headerIntro = (TextView) findViewById(R.id.header_intro);

        vExtras = findViewById(R.id.extras);
        btnFollowState = (TextView) findViewById(R.id.btn_state);

        vFollowingItem = findViewById(R.id.item1);
        vFollowersItem = findViewById(R.id.item2);
        vStatusesItem = findViewById(R.id.item3);

        tvFollowingItemTitle = (TextView) findViewById(R.id.text_title1);
        tvFollowersItemTitle = (TextView) findViewById(R.id.text_title2);
        tvStatusesItemTitle = (TextView) findViewById(R.id.text_title3);

        tvFollowingItemValue = (TextView) findViewById(R.id.text_value1);
        tvFollowersItemValue = (TextView) findViewById(R.id.text_value2);
        tvStatusesItemValue = (TextView) findViewById(R.id.text_value3);

        tvFollowingItemValue.setText("关注");
        tvFollowersItemValue.setText("被关注");
        tvStatusesItemValue.setText("消息");

        vStatusesRow = findViewById(R.id.button1);
        vFavoritesRow = findViewById(R.id.button2);
        vFollowingRow = findViewById(R.id.button3);
        vFollowersRow = findViewById(R.id.button4);
        vPhotosRow = findViewById(R.id.button5);

        tvStatusesRowTitle = (TextView) findViewById(R.id.text_left1);
        tvFavoritesRowTitle = (TextView) findViewById(R.id.text_left2);
        tvFollowingRowTitle = (TextView) findViewById(R.id.text_left3);
        tvFollowersRowTitle = (TextView) findViewById(R.id.text_left4);
        tvPhotosRowTitle = (TextView) findViewById(R.id.text_left5);

        tvStatusesRowValue = (TextView) findViewById(R.id.text_right1);
        tvFavoritesRowValue = (TextView) findViewById(R.id.text_right2);
        tvFollowingRowValue = (TextView) findViewById(R.id.text_right3);
        tvFollowersRowValue = (TextView) findViewById(R.id.text_right4);
        tvPhotosRowValue = (TextView) findViewById(R.id.text_right5);

        tvStatusesRowTitle.setText("消息");
        tvFavoritesRowTitle.setText("收藏");
        tvFollowingRowTitle.setText("关注");
        tvFollowersRowTitle.setText("被关注");
        tvPhotosRowTitle.setText("查看相册");

        tvPhotosRowValue.setText("");

        tvInfo = (TextView) findViewById(R.id.info);

    }

    private void setListeners() {

//        header.setOnClickListener(this);
        btnFollowState.setOnClickListener(this);

        vFollowingItem.setOnClickListener(this);
        vFollowersItem.setOnClickListener(this);
        vStatusesItem.setOnClickListener(this);

        vStatusesRow.setOnClickListener(this);
        vFavoritesRow.setOnClickListener(this);
        vFollowingRow.setOnClickListener(this);
        vFollowersRow.setOnClickListener(this);
        vPhotosRow.setOnClickListener(this);
    }

    private void updateHeader(final UserModel user) {
        headerName.setText(user.getScreenName());
        headerLock.setVisibility(user.isProtect() ? View.VISIBLE : View.INVISIBLE);
        String headerImageUrl = user.getProfileImageUrl();
        ImageLoader.getInstance().displayImage(headerImageUrl, headerImage);
    }

    private void updateStatistics(final UserModel user) {
        tvFollowingItemTitle.setText("" + user.getFriendsCount());
        tvFollowersItemTitle.setText("" + user.getFollowersCount());
        tvStatusesItemTitle.setText("" + user.getStatusesCount());

        tvStatusesRowValue.setText("" + user.getStatusesCount());
        tvFavoritesRowValue.setText("" + user.getFavouritesCount());
        tvFollowingRowValue.setText("" + user.getFriendsCount());
        tvFollowersRowValue.setText("" + user.getFollowersCount());
    }

    public void updateFollowState(boolean following) {
        btnFollowState.setVisibility(View.VISIBLE);
        btnFollowState.setBackgroundResource(following ? R.drawable.button_follow_on : R.drawable.button_follow_off);
        btnFollowState.setTextColor(following ? mFollowOnColor : mFollowOffColor);
        btnFollowState.setText(following ? "正在关注" : "添加关注");
    }

    private void updateDescription(final UserModel user) {
        String gender = user.getGender();
        String birthday = user.getBirthday();
        String location = user.getLocation();
        String url = user.getUrl();
        String time = DateTimeHelper.formatDateOnly(user.getTime());
        String description = user.getDescription();

        //TODO 如果有填写网址，MENU菜单里添加打开 个人网站 选项
        // TODO 个人页面需要发消息，发私信选项
        //TODO 添加箭头


        StringBuilder simpleBuilder = new StringBuilder();
        simpleBuilder.append("性别：").append(StringHelper.isEmpty(gender) ? "未知" : gender).append("\n");

        if (!StringHelper.isEmpty(location)) {
            simpleBuilder.append("位置：").append(location).append("\n");
        } else if (!StringHelper.isEmpty(birthday)) {
            simpleBuilder.append("生日：").append(birthday).append("\n");
        } else {
            simpleBuilder.append("注册时间：")
                    .append(time).append("\n");
        }

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

        headerIntro.setText(simpleBuilder.toString());
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
        headerState.setVisibility(follow ? View.VISIBLE : View.GONE);
    }

    public void setExpanded(boolean expanded) {
        if (expanded == mExpanded) {
            return;
        }
        mExpanded = expanded;
        headerIntro.setMaxLines(mExpanded ? Integer.MAX_VALUE : 2);
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
        } else if (v == btnFollowState) {
            onProfileItemClick(TYPE_FOLLOW_STATE);
        } else if (v == vPhotosRow) {
            onProfileItemClick(TYPE_PHOTOS);
        } else if (v == vFollowingRow) {
            onProfileItemClick(TYPE_FOLLOWING);
        } else if (v == vFollowersRow) {
            onProfileItemClick(TYPE_FOLLOWERS);
        } else if (v == vStatusesRow) {
            onProfileItemClick(TYPE_STATUSES);
        } else if (v == vFavoritesRow) {
            onProfileItemClick(TYPE_FAVORATIES);
        }
    }

}
