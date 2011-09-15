/**
 * 
 */
package com.fanfou.app.update;

import org.json.JSONException;
import org.json.JSONObject;

import com.fanfou.app.App;

/**
 * @author mcxiaoke
 * @version 1.0 20110904
 * 
 */
public final class VersionInfo {
	public static final String TYPE_BUGFIX = "bugfix";
	public static final String TYPE_MINOR = "minor";
	public static final String TYPE_MAJOR = "major";

	public int versionCode;// 版本号
	public String versionName;// 版本显示
	public String releaseDate;// 发布日期
	public String changelog;// 升级日志
	public String downloadUrl;// 下载地址
	public String versionType;// 升级类型：BUG修复，功能改进，重大更新
	public String packageName;// 安装包文件名
	public boolean forceUpdate;// 是否强制升级

	public static VersionInfo parse(String response) {
		try {
			JSONObject o = new JSONObject(response);
			VersionInfo info=new VersionInfo();
			info.versionCode=o.getInt("versionCode");
			info.versionName=o.getString("versionName");
			info.releaseDate=o.getString("releaseDate");
			info.changelog=o.getString("changelog");
			info.downloadUrl=o.getString("downloadUrl");
			info.versionType=o.getString("versionType");
			info.packageName=o.getString("packageName");
			info.forceUpdate=o.getBoolean("forceUpdate");
			return info;
		} catch (JSONException e) {
			if (App.DEBUG) {
				e.printStackTrace();
			}
			return null;
		}
	}

	@Override
	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append("[VersionInfo] versionCode="+versionCode);
		sb.append("[VersionInfo] versionName="+versionName);
		sb.append("[VersionInfo] releaseDate="+releaseDate);
		sb.append("[VersionInfo] downloadUrl="+downloadUrl);
		sb.append("[VersionInfo] versionType="+versionType);
		sb.append("[VersionInfo] packageName="+packageName);
		sb.append("[VersionInfo] forceUpdate="+forceUpdate);
		sb.append("[VersionInfo] changelog=("+changelog).append(")");
		return sb.toString();
	}

}
