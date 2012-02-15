package com.fanfou.app.hd.adapter;

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

import com.fanfou.app.hd.R;
import com.fanfou.app.hd.App;
import com.fanfou.app.hd.api.Status;
import com.fanfou.app.hd.ui.widget.ActionManager;
import com.fanfou.app.hd.util.DateTimeHelper;
import com.fanfou.app.hd.util.OptionHelper;
import com.fanfou.app.hd.util.StringHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.25
 * @version 1.1 2011.10.26
 * @version 2.0 2011.12.06
 * 
 */
public class StatusArrayAdapter extends BaseArrayAdapter<Status> {

	private static final String TAG = StatusArrayAdapter.class.getSimpleName();

	private static final int NONE = 0;
	private static final int MENTION = 1;
	private static final int SELF = 2;
	private static final int[] TYPES = new int[] { NONE, MENTION, SELF, };

	private boolean colored;

	private int mMentionedBgColor;// = 0x332266aa;
	private int mSelfBgColor;// = 0x33999999;

	private List<Status> mStatus;

	void log(String message) {
		Log.e(TAG, message);
	}

	public StatusArrayAdapter(Context context, List<Status> ss) {
		super(context, ss);
		init(context, false);
		if (ss == null) {
			mStatus = new ArrayList<Status>();
		} else {
			mStatus = ss;
		}
	}

	public StatusArrayAdapter(Context context, List<Status> ss, boolean colored) {
		super(context, ss);
		init(context, colored);
		if (ss == null) {
			mStatus = new ArrayList<Status>();
		} else {
			mStatus = ss;
		}
	}

	private void init(Context context, boolean colored) {
		this.colored = colored;
		if (colored) {
			mMentionedBgColor = OptionHelper.readInt(mContext,
					R.string.option_color_highlight_mention, context
							.getResources().getColor(R.color.mentioned_color));
			mSelfBgColor = OptionHelper.readInt(mContext,
					R.string.option_color_highlight_self, context
							.getResources().getColor(R.color.self_color));
			if (App.DEBUG) {
				log("init mMentionedBgColor="
						+ Integer.toHexString(mMentionedBgColor));
				log("init mSelfBgColor=" + Integer.toHexString(mSelfBgColor));
			}
		}
	}

	@Override
	public int getItemViewType(int position) {
		final Status s = getItem(position);
		if (s == null || s.isNull()) {
			return NONE;
		}
		if (s.simpleText.contains("@" + App.getUserName())) {
			return MENTION;
		} else {
			return s.self ? SELF : NONE;
		}
	}

	@Override
	public int getViewTypeCount() {
		return TYPES.length;
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

		final Status s = mStatus.get(position);

		if (!isTextMode()) {
			holder.headIcon.setTag(s.userProfileImageUrl);
			mLoader.displayImage(s.userProfileImageUrl, holder.headIcon,
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

		if (colored) {
			int itemType = getItemViewType(position);
			switch (itemType) {
			case MENTION:
				convertView.setBackgroundColor(mMentionedBgColor);
				break;
			case SELF:
				convertView.setBackgroundColor(mSelfBgColor);
				break;
			case NONE:
				break;
			default:
				break;
			}
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
		return DateTimeHelper.getInterval(date);
	}

	public void updateDataAndUI(List<Status> ss) {
		mStatus = ss;
		notifyDataSetChanged();
	}

}
