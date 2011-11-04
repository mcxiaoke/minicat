package com.fanfou.app.hd.module;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.api.Api;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.api.Photo;
import com.fanfou.app.api.Status;

/**
 * @author mcxiaoke
 * @version 1.0 2011.11.04
 *
 */
public class PhotosLoader extends BaseDataLoader<List<Photo>> {
	
	private static final String TAG=PhotosLoader.class.getSimpleName();
	private void log(String message){
		Log.d(TAG, message);
	}
	
	private String userId;

	public PhotosLoader(Context context, String userId) {
		super(context);
		this.userId = userId;
	}

	@Override
	public List<Photo> loadInBackground() {
		Api api = App.me.api;
		ArrayList<Photo> photos=null;
		try {
			List<Status> ss = api
					.photosTimeline(0, 0, userId, null, null, true);
			if(ss!=null){
				if(App.DEBUG){
					log("loadInBackground() result.size="+ss.size());
				}
				photos=new ArrayList<Photo>();
				for (Status status : ss) {
					photos.add(status.photo);
					onContentChanged();
					
				}
			}
		} catch (ApiException e) {
			if (App.DEBUG) {
				e.printStackTrace();
			}
		}
		return photos;
	}

}
