package com.fanfou.app.hd.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.R;
import com.fanfou.app.hd.dao.model.StatusModel;
import com.fanfou.app.hd.util.OptionHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.01
 * @version 1.5 2011.10.24
 * @version 1.6 2011.12.06
 * @version 1.7 2012.02.03
 * @version 2.0 2012.02.22
 * @version 2.1 2012.02.27
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
		final Cursor cursor = (Cursor) getItem(position);
		if (cursor == null) {
			return NONE;
		}
		final StatusModel s = StatusModel.from(cursor);
		if (s == null) {
			return NONE;
		}
		if (s.getType() == StatusModel.TYPE_MENTIONS
				|| s.getSimpleText().contains("@" + App.getScreenName())) {
			return MENTION;
		}

		return s.isSelf() ? SELF : NONE;
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

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater.inflate(getLayoutId(), null);
		StatusViewHolder holder = new StatusViewHolder(view);
		UIHelper.setStatusTextStyle(holder, getFontSize());
		view.setTag(holder);
		return view;
	}

	@Override
	public void bindView(View view, Context context, final Cursor cursor) {
		View row = view;
		final StatusViewHolder holder = (StatusViewHolder) row.getTag();

		final StatusModel s = StatusModel.from(cursor);

		String headUrl = s.getUserProfileImageUrl();
		if (busy) {
			Bitmap bitmap = mLoader.getImage(headUrl, null);
			if (bitmap != null) {
				holder.headIcon.setImageBitmap(bitmap);
			}else{
				holder.headIcon.setImageResource(R.drawable.ic_head);
			}
		} else {
			holder.headIcon.setTag(headUrl);
			mLoader.displayImage(headUrl, holder.headIcon,
					R.drawable.ic_head);
		}

		setColor(cursor, row);
		UIHelper.setStatusMetaInfo(holder, s);
		holder.contentText.setText(s.getSimpleText());

	}

	private void setColor(final Cursor cursor, View row) {
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
	}

	@Override
	int getLayoutId() {
		return R.layout.list_item_status;
	}

}
