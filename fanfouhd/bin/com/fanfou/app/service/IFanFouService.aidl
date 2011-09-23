package com.fanfou.app.service;

import com.fanfou.app.service.IFanFouServiceCallback;

interface IFanFouService{

	boolean isRunning();

	int getPid();

	void registerCallback(IFanFouServiceCallback callback);

	void unregisterCallback(IFanFouServiceCallback callback);

}