package com.fanfou.app.ui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.BaseAdapter;

import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.api.Status;
import com.fanfou.app.config.Commons;
import com.fanfou.app.dialog.ConfirmDialog;
import com.fanfou.lib.quickaction.ActionItem;
import com.fanfou.lib.quickaction.QuickAction;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.09
 * @version 1.1 2011.10.25
 * @version 1.2 2011.10.27
 * @version 1.3 2011.10.28
 * @version 2.0 2011.10.29
 * @version 2.1 2011.11.07
 * 
 */
public final class UIManager {
	public static final int QUICK_ACTION_ID_REPLY = 0;
	public static final int QUICK_ACTION_ID_DELETE = 1;
	public static final int QUICK_ACTION_ID_RETWEET = 2;
	public static final int QUICK_ACTION_ID_FAVORITE = 3;
	public static final int QUICK_ACTION_ID_UNFAVORITE = 4;
	public static final int QUICK_ACTION_ID_PROFILE = 5;
	public static final int QUICK_ACTION_ID_SHARE = 6;

	public static QuickAction makePopup(Context context, final Status status) {
		ActionItem reply = new ActionItem(QUICK_ACTION_ID_REPLY, "回复", context
				.getResources().getDrawable(R.drawable.ic_pop_reply));

		ActionItem delete = new ActionItem(QUICK_ACTION_ID_DELETE, "删除",
				context.getResources().getDrawable(R.drawable.ic_pop_delete));

		ActionItem retweet = new ActionItem(QUICK_ACTION_ID_RETWEET, "转发",
				context.getResources().getDrawable(R.drawable.ic_pop_retweet));

		ActionItem favorite = new ActionItem(QUICK_ACTION_ID_FAVORITE, "收藏",
				context.getResources().getDrawable(R.drawable.ic_pop_favorite));
//		favorite.setSticky(true);

		ActionItem unfavorite = new ActionItem(QUICK_ACTION_ID_UNFAVORITE,
				"取消", context.getResources().getDrawable(
						R.drawable.ic_pop_unfavorite));
//		unfavorite.setSticky(true);

		ActionItem profile = new ActionItem(QUICK_ACTION_ID_PROFILE, "空间",
				context.getResources().getDrawable(R.drawable.ic_pop_profile));

		ActionItem share = new ActionItem(QUICK_ACTION_ID_SHARE, "分享", context
				.getResources().getDrawable(R.drawable.ic_pop_share));

		final boolean me = status.userId.equals(App.me.userId);

		final QuickAction q = new QuickAction(context, QuickAction.HORIZONTAL);
		q.addActionItem(me ? delete : reply);
		q.addActionItem(retweet);
		q.addActionItem(status.favorited ? unfavorite : favorite);
		q.addActionItem(share);
		q.addActionItem(profile);

		return q;
	}

	public static void showPopup(final Activity a, final View v,
			final Status s, final BaseAdapter adapter, final List<Status> ss) {

		QuickAction q = makePopup(a, s);
		q.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {

			@Override
			public void onItemClick(QuickAction source, int pos, int actionId) {
				switch (actionId) {
				case QUICK_ACTION_ID_REPLY:
					ActionManager.doReply(a, s);
					break;
				case QUICK_ACTION_ID_DELETE:
					final ConfirmDialog dialog = new ConfirmDialog(a, "删除消息",
							"要删除这条消息吗？");
					dialog.setClickListener(new ConfirmDialog.AbstractClickHandler() {

						@Override
						public void onButton1Click() {
							doDelete(a, s, adapter, ss);
						}
					});
					dialog.show();
					break;
				case QUICK_ACTION_ID_FAVORITE:
				case QUICK_ACTION_ID_UNFAVORITE:
					UIManager.doFavorite(a, s, adapter);
					break;
				case QUICK_ACTION_ID_RETWEET:
					ActionManager.doRetweet(a, s);
					break;
				case QUICK_ACTION_ID_SHARE:
					ActionManager.doShare(a, s);
					break;
				case QUICK_ACTION_ID_PROFILE:
					ActionManager.doProfile(a, s);
					break;
				default:
					break;
				}
			}
		});

