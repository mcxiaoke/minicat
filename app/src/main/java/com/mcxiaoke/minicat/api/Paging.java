package com.mcxiaoke.minicat.api;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author mcxiaoke
 * @version 2.0 2012.02.24
 */
public class Paging implements Parcelable {

    public static final Parcelable.Creator<Paging> CREATOR = new Parcelable.Creator<Paging>() {

        @Override
        public Paging createFromParcel(Parcel source) {
            return new Paging(source);
        }

        @Override
        public Paging[] newArray(int size) {
            return new Paging[size];
        }
    };
    public int page;
    public int count;
    public String sinceId;
    public String maxId;
    public boolean trim;

    public Paging() {

    }

    public Paging(Parcel in) {
        page = in.readInt();
        count = in.readInt();
        sinceId = in.readString();
        maxId = in.readString();
        trim = in.readInt() == 0 ? false : true;
    }

    public Paging(int page, int count) {
        this.page = page;
        this.count = count;
    }

    public Paging(String sinceId, String maxId) {
        this.sinceId = sinceId;
        this.maxId = maxId;
    }

    public Paging(int page, int count, String sinceId, String maxId) {
        this.page = page;
        this.count = count;
        this.sinceId = sinceId;
        this.maxId = maxId;
    }

    public Paging(Builder builder) {
        this.page = builder.page;
        this.count = builder.count;
        this.sinceId = builder.sinceId;
        this.maxId = builder.maxId;
        this.trim = builder.trim;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(page);
        dest.writeInt(count);
        dest.writeString(sinceId);
        dest.writeString(maxId);
        dest.writeInt(trim ? 1 : 0);
    }

    @Override
    public String toString() {
        return "Paging [page=" + page + ", count=" + count + ", sinceId="
                + sinceId + ", maxId=" + maxId + ", trim=" + trim + "]";
    }

    public static class Builder {
        private int page;
        private int count;
        private String sinceId;
        private String maxId;
        private boolean trim;

        public Builder() {
        }

        public Builder page(int page) {
            assert (page > 0);
            this.page = page;
            return this;
        }

        public Builder count(int count) {
            assert (count > 0);
            this.count = count;
            return this;
        }

        public Builder sinceId(String sinceId) {
            this.sinceId = sinceId;
            return this;
        }

        public Builder maxId(String maxId) {
            this.maxId = maxId;
            return this;
        }

        public Builder trim(boolean trim) {
            this.trim = trim;
            return this;
        }

        public Paging build() {
            return new Paging(this);
        }

    }

}
