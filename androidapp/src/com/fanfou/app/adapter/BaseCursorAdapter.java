package com.fanfou.app.adapter;

import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.cache.IImageLoader;
import com.fanfou.app.config.Commons;
import com.fanfou.app.util.OptionHelper;

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

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.01
 * 
 */
public abstract class BaseCursorAdapter extends CursorAdapter implements
		FilterQueryProvider,OnScrollListener {
	boolean fling;

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if(scrollState==SCROLL_STATE_FLING){
			fling=true;
		}else{
			fling=false;
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
	}

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
	
	private void initFontSize(){
		fontSize=OptionHelper.parseInt(mContext, R.string.option_fontsize,String.valueOf(Commons.FONT_SIZE_DEFAULT));
	}
	
	protected void setHeadImage(ImageView headIcon) {
		boolean show = OptionHelper.readBoolean(mContext,
				R.string.option_show_head, true);
		if (show) {
			headIcon.setVisibility(View.VISIBLE);
		} else {
			headIcon.setVisibility(View.GONE);
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
		this.mLoader = App.me.imageLoader;
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
