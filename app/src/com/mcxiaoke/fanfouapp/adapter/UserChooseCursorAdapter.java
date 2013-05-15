package com.mcxiaoke.fanfouapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.TextPaint;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import com.mcxiaoke.fanfouapp.dao.model.UserModel;
import com.mcxiaoke.fanfouapp.R;

import java.util.ArrayList;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.21
 * @version 1.1 2011.10.24
 * @version 1.5 2011.10.25
 * @version 1.6 2011.11.09
 * @version 2.0 2012.02.22
 * @version 2.1 2012.02.27
 * 
 */
public class UserChooseCursorAdapter extends BaseCursorAdapter {

	private ArrayList<Boolean> mStates;
	private SparseBooleanArray mStateMap;

	public UserChooseCursorAdapter(Context context) {
		super(context, null);
		initialize();
	}

	public UserChooseCursorAdapter(Context context, Cursor c) {
		super(context, c);
		initialize();
	}

	private void initialize() {
		mStates = new ArrayList<Boolean>();
		mStateMap = new SparseBooleanArray();
	}

	public ArrayList<Boolean> getCheckedStates() {
		return mStates;
	}

	public void setItemChecked(int position, boolean checked) {
		mStateMap.put(position, checked);
		notifyDataSetChanged();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.list_item_chooseuser;
	}

	private void setTextStyle(UserCheckBoxViewHolder holder) {
		TextPaint tp = holder.nameText.getPaint();
		tp.setFakeBoldText(true);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater.inflate(getLayoutId(), null);
		UserCheckBoxViewHolder holder = new UserCheckBoxViewHolder(view);
		setTextStyle(holder);
		view.setTag(holder);
		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		View row = view;

		final UserCheckBoxViewHolder holder = (UserCheckBoxViewHolder) row
				.getTag();

		final UserModel u = UserModel.from(cursor);

		String headUrl = u.getProfileImageUrl();
		mImageLoader.displayImage(headUrl, holder.headIcon);

		holder.lockIcon.setVisibility(u.isProtect() ? View.VISIBLE : View.GONE);

		holder.nameText.setText(u.getScreenName());
		holder.idText.setText("(" + u.getId() + ")");
		holder.genderText.setText(u.getGender());
		holder.locationText.setText(u.getLocation());

		Boolean b = mStateMap.get(cursor.getPosition());
		holder.checkBox.setChecked(Boolean.TRUE == b);

	}

}
