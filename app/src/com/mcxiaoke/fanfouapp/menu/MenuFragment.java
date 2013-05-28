package com.mcxiaoke.fanfouapp.menu;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.mcxiaoke.fanfouapp.R;
import com.mcxiaoke.fanfouapp.app.AppContext;
import com.mcxiaoke.fanfouapp.app.UIHome;

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
        Log.d(TAG, message);
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

    private ListView mListView;
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
//        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (ListView) getView().findViewById(android.R.id.list);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
        MenuItemResource logout = MenuItemResource.newBuilder()
                .id(MENU_ID_LOGOUT).text("切换帐号")
                .iconId(R.drawable.ic_item_logout).highlight(false).build();

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
        mMenuItems.add(logout);
        mMenuItems.add(option);
        // mMenuItems.add(theme);
//        mMenuItems.add(blog);
        mMenuItems.add(about);

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
            holder.icon.setVisibility(View.GONE);
            holder.text.setText(item.text);

            debug("getView ,position=" + position + " item=" + item);

            if (position == currentPosition && item.highlight) {
                convertView.setBackgroundResource(R.drawable.selector_drawer_menu_light_checked);
//                holder.text.setTextColor(context.getResources()
//                        .getColorStateList(R.color.light_blue_text_color));
            } else {
                convertView.setBackgroundColor(0);
//                holder.text.setTextColor(context.getResources()
//                        .getColorStateList(R.color.text_white));
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
