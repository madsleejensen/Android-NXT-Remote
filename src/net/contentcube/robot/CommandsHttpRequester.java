package net.contentcube.robot;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

public class CommandsHttpRequester implements Runnable
{
	public interface OnCommandReceivedListener
	{
		public void onCommandRecived();
	}
	
	private DefaultHttpClient mHttpClient;
	private HttpGet mHttpRequest;
	private boolean mIsActive = false;
	private Handler mHandler;
	private long mLastPollTime = 0;
	private int mMinimumTimeBetweenPolling = 1500; // miliseconds.
	
	public CommandsHttpRequester(HttpGet request, Handler handler)
	{
		mHttpRequest = request;
		mHttpClient = new DefaultHttpClient();
		mHandler = handler;
	}
	
	public void stop()
	{
		
		mIsActive = false;
	}
	
	@Override
	public void run()
	{
		mIsActive = true;
		poll();
	}
	
	private void poll()
	{
		if (!mIsActive) return;
		
		mLastPollTime = SystemClock.elapsedRealtime();
		
		HttpResponse response = null;
		
		try
		{
			response = mHttpClient.execute(mHttpRequest);
			handleResponse(response);
		}
		catch (ClientProtocolException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		long delta = SystemClock.elapsedRealtime() - mLastPollTime;
		long delay = Math.max(0, mMinimumTimeBetweenPolling - delta);
		
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				poll(); // recursive.
			}
			
		}, delay);
	}
	
	private void handleResponse(HttpResponse response)
	{
		if (response == null) return;
		
		try
		{
			response.getEntity().getContent().close();
			int statusCode = response.getStatusLine().getStatusCode();

			Log.e("response", response.getStatusLine().toString());
		}
		catch (IllegalStateException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}