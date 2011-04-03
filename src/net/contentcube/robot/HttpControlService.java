package net.contentcube.robot;

import org.apache.http.client.methods.HttpGet;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

public class HttpControlService extends Service {
	
	private CommandsHttpRequester mCommandsRequester;
	private Thread mWorker;
	
	@Override
	public void onCreate() {
		
		super.onCreate();
		
		mCommandsRequester = new CommandsHttpRequester(new HttpGet("http://dev.contentcube.dk/nxt-web-control/index.php"), new Handler());
		mWorker = new Thread(mCommandsRequester);
		mWorker.start();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
