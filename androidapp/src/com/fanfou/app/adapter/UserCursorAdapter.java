package com.fanfou.app.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.adapter.StatusCursorAdapter.ViewHolder;
import com.fanfou.app.api.User;
import com.fanfou.app.cache.ImageLoader.ImageLoaderCallback;
import com.fanfou.app.config.Commons;
import com.fanfou.app.util.DateTimeHelper;
import com.fanfou.app.util.OptionHelper;
import com.fanfou.app.util.StringHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.04
 *
 */
public class UserCursorAdapter extends BaseCursorAdapter {
	private static final String tag=UserCursorAdapter.class.getSimpleName();
	
	private void log(String message){
		Log.e(tag, message);
	}

	public UserCursorAdapter(Context context, Cursor c) {
		super(context, c,false);
	}

	public UserCursorAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
	}

	@Override
	int getLayoutId() {
		return R.layout.list_item_user;
	}

	@Override
	public Cursor runQuery(CharSequence constraint) {
		return null;
	}
	
	private void setTextStyle(ViewHolder holder){
		holder.contentText.setTextSize(fontSize);
		holder.nameText.setTextSize(fontSize);
		holder.dateText.setTextSize(fontSize-3);
		TextPaint tp = holder.nameText.getPaint();
		tp.setFakeBoldText(true);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater.inflate(getLayoutId(), null);
		ViewHolder holder = new ViewHolder(view);
		setHeadImage(holder.headIcon);
		setTextStyle(holder);
		view.setTag(holder);
		bindView(view, context, cursor);
		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		View row = view;
		final ViewHolder holder = (ViewHolder) row.getTag();
		User u=User.parse(cursor);
		if(!fling){	
			mLoader.setHeadImage(u.profileImageUrl, holder.headIcon);
		}
		if(u.protect){
			holder.lockIcon.setVisibility(View.VISIBLE);
		}else{
			holder.lockIcon.setVisibility(View.GONE);
		}
		holder.nameText.setText(u.screenName);
		holder.contentText.setText(u.lastStatusText);
		String dateStr=DateTimeHelper.formatDateOnly(u.createdAt);
//		if(!StringHelper.isEmpty(u.lastStatusId)){
//			dateStr=DateTimeHelper.formatDateOnly(u.lastStatusCreatedAt);
//		}
		holder.dateText.setText(dateStr);
		
	}
	
	private static class ViewHolder {
		
		ImageView headIcon = null;
		ImageView lockIcon=null;
		TextView nameText = null;
		TextView contentText = null;
		TextView dateText = null;

		ViewHolder(View base) {
			this.headIcon=(ImageView) base.findViewById(R.id.item_user_head);
			this.lockIcon=(ImageView) base.findViewById(R.id.item_user_flag);
			this.nameText=(TextView) base.findViewById(R.id.item_user_name);
			this.contentText=(TextView) base.findViewById(R.id.item_user_text);
			this.dateText=(TextView) base.findViewById(R.id.item_user_date);
		}
	}

}
