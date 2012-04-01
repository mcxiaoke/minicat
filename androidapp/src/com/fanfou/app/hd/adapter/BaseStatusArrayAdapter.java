package com.fanfou.app.hd.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.R;
import com.fanfou.app.hd.cache.ImageLoader;
import com.fanfou.app.hd.dao.model.StatusModel;
import com.fanfou.app.hd.ui.widget.ItemView;
import com.fanfou.app.hd.util.OptionHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.01
 * @version 1.5 2011.10.24
 * @version 1.6 2011.10.30
 * @version 1.7 2011.11.10
 * @version 1.8 2011.12.06
 * @version 2.0 2012.02.22
 * @version 2.5 2012.03.28
 * 
 */
public abstract class BaseStatusArrayAdapter extends BaseAdapter implements
		OnScrollListener {
	private static final String TAG = BaseStatusArrayAdapter.class
			.getSimpleName();
	protected Context mContext;
	protected LayoutInflater mInflater;
	protected ImageLoader mLoader;
	protected boolean busy;

	protected List<StatusModel> mData;
	protected int fontSize;
	
	public BaseStatusArrayAdapter(Context context) {
		this(context, null);
	}

	public BaseStatusArrayAdapter(Context context, List<StatusModel> data) {
		super();
		initialize(context, data);
	}

	private void initialize(Context context, List<StatusModel> data) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		this.mLoader = App.getImageLoader();
		this.fontSize = OptionHelper.readInt(mContext,
				R.string.option_fontsize,
				context.getResources().getInteger(R.integer.defaultFontSize));

		mData = new ArrayList<StatusModel>();
		if (data != null) {
			mData.addAll(data);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ItemView view = (ItemView) convertView;
		if (view == null) {
			view = new ItemView(mContext);
			view.setId(R.id.list_item);
			if (App.DEBUG) {
				Log.d(TAG, "getView newView=" + view);
			}
		}

		final StatusModel s = getData().get(position);

		String headUrl = s.getUserProfileImageUrl();
		UIHelper.setImage(view, mLoader, headUrl, busy);

		UIHelper.setMetaInfo(view, s);
		setStatusContent(view, s.getSimpleText());
		return view;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public StatusModel getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	protected List<StatusModel> getData() {
		return mData;
	}

	public void clear() {
		mData.clear();
		notifyDataSetChanged();
	}

	public void setData(List<StatusModel> data) {
		mData.clear();
		mData.addAll(data);
		notifyDataSetChanged();
	}

	public void addData(List<StatusModel> data) {
		mData.addAll(data);
		notifyDataSetChanged();
	}

	public void remove(StatusModel status) {
		mData.remove(status);
		notifyDataSetChanged();
	}

	public void add(StatusModel status) {
		mData.add(status);
		notifyDataSetChanged();
	}

	protected void setStatusContent(ItemView view, String text) {
		view.setContent(text);
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int size) {
		fontSize = size;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_IDLE:
			busy = false;
			break;
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			busy = true;
			break;
		case OnScrollListener.SCROLL_STATE_FLING:
			busy = true;
			break;
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}

}
