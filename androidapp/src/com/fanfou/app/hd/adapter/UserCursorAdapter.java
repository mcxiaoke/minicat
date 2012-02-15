package com.fanfou.app.hd.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfou.app.hd.R;
import com.fanfou.app.hd.api.User;
import com.fanfou.app.hd.fragments.widget.ActionManager;
import com.fanfou.app.hd.util.DateTimeHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.04
 * @version 1.5 2011.10.24
 * @version 1.6 2011.11.07
 * @version 1.7 2011.11.09
 * 
 */
public class UserCursorAdapter extends BaseCursorAdapter {
	private static final String TAG = UserCursorAdapter.class.getSimpleName();

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
		holder.genderText.setTextSize(fontSize);
		holder.locationText.setTextSize(fontSize);
		holder.nameText.setTextSize(fontSize);
		holder.dateText.setTextSize(fontSize - 2);
		TextPaint tp = holder.nameText.getPaint();
		tp.setFakeBoldText(true);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater.inflate(getLayoutId(), null);
		ViewHolder holder = new ViewHolder(view);
		setHeadImage(mContext, holder.headIcon);
		// setTextStyle(holder);
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
			holder.headIcon.setTag(u.profileImageUrl);
			mLoader.displayImage(u.profileImageUrl, holder.headIcon,
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
		holder.idText.setText("(" + u.id + ")");
		holder.dateText.setText(DateTimeHelper.formatDateOnly(u.createdAt));
		holder.genderText.setText(u.gender);
		holder.locationText.setText(u.location);

	}

	private static class ViewHolder {

		final ImageView headIcon;
		final ImageView lockIcon;
		final TextView nameText;
		final TextView idText;
		final TextView dateText;
		final TextView genderText;
		final TextView locationText;

		ViewHolder(View base) {
			this.headIcon = (ImageView) base.findViewById(R.id.item_user_head);
			this.lockIcon = (ImageView) base.findViewById(R.id.item_user_flag);
			this.nameText = (TextView) base.findViewById(R.id.item_user_name);
			this.idText = (TextView) base.findViewById(R.id.item_user_id);
			this.dateText = (TextView) base.findViewById(R.id.item_user_date);
			this.genderText = (TextView) base
					.findViewById(R.id.item_user_gender);
			this.locationText = (TextView) base
					.findViewById(R.id.item_user_location);

		}
	}

}
