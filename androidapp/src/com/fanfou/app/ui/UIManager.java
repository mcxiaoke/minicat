package com.fanfou.app.ui;

import java.util.List;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.BaseAdapter;
import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.api.Status;
import com.fanfou.app.config.Commons;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.09
 * 
 */
public final class UIManager {
	
	public static QuickAction makePopup(Context context, final Status status){
		ActionItem reply = new ActionItem();
		reply.setIcon(context.getResources()
				.getDrawable(R.drawable.i_pop_reply));

		ActionItem delete = new ActionItem();
		delete.setIcon(context.getResources()
				.getDrawable(R.drawable.i_pop_delete));

		ActionItem favorite = new ActionItem();
		favorite.setIcon(context.getResources()
				.getDrawable(R.drawable.i_pop_favorite));

		ActionItem unfavorite = new ActionItem();
		unfavorite.setIcon(context.getResources()
				.getDrawable(R.drawable.i_pop_unfavorite));

		ActionItem retweet = new ActionItem();
		retweet.setIcon(context.getResources()
				.getDrawable(R.drawable.i_pop_retweet));

		ActionItem share = new ActionItem();
		share.setIcon(context.getResources()
				.getDrawable(R.drawable.i_pop_share));

		ActionItem profile = new ActionItem();
		profile.setIcon(context.getResources()
				.getDrawable(R.drawable.i_pop_profile));
		
		final boolean me = status.userId.equals(App.me.userId);

		final QuickAction q = new QuickAction(context);
		q.addActionItem(me ? delete : reply);
		q.addActionItem(status.favorited ? unfavorite : favorite);
		q.addActionItem(retweet);
		q.addActionItem(share);
		q.addActionItem(profile);
		
		return q;
	}
	
	public static void showPopup(final Activity a,final View v, final Status s,final BaseAdapter adapter, final List<Status> ss){
		
		QuickAction q=makePopup(a, s);
		
		q.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {

			@Override
			public void onItemClick(int pos) {
				switch (pos) {
				case 0:
					if (s.userId.equals(App.me.userId)) {
						UIManager.doDelete(a, s, adapter, ss);
					} else {
						ActionManager.doReply(a, s);
					}
					break;
				case 1:
					UIManager.doFavorite(a, s, adapter);
					break;
				case 2:
					ActionManager.doRetweet(a, s);
					break;
				case 3:
					ActionManager.doShare(a, s);
					break;
				case 4:
					ActionManager.doProfile(a, s);
					break;
				default:
					break;
				}
			}
		});
		q.show(v);	
	}
	
	public static void showPopup(final Activity a, final Cursor c,final View v, final Status s){
		
		QuickAction q=makePopup(a, s);
		
		q.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {

			@Override
			public void onItemClick(int pos) {
				switch (pos) {
				case 0:
					if (s.userId.equals(App.me.userId)) {
						UIManager.doDelete(a,s, c);
					} else {
						ActionManager.doReply(a, s);
					}
					break;
				case 1:
					UIManager.doFavorite(a,s, c);
					break;
				case 2:
					ActionManager.doRetweet(a, s);
					break;
				case 3:
					ActionManager.doShare(a, s);
					break;
				case 4:
					ActionManager.doProfile(a, s);
					break;
				default:
					break;
				}
			}
		});
		q.show(v);	
	}
	
	public static void doDelete(final Activity activity,final Status s, final BaseAdapter adapter,final List<Status> ss) {
		ResultHandler li=new ResultHandler() {
			@Override
			public void onActionSuccess(int type, String message) {
				ss.remove(s);
				adapter.notifyDataSetChanged();
			}
		};
		ActionManager.doDelete(activity, s.id, li);
	}
	
	public static void doDelete(final Activity activity,final Status s, final Cursor c) {
		ResultHandler li=new ResultHandler() {
			@Override
			public void onActionSuccess(int type, String message) {
				c.requery();
			}
		};
		ActionManager.doDelete(activity, s.id, li);
	}
	
	public static void doFavorite(final Activity activity,final Status s, final BaseAdapter adapter) {
		ResultHandler li=new ResultHandler() {	
			@Override
			public void onActionSuccess(int type, String message) {
				if(type==Commons.ACTION_STATUS_FAVORITE){
					s.favorited=true;
				}else{
					s.favorited=false;
				}
				adapter.notifyDataSetChanged();
			}
		};
		ActionManager.doFavorite(activity, s, li);
	}

	public static void doFavorite(final Activity activity,final Status s, final Cursor c) {
		ResultHandler li=new ResultHandler() {	
			@Override
			public void onActionSuccess(int type, String message) {
				c.requery();
			}
		};
		ActionManager.doFavorite(activity, s, li);
	}
	
	public abstract static class ResultHandler implements ActionManager.ResultListener{
		@Override
		public void onActionFailed(int type, String message) {
		}
	}

	

}
