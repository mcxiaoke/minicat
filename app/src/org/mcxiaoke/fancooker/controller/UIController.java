/**
 * 
 */
package org.mcxiaoke.fancooker.controller;

import java.io.File;
import java.util.ArrayList;

import org.mcxiaoke.fancooker.R;
import org.mcxiaoke.fancooker.UIAbout;
import org.mcxiaoke.fancooker.UIConversation;
import org.mcxiaoke.fancooker.UIEditProfile;
import org.mcxiaoke.fancooker.UIFavorites;
import org.mcxiaoke.fancooker.UIHome;
import org.mcxiaoke.fancooker.UILogin;
import org.mcxiaoke.fancooker.UIPhoto;
import org.mcxiaoke.fancooker.UIRecords;
import org.mcxiaoke.fancooker.UISearch;
import org.mcxiaoke.fancooker.UIStatus;
import org.mcxiaoke.fancooker.UIThread;
import org.mcxiaoke.fancooker.UITimeline;
import org.mcxiaoke.fancooker.UIUserList;
import org.mcxiaoke.fancooker.UIWrite;
import org.mcxiaoke.fancooker.dao.model.DirectMessageModel;
import org.mcxiaoke.fancooker.dao.model.StatusModel;
import org.mcxiaoke.fancooker.dao.model.UserModel;
import org.mcxiaoke.fancooker.service.FanFouService;
import org.mcxiaoke.fancooker.util.OptionHelper;
import org.mcxiaoke.fancooker.util.StatusHelper;
import org.mcxiaoke.fancooker.util.StringHelper;
import org.mcxiaoke.fancooker.util.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

/**
 * @author mcxiaoke
 * @version 1.0 2012-2-24 下午5:08:25
 * @version 1.1 2012.04.24
 * 
 */
public class UIController {

	private static void startUIByAnimation(Activity activity, Intent intent) {
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

	private static void startUIByAnimationBack(Activity activity, Intent intent) {
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

	public static void showFanfouBlog(Context context) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("http://blog.fanfou.com/"));
		context.startActivity(intent);
	}

