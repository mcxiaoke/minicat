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
import com.fanfou.app.db.Contents.StatusInfo;
import com.fanfou.app.db.Contents.UserInfo;
import com.fanfou.app.http.Response;
import com.fanfou.app.http.ResponseCode;
import com.fanfou.app.util.StringHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.04.30
 * @version 1.1 2011.05.01
 * @version 1.2 2011.05.02
 * @version 1.3 2011.07.21
 * @version 1.4 2011.10.21
 * 
 */
public class User implements Storable<User> {

	public static final String tag = User.class.getSimpleName();

	private static void log(String message) {
		Log.v(tag, message);
	}

	private static final long serialVersionUID = -1323382928867629730L;

	public static final int TYPE_FRIENDS = Commons.USER_TYPE_FRIENDS;
	public static final int TYPE_FOLLOWERS = Commons.USER_TYPE_FOLLOWERS;

	public Date createdAt;
	public String id;
	public String ownerId;
	public String name;
	public String screenName;
	public String location;
	public String gender;
	public String birthday;
	public String description;
	public String profileImageUrl;
	public String url;
	public boolean protect;
	public int followersCount;
	public int friendsCount;
	public int favouritesCount;
	public int statusesCount;
	public boolean following;
	public boolean notifications;
	public int utcOffset;

	public int type;

	public String lastStatusId;
	public String lastStatusText;
	public Date lastStatusCreatedAt = null;
	
	public long realId;

	@Override
	public int compareTo(User another) {
		return createdAt.compareTo(another.createdAt);
	}

	public boolean isNull() {
		return StringHelper.isEmpty(id);
	}

	public static List<User> parseUsers(Response r) throws ApiException {
		try {
			JSONArray a = new JSONArray(r.getContent());
			return User.parseUsers(a);
		} catch (JSONException e) {
			if (App.DEBUG)
				e.printStackTrace();
			throw new ApiException(ResponseCode.ERROR_PARSE_FAILED,
					e.getMessage(), e.getCause());
		}
	}

	public static List<User> parseUsers(JSONArray a) throws ApiException {
		if (a == null) {
			return null;
		}
		List<User> users = new ArrayList<User>();
		try {
			for (int i = 0; i < a.length(); i++) {
				JSONObject o = a.getJSONObject(i);
				User u = User.parse(o);
				users.add(u);
			}
		} catch (JSONException e) {
			throw new ApiException(ResponseCode.ERROR_PARSE_FAILED,
					e.getMessage(), e);
		}
		return users;
	}

	public static User parse(Cursor c) {
		if (c == null) {
			return null;
		}
		User user = new User();

		user.createdAt = Parser.parseDate(c, UserInfo.CREATED_AT);
		user.id = Parser.parseString(c, UserInfo.ID);
		user.realId=Parser.parseLong(c, UserInfo.REAL_ID);
		user.ownerId = Parser.parseString(c, UserInfo.OWNER_ID);
		user.name = Parser.parseString(c, UserInfo.NAME);
		user.screenName = Parser.parseString(c, UserInfo.SCREEN_NAME);
		user.location = Parser.parseString(c, UserInfo.LOCATION);
		user.gender = Parser.parseString(c, UserInfo.GENDER);
		user.birthday = Parser.parseString(c, UserInfo.BIRTHDAY);
		user.description = Parser.parseString(c, UserInfo.DESCRIPTION);
		user.profileImageUrl = Parser
				.parseString(c, UserInfo.PROFILE_IMAGE_URL);
		user.url = Parser.parseString(c, UserInfo.URL);
		user.protect = Parser.parseBoolean(c, UserInfo.PROTECTED);
		user.followersCount = Parser.parseInt(c, UserInfo.FOLLOWERS_COUNT);
		user.friendsCount = Parser.parseInt(c, UserInfo.FRIENDS_COUNT);
		user.favouritesCount = Parser.parseInt(c, UserInfo.FAVORITES_COUNT);
		user.statusesCount = Parser.parseInt(c, UserInfo.STATUSES_COUNT);
		user.following = Parser.parseBoolean(c, UserInfo.FOLLOWING);
		user.notifications = Parser.parseBoolean(c, UserInfo.NOTIFICATIONS);
		user.utcOffset = Parser.parseInt(c, UserInfo.UTC_OFFSET);
		user.type = Parser.parseInt(c, UserInfo.TYPE);

		user.lastStatusId = Parser.parseString(c, UserInfo.LAST_STATUS_ID);
		user.lastStatusText = Parser.parseString(c, UserInfo.LAST_STATUS_TEXT);
		user.lastStatusCreatedAt = Parser.parseDate(c,
				UserInfo.LAST_STATUS_CREATED_AT);
		// if(App.DEBUG)
		// log("user parse cursor, last status date =" +
		// user.lastStatusCreatedAt);

		return user;
	}

	public static User parse(Response r) throws ApiException {
		try {
			JSONObject o = new JSONObject(r.getContent());
			return User.parse(o);
		} catch (JSONException e) {
			if (App.DEBUG)
				e.printStackTrace();
			throw new ApiException(ResponseCode.ERROR_PARSE_FAILED,
					e.getMessage(), e.getCause());
		}
	}

