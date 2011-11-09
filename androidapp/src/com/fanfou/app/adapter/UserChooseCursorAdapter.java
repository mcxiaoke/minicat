package com.fanfou.app.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.database.Cursor;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfou.app.R;
import com.fanfou.app.api.User;
import com.fanfou.app.util.DateTimeHelper;
import com.fanfou.app.util.StringHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.21
 * @version 1.1 2011.10.24
 * @version 1.5 2011.10.25
 * @version 1.6 2011.11.09
 * 
 */
public class UserChooseCursorAdapter extends BaseCursorAdapter {
	private static final String tag = UserChooseCursorAdapter.class
			.getSimpleName();

	private ArrayList<Boolean> mStates;
	private HashMap<Integer, Boolean> mStateMap;

	private void log(String message) {
		Log.e(tag, message);
	}

	public UserChooseCursorAdapter(Context context, Cursor c) {
		super(context, c, false);
		init();
	}

	public UserChooseCursorAdapter(Context context, Cursor c,
			boolean autoRequery) {
		super(context, c, autoRequery);
		init();
	}

	private void init() {
		mStates = new ArrayList<Boolean>();
		mStateMap = new HashMap<Integer, Boolean>();
	}

	public ArrayList<Boolean> getCheckedStates() {
		return mStates;
	}

	public void setItemChecked(int position, boolean checked) {
		mStateMap.put(position, checked);
	}

	@Override
	int getLayoutId() {
		return R.layout.list_item_chooseuser;
	}

	private void setTextStyle(ViewHolder holder) {
		int fontSize = getFontSize();
		holder.nameText.setTextSize(fontSize);
		TextPaint tp = holder.nameText.getPaint();
		tp.setFakeBoldText(true);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater.inflate(getLayoutId(), null);
		ViewHolder holder = new ViewHolder(view);
		setHeadImage(mContext, holder.headIcon);
//		setTextStyle(holder);
		view.setTag(holder);
		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		View row = view;
		final User u = User.parse(cursor);

		final ViewHolder holder = (ViewHolder) row.getTag();

		if (!isTextMode()) {
			mLoader.set(u.profileImageUrl, holder.headIcon,
					R.drawable.default_head);
		}
		
		if (u.protect) {
			holder.lockIcon.setVisibility(View.VISIBLE);
		} else {
			holder.lockIcon.setVisibility(View.GONE);
		}
		holder.nameText.setText(u.screenName);
		holder.idText.setText("("+u.id+")");
		holder.genderText.setText(u.gender);
		holder.locationText.setText(u.location);

		Boolean b = mStateMap.get(cursor.getPosition());
		if (b == null || b == Boolean.FALSE) {
			holder.checkBox.setChecked(false);
		} else {
			holder.checkBox.setChecked(true);
		}
	}

	public void setChecked(int position) {
	}

	private static class ViewHolder {

		final ImageView headIcon;
		final ImageView lockIcon;
		final TextView nameText;
		final TextView idText;
		final TextView genderText;
		final TextView locationText;
		final CheckBox checkBox;

		ViewHolder(View base) {
			this.headIcon = (ImageView) base.findViewById(R.id.item_user_head);
			this.lockIcon = (ImageView) base.findViewById(R.id.item_user_flag);
			this.nameText = (TextView) base.findViewById(R.id.item_user_name);
			this.genderText = (TextView) base.findViewById(R.id.item_user_gender);
			this.locationText = (TextView) base.findViewById(R.id.item_user_location);
			this.idText = (TextView) base.findViewById(R.id.item_user_id);
			this.checkBox = (CheckBox) base
					.findViewById(R.id.item_user_checkbox);
		}
	}

}
