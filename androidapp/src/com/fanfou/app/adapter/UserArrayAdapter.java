package com.fanfou.app.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfou.app.R;
import com.fanfou.app.api.User;
import com.fanfou.app.util.DateTimeHelper;

public class UserArrayAdapter extends BaseArrayAdapter<User> {

	private static final String tag = UserArrayAdapter.class.getSimpleName();

	private void log(String message) {
		Log.e(tag, message);
	}

	private List<User> mUsers;

	public UserArrayAdapter(Context context, List<User> users) {
		super(context, users);
		if (users == null) {
			mUsers = new ArrayList<User>();
		} else {
			this.mUsers = users;
		}
	}

	public void updateDataAndUI(List<User> us) {
		this.mUsers = us;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mUsers.size();
	}

	@Override
	public User getItem(int position) {
		return mUsers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(getLayoutId(), null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final User u = mUsers.get(position);

		mLoader.set(u.profileImageUrl, holder.headIcon, R.drawable.default_head);
		if (u.protect) {
			holder.lockIcon.setVisibility(View.VISIBLE);
		} else {
			holder.lockIcon.setVisibility(View.GONE);
		}
		holder.nameText.setText(u.screenName);
		holder.idText.setText("(" + u.id + ")");
		holder.contentText.setText(u.lastStatusText);
		// log("userid="+u.id+" u.createdAt="+u.createdAt);
		holder.dateText.setText("创建时间："
				+ DateTimeHelper.formatDateOnly(u.createdAt));

		return convertView;
	}

	static class ViewHolder {

		ImageView headIcon = null;
		ImageView lockIcon = null;
		TextView nameText = null;
		TextView idText = null;
		TextView contentText = null;
		TextView dateText = null;

		ViewHolder(View base) {
			this.headIcon = (ImageView) base.findViewById(R.id.item_user_head);
			this.lockIcon = (ImageView) base.findViewById(R.id.item_user_flag);
			this.nameText = (TextView) base.findViewById(R.id.item_user_name);
			this.idText = (TextView) base.findViewById(R.id.item_user_id);
			this.contentText = (TextView) base
					.findViewById(R.id.item_user_text);
			this.dateText = (TextView) base.findViewById(R.id.item_user_date);
		}
	}

	@Override
	int getLayoutId() {
		return R.layout.list_item_user;
	}

}
