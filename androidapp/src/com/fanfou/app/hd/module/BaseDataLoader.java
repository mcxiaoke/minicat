package com.fanfou.app.hd.module;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public abstract class BaseDataLoader<D> extends AsyncTaskLoader<D> {

	private D data;

	public BaseDataLoader(Context context) {
		super(context);
	}

	@Override
	public void deliverResult(D data) {
		if (isReset()) {
			return;
		}
		this.data = data;
		super.deliverResult(data);
	}

	@Override
	protected void onStartLoading() {
		if (data != null) {
			deliverResult(data);
		}
		if (takeContentChanged() || data == null) {
			forceLoad();
		}
	}

	@Override
	protected void onReset() {
		super.onReset();
		onStopLoading();
		data = null;
	}

	@Override
	protected void onStopLoading() {
		cancelLoad();
	}

}
