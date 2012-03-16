package com.fanfou.app.hd.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfou.app.hd.R;

class StatusViewHolder {
	ImageView headIcon = null;
	ImageView photoIcon = null;
	ImageView replyIcon = null;
	ImageView retweetIcon = null;
	ImageView favoriteIcon = null;
	TextView nameText = null;
	TextView metaText = null;
	TextView contentText = null;

	StatusViewHolder(View base) {
		this.headIcon = (ImageView) base.findViewById(R.id.head);
		this.photoIcon = (ImageView) base.findViewById(R.id.ic_mini_photo);
		this.replyIcon = (ImageView) base.findViewById(R.id.ic_mini_reply);
		this.retweetIcon = (ImageView) base.findViewById(R.id.ic_mini_retweet);
		this.favoriteIcon = (ImageView) base.findViewById(R.id.ic_mini_favorite);
		this.contentText = (TextView) base.findViewById(R.id.text);
		this.metaText = (TextView) base.findViewById(R.id.metainfo);
		this.nameText = (TextView) base.findViewById(R.id.name);

	}
}
