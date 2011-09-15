package com.fanfou.app.service;

oneway interface IFanFouServiceCallback{

	void onHomeReceived(int count);

	void onMentionsReceived(int count);

	void onMessageReceived(int count);
	
	void onActionReceived(int type, int count);


}