		q.show(v);
	}

	public static void showPopup(final Activity a, final Cursor c,
			final View v, final Status s) {

		QuickAction q = makePopup(a, s);
		q.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {

			@Override
			public void onItemClick(QuickAction source, int pos, int actionId) {

				switch (actionId) {
				case QUICK_ACTION_ID_REPLY:
					ActionManager.doReply(a, s);
					break;
				case QUICK_ACTION_ID_DELETE:
					final ConfirmDialog dialog = new ConfirmDialog(a, "删除消息",
							"要删除这条消息吗？");
					dialog.setClickListener(new ConfirmDialog.AbstractClickHandler() {

						@Override
						public void onButton1Click() {
							doDelete(a, s, c);
						}
					});
					dialog.show();
					break;
				case QUICK_ACTION_ID_FAVORITE:
				case QUICK_ACTION_ID_UNFAVORITE:
					UIManager.doFavorite(a, s, c);
					break;
				case QUICK_ACTION_ID_RETWEET:
					ActionManager.doRetweet(a, s);
					break;
				case QUICK_ACTION_ID_SHARE:
					ActionManager.doShare(a, s);
					break;
				case QUICK_ACTION_ID_PROFILE:
					ActionManager.doProfile(a, s);
					break;
				default:
					break;
				}
			}
		});
		q.show(v);
	}
	
	public static void showPopup(final Activity a, final View v, final Status s,final BaseAdapter adapter) {

		QuickAction q = makePopup(a, s);
		q.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {

			@Override
			public void onItemClick(QuickAction source, int pos, int actionId) {

				switch (actionId) {
				case QUICK_ACTION_ID_REPLY:
					ActionManager.doReply(a, s);
					break;
				case QUICK_ACTION_ID_DELETE:
					final ConfirmDialog dialog = new ConfirmDialog(a, "删除消息",
							"要删除这条消息吗？");
					dialog.setClickListener(new ConfirmDialog.AbstractClickHandler() {

						@Override
						public void onButton1Click() {
							doDelete(a, s, adapter);
						}
					});
					dialog.show();
					break;
				case QUICK_ACTION_ID_FAVORITE:
				case QUICK_ACTION_ID_UNFAVORITE:
					doFavorite(a, s, adapter);
					break;
				case QUICK_ACTION_ID_RETWEET:
					ActionManager.doRetweet(a, s);
					break;
				case QUICK_ACTION_ID_SHARE:
					ActionManager.doShare(a, s);
					break;
				case QUICK_ACTION_ID_PROFILE:
					ActionManager.doProfile(a, s);
					break;
				default:
					break;
				}
			}
		});
		q.show(v);
	}

	public static void doDelete(final Activity activity, final Status s,
			final BaseAdapter adapter, final List<Status> ss) {
		ResultHandler li = new ResultHandler() {
			@Override
			public void onActionSuccess(int type, String message) {
				ss.remove(s);
				adapter.notifyDataSetChanged();
			}
		};
		ActionManager.doStatusDelete(activity, s.id, li);
	}

	public static void doDelete(final Activity activity, final Status s,
			final Cursor c) {
		ResultHandler li = new ResultHandler() {
			@Override
			public void onActionSuccess(int type, String message) {
				c.requery();
			}
		};
		ActionManager.doStatusDelete(activity, s.id, li);
	}
	
	public static void doDelete(final Activity activity, final Status s, final BaseAdapter adapter) {
		ResultHandler li = new ResultHandler() {
			@Override
			public void onActionSuccess(int type, String message) {
				adapter.notifyDataSetChanged();
			}
		};
		ActionManager.doStatusDelete(activity, s.id, li);
	}

	public static void doFavorite(final Activity activity, final Status s,
			final BaseAdapter adapter) {
		ResultHandler li = new ResultHandler() {
			@Override
			public void onActionSuccess(int type, String message) {
				if (type == Commons.ACTION_STATUS_FAVORITE) {
					s.favorited = true;
				} else {
					s.favorited = false;
				}
				adapter.notifyDataSetChanged();
			}
		};
		ActionManager.doFavorite(activity, s, li);
	}

	public static void doFavorite(final Activity activity, final Status s,
			final Cursor c) {
		ResultHandler li = new ResultHandler() {
			@Override
			public void onActionSuccess(int type, String message) {
				c.requery();
			}
		};
		ActionManager.doFavorite(activity, s, li);
	}

	public abstract static class ResultHandler implements
			ActionManager.ResultListener {
		@Override
		public void onActionFailed(int type, String message) {
		}
	}

}
