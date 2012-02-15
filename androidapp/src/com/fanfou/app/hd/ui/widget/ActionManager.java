package com.fanfou.app.hd.ui.widget;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.fanfou.app.hd.R;
import com.fanfou.app.hd.App;
import com.fanfou.app.hd.UIDrafts;
import com.fanfou.app.hd.UIMyProfile;
import com.fanfou.app.hd.UIDMSend;
import com.fanfou.app.hd.UIProfile;
import com.fanfou.app.hd.UIUserList;
import com.fanfou.app.hd.UIWrite;
import com.fanfou.app.hd.api.DirectMessage;
import com.fanfou.app.hd.api.Status;
import com.fanfou.app.hd.api.User;
import com.fanfou.app.hd.service.Constants;
import com.fanfou.app.hd.util.OptionHelper;
import com.fanfou.app.hd.util.StatusHelper;
import com.fanfou.app.hd.util.StringHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.08.25
 * @version 1.1 2011.10.26
 * @version 1.2 2011.10.27
 * @version 1.3 2011.10.28
 * @version 2.0 2011.10.29
 * @version 2.1 2011.11.09
 * @version 2.2 2011.11.11
 * @version 2.3 2011.11.21
 * @version 2.4 2011.12.08
 * @version 3.0 2011.12.19
 * @version 4.0 2012.02.08
 * 
 */
public final class ActionManager {
	private static final String TAG = ActionManager.class.getSimpleName();

	private ActionManager() {
	}

	public static void doShowTimeline(Context context, final User user) {
		Intent intent = new Intent(context, UIProfile.class);
		intent.putExtra(Constants.EXTRA_DATA, user);
		intent.putExtra(Constants.EXTRA_PAGE, UIProfile.Page.TIMELINE.ordinal());
		intent.putExtra(Constants.EXTRA_TYPE,
				Constants.TYPE_STATUSES_USER_TIMELINE);
		context.startActivity(intent);
	}

	//
	// public static void doShowTimeline(Context context, final User user) {
	// Intent intent = new Intent(context, UITimeline.class);
	// intent.putExtra(Constants.EXTRA_DATA, user);
	// context.startActivity(intent);
	// }

	public static void doShowFavorites(Context context, final User user) {
		Intent intent = new Intent(context, UIProfile.class);
		intent.putExtra(Constants.EXTRA_DATA, user);
		intent.putExtra(Constants.EXTRA_PAGE,
				UIProfile.Page.FAVORITES.ordinal());
		intent.putExtra(Constants.EXTRA_TYPE, Constants.TYPE_FAVORITES_LIST);
		context.startActivity(intent);
	}

	// public static void doShowFavorites(Context context, final User user) {
	// Intent intent = new Intent(context, UIFavorites.class);
	// intent.putExtra(Constants.EXTRA_DATA, user);
	// context.startActivity(intent);
	// }

	public static void doShowFriends(Context context, final User user) {
		Intent intent = new Intent(context, UIUserList.class);
		intent.putExtra(Constants.EXTRA_DATA, user);
//		intent.putExtra(Constants.EXTRA_PAGE, UIProfile.Page.FRIENDS.ordinal());
		intent.putExtra(Constants.EXTRA_TYPE, Constants.TYPE_USERS_FRIENDS);
		context.startActivity(intent);
	}

	// public static void doShowFriends(Context context, final User user) {
	// Intent intent = new Intent(context, UIUserList.class);
	// intent.putExtra(Constants.EXTRA_DATA, user);
	// intent.putExtra(Constants.EXTRA_TYPE, Constants.TYPE_USERS_FRIENDS);
	// context.startActivity(intent);
	// }

	public static void doShowFollowers(Context context, final User user) {
		Intent intent = new Intent(context, UIUserList.class);
		intent.putExtra(Constants.EXTRA_DATA, user);
//		intent.putExtra(Constants.EXTRA_PAGE,
//				UIProfile.Page.FOLLOWERS.ordinal());
		intent.putExtra(Constants.EXTRA_TYPE, Constants.TYPE_USERS_FOLLOWERS);
		context.startActivity(intent);
	}

