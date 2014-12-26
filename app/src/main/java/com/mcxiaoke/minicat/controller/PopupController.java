package com.mcxiaoke.minicat.controller;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.adapter.BaseStatusArrayAdapter;
import com.mcxiaoke.minicat.dao.model.StatusModel;
import com.mcxiaoke.minicat.dialog.ConfirmDialog;
import com.mcxiaoke.minicat.quickaction.ActionItem;
import com.mcxiaoke.minicat.quickaction.QuickAction;
import com.mcxiaoke.minicat.quickaction.QuickAction.OnActionItemClickListener;
import com.mcxiaoke.minicat.service.SyncService;
import com.mcxiaoke.minicat.util.Utils;

/**
 * @author mcxiaoke
 * @version 2.0 2012.03.02
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
                .getResources().getDrawable(R.drawable.ic_reply));

        ActionItem delete = new ActionItem(QUICK_ACTION_ID_DELETE, context
                .getResources().getDrawable(R.drawable.ic_delete));

        ActionItem retweet = new ActionItem(QUICK_ACTION_ID_RETWEET, context
                .getResources().getDrawable(R.drawable.ic_retweet));

        ActionItem favorite = new ActionItem(QUICK_ACTION_ID_FAVORITE, context
                .getResources().getDrawable(R.drawable.ic_favorite_0));

        ActionItem unfavorite = new ActionItem(QUICK_ACTION_ID_UNFAVORITE,
                context.getResources().getDrawable(R.drawable.ic_favorite_1));

        ActionItem profile = new ActionItem(QUICK_ACTION_ID_PROFILE, context
                .getResources().getDrawable(R.drawable.ic_profile));

        ActionItem share = new ActionItem(QUICK_ACTION_ID_SHARE, context
                .getResources().getDrawable(R.drawable.ic_share));

        final boolean me = status.getUserId().equals(AppContext.getAccount());

        final QuickAction q = new QuickAction(context);
        q.addActionItem(me ? delete : reply);
        q.addActionItem(retweet);
        q.addActionItem(status.isFavorited() ? unfavorite : favorite);
        q.addActionItem(share);
        q.addActionItem(profile);

        return q;
    }

    private static void deleteStatus(final Activity context, final String id,
                                     final Handler handler) {
        final ConfirmDialog dialog = new ConfirmDialog(context);
        dialog.setTitle("提示");
        dialog.setMessage("确定要删除这条消息吗?");
        dialog.setClickListener(new SimpleDialogListener() {

            @Override
            public void onPositiveClick() {
                super.onPositiveClick();
                SyncService.deleteStatus(context, id, handler);
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
                    case SyncService.RESULT_SUCCESS:
                        context.finish();
                        cursor.requery();
                        break;
                    case SyncService.RESULT_ERROR:
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
                    case SyncService.RESULT_SUCCESS:
                        context.finish();
                        adapter.remove(status);
                        break;
                    case SyncService.RESULT_ERROR:
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
