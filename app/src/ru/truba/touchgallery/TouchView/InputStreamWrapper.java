package ru.truba.touchgallery.TouchView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class InputStreamWrapper extends BufferedInputStream
{
	protected long mContentLen, mBytesLoaded;
	protected InputStreamProgressListener mProgressListener;
	public InputStreamWrapper(InputStream in, int size, long contentLen)
	{
		super(in, size);
		mContentLen = contentLen;
		mBytesLoaded = 0;
	}

	@Override
	public synchronized int read(byte[] buffer, int offset, int byteCount)
			throws IOException
	{
		mBytesLoaded += byteCount;
		if (mProgressListener != null) 
		{
			mProgressListener.onProgress(mBytesLoaded * 1.0f / mContentLen, mBytesLoaded, mContentLen);
		}
		return super.read(buffer, offset, byteCount);
	}
	
	public void setProgressListener(InputStreamProgressListener listener)
	{
		mProgressListener = listener;
	}
	
	public static interface InputStreamProgressListener 
	{
		public void onProgress(float progressValue, long bytesLoaded, long bytesTotal);		
	}

	
}
