package com.fanfou.app.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfou.app.R;
import com.fanfou.app.api.User;
import com.fanfou.app.ui.ActionManager;
import com.fanfou.app.util.DateTimeHelper;
import com.fanfou.app.util.StringHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.04
 * @version 1.5 2011.10.24
 * @version 1.6 2011.11.07
 * 
 */
public class UserCursorAdapter extends BaseCursorAdapter {
	private static final String tag = UserCursorAdapter.class.getSimpleName();

	private void log(String message) {
		Log.e(tag, message);
	}

	public UserCursorAdapter(Context context, Cursor c) {
		super(context, c, false);
	}

	public UserCursorAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
	}

	@Override
	int getLayoutId() {
		return R.layout.list_item_user;
	}

	private void setTextStyle(ViewHolder holder) {
		int fontSize = getFontSize();
		holder.contentText.setTextSize(fontSize);
		holder.nameText.setTextSize(fontSize);
		holder.dateText.setTextSize(fontSize - 3);
		TextPaint tp = holder.nameText.getPaint();
		tp.setFakeBoldText(true);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater.inflate(getLayoutId(), null);
		ViewHolder holder = new ViewHolder(view);
		setHeadImage(mContext, holder.headIcon);
		setTextStyle(holder);
		view.setTag(holder);
		// bindView(view, context, cursor);
		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		View row = view;
		final ViewHolder holder = (ViewHolder) row.getTag();
		final User u = User.parse(cursor);
		if (!isTextMode()) {
			mLoader.set(u.profileImageUrl, holder.headIcon,
					R.drawable.default_head);
			holder.headIcon.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (u != null) {
						ActionManager.doProfile(mContext, u);
					}
				}
			});
		}
		if (u.protect) {
			holder.lockIcon.setVisibility(View.VISIBLE);
		} else {
			holder.lockIcon.setVisibility(View.GONE);
		}
		holder.nameText.setText(u.screenName);

		if (!StringHelper.isEmpty(u.lastStatusId)) {
			holder.contentText.setText(u.lastStatusText);
			String dateStr = DateTimeHelper.formatDate(u.lastStatusCreatedAt);
			holder.dateText.setText(dateStr);
		} else {
			holder.contentText.setText("");
			holder.dateText.setText("");
		}

	}

	private static class ViewHolder {

		ImageView headIcon = null;
		ImageView lockIcon = null;
		TextView nameText = null;
		TextView contentText = null;
		TextView dateText = null;

		ViewHolder(View base) {
			this.headIcon = (ImageView) base.findViewById(R.id.item_user_head);
			this.lockIcon = (ImageView) base.findViewById(R.id.item_user_flag);
			this.nameText = (TextView) base.findViewById(R.id.item_user_name);
			this.contentText = (TextView) base
					.findViewById(R.id.item_user_text);
			this.dateText = (TextView) base.findViewById(R.id.item_user_date);
		}
	}

}
