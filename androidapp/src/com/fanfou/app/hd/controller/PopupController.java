package com.fanfou.app.hd.controller;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.R;
import com.fanfou.app.hd.adapter.BaseStatusArrayAdapter;
import com.fanfou.app.hd.dao.model.StatusModel;
import com.fanfou.app.hd.dialog.ConfirmDialog;
import com.fanfou.app.hd.service.FanFouService;
import com.fanfou.app.hd.util.Utils;
import com.lib.quickaction.ActionItem;
import com.lib.quickaction.QuickAction;
import com.lib.quickaction.QuickAction.OnActionItemClickListener;

/**
 * @author mcxiaoke
 * @version 1.0 2012.03.01
 * @version 2.0 2012.03.02
 * 
 */
public class PopupController {

	public static final int QUICK_ACTION_ID_REPLY = 0;
	public static final int QUICK_ACTION_ID_DELETE = 1;
	public static final int QUICK_ACTION_ID_RETWEET = 2;
	public static final int QUICK_ACTION_ID_FAVORITE = 3;
	public static final int QUICK_ACTION_ID_UNFAVORITE = 4;
	public static final int QUICK_ACTION_ID_PROFILE = 5;
	public static final int QUICK_ACTION_ID_SHARE = 6;

	public static void showPopup(final View view, final StatusModel status,
			final Cursor cursor) {
		final Activity context = (Activity) view.getContext();

		QuickAction q = makePopup(context, status);
		q.setOnActionItemClickListener(new QuickActionListener(context,
				getDeleteHandler(context, cursor), status));
		q.show(view);
	}

	public static void showPopup(final View view, final StatusModel status,
			final BaseStatusArrayAdapter adapter) {
		final Activity context = (Activity) view.getContext();

		QuickAction q = makePopup(context, status);
		q.setOnActionItemClickListener(new QuickActionListener(context,
				getDeleteHandler(context, status, adapter), status));
		q.show(view);
	}

	private static QuickAction makePopup(Context context,
			final StatusModel status) {
		ActionItem reply = new ActionItem(QUICK_ACTION_ID_REPLY, context
				.getResources().getDrawable(R.drawable.ic_pop_reply));

		ActionItem delete = new ActionItem(QUICK_ACTION_ID_DELETE, context
				.getResources().getDrawable(R.drawable.ic_pop_delete));

		ActionItem retweet = new ActionItem(QUICK_ACTION_ID_RETWEET, context
				.getResources().getDrawable(R.drawable.ic_pop_retweet));

		ActionItem favorite = new ActionItem(QUICK_ACTION_ID_FAVORITE, context
				.getResources().getDrawable(R.drawable.ic_pop_favorite_0));

		ActionItem unfavorite = new ActionItem(QUICK_ACTION_ID_UNFAVORITE,
				context.getResources()
						.getDrawable(R.drawable.ic_pop_favorite_1));

		ActionItem profile = new ActionItem(QUICK_ACTION_ID_PROFILE, context
				.getResources().getDrawable(R.drawable.ic_pop_profile));

		ActionItem share = new ActionItem(QUICK_ACTION_ID_SHARE, context
				.getResources().getDrawable(R.drawable.ic_pop_share));

		final boolean me = status.getUserId().equals(App.getAccount());

		final QuickAction q = new QuickAction(context, QuickAction.HORIZONTAL);
		q.addActionItem(me ? delete : reply);
		q.addActionItem(retweet);
		q.addActionItem(status.isFavorited() ? unfavorite : favorite);
		q.addActionItem(share);
		q.addActionItem(profile);

		return q;
	}

	private static void deleteStatus(final Activity context, final String id,
			final Handler handler) {
		final ConfirmDialog dialog = new ConfirmDialog(context, "删除消息",
				"要删除这条消息吗？");
		dialog.setClickListener(new ConfirmDialog.AbstractClickHandler() {

			@Override
			public void onButton1Click() {
				FanFouService.deleteStatus(context, id, handler);
			}
		});
		dialog.show();
	}

	private static Handler getDeleteHandler(final Activity context,
			final Cursor cursor) {
		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				int what = msg.what;
				switch (what) {
				case FanFouService.RESULT_SUCCESS:
					context.finish();
					cursor.requery();
					break;
				case FanFouService.RESULT_ERROR:
					int code = msg.getData().getInt("error_code");
					if (code == 404) {
						cursor.requery();
					}
					String message = msg.getData().getString("error_message");
					Utils.notify(context, message);
					break;
				default:
					break;
				}
			}

		};
		return handler;
	}

	private static Handler getDeleteHandler(final Activity context,
			final StatusModel status, final BaseStatusArrayAdapter adapter) {
		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				int what = msg.what;
				switch (what) {
				case FanFouService.RESULT_SUCCESS:
					context.finish();
					adapter.remove(status);
					break;
				case FanFouService.RESULT_ERROR:
					int code = msg.getData().getInt("error_code");
					String message = msg.getData().getString("error_message");
					Utils.notify(context, message);
					break;
				default:
					break;
				}
			}

		};
		return handler;
	}

	private static class QuickActionListener implements
			OnActionItemClickListener {
		private Activity context;
		private Handler handler;
		private StatusModel status;

		public QuickActionListener(Activity context, Handler handler,
				StatusModel status) {
			this.context = context;
			this.handler = handler;
			this.status = status;
		}

		@Override
		public void onItemClick(QuickAction source, int pos, int actionId) {
			switch (actionId) {
			case QUICK_ACTION_ID_REPLY:
				UIController.doReply(context, status);
				break;
			case QUICK_ACTION_ID_DELETE:
				deleteStatus(context, status.getId(), handler);
				break;
			case QUICK_ACTION_ID_FAVORITE:
				UIController.doFavorite(context, status.getId());
				break;
			case QUICK_ACTION_ID_UNFAVORITE:
				UIController.doUnFavorite(context, status.getId());
				break;
			case QUICK_ACTION_ID_RETWEET:
				UIController.doRetweet(context, status);
				break;
			case QUICK_ACTION_ID_SHARE:
				UIController.doShare(context, status);
				break;
			case QUICK_ACTION_ID_PROFILE:
				UIController.showProfile(context, status.getUserId());
				break;
			default:
				break;
			}
		}

	}

}
