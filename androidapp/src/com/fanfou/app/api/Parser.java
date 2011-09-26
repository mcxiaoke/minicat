package com.fanfou.app.api;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.http.Response;
import com.fanfou.app.http.ResponseCode;
import com.fanfou.app.util.DateTimeHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.05.04
 * @version 1.1 2011.05.15
 * @version 1.2 2011.05.17
 * 
 */
public final class Parser implements ResponseCode {

	public static final String TAG = "Parser";

	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String SCREEN_NAME = "screen_name";
	public static final String LOCATION = "location";
	public static final String GENDER = "gender";
	public static final String BIRTHDAY = "birthday";
	public static final String DESCRIPTION = "description";
	public static final String PROFILE_IMAGE_URL = "profile_image_url";
	public static final String URL = "url";
	public static final String PROTECTED = "protected";
	public static final String FOLLOWERS_COUNT = "followers_count";
	public static final String FRIENDS_COUNT = "friends_count";
	public static final String FAVORITES_COUNT = "favourites_count";
	public static final String STATUSES_COUNT = "statuses_count";
	public static final String FOLLOWING = "following";
	public static final String NOTIFICATIONS = "notifications";
	public static final String CREATED_AT = "created_at";
	public static final String UTC_OFFSET = "utc_offset";
	public static final String TEXT = "text";
	public static final String SOURCE = "source";
	public static final String TRUNCATED = "truncated";
	public static final String IN_REPLY_TO_LASTMSG_ID = "in_reply_to_lastmsg_id";
	public static final String IN_REPLY_TO_USER_ID = "in_reply_to_user_id";
	public static final String IN_REPLY_TO_STATUS_ID = "in_reply_to_status_id";
	public static final String FAVORITED = "favorited";
	public static final String IN_REPLY_TO_SCREEN_NAME = "in_reply_to_screen_name";
	public static final String SENDER_ID = "sender_id";
	public static final String RECIPIENT_ID = "recipient_id";
	public static final String SENDER_SCREEN_NAME = "sender_screen_name";
	public static final String RECIPIENT_SCREEN_NAME = "recipient_screen_name";
	public static final String SENDER = "sender";
	public static final String RECIPIENT = "recipient";
	public static final String USER = "user";
	public static final String STATUS = "status";
	public static final String PHOTO = "photo";
	public static final String PHOTO_IMAGEURL = "imageurl";
	public static final String PHOTO_THUMBURL = "thumburl";
	public static final String PHOTO_LARGEURL = "largeurl";
	public static final String QUERY = "query";
	public static final String TRENDS = "trends";
	public static final String TREND = "trend";
	public static final String AS_OF = "as_of";
	public static final String REQUEST = "request";
	public static final String ERROR = "error";

	public static List<String> ids(Response r) throws ApiException {

		return ids(r.getContent());

	}

	public static List<Search> trends(Response r) throws ApiException {

		return trends(r.getContent());

	}

	public static Search savedSearch(Response r) throws ApiException {

		return savedSearch(r.getContent());

	}

	public static List<Search> savedSearches(Response r) throws ApiException {

		return savedSearches(r.getContent());

	}

	public static Search trend(Response r) throws ApiException {

		return trend(r.getContent());

	}

	public static List<String> ids(String content) throws ApiException {
		try {
			JSONArray a = new JSONArray(content);
			return ids(a);
		} catch (JSONException e) {
			if (App.DEBUG)
				e.printStackTrace();
			throw new ApiException(ERROR_PARSE_FAILED, e.getMessage(),
					e.getCause());
		}
	}

	public static List<String> ids(JSONArray a) throws ApiException {
		try {
			List<String> ids = new ArrayList<String>();
			for (int i = 0; i < a.length(); i++) {
				ids.add(a.getString(i));
			}
			return ids;
		} catch (JSONException e) {
			if (App.DEBUG)
				e.printStackTrace();
			throw new ApiException(ERROR_PARSE_FAILED, e.getMessage(),
					e.getCause());
		}
	}

	public static Search trend(String content) throws ApiException {
		try {
			JSONObject o = new JSONObject(content);
			return trend(o);
		} catch (JSONException e) {
			if (App.DEBUG)
				e.printStackTrace();
			throw new ApiException(ERROR_PARSE_FAILED, e.getMessage(),
					e.getCause());
		}
	}

	public static Search trend(JSONObject o) throws ApiException {
		try {
			Search t = new Search();
			t.name = o.getString(NAME);
			t.query = o.getString(QUERY);
			return t;
		} catch (JSONException e) {
			if (App.DEBUG)
				e.printStackTrace();
			throw new ApiException(ERROR_PARSE_FAILED, e.getMessage(),
					e.getCause());
		}
	}

	public static List<Search> trends(String content) throws ApiException {
		try {
			JSONObject o = new JSONObject(content);
			return trends(o);
		} catch (JSONException e) {
			if (App.DEBUG)
				e.printStackTrace();
			throw new ApiException(ERROR_PARSE_FAILED, e.getMessage(),
					e.getCause());
		}
	}

	public static List<Search> trends(JSONObject o) throws ApiException {
		List<Search> ts = new ArrayList<Search>();
		try {
			JSONArray a = o.getJSONArray(TRENDS);
			for (int i = 0; i < a.length(); i++) {
				Search t = trend(a.getJSONObject(i));
				ts.add(t);
			}
		} catch (JSONException e) {
			if (App.DEBUG)
				e.printStackTrace();
			throw new ApiException(ERROR_PARSE_FAILED, e.getMessage(),
					e.getCause());
		}

		return ts;
	}

	public static Search savedSearch(String content) throws ApiException {
		try {
			JSONObject o = new JSONObject(content);
			return savedSearch(o);
		} catch (JSONException e) {
			if (App.DEBUG)
				e.printStackTrace();
			throw new ApiException(ERROR_PARSE_FAILED, e.getMessage(),
					e.getCause());
		}
	}

