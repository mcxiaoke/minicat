package com.fanfou.app.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfou.app.R;
import com.fanfou.app.api.Status;
import com.fanfou.app.ui.ActionManager;
import com.fanfou.app.util.DateTimeHelper;
import com.fanfou.app.util.StringHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.25
 * @version 1.1 2011.10.26
 * 
 */
public class ConversationAdapter extends BaseArrayAdapter<Status> {

	private static final String TAG = ConversationAdapter.class.getSimpleName();

	void log(String message) {
		Log.e(TAG, message);
	}

	private List<Status> mStatus;

	public ConversationAdapter(Context context, List<Status> ss) {
		super(context, ss);
		if (ss == null) {
			mStatus = new ArrayList<Status>();
		} else {
			mStatus = ss;
		}
	}

	@Override
	public int getCount() {
		return mStatus.size();
	}

	@Override
	public Status getItem(int position) {
		return mStatus.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private void setTextStyle(ViewHolder holder) {
		int fontSize = getFontSize();
		holder.contentText.setTextSize(fontSize);
		holder.nameText.setTextSize(fontSize);
		holder.metaText.setTextSize(fontSize - 4);
		TextPaint tp = holder.nameText.getPaint();
		tp.setFakeBoldText(true);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(getLayoutId(), null);
			holder = new ViewHolder(convertView);
			setTextStyle(holder);
			setHeadImage(holder.headIcon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (position % 2 == 1) {
			convertView.setBackgroundColor(0x33999999);
		} else {
			convertView.setBackgroundColor(0);
		}

		final Status s = mStatus.get(position);

		if (!isTextMode()) {
			holder.headIcon.setTag(s.userProfileImageUrl);
			mLoader.set(s.userProfileImageUrl, holder.headIcon,
					R.drawable.default_head);
			holder.headIcon.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (s != null) {
						ActionManager.doProfile(mContext, s);
					}
				}
			});
		}

		if (StringHelper.isEmpty(s.inReplyToStatusId)) {
			holder.replyIcon.setVisibility(View.GONE);
		} else {
			holder.replyIcon.setVisibility(View.VISIBLE);
		}

		if (StringHelper.isEmpty(s.photoLargeUrl)) {
			holder.photoIcon.setVisibility(View.GONE);
		} else {
			holder.photoIcon.setVisibility(View.VISIBLE);
		}

		holder.nameText.setText(s.userScreenName);
		holder.contentText.setText(s.simpleText);
		holder.metaText.setText(getDateString(s.createdAt) + " 通过" + s.source);

		return convertView;
	}

	static class ViewHolder {
		ImageView headIcon = null;
		ImageView replyIcon = null;
		ImageView photoIcon = null;
		TextView nameText = null;
		TextView metaText = null;
		TextView contentText = null;

		ViewHolder(View base) {
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

	@Override
	int getLayoutId() {
		return R.layout.list_item_status;
	}

	protected String getDateString(Date date) {
		return DateTimeHelper.formatDate(date);
	}

	public void updateDataAndUI(List<Status> ss) {
		mStatus = ss;
		notifyDataSetChanged();
	}

}
