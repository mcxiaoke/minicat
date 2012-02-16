package com.fanfou.app.hd.api;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.db.Contents.BasicColumns;
import com.fanfou.app.hd.db.Contents.StatusInfo;
import com.fanfou.app.hd.db.Contents.UserInfo;
import com.fanfou.app.hd.http.NetResponse;
import com.fanfou.app.hd.http.ResponseCode;
import com.fanfou.app.hd.service.Constants;
import com.fanfou.app.hd.util.StatusHelper;
import com.fanfou.app.hd.util.StringHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.04.30
 * @version 1.1 2011.05.01
 * @version 1.2 2011.05.02
 * @version 1.3 2011.06.03
 * @version 1.4 2011.07.21
 * @version 1.5 2011.10.19
 * @version 1.6 2011.10.21
 * @version 1.7 2011.11.04
 * @version 2.0 2011.11.10
 * @version 2.1 2011.11.11
 * @version 2.2 2011.12.01
 * @version 2.3 2011.12.16
 * @version 2.4 2011.12.19
 * @version 3.0 2011.12.21
 * 
 */
public class Status implements Storable<Status> {

	public static final String TAG = Status.class.getSimpleName();

	private static void log(String message) {
		Log.d(TAG, message);
	}

	public String id;
	public long rawId;
	public String ownerId;
	public Date createdAt;

	public String text;
	public String simpleText;
	public String source;

	public String inReplyToStatusId;
	public String inReplyToUserId;
	public String inReplyToScreenName;

	public String photoImageUrl;
	public String photoThumbUrl;
	public String photoLargeUrl;

	public String userId;
	public String userScreenName;
	public String userProfileImageUrl;

	public boolean truncated;
	public boolean favorited;
	public boolean self;

	public boolean isRead;
	public boolean isThread;
	public boolean hasPhoto;
	public boolean special;

	public int type;

	public User user;
	public Photo photo;

	public Status() {
	}

	public Status(Parcel in) {
		id=in.readString();
		rawId=in.readLong();
		ownerId=in.readString();
		createdAt=new Date(in.readLong());
		type=in.readInt();
		
		text=in.readString();
		simpleText=in.readString();
		source=in.readString();
		
		inReplyToStatusId=in.readString();
		inReplyToUserId=in.readString();
		inReplyToScreenName=in.readString();
		
		photoImageUrl=in.readString();
		photoLargeUrl=in.readString();
		photoThumbUrl=in.readString();
		
		userId=in.readString();
		userScreenName=in.readString();
		userProfileImageUrl=in.readString();
		
		truncated=in.readInt()==0?false:true;
		favorited=in.readInt()==0?false:true;
		self=in.readInt()==0?false:true;
		
		isRead=in.readInt()==0?false:true;
		isThread=in.readInt()==0?false:true;
		hasPhoto=in.readInt()==0?false:true;
		
		special=in.readInt()==0?false:true;
		
	}

	@Override
	public int compareTo(Status another) {
		return createdAt.compareTo(another.createdAt);
	}

	public boolean isNull() {
		return StringHelper.isEmpty(id);
	}

	public static ArrayList<Status> parseStatuses(NetResponse r, int type)
			throws ApiException {
		if (App.DEBUG) {
			log("parseStatuses response");
		}
		return Status.parseStatuses(r.getJSONArray(), type);
	}

	public static ArrayList<Status> parseStatuses(String content, int type)
			throws ApiException {
		if (App.DEBUG) {
			log("parseStatuses content");
		}
		JSONArray a;
		try {
			a = new JSONArray(content);
		} catch (JSONException e) {
			if (App.DEBUG) {
				e.printStackTrace();
			}
			throw new ApiException(ResponseCode.ERROR_JSON_EXCEPTION,
					e.getMessage(), e);
		}
		return parseStatuses(a, type);
	}

	public static ArrayList<Status> parseStatuses(JSONArray a, int type)
			throws ApiException {
		if (a == null) {
			return null;
		}
		if (App.DEBUG) {
			log("parseStatuses jsonarray.size=" + a.length());
		}
		try {
			ArrayList<Status> statuses = new ArrayList<Status>();
			for (int i = 0; i < a.length(); i++) {
				JSONObject o = a.getJSONObject(i);
				Status s = Status.parse(o, type);
				statuses.add(s);
			}
			return statuses;
		} catch (JSONException e) {
			if (App.DEBUG)
				e.printStackTrace();
			throw new ApiException(ResponseCode.ERROR_JSON_EXCEPTION,
					e.getMessage(), e);
		}
	}

