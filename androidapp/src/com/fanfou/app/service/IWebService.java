package com.fanfou.app.service;

import com.fanfou.app.service.WebService.IWebServiceCallback;

public interface IWebService {
	public void friendshipsCreate(String id,final IWebServiceCallback callback);

	public void friendshipsDelete(String id,final IWebServiceCallback callback);

	public void favoritesCreate(String id,final IWebServiceCallback callback);

	public void favoritesDelete(String id,final IWebServiceCallback callback);
}
