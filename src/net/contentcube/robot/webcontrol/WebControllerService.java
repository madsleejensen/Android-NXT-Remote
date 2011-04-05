package net.contentcube.robot.webcontrol;

import net.contentcube.robot.NXTApplication;
import net.contentcube.robot.nxt.NXTController;
import net.contentcube.robot.nxt.NXTMovementCommand;
import net.contentcube.robot.webcontrol.CommandsWebRequester.OnCommandReceivedListener;

import org.apache.http.client.methods.HttpGet;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class WebControllerService extends Service {
	
	private NXTController mNXTController;
	private CommandsWebRequester mCommandsRequester;
	private Thread mWorker;
	
	@Override
	public void onCreate() {
		
		super.onCreate();
		
		mNXTController = NXTController.getInstance();
		
		mCommandsRequester = new CommandsWebRequester(new HttpGet(NXTApplication.WEBSERVICE_COMMAND_URL), new Handler());
		mCommandsRequester.setOnCommandReceivedListener(new OnCommandReceivedListener() {
			
			@Override
			public void onCommandRecived(NXTMovementCommand command) {
				
				mNXTController.queue(command);
				
			}
			
		});
		
		mWorker = new Thread(mCommandsRequester);
		mWorker.start();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onDestroy() {
	
		super.onDestroy();
	
		mCommandsRequester.stop();
		mWorker.stop();
		
		Log.e("STOP", "STOP");
	}
}