	public static Search savedSearch(JSONObject o) throws ApiException {
		try {
			Search s = new Search();
			s.name = o.getString(NAME);
			s.query = o.getString(QUERY);
			return s;
		} catch (JSONException e) {
			if (App.DEBUG)
				e.printStackTrace();
			throw new ApiException(ERROR_PARSE_FAILED, e.getMessage(),
					e.getCause());
		}
	}

	public static List<Search> savedSearches(String content)
			throws ApiException {
		try {
			JSONArray a = new JSONArray(content);
			return savedSearches(a);
		} catch (JSONException e) {
			if (App.DEBUG)
				e.printStackTrace();
			throw new ApiException(ERROR_PARSE_FAILED, e.getMessage(),
					e.getCause());
		}
	}

	public static List<Search> savedSearches(JSONArray a) throws ApiException {
		try {
			List<Search> ss = new ArrayList<Search>();
			for (int i = 0; i < a.length(); i++) {
				JSONObject o = a.getJSONObject(i);
				Search s = savedSearch(o);
				ss.add(s);
			}
			return ss;
		} catch (JSONException e) {
			if (App.DEBUG)
				e.printStackTrace();
			throw new ApiException(ERROR_PARSE_FAILED, e.getMessage(),
					e.getCause());
		}
	}

	public static String error(HttpResponse response) throws ApiException {
		try {
			String content = EntityUtils.toString(response.getEntity());
			if(App.DEBUG)
			Log.d("Parser", "error() content="+content);
			if(content==null){
				return null;
			}
			return error(content);
		} catch (IOException e) {
			if(App.DEBUG)
			e.printStackTrace();
			return null;
		}
	}

	public static String error(String error) {
		Log.e(TAG, "Parser.error() error=" + error);
		String result = error;
		try {
			JSONObject o = new JSONObject(error);
			if (o.has("error")) {
				result = o.getString("error");
			}
		} catch (JSONException e) {
			if (App.DEBUG)
				e.printStackTrace();
			result = parseXMLError(error);
		}
		return result;
	}

	public static String parseXMLError(String error) {
		// DocumentBuilder
		// builder=DocumentBuilderFactory.newInstance().newDocumentBuilder();
		// Document doc=builder.parse(new InputSource(new StringReader(error)));
		// Element root=doc.getDocumentElement();
		String result = error;
		XmlPullParser pull;
		String tag = null;
		try {
			pull = XmlPullParserFactory.newInstance().newPullParser();
			pull.setInput(new StringReader(error));
			boolean found = false;
			while (!found) {
				int eventType = pull.getEventType();
				switch (eventType) {
				case XmlPullParser.START_TAG:
					tag = pull.getName();
					if (tag.equalsIgnoreCase("error")) {
						result = pull.nextText();
					}
					break;
				case XmlPullParser.END_TAG:
					if (tag.equalsIgnoreCase("error")) {
						found = true;
					}
					break;
				default:
					break;
				}
				pull.next();
			}
		} catch (XmlPullParserException e) {
			if (App.DEBUG)
				e.printStackTrace();
		} catch (IOException e) {
			if (App.DEBUG)
				e.printStackTrace();
		}

		return result;
	}

	static final Pattern PATTERN_SOURCE = Pattern
			.compile("<a href.+blank\">(.+)</a>");

	public static String parseSource(String input) {
		String source = input;
		Matcher m = PATTERN_SOURCE.matcher(input);
		if (m.find()) {
			source = m.group(1);
		}
		// Log.e("SourceParse", "source="+source);
		return source;
	}

	/**
	 * @param s
	 *            代表饭否日期和时间的字符串
	 * @return 字符串解析为对应的Date对象
	 */
	public static Date date(String s) {
		return DateTimeHelper.fanfouStringToDate(s);
	}

	/**
	 * 将Date对象解析为饭否格式的字符串
	 * 
	 * @param date
	 *            Date对象
	 * @return 饭否格式日期字符串
	 */
	public static String formatDate(Date date) {
		return DateTimeHelper.formatDate(date);
	}

	public static int parseInt(Cursor c, String columnName) {
		return c.getInt(c.getColumnIndex(columnName));
	}

	public static String parseString(Cursor c, String columnName) {
		try {
			return c.getString(c.getColumnIndexOrThrow(columnName));
		} catch (IllegalArgumentException e) {
			if (App.DEBUG) {
				e.printStackTrace();
			}
		}
		return "";
	}

	public static boolean parseBoolean(Cursor c, String columnName) {
		return c.getInt(c.getColumnIndex(columnName)) == 1;
	}

	public static Date parseDate(Cursor c, String columnName) {
		return new Date(c.getLong(c.getColumnIndex(columnName)));
	}

	/**
	 * 批量生成ContentValues List
	 * 
	 * @param <T>
	 * @param t
	 * @return
	 */
	public static <T> List<ContentValues> toContentValuesList(
			List<? extends Storable<T>> t) {
		if (t == null || t.size() == 0) {
			return null;
		}
		List<ContentValues> values = new ArrayList<ContentValues>();
		for (Storable<?> s : t) {
			values.add(s.toContentValues());
		}
		return values;

	}

	/**
	 * 批量生成ContentValues Array
	 * 
	 * @param <T>
	 * @param t
	 * @return
	 */
	public static <T> ContentValues[] toContentValuesArray(
			List<? extends Storable<T>> t) {
		if (t == null || t.size() == 0) {
			return null;
		}
		ContentValues[] values = new ContentValues[t.size()];
		for (int i = 0; i < t.size(); i++) {
			values[i] = t.get(i).toContentValues();
		}
		return values;

	}

}
