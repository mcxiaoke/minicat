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
import com.fanfou.app.util.OptionHelper;

public abstract class BaseArrayAdapter<T> extends BaseAdapter {
	Context mContext;
	LayoutInflater mInflater;
	IImageLoader mLoader;
	int fontSize;

	public BaseArrayAdapter(Context context, List<T> t) {
		super();
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		this.mLoader = App.me.getImageLoader();
		initFontSize();
	}

	private void initFontSize() {
		fontSize = OptionHelper.parseInt(mContext, R.string.option_fontsize,
				mContext.getString(R.string.config_fontsize_default));
	}

	protected void setHeadImage(ImageView headIcon) {
		boolean textMode = OptionHelper.readBoolean(mContext,
				R.string.option_text_mode, false);
		if (textMode) {
			headIcon.setVisibility(View.GONE);
		} else {
			headIcon.setVisibility(View.VISIBLE);
		}
	}

	abstract int getLayoutId();

}