	// public static void doShowFollowers(Context context, final User user) {
	// Intent intent = new Intent(context, UIUserList.class);
	// intent.putExtra(Constants.EXTRA_DATA, user);
	// intent.putExtra(Constants.EXTRA_TYPE,
	// Constants.TYPE_USERS_FOLLOWERS);
	// context.startActivity(intent);
	// }

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
			if (App.DEBUG) {
				Log.d(TAG, "doProfile: userid is null.");
			}
			throw new NullPointerException("userid cannot be null.");
		}
		Intent intent = new Intent(context, UIProfile.class);
		intent.putExtra(Constants.EXTRA_ID, userId);
		intent.putExtra(Constants.EXTRA_PAGE, UIProfile.Page.PROFILE.ordinal());
		intent.putExtra(Constants.EXTRA_TYPE, Constants.TYPE_USERS_SHOW);
		context.startActivity(intent);
	}

	public static void doProfile(Context context, DirectMessage dm) {
		if (dm == null || dm.isNull()) {
			if (App.DEBUG) {
				Log.d(TAG, "doProfile: status is null.");
			}
			throw new NullPointerException("directmessage cannot be null.");
		}
		Intent intent = new Intent(context, UIProfile.class);
		intent.putExtra(Constants.EXTRA_ID, dm.senderId);
		intent.putExtra(Constants.EXTRA_USER_NAME, dm.senderScreenName);
		intent.putExtra(Constants.EXTRA_USER_HEAD, dm.senderProfileImageUrl);
		intent.putExtra(Constants.EXTRA_PAGE, UIProfile.Page.PROFILE.ordinal());
		intent.putExtra(Constants.EXTRA_TYPE, Constants.TYPE_USERS_SHOW);
		context.startActivity(intent);
	}

	public static void doProfile(Context context, Status status) {
		if (status == null || status.isNull()) {
			if (App.DEBUG) {
				Log.d(TAG, "doProfile: status is null.");
			}
			throw new NullPointerException("status cannot be null.");
		}
		if (status.userId.equals(App.getUserId())) {
			doMyProfile(context);
			return;
		}
		Intent intent = new Intent(context, UIProfile.class);
		intent.putExtra(Constants.EXTRA_ID, status.userId);
		intent.putExtra(Constants.EXTRA_USER_NAME, status.userScreenName);
		intent.putExtra(Constants.EXTRA_USER_HEAD, status.userProfileImageUrl);
		intent.putExtra(Constants.EXTRA_PAGE, UIProfile.Page.PROFILE.ordinal());
		intent.putExtra(Constants.EXTRA_TYPE, Constants.TYPE_USERS_SHOW);
		context.startActivity(intent);
	}

	public static void doProfile(Context context, User user) {
		if (user == null || user.isNull()) {
			if (App.DEBUG) {
				Log.d(TAG, "doProfile: user is null.");
			}
			throw new NullPointerException("user cannot be null.");
		}
		if (user.id.equals(App.getUserId())) {
			doMyProfile(context);
			return;
		}
		Intent intent = new Intent(context, UIProfile.class);
		intent.putExtra(Constants.EXTRA_DATA, user);
		intent.putExtra(Constants.EXTRA_PAGE, UIProfile.Page.PROFILE.ordinal());
		intent.putExtra(Constants.EXTRA_TYPE, Constants.TYPE_USERS_SHOW);
		context.startActivity(intent);
	}

	public static void doShare(Context context, Status status) {
		if (status == null || status.isNull()) {
			if (App.DEBUG) {
				Log.d(TAG, "doShare: status is null.");
			}
			throw new NullPointerException("status cannot be null.");
		}
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "来自" + status.userScreenName
				+ "的饭否消息");
		intent.putExtra(Intent.EXTRA_TEXT, status.simpleText);
		context.startActivity(Intent.createChooser(intent, "分享"));
	}

	public static void doShare(Context context, File image) {
		if (App.DEBUG) {
			Log.d(TAG, "doShare: image is " + image);
		}
		if (image == null) {
			return;
		}
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("image/*");
		intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(image));
		context.startActivity(Intent.createChooser(intent, "分享"));
	}

	public static void doReply(Context context, Status status) {

		if (status != null) {
			if (App.DEBUG) {
				Log.d(TAG, "doReply: status is null.");
			}
			StringBuilder sb = new StringBuilder();
			boolean replyToAll = OptionHelper.readBoolean(context,
					R.string.option_reply_to_all_default, true);
			if (replyToAll) {
				ArrayList<String> names = StatusHelper.getMentions(status);
				for (String name : names) {
					sb.append("@").append(name).append(" ");
				}
			} else {
				sb.append("@").append(status.userScreenName).append(" ");
			}

			Intent intent = new Intent(context, UIWrite.class);
			intent.putExtra(Constants.EXTRA_IN_REPLY_TO_ID, status.id);
			intent.putExtra(Constants.EXTRA_TEXT, sb.toString());
			intent.putExtra(Constants.EXTRA_TYPE, UIWrite.TYPE_REPLY);
			context.startActivity(intent);
		} else {
			doWrite(context, null);
		}

	}

	public static void doWrite(Context context, String text, File file, int type) {
		Intent intent = new Intent(context, UIWrite.class);
		intent.putExtra(Constants.EXTRA_TYPE, type);
		intent.putExtra(Constants.EXTRA_TEXT, text);
		intent.putExtra(Constants.EXTRA_DATA, file);
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
		Intent intent = new Intent(context, UIDMSend.class);
		context.startActivity(intent);
	}

	public static void doRetweet(Context context, Status status) {
		if (status == null || status.isNull()) {
			throw new NullPointerException("status cannot be null.");
		}
		Intent intent = new Intent(context, UIWrite.class);
		intent.putExtra(Constants.EXTRA_TYPE, UIWrite.TYPE_REPOST);
		intent.putExtra(Constants.EXTRA_IN_REPLY_TO_ID, status.id);
		intent.putExtra(Constants.EXTRA_TEXT, "转@" + status.userScreenName
				+ " " + status.simpleText);
		context.startActivity(intent);
	}

	public static void doMessage(Context context, final User user) {
		final Intent intent = new Intent(context, UIDMSend.class);
		intent.putExtra(Constants.EXTRA_ID, user.id);
		intent.putExtra(Constants.EXTRA_USER_NAME, user.screenName);
		context.startActivity(intent);
	}

	public interface ResultListener {
		public void onActionSuccess(int type, String message);

		public void onActionFailed(int type, String message);
	}

}
