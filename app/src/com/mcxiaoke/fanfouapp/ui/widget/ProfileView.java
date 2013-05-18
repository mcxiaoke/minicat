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
    private static final String TAG = ProfileView.class.getSimpleName();

    private ScrollView vContent;

    private ViewGroup header;
    private ImageView headerImage;

    private TextView headerName;

    private TextView headerState;

    private ImageView headerLock;

    private TextView headerIntro;
    private TextView headerExpand;


    private TextView tvStatusesLeft;

    private TextView tvFavoritesLeft;

    private TextView tvFriendsLeft;

    private TextView tvFollowersLeft;

    private TextView tvPhotosLeft;

    private TextView tvStatusesRight;

    private TextView tvFavoritesRight;

    private TextView tvFriendsRight;

    private TextView tvFollowersRight;

    private TextView tvPhotosRight;

    private View vStatuses;
    private View vFavorites;
    private View vFriends;
    private View vFollowers;
    private View vPhotos;

//    private TextView tvDescription;

    private boolean mExpanded;


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
        headerLock = (ImageView) findViewById(R.id.header_lock);
        headerIntro = (TextView) findViewById(R.id.header_intro);
        headerExpand = (TextView) findViewById(R.id.header_expand);

        vStatuses = findViewById(R.id.button1);
        vFavorites = findViewById(R.id.button2);

        vFriends = findViewById(R.id.button3);
        vFollowers = findViewById(R.id.button4);
        vPhotos = findViewById(R.id.button5);

        tvStatusesLeft = (TextView) findViewById(R.id.text_left1);
        tvFavoritesLeft = (TextView) findViewById(R.id.text_left2);
        tvFriendsLeft = (TextView) findViewById(R.id.text_left3);
        tvFollowersLeft = (TextView) findViewById(R.id.text_left4);
        tvPhotosLeft = (TextView) findViewById(R.id.text_left5);

        tvStatusesLeft.setText("消息");
        tvFavoritesLeft.setText("收藏");
        tvFriendsLeft.setText("关注");
        tvFollowersLeft.setText("被关注");
        tvPhotosLeft.setText("查看相册");

        tvStatusesRight = (TextView) findViewById(R.id.text_right1);
        tvFavoritesRight = (TextView) findViewById(R.id.text_right2);
        tvFriendsRight = (TextView) findViewById(R.id.text_right3);
        tvFollowersRight = (TextView) findViewById(R.id.text_right4);
        tvPhotosRight = (TextView) findViewById(R.id.text_right5);

//        tvDescription = (TextView) root.findViewById(R.id.description);

    }

    private void setListeners() {

        header.setOnClickListener(this);
//        headerLock.setOnClickListener(this);
//        headerExpand.setOnClickListener(this);

        vStatuses.setOnClickListener(this);
        vFavorites.setOnClickListener(this);
        vFriends.setOnClickListener(this);
        vFollowers.setOnClickListener(this);
        vPhotos.setOnClickListener(this);
    }

    private void updateHeader(final UserModel user) {
        headerName.setText(user.getScreenName());
        headerLock.setVisibility(user.isProtect() ? View.VISIBLE : View.INVISIBLE);
        headerIntro.setText(user.getDescription());
        String headerImageUrl = user.getProfileImageUrl();
        ImageLoader.getInstance().displayImage(headerImageUrl, headerImage);
    }

    private void updateStatistics(final UserModel user) {
        tvStatusesRight.setText("" + user.getStatusesCount());
        tvFavoritesRight.setText("" + user.getFavouritesCount());
        tvFriendsRight.setText("" + user.getFriendsCount());
        tvFollowersRight.setText("" + user.getFollowersCount());
    }

    private void updateDescription(final UserModel user) {

        StringBuffer sb = new StringBuffer();

        sb.append("自述：").append("\n");
        sb.append(user.getDescription()).append("\n\n");

        if (!StringHelper.isEmpty(user.getGender())) {
            sb.append("性别：").append(user.getGender()).append("\n");
        }
        if (!StringHelper.isEmpty(user.getBirthday())) {
            sb.append("生日：").append(user.getBirthday()).append("\n");
        }
        if (!StringHelper.isEmpty(user.getLocation())) {
            sb.append("位置：").append(user.getLocation()).append("\n");
        }

        if (!StringHelper.isEmpty(user.getUrl())) {
            sb.append("网站：").append(user.getUrl()).append("\n");
        }

        sb.append("注册时间：")
                .append(DateTimeHelper.formatDateOnly(user.getTime())).append("\n");


        headerIntro.setText(sb.toString());

//        tvDescription.setText(sb.toString());

    }


    public void setContent(final UserModel user) {
        updateHeader(user);
        updateStatistics(user);
        updateDescription(user);
    }

    public void setFollowState(boolean follow) {
        headerState.setText(follow ? "正在关注你" : "没有关注你");
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
        if (id == R.id.header) {
            setExpanded(!mExpanded);
        }
    }

}
