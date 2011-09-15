package com.fanfou.app.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.config.Commons;
import com.fanfou.app.db.Contents.BasicColumns;
import com.fanfou.app.db.Contents.DirectMessageInfo;
import com.fanfou.app.http.Response;
import com.fanfou.app.http.ResponseCode;
import com.fanfou.app.util.StringHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.04.30
 * @version 1.1 2011.05.02
 * @version 1.5 2011.05.20
 * @version 1.6 2011.07.22
 * 
 */
public class DirectMessage implements Storable<DirectMessage> {

	public static final String tag = DirectMessage.class.getSimpleName();

	private static void log(String message) {
		Log.e(tag, message);
	}

	private static final long serialVersionUID = 7135927428287533074L;
	// public static final int TYPE_IN=Commons.DIRECT_MESSAGE_TYPE_INBOX;
	// public static final int TYPE_OUT=Commons.DIRECT_MESSAGE_TYPE_OUTBOX;
	public static final int TYPE_NONE = Commons.DIRECT_MESSAGE_TYPE_NONE;

	public String id;
	public String ownerId;
	public String text;
	public Date createdAt;
	public String senderId;
	public String senderScreenName;
	public String recipientId;
	public String recipientScreenName;

	public String senderProfileImageUrl;
	public String recipientProfileImageUrl;

	public int type;

	public User sender = null;
	public User recipient = null;

	@Override
	public int compareTo(DirectMessage another) {
		return createdAt.compareTo(another.createdAt);
	}

	public boolean isNull() {
		return StringHelper.isEmpty(id);
	}

	public static List<DirectMessage> parseMessges(Response r)
			throws ApiException {
		try {
			JSONArray a = new JSONArray(r.getContent());
			return parseMessges(a);
		} catch (JSONException e) {
			if (App.DEBUG)
				e.printStackTrace();
			throw new ApiException(ResponseCode.ERROR_PARSE_FAILED,
					e.getMessage(), e.getCause());
		}
	}

	public static List<DirectMessage> parseMessges(JSONArray a)
			throws ApiException {
		if (a == null) {
			return null;
		}
		List<DirectMessage> dms = new ArrayList<DirectMessage>();
		try {
			for (int i = 0; i < a.length(); i++) {
				JSONObject o = a.getJSONObject(i);
				DirectMessage dm = parse(o);
				dms.add(dm);
			}
		} catch (JSONException e) {
			throw new ApiException(ResponseCode.ERROR_PARSE_FAILED,
					e.getMessage(), e);
		}
		return dms;
	}

	public static DirectMessage parse(Cursor c) {
		if (c == null) {
			return null;
		}
		DirectMessage dm = new DirectMessage();
		dm.id = Parser.parseString(c, BasicColumns.ID);
		dm.ownerId = Parser.parseString(c, BasicColumns.OWNER_ID);
		dm.text = Parser.parseString(c, DirectMessageInfo.TEXT);
		dm.createdAt = Parser.parseDate(c, BasicColumns.CREATED_AT);
		dm.senderId = Parser.parseString(c, DirectMessageInfo.SENDER_ID);
		dm.senderScreenName = Parser.parseString(c,
				DirectMessageInfo.SENDER_SCREEN_NAME);
		dm.recipientId = Parser.parseString(c, DirectMessageInfo.RECIPIENT_ID);
		dm.recipientScreenName = Parser.parseString(c,
				DirectMessageInfo.RECIPIENT_SCREEN_NAME);
		dm.senderProfileImageUrl = Parser.parseString(c,
				DirectMessageInfo.SENDER_PROFILE_IMAGE_URL);
		dm.recipientProfileImageUrl = Parser.parseString(c,
				DirectMessageInfo.RECIPIENT_PROFILE_IMAGE_URL);

		dm.type = Parser.parseInt(c, BasicColumns.TYPE);

		return dm;
	}

	public static DirectMessage parse(Response r) throws ApiException {
		try {
			JSONObject o = new JSONObject(r.getContent());
			return parse(o);
		} catch (JSONException e) {
			if (App.DEBUG)
				e.printStackTrace();
			throw new ApiException(ResponseCode.ERROR_PARSE_FAILED,
					e.getMessage(), e.getCause());
		}
	}

	public static DirectMessage parse(JSONObject o) throws ApiException {
		if (o == null) {
			return null;
		}
		DirectMessage dm = null;
		try {
			dm = new DirectMessage();
			dm.id = o.getString(BasicColumns.ID);
			dm.text = o.getString(DirectMessageInfo.TEXT);
			dm.createdAt = Parser.date(o.getString(BasicColumns.CREATED_AT));
			dm.senderId = o.getString(DirectMessageInfo.SENDER_ID);
			dm.senderScreenName = o
					.getString(DirectMessageInfo.SENDER_SCREEN_NAME);
			dm.recipientId = o.getString(DirectMessageInfo.RECIPIENT_ID);
			dm.recipientScreenName = o
					.getString(DirectMessageInfo.RECIPIENT_SCREEN_NAME);

			if (o.has("sender")) {
				JSONObject so = o.getJSONObject("sender");
				dm.sender = User.parse(so);
				dm.senderProfileImageUrl = dm.sender.profileImageUrl;
			}
			if (o.has("recipient")) {
				JSONObject so = o.getJSONObject("recipient");
				dm.recipient = User.parse(so);
				dm.recipientProfileImageUrl = dm.recipient.profileImageUrl;
			}

			dm.type = DirectMessage.TYPE_NONE;
		} catch (JSONException e) {
			throw new ApiException(ResponseCode.ERROR_PARSE_FAILED,
					e.getMessage(), e);
		}
		if (App.DEBUG)
			log("DirectMessage.parse id=" + dm.id);
		return dm;
	}

	@Override
	public ContentValues toContentValues() {
		ContentValues cv = new ContentValues();
		cv.put(BasicColumns.ID, this.id);
		cv.put(BasicColumns.OWNER_ID, this.ownerId);
		cv.put(DirectMessageInfo.TEXT, this.text);
		cv.put(BasicColumns.CREATED_AT, this.createdAt.getTime());
		cv.put(DirectMessageInfo.SENDER_ID, this.senderId);
		cv.put(DirectMessageInfo.RECIPIENT_ID, this.recipientId);
		cv.put(DirectMessageInfo.SENDER_SCREEN_NAME, this.senderScreenName);
		cv.put(DirectMessageInfo.RECIPIENT_SCREEN_NAME,
				this.recipientScreenName);
		cv.put(DirectMessageInfo.SENDER_PROFILE_IMAGE_URL,
				this.senderProfileImageUrl);
		cv.put(DirectMessageInfo.RECIPIENT_PROFILE_IMAGE_URL,
				this.recipientProfileImageUrl);
		cv.put(BasicColumns.TYPE, this.type);
		cv.put(BasicColumns.TIMESTAMP, new Date().getTime());
		return cv;
	}

	@Override
	public String toString() {
		// return toContentValues().toString();
		return "[Message] " + BasicColumns.ID + "=" + this.id + " "
				+ DirectMessageInfo.TEXT + "=" + this.text + " "
				+ BasicColumns.CREATED_AT + "=" + this.createdAt + " "
				+ DirectMessageInfo.SENDER_ID + "=" + this.senderId + " "
				+ DirectMessageInfo.RECIPIENT_ID + "=" + this.recipientId + " ";
	}

}
