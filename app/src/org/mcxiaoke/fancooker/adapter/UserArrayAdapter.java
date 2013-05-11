package org.mcxiaoke.fancooker.adapter;

import java.util.ArrayList;
import java.util.List;

import org.mcxiaoke.fancooker.AppContext;
import org.mcxiaoke.fancooker.R;
import org.mcxiaoke.fancooker.dao.model.UserModel;
import org.mcxiaoke.fancooker.ui.widget.ItemView;
import org.mcxiaoke.fancooker.util.OptionHelper;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.01
 * @version 1.5 2011.10.24
 * @version 1.6 2011.10.30
 * @version 1.7 2011.11.10
 * @version 1.8 2011.12.06
 * @version 2.0 2012.02.22
 * @version 2.1 2012.02.27
 * 
 */
public abstract class UserArrayAdapter extends BaseAdapter implements
		OnScrollListener {
	private static final String TAG = UserArrayAdapter.class.getSimpleName();
	protected Context mContext;
	protected LayoutInflater mInflater;
	protected ImageLoader mLoader;

	protected List<UserModel> mData;
	protected boolean busy;

	public UserArrayAdapter(Context context, List<UserModel> data) {
		super();
		initialize(context, data);
	}

	private void initialize(Context context, List<UserModel> data) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		this.mLoader = ImageLoader.getInstance();
		mData = new ArrayList<UserModel>();
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
			if (AppContext.DEBUG) {
				Log.d(TAG, "getView newView=" + view);
			}
		}

		final UserModel u = getData().get(position);
		UIHelper.setContent(view, u);
		String headUrl = u.getProfileImageUrl();
		mLoader.displayImage(headUrl, view.getImageView());
		return convertView;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public UserModel getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	protected List<UserModel> getData() {
		return mData;
	}

	public void setData(List<UserModel> data) {
		mData.clear();
		mData.addAll(data);
	}

	public void addData(List<UserModel> data) {
		mData.addAll(data);
	}

	protected int getLayoutId() {
		// return R.layout.list_item_user;
		return -1;
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
