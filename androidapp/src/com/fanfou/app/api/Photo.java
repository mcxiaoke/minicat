package com.fanfou.app.api;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.fanfou.app.App;
import com.fanfou.app.db.Contents.StatusInfo;
import com.fanfou.app.http.ResponseCode;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author mcxiaoke
 * @version 1.0 2011.11.10
 * 
 */
public class Photo implements Storable<Photo> {
	public String id;
	public Date createdAt;
	public String thumbUrl;
	public String largeUrl;
	public String imageUrl;

	public Photo() {

	}

	public static Photo parse(JSONObject o) throws ApiException {
		if (o == null) {
			return null;
		}
		try {
			Photo p = new Photo();
			p.imageUrl = o.getString(StatusInfo.PHOTO_IMAGE_URL);
			p.largeUrl = o.getString(StatusInfo.PHOTO_LARGE_URL);
			p.thumbUrl = o.getString(StatusInfo.PHOTO_THUMB_URL);
			return p;
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
		cv.put("id", id);
		cv.put("createdAt", createdAt.getTime());
		cv.put("imageUrl", imageUrl);
		cv.put("thumbUrl", thumbUrl);
		cv.put("largeUrl", largeUrl);
		return cv;
	}

	@Override
	public void fromContentValues(ContentValues values) {
		ContentValues cv = values;
		id = cv.getAsString("id");
		createdAt = new Date(cv.getAsLong("createdAt"));
		imageUrl = cv.getAsString("imageUrl");
		thumbUrl = cv.getAsString("thumbUrl");
		largeUrl = cv.getAsString("largeUrl");
	}

	@Override
	public int compareTo(Photo another) {
		return 0;
	}

	@Override
	public int hashCode() {
		return largeUrl.hashCode();
	}

	@Override
	public String toString() {
		return largeUrl;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		ContentValues cv = toContentValues();
		dest.writeParcelable(cv, flags);
	}

	public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() {

		@Override
		public Photo createFromParcel(Parcel source) {
			return new Photo(source);
		}

		@Override
		public Photo[] newArray(int size) {
			return new Photo[size];
		}
	};

	public Photo(Parcel in) {
		ContentValues cv = in.readParcelable(null);
		fromContentValues(cv);
	}

}
