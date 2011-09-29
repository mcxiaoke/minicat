package com.fanfou.app.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfou.app.R;
import com.fanfou.app.api.DirectMessage;
import com.fanfou.app.ui.ActionManager;
import com.fanfou.app.util.DateTimeHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.09
 * 
 */
public class MessageCursorAdapter extends BaseCursorAdapter{

	public static final String TAG = "MessageAdapter";

	private void log(String message) {
		Log.e(TAG, message);
	}

	private void setTextStyle(ViewHolder holder) {
		holder.contentText.setTextSize(fontSize);
		holder.nameText.setTextSize(fontSize);
		holder.dateText.setTextSize(fontSize - 4);
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

		final DirectMessage dm = DirectMessage.parse(cursor);

//		if(!fling){
			mLoader.set(dm.senderProfileImageUrl, holder.headIcon,R.drawable.default_head);
//		}
		// Bitmap bitmap=mLoader.get(dm.senderProfileImageUrl,
		// getImageCallback(holder.headIcon));
		// if(bitmap!=null){
		// holder.headIcon.setImageBitmap(bitmap);
		// }else{
		// holder.headIcon.setImageResource(R.drawable.default_head);
		// }
		// row.setBackgroundColor(0x44888800);
			holder.headIcon.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(dm!=null){
						ActionManager.doProfile(mContext, dm);
					}
				}
			});

		holder.nameText.setText(dm.senderScreenName);
		holder.dateText.setText(DateTimeHelper.getInterval(dm.createdAt));
		holder.contentText.setText(dm.text);
	}

	public MessageCursorAdapter(Context context, Cursor c) {
		super(context, c, false);
	}

	public MessageCursorAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
	}

	@Override
	int getLayoutId() {
		return R.layout.list_item_message;
	}

	@Override
	public Cursor runQuery(CharSequence constraint) {
		return null;
	}

	static class ViewHolder {
		ImageView headIcon = null;
		TextView nameText = null;
		TextView dateText = null;
		TextView contentText = null;

		ViewHolder(View base) {
			this.headIcon = (ImageView) base
					.findViewById(R.id.item_message_head);
			this.contentText = (TextView) base
					.findViewById(R.id.item_message_text);
			this.dateText = (TextView) base
					.findViewById(R.id.item_message_date);
			this.nameText = (TextView) base
					.findViewById(R.id.item_message_user);

		}
	}

}
