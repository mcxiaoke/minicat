package com.fanfou.app.hd.adapter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfou.app.hd.R;

class UserCheckBoxViewHolder {
	
	final ImageView headIcon;
	final ImageView lockIcon;
	final TextView nameText;
	final TextView idText;
	final TextView genderText;
	final TextView locationText;
	final CheckBox checkBox;

	UserCheckBoxViewHolder(View base) {
		this.headIcon = (ImageView) base.findViewById(R.id.item_user_head);
		this.lockIcon = (ImageView) base.findViewById(R.id.item_user_flag);
		this.nameText = (TextView) base.findViewById(R.id.item_user_name);
		this.genderText = (TextView) base
				.findViewById(R.id.item_user_gender);
		this.locationText = (TextView) base
				.findViewById(R.id.item_user_location);
		this.idText = (TextView) base.findViewById(R.id.item_user_id);
		this.checkBox = (CheckBox) base
				.findViewById(R.id.item_user_checkbox);
	}

}
