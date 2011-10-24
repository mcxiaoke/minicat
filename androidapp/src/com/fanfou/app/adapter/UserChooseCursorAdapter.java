package com.fanfou.app.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfou.app.R;
import com.fanfou.app.api.User;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.21
 * @version 1.1 2011.10.24
 * 
 */
public class UserChooseCursorAdapter extends BaseCursorAdapter{
	private static final String tag = UserChooseCursorAdapter.class.getSimpleName();

	private void log(String message) {
		Log.e(tag, message);
	}

	public UserChooseCursorAdapter(Context context, Cursor c) {
		super(context, c, false);
	}

	public UserChooseCursorAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
	}

	@Override
	int getLayoutId() {
//		return R.layout.list_item_chooseuser;
		return android.R.layout.simple_list_item_multiple_choice;
	}

	private void setTextStyle(ViewHolder holder) {
		holder.tv.setTextSize(fontSize);
		
//		holder.nameText.setTextSize(fontSize);
//		TextPaint tp = holder.nameText.getPaint();
//		tp.setFakeBoldText(true);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater.inflate(getLayoutId(), null);
		ViewHolder holder = new ViewHolder(view);
//		setHeadImage(holder.headIcon);
		setTextStyle(holder);
		view.setTag(holder);
		bindView(view, context, cursor);
		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		View row = view;
		final User u = User.parse(cursor);
		
		final ViewHolder holder = (ViewHolder) row.getTag();
//		mLoader.set(u.profileImageUrl, holder.headIcon, R.drawable.default_head);
		holder.tv.setText(u.screenName+" ("+u.id+")");
//		holder.nameText.setText(u.screenName);
//		holder.idText.setText(u.id);
//		holder.checkBox.setTag(u);
//		holder.checkBox.setOnCheckedChangeListener(occ);

	}
	
	private OnCheckedChangeListener occ=new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			User u=(User) buttonView.getTag();
		}
	};
	
	public void setChecked(int position){
	}
	
	private static class ViewHolder {
		CheckedTextView tv;
		
		ViewHolder(View base){
			this.tv=(CheckedTextView) base.findViewById(android.R.id.text1);
		}
	}

	private static class ViewHolder2 {

		ImageView headIcon = null;
		TextView nameText = null;
		TextView idText = null;
		CheckBox checkBox=null;

		ViewHolder2(View base) {
			this.headIcon = (ImageView) base.findViewById(R.id.item_user_head);
			this.nameText = (TextView) base.findViewById(R.id.item_user_name);
			this.idText = (TextView) base
					.findViewById(R.id.item_user_id);
			this.checkBox=(CheckBox) base.findViewById(R.id.item_user_checkbox);
		}
	}

}
