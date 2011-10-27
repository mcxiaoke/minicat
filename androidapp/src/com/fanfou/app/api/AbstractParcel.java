package com.fanfou.app.api;


import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public abstract class AbstractParcel<T> implements Parcelable, Comparable<T> {

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

	}
	
    /** Identifies a null parcelled Uri. */
    private static final int NULL_TYPE_ID = 0;

    /**
     * Reads Uris from Parcels.
     */
    public static final Parcelable.Creator<Uri> CREATOR
            = new Parcelable.Creator<Uri>() {
        public Uri createFromParcel(Parcel in) {
            throw new AssertionError("Unknown URI type: ");
        }

        public Uri[] newArray(int size) {
            return new Uri[size];
        }
    };

}