	public static void showAnnounce(Activity context) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("fanfouhd://user/androidsupport"));
		startUIByAnimation(context, intent);
	}

	public static void showOption(Context context) {
	}

	public static void goStatusPage(Activity context, String id) {
		if (!StringHelper.isEmpty(id)) {
			Intent intent = new Intent(context, UIStatus.class);
			intent.putExtra("id", id);
			startUIByAnimation(context, intent);
		}
	}

	public static void goStatusPage(Activity context, StatusModel s) {
		if (s != null) {
			Intent intent = new Intent(context, UIStatus.class);
			intent.putExtra("data", s);
			startUIByAnimation(context, intent);
		}
	}

	public static void goPhotoViewPage(Activity context, String photoUrl) {
		Intent intent = new Intent(context, UIPhoto.class);
		intent.putExtra("url", photoUrl);
		startUIByAnimation(context, intent);
	}

	public static void showEditProfile(Activity context, final UserModel user) {
		Intent intent = new Intent(context, UIEditProfile.class);
		intent.putExtra("data", user);
		startUIByAnimation(context, intent);
	}

	public static void showAbout(Activity context) {
		startUIByAnimation(context, new Intent(context, UIAbout.class));
	}

	public static void showLogin(Context context) {
		Intent intent = new Intent(context, UILogin.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
	}

	public static void showTopic(Activity context) {
		startUIByAnimation(context, new Intent(context, UISearch.class));
	}

	public static void showHome(Activity context) {
		Intent intent = new Intent(context, UIHome.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
	}

	public static void backHome(Activity context) {
		Intent intent = new Intent(context, UIHome.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// context.startActivity(intent);
		startUIByAnimationBack(context, intent);
	}

	public static void showConversation(Activity context, DirectMessageModel dm) {
		Intent intent = new Intent(context, UIConversation.class);
		intent.putExtra("refresh", true);
		if (dm.isIncoming()) {
			intent.putExtra("id", dm.getSenderId());
			intent.putExtra("screen_name", dm.getSenderScreenName());
			intent.putExtra("profile_image_url", dm.getSenderProfileImageUrl());
		} else {
			intent.putExtra("id", dm.getRecipientId());
			intent.putExtra("screen_name", dm.getRecipientScreenName());
			intent.putExtra("profile_image_url",
					dm.getRecipientProfileImageUrl());
		}
		startUIByAnimation(context, intent);
	}

	public static void showConversation(Activity context, UserModel user,
			boolean refresh) {
		Intent intent = new Intent(context, UIConversation.class);
		intent.putExtra("id", user.getId());
		intent.putExtra("screen_name", user.getScreenName());
		intent.putExtra("profile_image_url", user.getProfileImageUrl());
		intent.putExtra("refresh", refresh);
		startUIByAnimation(context, intent);
	}

	public static void showWrite(Activity context) {
		Intent intent = new Intent(context, UIWrite.class);
		startUIByAnimation(context, intent);
	}

	public static void showWrite(Activity context, String text, File file) {
		Intent intent = new Intent(context, UIWrite.class);
		intent.putExtra("text", text);
		intent.putExtra("data", file);
		startUIByAnimation(context, intent);
	}

	public static void showWrite(Activity context, String text) {
		Intent intent = new Intent(context, UIWrite.class);
		intent.putExtra("text", text);
		startUIByAnimation(context, intent);
	}

	public static void doFavorite(final Context context, String id) {
		final Handler handler = getFavoriteHandler(context);
		FanFouService.favorite(context, id, handler);
	}

	public static void doUnFavorite(final Context context, String id) {
		final Handler handler = getFavoriteHandler(context);
		FanFouService.unfavorite(context, id, handler);
	}

	private static Handler getFavoriteHandler(final Context context) {
		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case FanFouService.RESULT_SUCCESS:
					boolean favorited = msg.getData().getBoolean("boolean");
					Utils.notify(context, favorited ? "收藏成功" : "取消收藏成功");
					break;
				case FanFouService.RESULT_ERROR:
					break;
				default:
					break;
				}
			}
		};
		return handler;
	}

	public static void doRetweet(Activity context, final StatusModel status) {
		Intent intent = new Intent(context, UIWrite.class);
		StringBuilder builder = new StringBuilder();
		builder.append(" 转@").append(status.getUserScreenName()).append(" ")
				.append(status.getSimpleText());
		intent.putExtra("text", builder.toString());
		intent.putExtra("id", status.getId());
		intent.putExtra("type", UIWrite.TYPE_REPOST);
		startUIByAnimation(context, intent);
	}

	public static void doShare(Context context, StatusModel status) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "来自" + status.getUserScreenName()
				+ "的饭否消息");
		intent.putExtra(Intent.EXTRA_TEXT, status.getSimpleText());
		context.startActivity(Intent.createChooser(intent, "分享"));
	}

	public static void doReply(Activity context, StatusModel status) {

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
			startUIByAnimation(context, intent);
		} else {
			showWrite(context);
		}

	}

	public static void showRecords(Activity context) {
		Intent intent = new Intent(context, UIRecords.class);
		startUIByAnimation(context, intent);
	}

	public static void showProfile(Activity context, String id) {
		Intent intent = new Intent(context, UITimeline.class);
		intent.putExtra("id", id);
		startUIByAnimation(context, intent);
	}

	public static void showProfile(Activity context, UserModel user) {
		Intent intent = new Intent(context, UITimeline.class);
		intent.putExtra("id", user.getId());
		startUIByAnimation(context, intent);
	}

	public static void showTimeline(Activity context, String id) {
		Intent intent = new Intent(context, UITimeline.class);
		intent.putExtra("id", id);
		startUIByAnimation(context, intent);
	}

	public static void showFavorites(Activity context, String id) {
		Intent intent = new Intent(context, UIFavorites.class);
		intent.putExtra("id", id);
		startUIByAnimation(context, intent);
	}

	public static void showThread(Activity context, String id) {
		Intent intent = new Intent(context, UIThread.class);
		intent.putExtra("id", id);
		startUIByAnimation(context, intent);
	}

	public static void showFriends(Activity context, String id) {
		Intent intent = new Intent(context, UIUserList.class);
		intent.putExtra("type", UserModel.TYPE_FRIENDS);
		intent.putExtra("id", id);
		startUIByAnimation(context, intent);
	}

	public static void showFollowers(Activity context, String id) {
		Intent intent = new Intent(context, UIUserList.class);
		intent.putExtra("type", UserModel.TYPE_FOLLOWERS);
		intent.putExtra("id", id);
		startUIByAnimation(context, intent);
	}

}
