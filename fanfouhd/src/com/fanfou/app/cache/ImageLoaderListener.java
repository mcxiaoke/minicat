package com.fanfou.app.cache;

public interface ImageLoaderListener{

		void onFinish(String key);

		void onError(String message);
}
