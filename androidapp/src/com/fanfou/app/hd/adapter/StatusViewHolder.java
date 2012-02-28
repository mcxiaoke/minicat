package com.fanfou.app.hd.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfou.app.hd.R;

class StatusViewHolder {
	ImageView headIcon = null;
	ImageView replyIcon = null;
	ImageView photoIcon = null;
	TextView nameText = null;
	TextView metaText = null;
	TextView contentText = null;

	StatusViewHolder(View base) {
		this.headIcon = (ImageView) base
				.findViewById(R.id.item_status_head);
		this.replyIcon = (ImageView) base
				.findViewById(R.id.item_status_icon_reply);
		this.photoIcon = (ImageView) base
				.findViewById(R.id.item_status_icon_photo);
		this.contentText = (TextView) base
				.findViewById(R.id.item_status_text);
		this.metaText = (TextView) base.findViewById(R.id.item_status_meta);
		this.nameText = (TextView) base.findViewById(R.id.item_status_user);

	}
}
