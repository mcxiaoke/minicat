package com.mcxiaoke.minicat.service;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author mcxiaoke
 * @version 2.0 2011.10.31
 */
public final class VersionInfo implements Parcelable {

    public static final String TYPE_BUGFIX = "bugfix";
    public static final String TYPE_MINOR = "minor";
    public static final String TYPE_MAJOR = "major";
    public static final Parcelable.Creator<VersionInfo> CREATOR = new Parcelable.Creator<VersionInfo>() {

        @Override
        public VersionInfo createFromParcel(Parcel source) {
            Bundle bundle = source.readBundle();
            return parseBundle(bundle);
        }

        @Override
        public VersionInfo[] newArray(int size) {
            return new VersionInfo[size];
        }

    };
    public int versionCode;// 版本号
    public String versionName;// 版本显示
    public String releaseDate;// 发布日期
    public String changelog;// 升级日志
    public String downloadUrl;// 下载地址
    public String versionType;// 升级类型：BUG修复，功能改进，重大更新
    public String packageName;// 安装包文件名
    public boolean forceUpdate;// 是否强制升级

    public VersionInfo() {
    }

    public VersionInfo(Parcel in) {
        this();
        Bundle bundle = in.readBundle();
        readFromBundle(bundle);
    }

    public static VersionInfo parse(String response) {
        try {
            JSONObject o = new JSONObject(response);
            VersionInfo info = new VersionInfo();
            info.versionCode = o.getInt("versionCode");
            info.versionName = o.getString("versionName");
            info.releaseDate = o.getString("releaseDate");
            info.changelog = o.getString("changelog");
            info.downloadUrl = o.getString("downloadUrl");
            info.versionType = o.getString("versionType");
            info.packageName = o.getString("packageName");
            info.forceUpdate = o.getBoolean("forceUpdate");
            return info;
        } catch (JSONException e) {
            return null;
        }
    }

    public static VersionInfo parseBundle(Bundle bundle) {
        VersionInfo info = new VersionInfo();
        info.versionCode = bundle.getInt("versionCode");
        info.versionName = bundle.getString("versionName");
        info.releaseDate = bundle.getString("releaseDate");
        info.changelog = bundle.getString("changelog");
        info.downloadUrl = bundle.getString("downloadUrl");
        info.versionType = bundle.getString("versionType");
        info.packageName = bundle.getString("packageName");
        info.forceUpdate = bundle.getBoolean("forceUpdate");
        if (info.versionCode > 0) {
            return info;
        } else {
            return null;
        }
    }

    public void readFromBundle(Bundle bundle) {
        versionCode = bundle.getInt("versionCode");
        versionName = bundle.getString("versionName");
        releaseDate = bundle.getString("releaseDate");
        changelog = bundle.getString("changelog");
        downloadUrl = bundle.getString("downloadUrl");
        versionType = bundle.getString("versionType");
        packageName = bundle.getString("packageName");
        forceUpdate = bundle.getBoolean("forceUpdate");
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[VersionInfo] versionCode=" + versionCode);
        sb.append("[VersionInfo] versionName=" + versionName);
        sb.append("[VersionInfo] releaseDate=" + releaseDate);
        sb.append("[VersionInfo] changelog=(" + changelog).append(")");
        sb.append("[VersionInfo] downloadUrl=" + downloadUrl);
        sb.append("[VersionInfo] versionType=" + versionType);
        sb.append("[VersionInfo] packageName=" + packageName);
        sb.append("[VersionInfo] forceUpdate=" + forceUpdate);
        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putInt("versionCode", versionCode);
        bundle.putString("versionName", versionName);
        bundle.putString("releaseDate", releaseDate);
        bundle.putString("changelog", changelog);
        bundle.putString("downloadUrl", downloadUrl);
        bundle.putString("versionType", versionType);
        bundle.putString("packageName", packageName);
        bundle.putBoolean("forceUpdate", forceUpdate);
        dest.writeBundle(bundle);
    }

}
