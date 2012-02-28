package com.fanfou.app.hd.http;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

import android.os.SystemClock;

import com.fanfou.app.hd.App;

public class RequestRetryHandler implements HttpRequestRetryHandler {
	private static final int RETRY_SLEEP_TIME_MILLIS = 1500;
	private static HashSet<Class<?>> exceptionWhitelist = new HashSet<Class<?>>();
	private static HashSet<Class<?>> exceptionBlacklist = new HashSet<Class<?>>();

	static {
		// Retry if the server dropped connection on us
		exceptionWhitelist.add(NoHttpResponseException.class);
		// retry-this, since it may happens as part of a Wi-Fi to 3G failover
		exceptionWhitelist.add(UnknownHostException.class);
		// retry-this, since it may happens as part of a Wi-Fi to 3G failover
		exceptionWhitelist.add(SocketException.class);

		// never retry timeouts
		exceptionBlacklist.add(InterruptedIOException.class);
		// never retry SSL handshake failures
		exceptionBlacklist.add(SSLHandshakeException.class);
	}

	private final int maxRetries;

	public RequestRetryHandler(int maxRetries) {
		this.maxRetries = maxRetries;
	}

	@Override
	public boolean retryRequest(IOException exception, int executionCount,
			HttpContext context) {
		boolean retry;

		Boolean b = (Boolean) context
				.getAttribute(ExecutionContext.HTTP_REQ_SENT);
		boolean sent = (b != null && b.booleanValue());

		if (executionCount > maxRetries) {
			// Do not retry if over max retry count
			retry = false;
		} else if (exceptionBlacklist.contains(exception.getClass())) {
			// immediately cancel retry if the error is blacklisted
			retry = false;
		} else if (exceptionWhitelist.contains(exception.getClass())) {
			// immediately retry if error is whitelisted
			retry = true;
		} else if (!sent) {
			// for most other errors, retry only if request hasn't been fully
			// sent yet
			retry = true;
		} else {
			// resend all idempotent requests
			HttpUriRequest currentReq = (HttpUriRequest) context
					.getAttribute(ExecutionContext.HTTP_REQUEST);
			String requestType = currentReq.getMethod();
			if (!requestType.equals("POST")) {
				retry = true;
			} else {
				retry = false;
			}
		}

		if (retry) {
			SystemClock.sleep(RETRY_SLEEP_TIME_MILLIS);
		} else {
			if (App.DEBUG)
				exception.printStackTrace();
		}

		return retry;
	}
}