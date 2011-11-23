package com.fanfou.app.adapter;

import java.util.List;

import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.api.Photo;
import com.fanfou.app.cache.IImageLoader;
import com.fanfou.app.cache.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class PhotoAdapter extends BaseAdapter {

	private Context mContext;
	private IImageLoader mLoader;
	private List<Photo> mImageList;

	public PhotoAdapter(Context context, List<Photo> list) {
		super();
		if (list == null) {
			throw new NullPointerException("data cannot be null.");
		}
		mContext = context;
		mLoader = ImageLoader.getInstance(mContext);
		mImageList = list;
	}

	@Override
	public int getCount() {
		return mImageList.size();
	}

	@Override
	public Photo getItem(int position) {
		return mImageList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setData(List<Photo> data) {
		clear();
		if (data != null) {
			mImageList.addAll(data);
			notifyDataSetChanged();
		}
	}

	public void clear() {
		mImageList.clear();
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.grid_item_photo, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final Photo photo = mImageList.get(position);
		if (photo != null) {
			mLoader.set(photo.thumbUrl, holder.image, R.drawable.photo_frame);
		}

		return convertView;
	}

	private static class ViewHolder {
		ImageView image;

		public ViewHolder(View base) {
			image = (ImageView) base.findViewById(R.id.image);
		}
	}

}
