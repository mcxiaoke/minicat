package org.mcxiaoke.fancooker.adapter;

import org.mcxiaoke.fancooker.R;
import org.mcxiaoke.fancooker.dao.model.UserModel;
import org.mcxiaoke.fancooker.ui.widget.ItemView;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;

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
		// return R.layout.list_item_user;
		return -1;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		ItemView view = new ItemView(mContext);
		view.setId(R.id.list_item);
		return view;
	}

	@Override
	public void bindView(View row, Context context, Cursor cursor) {
		ItemView view = (ItemView) row;
		final UserModel u = UserModel.from(cursor);

		UIHelper.setContent(view, u);

		String headUrl = u.getProfileImageUrl();
		mImageLoader.displayImage(headUrl, view.getImageView());
	}

}
