package com.fanfou.app.test;

import java.util.List;

import com.fanfou.app.App;
import com.fanfou.app.api.Api;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.api.ApiImpl2;
import com.fanfou.app.api.Status;
import com.fanfou.app.util.Utils;

import android.test.AndroidTestCase;
import android.util.Log;

public class NetworkTest extends AndroidTestCase {
	public static final String TAG=NetworkTest.class.getSimpleName();

	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}
	
	public void testPublicTimeline() throws ApiException{
	}

}