	public static Status parse(Cursor c) {
		if (c == null) {
			return null;
		}
		Status s = new Status();

		s.id = Parser.parseString(c, BasicColumns.ID);
		s.rawId=Parser.parseLong(c, BasicColumns.RAWID);
		s.ownerId = Parser.parseString(c, BasicColumns.OWNER_ID);
		s.createdAt = Parser.parseDate(c, BasicColumns.CREATED_AT);

		s.text = Parser.parseString(c, StatusInfo.TEXT);
		s.simpleText = Parser.parseString(c, StatusInfo.SIMPLE_TEXT);
		s.source = Parser.parseString(c, StatusInfo.SOURCE);

		s.inReplyToStatusId = Parser.parseString(c,
				StatusInfo.IN_REPLY_TO_STATUS_ID);
		s.inReplyToUserId = Parser.parseString(c,
				StatusInfo.IN_REPLY_TO_USER_ID);
		s.inReplyToScreenName = Parser.parseString(c,
				StatusInfo.IN_REPLY_TO_SCREEN_NAME);

		s.photoImageUrl = Parser.parseString(c, StatusInfo.PHOTO_IMAGE_URL);
		s.photoLargeUrl = Parser.parseString(c, StatusInfo.PHOTO_LARGE_URL);
		s.photoThumbUrl = Parser.parseString(c, StatusInfo.PHOTO_THUMB_URL);

		s.userId = Parser.parseString(c, StatusInfo.USER_ID);
		s.userScreenName = Parser.parseString(c, StatusInfo.USER_SCREEN_NAME);
		s.userProfileImageUrl = Parser.parseString(c,
				StatusInfo.USER_PROFILE_IMAGE_URL);

		s.truncated = Parser.parseBoolean(c, StatusInfo.TRUNCATED);
		s.favorited = Parser.parseBoolean(c, StatusInfo.FAVORITED);
		s.self = Parser.parseBoolean(c, StatusInfo.IS_SELF);

		s.isRead = Parser.parseBoolean(c, StatusInfo.IS_READ);
		s.isThread = Parser.parseBoolean(c, StatusInfo.IS_THREAD);
		s.hasPhoto = Parser.parseBoolean(c, StatusInfo.HAS_PHOTO);
		s.special = Parser.parseBoolean(c, StatusInfo.SPECIAL);

		s.type = Parser.parseInt(c, BasicColumns.TYPE);

		if (TextUtils.isEmpty(s.id)) {
			return null;
		}
		return s;

	}

	public static Status parse(NetResponse response, int type)
			throws ApiException {
		return parse(response.getJSONObject(), type);
	}

	public static Status parse(NetResponse response) throws ApiException {
		return parse(response.getJSONObject());
	}

	public static Status parse(String content) throws ApiException {
		return parse(content, Constants.TYPE_NONE);
	}

	public static Status parse(String content, int type) throws ApiException {
		try {
			JSONObject o = new JSONObject(content);
			return parse(o, type);
		} catch (JSONException e) {
			if (App.DEBUG)
				e.printStackTrace();
			throw new ApiException(ResponseCode.ERROR_JSON_EXCEPTION,
					e.getMessage(), e);
		}
	}

	public static Status parse(JSONObject o) throws ApiException {
		return parse(o, Constants.TYPE_NONE);
	}

	public static Status parse(JSONObject o, int type) throws ApiException {
		if (o == null) {
			return null;
		}
		try {
			Status s = new Status();

			s.id = o.getString(BasicColumns.ID);
			s.rawId=o.getLong(BasicColumns.RAWID);
			if(App.DEBUG){
				Log.d(TAG, "id="+s.id+" rawid="+s.rawId+" ownerId="+s.ownerId);
			}
			s.ownerId = App.getUserId();
			s.createdAt = Parser.date(o.getString(BasicColumns.CREATED_AT));

			s.text = o.getString(StatusInfo.TEXT);
			s.simpleText = StatusHelper.getSimpifiedText(s.text);
			s.source = Parser.parseSource(o.getString(StatusInfo.SOURCE));

			if (o.has(StatusInfo.IN_REPLY_TO_STATUS_ID)) {
				s.inReplyToStatusId = o
						.getString(StatusInfo.IN_REPLY_TO_STATUS_ID);
				s.inReplyToUserId = o.getString(StatusInfo.IN_REPLY_TO_USER_ID);
				s.inReplyToScreenName = o
						.getString(StatusInfo.IN_REPLY_TO_SCREEN_NAME);
				if (!TextUtils.isEmpty(s.inReplyToStatusId)) {
					s.isThread = true;
				}
			}

			s.favorited = o.getBoolean(StatusInfo.FAVORITED);
			s.truncated = o.getBoolean(StatusInfo.TRUNCATED);
			s.self = o.getBoolean(StatusInfo.IS_SELF);

			s.isRead = false;
			s.special = false;

			if (o.has("photo")) {
				JSONObject po = o.getJSONObject("photo");
				s.photo = Photo.parse(po);
				s.photoImageUrl = s.photo.imageUrl;
				s.photoLargeUrl = s.photo.largeUrl;
				s.photoThumbUrl = s.photo.thumbUrl;
				s.hasPhoto = true;
			}

			if (o.has("user")) {
				JSONObject uo = o.getJSONObject("user");
				s.userId = uo.getString(BasicColumns.ID);
				s.userScreenName = uo.getString(UserInfo.SCREEN_NAME);
				s.userProfileImageUrl = uo
						.getString(UserInfo.PROFILE_IMAGE_URL);
				s.user = User.parse(uo);
			}

			s.type = type;

			return s;
		} catch (JSONException e) {
			if (App.DEBUG)
				e.printStackTrace();
			throw new ApiException(ResponseCode.ERROR_JSON_EXCEPTION,
					e.getMessage(), e);
		}
	}

