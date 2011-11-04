package com.fanfou.app.api;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.fanfou.app.App;
import com.fanfou.app.db.Contents.StatusInfo;
import com.fanfou.app.http.ResponseCode;

import android.content.ContentValues;

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
			throw new ApiException(ResponseCode.ERROR_PARSE_FAILED,
					e.getMessage(), e);
		}
	}

	@Override
	public int compareTo(Photo another) {
		return 0;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public String toString() {
		return super.toString();
	}

	@Override
	public ContentValues toContentValues() {
		return null;
	}

}