	public static User parse(JSONObject o) throws ApiException {
		if (o == null) {
			return null;
		}
		User u = null;
		try {
			u = new User();
			u.id = o.getString(UserInfo.ID);
			u.realId=Parser.decodeUserRealId(u.id);
			u.name = o.getString(UserInfo.NAME);
			u.screenName = o.getString(UserInfo.SCREEN_NAME);
			u.location = o.getString(UserInfo.LOCATION);
			u.gender = o.getString(UserInfo.GENDER);
			u.birthday = o.getString(UserInfo.BIRTHDAY);
			u.description = o.getString(UserInfo.DESCRIPTION);
			u.profileImageUrl = o.getString(UserInfo.PROFILE_IMAGE_URL);
			u.url = o.getString(UserInfo.URL);
			u.protect = o.getBoolean(UserInfo.PROTECTED);
			u.followersCount = o.getInt(UserInfo.FOLLOWERS_COUNT);
			u.friendsCount = o.getInt(UserInfo.FRIENDS_COUNT);
			u.favouritesCount = o.getInt(UserInfo.FAVORITES_COUNT);
			u.statusesCount = o.getInt(UserInfo.STATUSES_COUNT);
			u.following = o.getBoolean(UserInfo.FOLLOWING);
			u.notifications = o.getBoolean(UserInfo.NOTIFICATIONS);
			u.createdAt = Parser.date(o.getString(UserInfo.CREATED_AT));
			u.utcOffset = o.getInt(UserInfo.UTC_OFFSET);

			u.type = Commons.TYPE_NONE;
			u.ownerId=App.me.userId;

			if (o.has("status")) {
				JSONObject so = o.getJSONObject("status");
				String d = so.getString(UserInfo.CREATED_AT);
				u.lastStatusCreatedAt = Parser.date(d);
				if (App.DEBUG)
					log("userid=" + u.id + " date="
							+ Parser.formatDate(u.lastStatusCreatedAt));
				u.lastStatusId = so.getString(UserInfo.ID);
				u.lastStatusText = so.getString(StatusInfo.TEXT);
			}
		} catch (JSONException e) {
			throw new ApiException(ResponseCode.ERROR_PARSE_FAILED,
					e.getMessage(), e);
		}
		if (App.DEBUG)
			log("User.parse id=" + u.id);
		return u;
	}

	@Override
	public ContentValues toContentValues() {
		User u = this;
		ContentValues cv = new ContentValues();

		cv.put(UserInfo.ID, u.id);
		cv.put(UserInfo.REAL_ID, u.realId);
		cv.put(UserInfo.OWNER_ID, u.ownerId);
		cv.put(UserInfo.NAME, u.name);

		cv.put(UserInfo.SCREEN_NAME, u.screenName);
		cv.put(UserInfo.LOCATION, u.location);
		cv.put(UserInfo.GENDER, u.gender);
		cv.put(UserInfo.BIRTHDAY, u.birthday);

		cv.put(UserInfo.DESCRIPTION, u.description);
		cv.put(UserInfo.PROFILE_IMAGE_URL, u.profileImageUrl);
		cv.put(UserInfo.URL, u.url);
		cv.put(UserInfo.PROTECTED, u.protect);

		cv.put(UserInfo.FOLLOWERS_COUNT, u.followersCount);
		cv.put(UserInfo.FRIENDS_COUNT, u.friendsCount);
		cv.put(UserInfo.FAVORITES_COUNT, u.favouritesCount);
		cv.put(UserInfo.STATUSES_COUNT, u.statusesCount);

		cv.put(UserInfo.FOLLOWING, u.following);
		cv.put(UserInfo.NOTIFICATIONS, u.notifications);
		cv.put(UserInfo.CREATED_AT, u.createdAt.getTime());
		cv.put(UserInfo.UTC_OFFSET, u.utcOffset);

		if (u.lastStatusId != null) {
			cv.put(UserInfo.LAST_STATUS_CREATED_AT,
					u.lastStatusCreatedAt.getTime());
			cv.put(UserInfo.LAST_STATUS_ID, u.lastStatusId);
			cv.put(UserInfo.LAST_STATUS_TEXT, u.lastStatusText);
		}
		cv.put(UserInfo.TYPE, u.type);
		cv.put(UserInfo.TIMESTAMP, new Date().getTime());
		return cv;
	}

	@Override
	public String toString() {
		return "[User] " + UserInfo.ID + "=" + id + " "
				+ UserInfo.SCREEN_NAME + "=" + screenName + " " + UserInfo.NAME
				+ "=" + name + " " + UserInfo.LOCATION + "=" + location + " "
				+ UserInfo.GENDER + "=" + gender + " " + UserInfo.BIRTHDAY
				+ "=" + birthday + " " + UserInfo.DESCRIPTION + "="
				+ description + " " + UserInfo.PROFILE_IMAGE_URL + "="
				+ profileImageUrl + " " + UserInfo.URL + "=" + url + " "
				+ UserInfo.PROTECTED + "=" + protect + " "
				+ UserInfo.FOLLOWERS_COUNT + "=" + followersCount + " "
				+ UserInfo.FRIENDS_COUNT + "=" + friendsCount + " "
				+ UserInfo.FAVORITES_COUNT + "=" + favouritesCount + " "
				+ UserInfo.STATUSES_COUNT + "=" + statusesCount + " "
				+ UserInfo.FOLLOWING + "=" + following + " "
				+ UserInfo.NOTIFICATIONS + "=" + notifications + " "
				+ UserInfo.CREATED_AT + "=" + createdAt + " "
				+ UserInfo.UTC_OFFSET + "=" + utcOffset + " "
				+ UserInfo.LAST_STATUS_CREATED_AT + "=" + lastStatusCreatedAt
				+ " " + UserInfo.LAST_STATUS_ID + "=" + lastStatusId + " "
				+ UserInfo.LAST_STATUS_TEXT + "=" + lastStatusText + " "
				+ UserInfo.TYPE + "=" + type + " ";
	}

}
