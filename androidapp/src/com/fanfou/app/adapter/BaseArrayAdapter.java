package com.fanfou.app.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.cache.IImageLoader;
import com.fanfou.app.cache.ImageLoader;
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
public abstract class BaseArrayAdapter<T> extends BaseAdapter {
	Context mContext;
	LayoutInflater mInflater;
	IImageLoader mLoader;
	private int fontSize;
	private boolean textMode;

	public BaseArrayAdapter(Context context, List<T> t) {
		super();
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		this.mLoader = App.getImageLoader();
		this.textMode = OptionHelper.readBoolean(
				R.string.option_text_mode, false);
		this.fontSize = OptionHelper.readInt(R.string.option_fontsize,
				context.getResources().getInteger(R.integer.defaultFontSize));
	}

	protected void setHeadImage(ImageView headIcon) {
		if (textMode) {
			headIcon.setVisibility(View.GONE);
		} else {
			headIcon.setVisibility(View.VISIBLE);
		}
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
