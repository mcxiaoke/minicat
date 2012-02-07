package com.fanfou.app.hd.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.api.Status;
import com.fanfou.app.service.Constants;
import com.fanfou.app.ui.UIManager;
import com.fanfou.app.util.Utils;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.06
 * 
 */
public abstract class PullToRefreshListFragment extends AbstractFragment
		implements OnRefreshListener, OnItemClickListener,
		OnItemLongClickListener {

	protected static final String TAG = PullToRefreshListFragment.class
			.getSimpleName();

	protected PullToRefreshListView mPullToRefreshView;
	protected ListView mListView;

	private Parcelable mParcelable;

	private CursorAdapter mAdapter;
	private Cursor mCursor;

	public PullToRefreshListFragment() {
		super();
		if (App.DEBUG) {
			Log.d(TAG, "PullToRefreshListFragment() id=" + this);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (App.DEBUG) {
			Log.d(TAG, "onAttach()");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (App.DEBUG) {
			Log.d(TAG, "onCreate()");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (App.DEBUG) {
			Log.d(TAG, "onCreateView()");
		}
		View v = inflater.inflate(R.layout.list_only, container, false);
		mPullToRefreshView = (PullToRefreshListView) v;
		mPullToRefreshView.setOnRefreshListener(this);
		mListView = (ListView) mPullToRefreshView.getRefreshableView();
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (App.DEBUG) {
			Log.d(TAG, "onActivityCreated()");
		}

		if (savedInstanceState != null) {
			mParcelable = savedInstanceState.getParcelable("state");
		}

		mCursor = createCursor();
		mAdapter = createAdapter();
		mListView.setAdapter(mAdapter);
	}

	@Override
	public void onRefresh() {
		if (App.DEBUG) {
			Log.d(TAG, "onRefresh()");
		}
		doFetch(!mPullToRefreshView.hasPullFromTop());
	}

	protected void doRefresh() {
		if (App.DEBUG) {
			Log.d(TAG, "doRefresh()");
		}
		doFetch(false);
	}

	protected void doGetMore() {
		if (App.DEBUG) {
			Log.d(TAG, "doGetMore()");
		}
		doFetch(true);
	}

	protected abstract CursorAdapter createAdapter();

	protected abstract Cursor createCursor();

	public Cursor getCursor() {
		return mCursor;
	}

	public CursorAdapter getAdapter() {
		return mAdapter;
	}

	protected abstract void doFetch(boolean doGetMore);

	protected abstract void showToast(int count);

	protected abstract int getType();

	public void goTop() {
		mListView.setSelection(0);
	}

	public void updateUI() {
		if (mCursor != null) {
			mCursor.requery();
		}
	}

	public void startRefresh() {
		if (App.DEBUG) {
			Log.d(TAG, "startRefresh()");
		}
		doRefresh();
		mPullToRefreshView.setRefreshing();
	}

	private void onSuccess(Bundle data) {
		if (App.DEBUG) {
			Log.d(TAG, "onSuccess(data)");
		}
		int count = data.getInt(Constants.EXTRA_COUNT);
		onSuccess(count);
	}

	private void onSuccess(int count) {
		if (App.DEBUG) {
			Log.d(TAG, "onSuccess(count)");
		}
		if (count > 0 && mCursor != null) {
			mCursor.requery();
			showToast(count);
			// if (soundEffect) {
			// SoundManager.playSound(1, 0);
			// }
		}
	}

	private void onError(Bundle data) {
		if (App.DEBUG) {
			Log.d(TAG, "onSuccess()");
		}
		String errorMessage = data.getString(Constants.EXTRA_ERROR);
		int errorCode = data.getInt(Constants.EXTRA_CODE);
		Utils.notify(getActivity(), errorMessage);
		Utils.checkAuthorization(getActivity(), errorCode);
	}

	private void onRefreshComplete() {
		if (mPullToRefreshView != null) {
			mPullToRefreshView.onRefreshComplete();
		}
	}

	protected static void showPopup(Activity context, final View view,
			final Cursor c) {
		if (c != null) {
			final Status s = Status.parse(c);
			if (s == null) {
				return;
			}
			UIManager.showPopup(context, c, view, s);
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (App.DEBUG) {
			Log.d(TAG, "onViewCreated()");
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		if (App.DEBUG) {
			Log.d(TAG, "onStart()");
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(mListView!=null){
			mParcelable = mListView.onSaveInstanceState();
			outState.putParcelable("state", mParcelable);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mParcelable != null && mListView!=null) {
			mListView.onRestoreInstanceState(mParcelable);
			mParcelable = null;
		}
		if (App.DEBUG) {
			Log.d(TAG, "onResume()");
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (App.DEBUG) {
			Log.d(TAG, "onPause()");
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		if (App.DEBUG) {
			Log.d(TAG, "onStop()");
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (App.DEBUG) {
			Log.d(TAG, "onDestroyView()");
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (App.DEBUG) {
			Log.d(TAG, "onDestroy()");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		if (App.DEBUG) {
			Log.d(TAG, "onDetach()");
		}
	}

	/**
	 * FetchService返回数据处理 根据resultData里面的type信息分别处理
	 */
	protected static class ResultHandler extends Handler {
		private PullToRefreshListFragment mFragment;

		public ResultHandler(PullToRefreshListFragment fragment) {
			this.mFragment = fragment;
		}

		@Override
		public void handleMessage(Message msg) {
			mFragment.onRefreshComplete();
			Bundle data = msg.getData();
			switch (msg.what) {
			case Constants.RESULT_SUCCESS:
				mFragment.onSuccess(data);
				break;
			case Constants.RESULT_ERROR:
				mFragment.onError(data);
				break;
			default:
				break;
			}
		}

	}

}
