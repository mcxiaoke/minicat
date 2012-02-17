package com.fanfou.app.hd.dao;

import android.content.ContentUris;
import android.net.Uri;

import com.fanfou.app.hd.dao.model.DirectMessageColumns;
import com.fanfou.app.hd.dao.model.RecordColumns;
import com.fanfou.app.hd.dao.model.StatusColumns;
import com.fanfou.app.hd.dao.model.UserColumns;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.17
 * 
 */
public final class UriHelper {
	public static Uri userFriendsUriForId(String id) {
		return Uri.withAppendedPath(UserColumns.CONTENT_URI, "friends/" + id);
	}

	public static Uri userFollowersUriForId(String id) {
		return Uri.withAppendedPath(UserColumns.CONTENT_URI, "followers/" + id);
	}

	public static Uri userUriForId(String id) {
		return Uri.withAppendedPath(UserColumns.CONTENT_URI, "id/" + id);
	}
	

	public static Uri statusUriForId(String id) {
		return Uri.withAppendedPath(StatusColumns.CONTENT_URI, "id/" + id);
	}

	public static Uri dmConversationUri() {
		return Uri.withAppendedPath(DirectMessageColumns.CONTENT_URI,
				"conversation");
	}

	public static Uri dmInboxUri() {
		return Uri.withAppendedPath(DirectMessageColumns.CONTENT_URI, "inbox");
	}

	public static Uri dmOutboxUri() {
		return Uri.withAppendedPath(DirectMessageColumns.CONTENT_URI, "outbox");
	}

	public static Uri dmThreadUriForId(String id) {
		return Uri.withAppendedPath(DirectMessageColumns.CONTENT_URI, "thread/"
				+ id);
	}

	public static Uri dmUriForId(String id) {
		return Uri.withAppendedPath(DirectMessageColumns.CONTENT_URI, "id/"
				+ id);
	}
	
	

	public static Uri recordUriForId(int id) {
		return ContentUris.withAppendedId(RecordColumns.CONTENT_URI, id);
	}
	
	
}
