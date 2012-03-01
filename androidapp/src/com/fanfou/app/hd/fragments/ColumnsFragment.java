/**
 * 
 */
package com.fanfou.app.hd.fragments;

import java.util.ArrayList;
import java.util.List;

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.R;
import com.fanfou.app.hd.UIAbout;
import com.fanfou.app.hd.UIConversation;
import com.fanfou.app.hd.UIDrafts;
import com.fanfou.app.hd.UIEditProfile;
import com.fanfou.app.hd.UIProfile;
import com.fanfou.app.hd.UISearch;
import com.fanfou.app.hd.UISetting;
import com.fanfou.app.hd.UIUserList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author mcxiaoke
 * @version 1.0 2012-2-29 上午10:25:45
 * 
 */
public class ColumnsFragment extends AbstractListFragment {
	private ListView mListView;
	private ColumnsAdapter mAdapter;
	private List<ActionColumn> mColumns;

	public static ColumnsFragment newInstance() {
		return new ColumnsFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mColumns = new ArrayList<ActionColumn>();
		fillColumns();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fm_list, container, false);
		mListView = (ListView) v;
		mListView.setOnItemClickListener(this);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mAdapter = new ColumnsAdapter(getActivity(), mColumns);
		mListView.setAdapter(mAdapter);
	}

	private void fillColumns() {
		Intent profile=new Intent(getActivity(), UIProfile.class);
		profile.putExtra("id", App.getAccount());
		ActionColumn myProfile = new ActionColumn("我的空间", profile);
		
		Intent friends=new Intent(getActivity(), UIUserList.class);
		friends.putExtra("id", App.getAccount());
		ActionColumn myFriends = new ActionColumn("我的好友", friends);
		
		Intent edit=new Intent(getActivity(), UIEditProfile.class);
		ActionColumn editProfile = new ActionColumn("编辑资料", edit);
		
//		Intent conver=new Intent(getActivity(), UIConversationList.class);
		ActionColumn conversation = new ActionColumn("我的私信", null);
		
		Intent search=new Intent(getActivity(), UISearch.class);
		ActionColumn trends = new ActionColumn("热词和搜索", search);
		
		Intent option=new Intent(getActivity(), UISetting.class);
		ActionColumn setting = new ActionColumn("程序设置", option);

		Intent record=new Intent(getActivity(), UIDrafts.class);
		ActionColumn records = new ActionColumn("我的草稿箱", record);
		
		
		ActionColumn digest = new ActionColumn("饭否语录", null);
		
		Intent ab=new Intent(getActivity(), UIAbout.class);
		ActionColumn about = new ActionColumn("关于饭否", ab);

		mColumns.add(myProfile);
		mColumns.add(myFriends);
		mColumns.add(editProfile);
		mColumns.add(conversation);
		mColumns.add(trends);
		mColumns.add(setting);
		mColumns.add(records);
		mColumns.add(digest);
		mColumns.add(about);

	}

	private static class ActionColumn {
		private final String text;
		private final Intent intent;

		public ActionColumn(String text, Intent intent) {
			this.text = text;
			this.intent = intent;
		}
	}

	private static class ColumnsAdapter extends BaseAdapter {
		// TODO 可以保存列表到数据库，这个列表可以考虑自定义，给一些预定义选项可供用户选择，添加或者删除
		// TODO 首页活动页面也可以提供给用户自定义，需要考虑一个完善的方案
		// TODO 这个选择列表和滑动页面属于同一个大的List，可自定义，选择，添加，删除，个数可限定最小为3，最大为7，之类的
		// TODO 或者可以在这个Columns里面选择一些放入滑动页面，所以还需要加一个标志，标明是否在滑动页面已经显示
		// TODO 这些等工作做完之后再考虑
		private Context context;
		private LayoutInflater inflater;
		private List<ActionColumn> columns;

		public ColumnsAdapter(Context context, List<ActionColumn> data) {
			super();
			this.context = context;
			this.inflater = LayoutInflater.from(context);
			if (data != null) {
				columns = data;
			} else {
				columns = new ArrayList<ActionColumn>();
			}
		}

		@Override
		public int getCount() {
			return columns.size();
		}

		@Override
		public ActionColumn getItem(int position) {
			return columns.get(position);
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

			holder.text.setText(columns.get(position).text);

			return convertView;
		}

		private static class ViewHolder {
			TextView text;

			public ViewHolder(View base) {
				text = (TextView) base.findViewById(R.id.text);
			}
		}

	}
	
	private static void setBold(TextView view){
		TextPaint tp=view.getPaint();
		tp.setFakeBoldText(true);
	}

	@Override
	public BaseAdapter getAdapter() {
		return mAdapter;
	}

	@Override
	public void startRefresh() {
	}

	@Override
	public ListView getListView() {
		return mListView;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		final ActionColumn action=(ActionColumn) parent.getItemAtPosition(position);
		if(action!=null&&action.intent!=null){
			getActivity().startActivity(action.intent);
		}

	}

}
