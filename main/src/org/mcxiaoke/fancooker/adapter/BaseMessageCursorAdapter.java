package org.mcxiaoke.fancooker.adapter;

import org.mcxiaoke.fancooker.R;
import org.mcxiaoke.fancooker.ui.widget.ItemView;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.09
 * @version 1.5 2011.10.24
 * @version 1.6 2012.02.22
 * @version 1.7 2012.02.27
 * @version 2.0 2012.02.28
 * 
 */
public abstract class BaseMessageCursorAdapter extends BaseCursorAdapter {

	public BaseMessageCursorAdapter(Context context) {
		super(context, null);
	}

	public BaseMessageCursorAdapter(Context context, Cursor c) {
		super(context, c);
	}

	private static final String TAG = BaseMessageCursorAdapter.class
			.getSimpleName();

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		ItemView view=new ItemView(mContext);
		view.setId(R.id.list_item);
		return view;
	}

	@Override
	public abstract void bindView(View view, Context context, Cursor cursor);

	@Override
	protected int getLayoutId() {
		return -1;
	}

}
