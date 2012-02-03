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
import com.fanfou.app.api.Status;
import com.fanfou.app.service.Constants;
import com.fanfou.app.ui.ActionManager;
import com.fanfou.app.util.DateTimeHelper;
import com.fanfou.app.util.OptionHelper;
import com.fanfou.app.util.StringHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.01
 * @version 1.5 2011.10.24
 * @version 1.6 2011.12.06
 * @version 1.7 2012.02.03
 * 
 */
public class StatusCursorAdapter extends BaseCursorAdapter {

	private static final int NONE = 0;
	private static final int MENTION = 1;
	private static final int SELF = 2;
	private static final int[] TYPES = new int[] { NONE, MENTION, SELF, };

	private int mMentionedBgColor;// = 0x332266aa;
	private int mSelfBgColor;// = 0x33999999;
	private boolean colored;

	public static final String TAG = StatusCursorAdapter.class.getSimpleName();

	private void log(String message) {
		Log.d(TAG, message);

	}

	public StatusCursorAdapter(Context context) {
		super(context, null, false);
		init(context, false);
	}

	public StatusCursorAdapter(Context context, Cursor c) {
		super(context, c, false);
		init(context, false);
	}

	public StatusCursorAdapter(boolean colored, Context context, Cursor c) {
		super(context, c, false);
		init(context, colored);
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
		final Cursor c = (Cursor) getItem(position);
		if (c == null) {
			return NONE;
		}
		final Status s = Status.parse(c);
		if (s == null || s.isNull()) {
			return NONE;
		}
		if (s.type == Constants.TYPE_STATUSES_MENTIONS
				|| s.simpleText.contains("@" + App.getUserName())) {
			return MENTION;
		}

		return s.self ? SELF : NONE;
	}

	@Override
	public int getViewTypeCount() {
		return TYPES.length;
	}

	public void switchCursor(Cursor cursor) {
		if (cursor != null) {
			mCursor = cursor;
			changeCursor(mCursor);
			mCursor.requery();
		} else {
			mCursor = null;
			changeCursor(mCursor);
			notifyDataSetChanged();
		}
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
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater.inflate(getLayoutId(), null);
		ViewHolder holder = new ViewHolder(view);
		setHeadImage(mContext, holder.headIcon);
		setTextStyle(holder);
		view.setTag(holder);
		return view;
	}

	@Override
	public void bindView(View view, Context context, final Cursor cursor) {
		View row = view;
		final ViewHolder holder = (ViewHolder) row.getTag();

		final Status s = Status.parse(cursor);

		if (colored) {
			int itemType = getItemViewType(cursor.getPosition());
			switch (itemType) {
			case MENTION:
				row.setBackgroundColor(mMentionedBgColor);
				break;
			case SELF:
				row.setBackgroundColor(mSelfBgColor);
				break;
			case NONE:
				break;
			default:
				break;
			}
		}

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

		if (s.isThread) {
			holder.replyIcon.setVisibility(View.VISIBLE);
		} else {
			holder.replyIcon.setVisibility(View.GONE);
		}

		if (s.hasPhoto) {
			holder.photoIcon.setVisibility(View.VISIBLE);
		} else {
			holder.photoIcon.setVisibility(View.GONE);
		}

		holder.nameText.setText(s.userScreenName);
		holder.contentText.setText(s.simpleText);
		holder.metaText.setText(DateTimeHelper.getInterval(s.createdAt) + " 通过"
				+ s.source);

	}

	private static class ViewHolder {
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

}
