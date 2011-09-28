package com.fanfou.app.adapter;

import java.util.List;

import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.cache.IImageLoader;
import com.fanfou.app.config.Commons;
import com.fanfou.app.util.OptionHelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public abstract class BaseArrayAdapter<T> extends BaseAdapter {
	Context mContext;
	LayoutInflater mInflater;
	IImageLoader mLoader;
	int fontSize;

	public BaseArrayAdapter(Context context, List<T> t) {
		super();
		this.mContext=context;
		this.mInflater=LayoutInflater.from(mContext);
		this.mLoader=App.me.getImageLoader();
		initFontSize();
	}
	
	
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
	
	abstract int getLayoutId();

}
