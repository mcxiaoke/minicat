package com.mcxiaoke.minicat.menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.app.UIHome;
import com.mcxiaoke.minicat.config.AccountInfo;
import com.mcxiaoke.minicat.util.LogUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mcxiaoke
 * @version 2.1 2012.04.24
 */
public class MenuFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final boolean DEBUG = AppContext.DEBUG;
    private static final String TAG = MenuFragment.class.getSimpleName();

    static void debug(String message) {
        LogUtil.v(TAG, message);
    }

    private static final int MENU_ID = 1000;
    public static final int MENU_ID_HOME = MENU_ID + 1;
    public static final int MENU_ID_PROFILE = MENU_ID + 2;
    public static final int MENU_ID_MESSAGE = MENU_ID + 3;
    public static final int MENU_ID_TOPIC = MENU_ID + 4;
    public static final int MENU_ID_RECORD = MENU_ID + 5;
    public static final int MENU_ID_DIGEST = MENU_ID + 6;
    public static final int MENU_ID_THEME = MENU_ID + 7;
    public static final int MENU_ID_LOGOUT = MENU_ID + 8;
    public static final int MENU_ID_OPTION = MENU_ID + 9;
    public static final int MENU_ID_ABOUT = MENU_ID + 10;
    public static final int MENU_ID_DEBUG = MENU_ID + 99;

    private ViewGroup mHeaderView;
    private ImageView mHeaderImage;
    private TextView mHeaderText;
    private ListView mListView;
    private TextView mFooterTextView1;
    private TextView mFooterTextView2;
    private MenuItemListAdapter mMenuAdapter;
    private List<MenuItemResource> mMenuItems;
    private MenuCallback mCallback;
    private SparseBooleanArray mCheckedState;
    private UIHome mUiHome;

    public static MenuFragment newInstance() {
        return new MenuFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMenuItems = new ArrayList<MenuItemResource>();
        mCheckedState = new SparseBooleanArray();
        fillColumns();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (MenuCallback) activity;
        mUiHome = (UIHome) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fm_menu, null, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {

            }
        });
        mHeaderView = ButterKnife.findById(view, R.id.header);
        mHeaderImage = ButterKnife.findById(view, R.id.header_image);
        mHeaderText = ButterKnife.findById(view, R.id.header_text);
        mListView = (ListView) getView().findViewById(android.R.id.list);
        mFooterTextView1 = (TextView) getView().findViewById(android.R.id.text1);
        mFooterTextView2 = (TextView) getView().findViewById(android.R.id.text2);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final AccountInfo account = AppContext.getAccountInfo();
        if (account != null) {
            mHeaderView.setVisibility(View.VISIBLE);
            LogUtil.e(TAG, "profile image url:" + account.getProfileImage());
            ImageLoader.getInstance().displayImage(account.getProfileImage(), mHeaderImage);
            mHeaderText.setText(account.getScreenName());
            mHeaderImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    onMenuLogoutClick();
                }
            });
        } else {
            mHeaderView.setVisibility(View.GONE);
        }

        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setSelector(getResources().getDrawable(
                R.drawable.selector_drawer_menu));

