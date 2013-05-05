package org.mcxiaoke.fancooker.adapter;

import org.mcxiaoke.fancooker.AppContext;
import org.mcxiaoke.fancooker.R;
import org.mcxiaoke.fancooker.dao.model.StatusModel;
import org.mcxiaoke.fancooker.ui.widget.ItemView;
import org.mcxiaoke.fancooker.util.OptionHelper;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

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
		super(context, null);
		initialize(context, false);
	}

	public StatusCursorAdapter(Context context, Cursor c) {
		super(context, c);
		initialize(context, false);
	}

	private void initialize(Context context, boolean colored) {
		this.colored = colored;
		if (colored) {
			mMentionedBgColor = OptionHelper.readInt(mContext,
					R.string.option_color_highlight_mention, context
							.getResources().getColor(R.color.mentioned_color));
			mSelfBgColor = OptionHelper.readInt(mContext,
					R.string.option_color_highlight_self, context
							.getResources().getColor(R.color.self_color));
			if (AppContext.DEBUG) {
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
				|| s.getSimpleText().contains("@" + AppContext.getScreenName())) {
			return MENTION;
		}

		return s.isSelf() ? SELF : NONE;
	}

	@Override
	public int getViewTypeCount() {
		return TYPES.length;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		ItemView view = new ItemView(context);
		view.setId(R.id.list_item);
		if (AppContext.DEBUG) {
			Log.d(TAG, "newView newView=" + view);
		}
		return view;
	}

	@Override
	public void bindView(View row, Context context, final Cursor cursor) {
		final StatusModel s = StatusModel.from(cursor);
		final ItemView view = (ItemView) row;
		
		setColor(cursor, view);
		UIHelper.setMetaInfo(view, s);
		view.setContent(s.getSimpleText());

		String headUrl = s.getUserProfileImageUrl();
		UIHelper.setImage(view, mLoader, headUrl, busy);
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

}
