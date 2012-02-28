package com.fanfou.app.hd.ui.widget;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.widget.BaseAdapter;

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.R;
import com.fanfou.app.hd.UIConversation;
import com.fanfou.app.hd.UIDrafts;
import com.fanfou.app.hd.UIMyProfile;
import com.fanfou.app.hd.UIProfile;
import com.fanfou.app.hd.UIWrite;
import com.fanfou.app.hd.dao.model.StatusModel;
import com.fanfou.app.hd.dialog.ConfirmDialog;
import com.fanfou.app.hd.service.FanFouService;
import com.fanfou.app.hd.util.OptionHelper;
import com.fanfou.app.hd.util.StatusHelper;
import com.fanfou.app.hd.util.StringHelper;
import com.lib.quickaction.ActionItem;
import com.lib.quickaction.QuickAction;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.09
 * @version 1.1 2011.10.25
 * @version 1.2 2011.10.27
 * @version 1.3 2011.10.28
 * @version 2.0 2011.10.29
 * @version 2.1 2011.11.07
 * @version 3.0 2011.12.19
 * @version 3.1 2011.12.23
 * @version 4.0 2012.02.22
 * @version 4.1 2012.02.24
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

	public static QuickAction makePopup(Context context,
			final StatusModel status) {
		ActionItem reply = new ActionItem(QUICK_ACTION_ID_REPLY, "回复", context
				.getResources().getDrawable(R.drawable.ic_pop_reply));

		ActionItem delete = new ActionItem(QUICK_ACTION_ID_DELETE, "删除",
				context.getResources().getDrawable(R.drawable.ic_pop_delete));

		ActionItem retweet = new ActionItem(QUICK_ACTION_ID_RETWEET, "转发",
				context.getResources().getDrawable(R.drawable.ic_pop_retweet));

		ActionItem favorite = new ActionItem(QUICK_ACTION_ID_FAVORITE, "收藏",
				context.getResources().getDrawable(R.drawable.ic_pop_favorite));
		// favorite.setSticky(true);

		ActionItem unfavorite = new ActionItem(QUICK_ACTION_ID_UNFAVORITE,
				"取消", context.getResources().getDrawable(
						R.drawable.ic_pop_unfavorite));
		// unfavorite.setSticky(true);

		ActionItem profile = new ActionItem(QUICK_ACTION_ID_PROFILE, "空间",
				context.getResources().getDrawable(R.drawable.ic_pop_profile));

		ActionItem share = new ActionItem(QUICK_ACTION_ID_SHARE, "分享", context
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

	public static void showPopup(final Activity a, final View v,
			final StatusModel s, final BaseAdapter adapter,
			final List<StatusModel> ss) {

		QuickAction q = makePopup(a, s);
		q.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {

			@Override
			public void onItemClick(QuickAction source, int pos, int actionId) {
				switch (actionId) {
				case QUICK_ACTION_ID_REPLY:
					doReply(a, s);
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
//					FanFouService.doFavorite(a, s, adapter);
					break;
				case QUICK_ACTION_ID_RETWEET:
					// doRetweet(a, s);
					break;
				case QUICK_ACTION_ID_SHARE:
					doShare(a, s);
					break;
				case QUICK_ACTION_ID_PROFILE:
					// doProfile(a, s);
					break;
				default:
					break;
				}
			}
		});

		q.show(v);
	}

	public static void showPopup(final Activity a, final Cursor c,
			final View v, final StatusModel s) {

		QuickAction q = makePopup(a, s);
		q.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {

			@Override
			public void onItemClick(QuickAction source, int pos, int actionId) {

				switch (actionId) {
				case QUICK_ACTION_ID_REPLY:
					doReply(a, s);
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
//					FanFouService.doFavorite(a, s, c);
					break;
				case QUICK_ACTION_ID_RETWEET:
					// ActionManager.doRetweet(a, s);
					break;
				case QUICK_ACTION_ID_SHARE:
					// ActionManager.doShare(a, s);
					break;
				case QUICK_ACTION_ID_PROFILE:
					// ActionManager.doProfile(a, s);
					break;
				default:
					break;
				}
			}
		});
		q.show(v);
	}

	public static void showPopup(final Activity a, final View v,
			final StatusModel s, final BaseAdapter adapter) {

		QuickAction q = makePopup(a, s);
		q.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {

			@Override
			public void onItemClick(QuickAction source, int pos, int actionId) {

				switch (actionId) {
				case QUICK_ACTION_ID_REPLY:
					// ActionManager.doReply(a, s);
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
//					FanFouService.doFavorite(a, s, adapter);
					break;
				case QUICK_ACTION_ID_RETWEET:
					// ActionManager.doRetweet(a, s);
					break;
				case QUICK_ACTION_ID_SHARE:
					// ActionManager.doShare(a, s);
					break;
				case QUICK_ACTION_ID_PROFILE:
					// ActionManager.doProfile(a, s);
					break;
				default:
					break;
				}
			}
		});
		q.show(v);
	}

	public static void doDelete(final Activity activity, final StatusModel s,
			final BaseAdapter adapter, final List<StatusModel> ss) {
		ActionResultHandler li = new ActionResultHandler() {
			@Override
			public void onActionSuccess(int type, String message) {
				ss.remove(s);
				adapter.notifyDataSetChanged();
			}
		};
		// FanFouService.doStatusDelete(activity, s.getId(), li);
	}

	public static void doDelete(final Activity activity, final StatusModel s,
			final Cursor c) {
		ActionResultHandler li = new ActionResultHandler() {
			@Override
			public void onActionSuccess(int type, String message) {
				c.requery();
			}
		};
		// FanFouService.doStatusDelete(activity, s.getId(), li);
	}

	public static void doDelete(final Activity activity, final StatusModel s,
			final BaseAdapter adapter) {
		ActionResultHandler li = new ActionResultHandler() {
			@Override
			public void onActionSuccess(int type, String message) {
				adapter.notifyDataSetChanged();
			}
		};
		// FanFouService.doStatusDelete(activity, s.getId(), li);
	}

	public abstract static class ActionResultHandler implements ResultListener {
		@Override
		public void onActionFailed(int type, String message) {
		}
	}

	public static void doShowDrafts(Context context) {
		Intent intent = new Intent(context, UIDrafts.class);
		context.startActivity(intent);
	}

	public static void doMyProfile(Context context) {
		Intent intent = new Intent(context, UIMyProfile.class);
		context.startActivity(intent);
	}

	public static void doProfile(Context context, String userId) {
		if (StringHelper.isEmpty(userId)) {
			throw new NullPointerException("userid cannot be null.");
		}
		Intent intent = new Intent(context, UIProfile.class);
		intent.putExtra("id", userId);
		context.startActivity(intent);
	}

	public static void doShare(Context context, StatusModel status) {
		if (status == null) {
			throw new NullPointerException("status cannot be null.");
		}
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "来自" + status.getUserScreenName()
				+ "的饭否消息");
		intent.putExtra(Intent.EXTRA_TEXT, status.getSimpleText());
		context.startActivity(Intent.createChooser(intent, "分享"));
	}

	public static void doShare(Context context, File image) {
		if (image == null) {
			return;
		}
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("image/*");
		intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(image));
		context.startActivity(Intent.createChooser(intent, "分享"));
	}

	public static void doReply(Context context, StatusModel status) {

		if (status != null) {
			StringBuilder sb = new StringBuilder();
			boolean replyToAll = OptionHelper.readBoolean(context,
					R.string.option_reply_to_all_default, true);
			if (replyToAll) {
				ArrayList<String> names = StatusHelper.getMentions(status);
				for (String name : names) {
					sb.append("@").append(name).append(" ");
				}
			} else {
				sb.append("@").append(status.getUserScreenName()).append(" ");
			}

			Intent intent = new Intent(context, UIWrite.class);
			intent.putExtra("id", status.getId());
			intent.putExtra("text", sb.toString());
			intent.putExtra("type", UIWrite.TYPE_REPLY);
			context.startActivity(intent);
		} else {
			doWrite(context, null);
		}

	}

	public static void doWrite(Context context, String text, File file, int type) {
		Intent intent = new Intent(context, UIWrite.class);
		intent.putExtra("type", type);
		intent.putExtra("text", text);
		intent.putExtra("data", file);
		context.startActivity(intent);
	}

	public static void doWrite(Context context, String text, int type) {
		doWrite(context, text, null, type);
	}

	public static void doWrite(Context context, String text) {
		doWrite(context, text, UIWrite.TYPE_NORMAL);
	}

	public static void doWrite(Context context) {
		doWrite(context, null);
	}

	public static void doSend(Context context) {
		Intent intent = new Intent(context, UIConversation.class);
		context.startActivity(intent);
	}

	public interface ResultListener {
		public void onActionSuccess(int type, String message);

		public void onActionFailed(int type, String message);
	}

}
