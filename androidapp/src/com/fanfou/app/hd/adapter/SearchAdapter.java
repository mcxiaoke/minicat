package com.fanfou.app.hd.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fanfou.app.hd.R;
import com.fanfou.app.hd.dao.model.Search;

public class SearchAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context context;
	private List<Search> array;

	public SearchAdapter(Context context, List<Search> values) {
		super();
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		array = values;
	}

	@Override
	public int getCount() {
		return array.size();
	}

	@Override
	public Search getItem(int position) {
		return array.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_search, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.name.setText(array.get(position).name);

		return convertView;
	}

	private static class ViewHolder {
		TextView name;

		public ViewHolder(View base) {
			name = (TextView) base.findViewById(R.id.item_status_text);
		}
	}

}
