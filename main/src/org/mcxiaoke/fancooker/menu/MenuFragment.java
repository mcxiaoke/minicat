package org.mcxiaoke.fancooker.menu;

import java.util.ArrayList;
import java.util.List;

import org.mcxiaoke.fancooker.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author mcxiaoke
 * @version 1.0 2012-2-29 上午10:25:45
 * @version 2.0 2012.04.23
 * @version 2.1 2012.04.24
 * 
 */
public class MenuFragment extends ListFragment {

	private static final int MENU_ID = 1000;
	public static final int MENU_ID_HOME = MENU_ID + 1;
	public static final int MENU_ID_PROFILE = MENU_ID + 2;
	public static final int MENU_ID_MESSAGE = MENU_ID + 3;
	public static final int MENU_ID_TOPIC = MENU_ID + 4;
	public static final int MENU_ID_RECORD = MENU_ID + 5;
	public static final int MENU_ID_DIGEST = MENU_ID + 6;
	public static final int MENU_ID_THEME = MENU_ID + 7;
	public static final int MENU_ID_OPTION = MENU_ID + 8;
	public static final int MENU_ID_ABOUT = MENU_ID + 9;

	private ListView mListView;
	private MenuItemListAdapter mMenuAdapter;
	private List<MenuItemResource> mMenuItems;
	private MenuCallback mCallback;

	public static MenuFragment newInstance() {
		return new MenuFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMenuItems = new ArrayList<MenuItemResource>();
		fillColumns();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallback = (MenuCallback) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mListView = getListView();
		mListView.setBackgroundColor(0xff333333);
		mListView.setDivider(getResources().getDrawable(
				R.drawable.menu_list_divider));
		mMenuAdapter = new MenuItemListAdapter(getActivity(), mMenuItems);
		setListAdapter(mMenuAdapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		final MenuItemResource menuItem = (MenuItemResource) l
				.getItemAtPosition(position);
		if (menuItem != null && mCallback != null) {
			mCallback.onMenuItemSelected(position, menuItem);
		}
	}

	private void fillColumns() {
		mMenuItems.add(new MenuItemResource(MENU_ID_HOME, "我的首页",
				R.drawable.ic_item_announce));
		mMenuItems.add(new MenuItemResource(MENU_ID_PROFILE, "我的空间",
				R.drawable.ic_item_profile));
		mMenuItems.add(new MenuItemResource(MENU_ID_MESSAGE, "我的私信",
				R.drawable.ic_item_feedback));
		mMenuItems.add(new MenuItemResource(MENU_ID_TOPIC, "热词和搜索",
				R.drawable.ic_item_topic));
		mMenuItems.add(new MenuItemResource(MENU_ID_RECORD, "草稿箱",
				R.drawable.ic_item_record));
		mMenuItems.add(new MenuItemResource(MENU_ID_DIGEST, "饭否语录",
				R.drawable.ic_item_digest));
		mMenuItems.add(new MenuItemResource(MENU_ID_THEME, "主题切换",
				R.drawable.ic_item_theme));
		mMenuItems.add(new MenuItemResource(MENU_ID_OPTION, "程序设置",
				R.drawable.ic_item_option));
		mMenuItems.add(new MenuItemResource(MENU_ID_ABOUT, "关于饭否",
				R.drawable.ic_item_info));

	}

	private static class MenuItemListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<MenuItemResource> mItems;

		public MenuItemListAdapter(Context context, List<MenuItemResource> data) {
			this.inflater = LayoutInflater.from(context);
			mItems = new ArrayList<MenuItemResource>();
			if (data != null && data.size() > 0) {
				mItems.addAll(data);
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
				convertView = inflater.inflate(R.layout.list_item_column, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final MenuItemResource item = mItems.get(position);
			holder.icon.setImageResource(item.getIconId());
			holder.text.setText(item.getText());
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
