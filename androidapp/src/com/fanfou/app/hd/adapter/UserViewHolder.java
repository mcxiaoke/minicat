package com.fanfou.app.hd.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfou.app.hd.R;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.22
 * 
 */
class UserViewHolder {
	final ImageView headIcon;
	final ImageView lockIcon;
	final TextView nameText;
	final TextView idText;
	final TextView dateText;
	final TextView genderText;
	final TextView locationText;

	UserViewHolder(View base) {
		this.headIcon = (ImageView) base.findViewById(R.id.head);
		this.lockIcon = (ImageView) base.findViewById(R.id.lock);
		this.nameText = (TextView) base.findViewById(R.id.name);
		this.idText = (TextView) base.findViewById(R.id.id);
		this.dateText = (TextView) base.findViewById(R.id.date);
		this.genderText = (TextView) base.findViewById(R.id.gender);
		this.locationText = (TextView) base.findViewById(R.id.location);

	}
}
