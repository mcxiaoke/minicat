package com.fanfou.app.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.cache.IImageLoader;
import com.fanfou.app.util.OptionHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.01
 * @version 1.5 2011.10.24
 * @version 1.6 2011.10.30
 * @version 1.7 2011.11.10
 * @version 1.8 2011.12.06
 * 
 */
public abstract class BaseCursorAdapter extends CursorAdapter {

	protected Context mContext;
	protected LayoutInflater mInflater;
	protected Cursor mCursor;
	protected IImageLoader mLoader;
	private int fontSize;
	private boolean textMode;

	protected void setHeadImage(Context context, ImageView headIcon) {
		if (textMode) {
			headIcon.setVisibility(View.GONE);
		} else {
			headIcon.setVisibility(View.VISIBLE);
		}
	}

	public BaseCursorAdapter(Context context, Cursor c) {
		super(context, c, false);
		init(context, c);
	}

	public BaseCursorAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		init(context, c);
	}

	private void init(Context context, Cursor c) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		this.mLoader = App.getImageLoader();
		this.mCursor = c;
		this.textMode = OptionHelper.readBoolean(mContext,R.string.option_text_mode,
				false);
		this.fontSize = OptionHelper.readInt(mContext,R.string.option_fontsize, context
				.getResources().getInteger(R.integer.defaultFontSize));
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

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int size) {
		fontSize = size;
	}

	public boolean isTextMode() {
		return textMode;
	}

	public void setTextMode(boolean mode) {
		textMode = mode;
	}

}
