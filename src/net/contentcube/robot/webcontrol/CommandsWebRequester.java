package net.contentcube.robot.webcontrol;

import java.io.IOException;
import java.io.InputStream;

import net.contentcube.robot.helpers.StringHelper;
import net.contentcube.robot.nxt.NXTController;
import net.contentcube.robot.nxt.commands.MovementCommand;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

public class CommandsWebRequester implements Runnable
{
	public interface OnCommandReceivedListener
	{
		public void onCommandRecived(MovementCommand command);
	}
	
	private DefaultHttpClient mHttpClient;
	private HttpGet mHttpRequest;
	private boolean mIsActive = false;
	private Handler mHandler;
	private long mLastPollTime = 0;
	private int mMinimumTimeBetweenPolling = 500; // miliseconds.
	private OnCommandReceivedListener mCommandReceivedListener;
	
	public CommandsWebRequester(HttpGet request, Handler handler)
	{
		mHttpRequest = request;
		mHttpClient = new DefaultHttpClient();
		mHandler = handler;
	}
	
	public void setOnCommandReceivedListener(OnCommandReceivedListener listener)
	{
		mCommandReceivedListener = listener;
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
			HttpEntity entity = response.getEntity();
			InputStream inputStream = entity.getContent();
			
			int statusCode = response.getStatusLine().getStatusCode();
			
			if (statusCode == HttpStatus.SC_OK)
			{
				String responseJSON = StringHelper.createByInputStream(inputStream);
				
				Log.e("NXT", "WEB: " + responseJSON);
				
				MovementCommand command = MovementCommand.parseByJSONString(responseJSON);
				
				if (command != null && mCommandReceivedListener != null)
				{
					mCommandReceivedListener.onCommandRecived(command);
				}
			}
			
			inputStream.close();
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