//        mListView.setDivider(getResources().getDrawable(
//                R.drawable.sliding_menu_list_divider));
        mMenuAdapter = new MenuItemListAdapter(getActivity(), mMenuItems);
        mListView.setOnItemClickListener(this);
        mListView.setDrawSelectorOnTop(true);
        mListView.setAdapter(mMenuAdapter);
        mListView.setItemChecked(0, true);
        mFooterTextView1.setText(AppContext.versionName + " Build " + AppContext.versionCode + (AppContext.DEBUG ? " Debug" : ""));

    }

    protected void onMenuLogoutClick() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("@" + AppContext.getScreenName());
        builder.setMessage("确定注销当前登录帐号吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                AppContext.doLogin(getActivity());
                getActivity().finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out_long);
        mFooterTextView2.startAnimation(fadeOut);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFooterTextView2.clearAnimation();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final MenuItemResource menuItem = (MenuItemResource) parent
                .getItemAtPosition(position);
        debug("on item click ,position=" + position + " item=" + menuItem);
        ListView listView = (ListView) parent;
        listView.setItemChecked(position, true);
        if (menuItem != null) {
            mCheckedState.clear();
            mCheckedState.put(position, true);
            if (menuItem.highlight) {
                mMenuAdapter.setCurrentPosition(position);
            }
            mMenuAdapter.notifyDataSetChanged();
            if (mCallback != null) {
                mCallback.onMenuItemSelected(position, menuItem);
            }
        }
    }

    private void fillColumns() {
        MenuItemResource home = MenuItemResource.newBuilder().id(MENU_ID_HOME)
                .text("首页").iconId(R.drawable.ic_menu_home).highlight(true)
                .build();

        MenuItemResource profile = MenuItemResource.newBuilder()
                .id(MENU_ID_PROFILE).text("我的资料")
                .iconId(R.drawable.ic_item_profile).highlight(true).build();

        MenuItemResource message = MenuItemResource.newBuilder()
                .id(MENU_ID_MESSAGE).text("收件箱")
                .iconId(R.drawable.ic_item_feedback).highlight(true).build();

        MenuItemResource topic = MenuItemResource.newBuilder()
                .id(MENU_ID_TOPIC).text("热门话题")
                .iconId(R.drawable.ic_item_topic).highlight(false).build();

/*        MenuItemResource drafts = MenuItemResource.newBuilder()
                .id(MENU_ID_RECORD).text("草稿箱")
                .iconId(R.drawable.ic_item_record).highlight(false).build();*/
//        MenuItemResource logout = MenuItemResource.newBuilder()
//                .id(MENU_ID_LOGOUT).text("切换帐号")
//                .iconId(R.drawable.ic_item_logout).highlight(false).build();

        MenuItemResource option = MenuItemResource.newBuilder()
                .id(MENU_ID_OPTION).text("设置")
                .iconId(R.drawable.ic_item_option).highlight(false).build();

        //
        // MenuItemResource theme = MenuItemResource.newBuilder()
        // .id(MENU_ID_THEME).text("主题切换")
        // .iconId(R.drawable.ic_item_theme).highlight(false).build();

/*        MenuItemResource blog = MenuItemResource.newBuilder()
                .id(MENU_ID_DIGEST).text("饭否语录")
                .iconId(R.drawable.ic_item_digest).highlight(false).build();*/

        MenuItemResource about = MenuItemResource.newBuilder()
                .id(MENU_ID_ABOUT).text("关于").iconId(R.drawable.ic_item_info)
                .highlight(false).build();

        mMenuItems.add(home);
        mMenuItems.add(profile);
        mMenuItems.add(message);
        mMenuItems.add(topic);
//        mMenuItems.add(drafts);
//        mMenuItems.add(logout);
        mMenuItems.add(option);
        // mMenuItems.add(theme);
//        mMenuItems.add(blog);
//        mMenuItems.add(about);

        if (DEBUG) {
            MenuItemResource test = MenuItemResource.newBuilder()
                    .id(MENU_ID_DEBUG).text("调试模式")
                    .iconId(R.drawable.ic_item_modify).highlight(false).build();
//            mMenuItems.add(test);
        }

    }

    private static class MenuItemListAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater inflater;
        private List<MenuItemResource> mItems;
        private int currentPosition;
        private int mHighlightColor;

        public void setCurrentPosition(int position) {
            this.currentPosition = position;
        }

        public MenuItemListAdapter(Context context, List<MenuItemResource> data) {
            this.context = context;
            this.inflater = LayoutInflater.from(context);
            this.mItems = new ArrayList<MenuItemResource>();
            if (data != null && data.size() > 0) {
                this.mItems.addAll(data);
            }
            this.mHighlightColor = context.getResources().getColor(R.color.holo_primary);
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public MenuItemResource getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item_menu, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final MenuItemResource item = mItems.get(position);
            holder.icon.setImageResource(item.iconId);
//            holder.icon.setVisibility(View.GONE);
            holder.text.setText(item.text);

            if (position == currentPosition && item.highlight) {
                holder.text.setTextColor(mHighlightColor);
            } else {
                holder.text.setTextColor(Color.DKGRAY);
            }
            return convertView;
        }

        private static class ViewHolder {
            ImageView icon;
            TextView text;

            public ViewHolder(View base) {
                icon = (ImageView) base.findViewById(R.id.icon);
                text = (TextView) base.findViewById(R.id.text);
            }
        }

    }

}
