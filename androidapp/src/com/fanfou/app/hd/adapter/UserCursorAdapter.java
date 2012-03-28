package com.fanfou.app.hd.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;

import com.fanfou.app.hd.R;
import com.fanfou.app.hd.dao.model.UserModel;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.04
 * @version 1.5 2011.10.24
 * @version 1.6 2011.11.07
 * @version 1.7 2011.11.09
 * @version 2.0 2012.02.22
 * @version 2.1 2012.02.27
 * 
 */
public class UserCursorAdapter extends BaseCursorAdapter {
	public UserCursorAdapter(Context context) {
		super(context, null);
	}

	public UserCursorAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.list_item_user;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater.inflate(getLayoutId(), null);
		UserViewHolder holder = new UserViewHolder(view);
		UIHelper.setUserTextStyle(holder, getFontSize());
		view.setTag(holder);
		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		View row = view;
		final UserViewHolder holder = (UserViewHolder) row.getTag();
		final UserModel u = UserModel.from(cursor);

		String headUrl = u.getProfileImageUrl();
		if (busy) {
			Bitmap bitmap = mLoader.getImage(headUrl, null);
			if (bitmap != null) {
				holder.headIcon.setImageBitmap(bitmap);
			} else {
				holder.headIcon.setImageResource(R.drawable.ic_head);
			}
		} else {
			holder.headIcon.setTag(headUrl);
			mLoader.displayImage(headUrl, holder.headIcon, R.drawable.ic_head);
		}

		UIHelper.setUserContent(holder, u);

	}

}