	@Override
	public ContentValues toContentValues() {
		ContentValues cv = new ContentValues();

		cv.put(BasicColumns.ID, this.id);
		cv.put(BasicColumns.RAWID, rawId);
		cv.put(BasicColumns.OWNER_ID, this.ownerId);
		cv.put(BasicColumns.CREATED_AT, this.createdAt.getTime());

		cv.put(StatusInfo.TEXT, this.text);
		cv.put(StatusInfo.SOURCE, this.source);
		cv.put(StatusInfo.SIMPLE_TEXT, this.simpleText);

		cv.put(StatusInfo.IN_REPLY_TO_STATUS_ID, this.inReplyToStatusId);
		cv.put(StatusInfo.IN_REPLY_TO_USER_ID, this.inReplyToUserId);
		cv.put(StatusInfo.IN_REPLY_TO_SCREEN_NAME, this.inReplyToScreenName);

		cv.put(StatusInfo.PHOTO_IMAGE_URL, this.photoImageUrl);
		cv.put(StatusInfo.PHOTO_THUMB_URL, this.photoThumbUrl);
		cv.put(StatusInfo.PHOTO_LARGE_URL, this.photoLargeUrl);

		cv.put(StatusInfo.USER_ID, this.userId);
		cv.put(StatusInfo.USER_SCREEN_NAME, this.userScreenName);
		cv.put(StatusInfo.USER_PROFILE_IMAGE_URL, this.userProfileImageUrl);

		cv.put(StatusInfo.TRUNCATED, this.truncated);
		cv.put(StatusInfo.FAVORITED, this.favorited);
		cv.put(StatusInfo.IS_SELF, this.self);

		cv.put(StatusInfo.IS_READ, this.isRead);
		cv.put(StatusInfo.IS_THREAD, this.isThread);
		cv.put(StatusInfo.HAS_PHOTO, this.hasPhoto);
		cv.put(StatusInfo.SPECIAL, this.special);

		cv.put(BasicColumns.TYPE, this.type);

		return cv;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Status) {
			Status s = (Status) o;
			if (id.equals(s.id)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public String toString() {
		// return toContentValues().toString();
		return "[Status] " + BasicColumns.ID + "=" + this.id + " "
				+ StatusInfo.TEXT + "=" + this.text + " "
				+ BasicColumns.CREATED_AT + "+" + this.createdAt + " "
				// +StatusInfo.SOURCE+"="+this.source+" "
				// +StatusInfo.TRUNCATED+"="+this.truncated+" "
				// +StatusInfo.IN_REPLY_TO_STATUS_ID+"="+this.inReplyToStatusId+" "
				// +StatusInfo.IN_REPLY_TO_USER_ID+"="+this.inReplyToUserId+" "
				// +StatusInfo.FAVORITED+"="+this.favorited+" "
				// +StatusInfo.IN_REPLY_TO_SCREEN_NAME+"="+this.inReplyToScreenName+" "
				// +StatusInfo.PHOTO_IMAGE_URL+"="+this.photoImageUrl+" "
				// +StatusInfo.PHOTO_LARGE_URL+"="+this.photoLargeUrl+" "
				// +StatusInfo.PHOTO_THUMB_URL+"="+this.photoThumbUrl+" "
				+ StatusInfo.USER_ID + "=" + this.userId + " ";
		// +StatusInfo.USER_SCREEN_NAME+"="+this.userScreenName+" "
		// +StatusInfo.READ+"="+this.read+" "
		// +StatusInfo.TYPE+"="+this.type+" ";
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	public Status readFromParcel(Parcel source){
		return new Status(source);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeLong(rawId);
		dest.writeString(ownerId);
		dest.writeLong(createdAt.getTime());
		dest.writeInt(type);		
		
		dest.writeString(text);
		dest.writeString(simpleText);
		dest.writeString(source);
		
		dest.writeString(inReplyToStatusId);
		dest.writeString(inReplyToUserId);
		dest.writeString(inReplyToScreenName);
		
		dest.writeString(photoImageUrl);
		dest.writeString(photoLargeUrl);
		dest.writeString(photoThumbUrl);
		
		dest.writeString(userId);
		dest.writeString(userScreenName);
		dest.writeString(userProfileImageUrl);
		
		dest.writeInt(truncated?1:0);
		dest.writeInt(favorited?1:0);
		dest.writeInt(self?1:0);
		
		dest.writeInt(isRead?1:0);
		dest.writeInt(isThread?1:0);
		dest.writeInt(hasPhoto?1:0);
		
		dest.writeInt(special?1:0);
		
	}

	public static final Parcelable.Creator<Status> CREATOR = new Parcelable.Creator<Status>() {

		@Override
		public Status createFromParcel(Parcel source) {
			return new Status(source);
		}

		@Override
		public Status[] newArray(int size) {
			return new Status[size];
		}
	};

}
