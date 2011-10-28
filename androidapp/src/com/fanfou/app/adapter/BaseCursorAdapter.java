package com.fanfou.app.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.CursorAdapter;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;

import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.cache.IImageLoader;
import com.fanfou.app.util.OptionHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.01
 * @version 1.5 2011.10.24
 * 
 */
public abstract class BaseCursorAdapter extends CursorAdapter {

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return null;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {

	}

	Context mContext;
	LayoutInflater mInflater;
	Cursor mCursor;
	IImageLoader mLoader;
	int fontSize;

	void initFontSize() {
		fontSize = OptionHelper.parseInt(mContext, R.string.option_fontsize,
				mContext.getString(R.string.config_fontsize_default));
	}

	protected static void setHeadImage(Context context, ImageView headIcon) {
		boolean textMode = OptionHelper.readBoolean(context,
				R.string.option_text_mode, false);
		if (textMode) {
			headIcon.setVisibility(View.GONE);
		} else {
			headIcon.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * @param context
	 * @param c
	 */
	public BaseCursorAdapter(Context context, Cursor c) {
		super(context, c);
		init(context, c);
	}

	/**
	 * @param context
	 * @param c
	 * @param autoRequery
	 */
	public BaseCursorAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		init(context, c);
	}

	protected void init(Context context, Cursor c) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		this.mCursor = c;
		this.mLoader = App.me.getImageLoader();
		initFontSize();
	}

	@Override
	public CharSequence convertToString(Cursor cursor) {
		String result;
		if (cursor == null) {
			result = "Cursor Class: " + this.getClass().getSimpleName()
					+ ", Cursor is null. ";
		} else {
			result = "Cursor Class: " + this.getClass().getSimpleName()
					+ ", Count:" + cursor.getCount();
			;
		}
		return result;
	}

	abstract int getLayoutId();

}
