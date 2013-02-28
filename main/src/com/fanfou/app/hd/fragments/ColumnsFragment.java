package com.fanfou.app.hd.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.R;
import com.fanfou.app.hd.controller.UIController;
import com.fanfou.app.hd.task.CheckUpdateTask;
import com.fanfou.app.hd.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2012-2-29 上午10:25:45
 * @version 2.0 2012.04.23
 * @version 2.1 2012.04.24
 * 
 */
public class ColumnsFragment extends AbstractListFragment {
	private ListView mListView;
	private ColumnsAdapter mAdapter;
	private List<Action> mColumns;

	public static ColumnsFragment newInstance() {
		return new ColumnsFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mColumns = new ArrayList<Action>();
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
		// TODO need more icons
		// edit profile action
		// user search action

		mColumns.add(new ProfileAction());;
		mColumns.add(new RecordsAction());
		mColumns.add(new TopicAction());
		mColumns.add(new BlogAction());
		mColumns.add(new FeedbackAction());
		mColumns.add(new UpdateAction());
		mColumns.add(new OptionAction());
//		mColumns.add(new ThemeAction());
		mColumns.add(new AboutAction());

	}

	private static class ColumnsAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<Action> columns;

		public ColumnsAdapter(Context context, List<Action> data) {
			super();
			this.inflater = LayoutInflater.from(context);
			columns = new ArrayList<Action>();
			if (data != null && data.size() > 0) {
				columns.addAll(data);
			}
		}

		@Override
		public int getCount() {
			return columns.size();
		}

		@Override
		public Action getItem(int position) {
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

			final Action action = columns.get(position);
			holder.icon.setImageResource(action.getIconId());
			holder.text.setText(action.getName());

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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final Action action = (Action) parent.getItemAtPosition(position);
		action.perform(getActivity());
	}

	@Override
	public String getTitle() {
		return "栏目";
	}

	private static interface Action {
		public int getIconId();

		public String getName();

		public void perform(Activity context);
	}

	private static class ProfileAction implements Action {

		@Override
		public int getIconId() {
			return R.drawable.ic_item_profile;
		}

		@Override
		public String getName() {
			return "我的空间";
		}

		@Override
		public void perform(Activity context) {
			UIController.showProfile(context, App.getAccount());
		}

	}

	private static class OptionAction implements Action {

		@Override
		public int getIconId() {
			return R.drawable.ic_item_option;
		}

		@Override
		public String getName() {
			return "程序设置";
		}

		@Override
		public void perform(Activity context) {
			UIController.showOption(context);

		}

	}

	private static class TopicAction implements Action {

		@Override
		public int getIconId() {
			return R.drawable.ic_item_topic;
		}

		@Override
		public String getName() {
			return "热词和搜索";
		}

		@Override
		public void perform(Activity context) {
			UIController.showTopic(context);
		}

	}

	private static class BlogAction implements Action {

		@Override
		public int getIconId() {
			return R.drawable.ic_item_digest;
		}

		@Override
		public String getName() {
			return "饭否语录";
		}

		@Override
		public void perform(Activity context) {
			UIController.showFanfouBlog(context);
		}

	}

	private static class AnnounceAction implements Action {

		@Override
		public int getIconId() {
			return R.drawable.ic_item_announce;
		}

		@Override
		public String getName() {
			return "技术支持账号";
		}

		@Override
		public void perform(Activity context) {
			UIController.showAnnounce(context);

		}

	}

	private static class ThemeAction implements Action {

		@Override
		public int getIconId() {
			return R.drawable.ic_item_theme;
		}

		@Override
		public String getName() {
			return "更换主题(暂未实现)";
		}

		@Override
		public void perform(Activity context) {
			Utils.notify(context, "暂未实现");
		}

	}

	private static class AboutAction implements Action {

		@Override
		public int getIconId() {
			return R.drawable.ic_item_info;
		}

		@Override
		public String getName() {
			return "关于饭否";
		}

		@Override
		public void perform(Activity context) {
			UIController.showAbout(context);
		}

	}

	private static class FeedbackAction implements Action {

		@Override
		public int getIconId() {
			return R.drawable.ic_item_feedback;
		}

		@Override
		public String getName() {
			return "意见反馈";
		}

		@Override
		public void perform(Activity context) {
			String text = "@Android客户端 ";
			UIController.showWrite(context, text);
		}

	}

	private static class UpdateAction implements Action {

		@Override
		public int getIconId() {
			return R.drawable.ic_item_update;
		}

		@Override
		public String getName() {
			return "检查更新";
		}

		@Override
		public void perform(Activity context) {
			checkUpdate(context);
		}

		private void checkUpdate(Context context) {
			new CheckUpdateTask(context).execute();
		}

	}

	private static class RecordsAction implements Action {

		@Override
		public int getIconId() {
			return R.drawable.ic_item_record;
		}

		@Override
		public String getName() {
			return "草稿箱";
		}

		@Override
		public void perform(Activity context) {
			UIController.showRecords(context);
		}

	}

	private static class PublicTimelineAction implements Action {

		@Override
		public int getIconId() {
			return R.drawable.ic_item_public;
		}

		@Override
		public String getName() {
			return "随便看看";
		}

		@Override
		public void perform(Activity context) {
			UIController.showPublicTimeline(context);
		}

	}

}
