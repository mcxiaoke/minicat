package com.fanfou.app.hd.module;

import java.util.ArrayList;
import java.util.List;

import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.adapter.PhotoAdapter;
import com.fanfou.app.api.Photo;
import com.fanfou.app.api.User;
import com.fanfou.app.config.Commons;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.GridView;
import android.widget.ListView;

/**
 * @author mcxiaoke
 * @version 1.0 2011.11.04
 * 
 */
public class PhotosAlbumFragment extends Fragment implements
		LoaderCallbacks<List<Photo>> {
	private static final String TAG = PhotosAlbumFragment.class.getSimpleName();

	private void log(String message) {
		Log.d(TAG, message);
	}

	private ViewGroup root;
	private GridView mGridView;
	private PhotoAdapter mAdapter;
	private List<Photo> photos;
	private String userId;
	private User user;

	public static PhotosAlbumFragment newInstance(User user) {
		if (App.DEBUG) {
			Log.d("PhotosAlbumFragment", "newInstance");
		}
		Bundle b = new Bundle();
		b.putString(Commons.EXTRA_USER_ID, user.id);
		b.putSerializable(Commons.EXTRA_USER, user);
		PhotosAlbumFragment fragment = new PhotosAlbumFragment();
		fragment.setArguments(b);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (App.DEBUG) {
			log("onCreate");
		}
		Bundle b = getArguments();
		if (b != null) {
			user = (User) b.getSerializable(Commons.EXTRA_USER);
			userId = b.getString(Commons.EXTRA_USER_ID);
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		root = (ViewGroup) inflater.inflate(R.layout.fragment_album, container,false);
		mGridView = (GridView) root.findViewById(R.id.grid_album);
		if (App.DEBUG) {
			log("onCreateView mGridView=" + mGridView);
		}
		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (App.DEBUG) {
			log("onActivityCreated");
		}
		photos = new ArrayList<Photo>();
		mAdapter = new PhotoAdapter(getActivity(), photos);
		mGridView.setAdapter(mAdapter);
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public Loader<List<Photo>> onCreateLoader(int id, Bundle args) {
		if (App.DEBUG) {
			log("onCreateLoader");
		}
		return new PhotosLoader(getActivity(), userId);
	}

	@Override
	public void onLoadFinished(Loader<List<Photo>> loader, List<Photo> data) {
		if (App.DEBUG) {
			log("onLoadFinished data.size=" + data.size());
		}
		photos = data;
		 mAdapter.setData(photos);
	}

	@Override
	public void onLoaderReset(Loader<List<Photo>> loader) {
		if (App.DEBUG) {
			log("onLoaderReset");
		}
		photos = null;
		mAdapter.setData(null);
	}

}
