package net.contentcube.robot.nxt;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

public class NXTMovementCommand implements NXTCommand
{
	public enum Command
	{
		FORWARD,
		BACKWARD,
		BRAKE,
		TURN_RIGHT,
		TURN_LEFT
	}
	
	public interface OnCompleteListener
	{
		public void onComplete();
	}
	
	private Timer mTimer;
	private OutputStream mStream;
	private OnCompleteListener mCompleteListener;
	private NXTMovementCommand.Command mCommand;
	private int mDuration = -1; // -1 == infinity.
	
	public NXTMovementCommand(NXTMovementCommand.Command command)
	{
		mCommand = command;
	}
	
	public void setOnCompleteListener(OnCompleteListener listener)
	{
		mCompleteListener = listener;
	}
	
	public void setDuration(int duration)
	{
		mDuration = duration;
	}
	
	@Override
	public void run(OutputStream stream)
	{
		mStream = stream;
		byte[][] buffers = NXTCommandFactory.build(mCommand);
		
		writeBuffers(buffers);
		
		if (mDuration != -1)
		{
			startTimer();
		}
		else
		{
			if (mCompleteListener != null)
			{
				mCompleteListener.onComplete();
			}
		}
	}
	
	private void writeBuffers(byte[][] buffers)
	{
		try
		{
			for (int cursor = 0; cursor < buffers.length; cursor++)
			{
				mStream.write(buffers[cursor]);
			}
			
			mStream.flush();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void startTimer()
	{
		mTimer = new Timer("completion");
		mTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				
				writeBuffers(NXTCommandFactory.build(NXTMovementCommand.Command.BRAKE));
				
				if (mCompleteListener != null)
				{
					mCompleteListener.onComplete();
				}
			}
			
		}, mDuration);
	
	}
	
	public static NXTMovementCommand parseByJSONString(String jsonString)
	{
		NXTMovementCommand command = null;
		String commandName;
		
		try
		{
			JSONObject data = new JSONObject(jsonString);
			
			commandName = data.getString("command");
			
			if (commandName.equals("forward"))
			{
				command = new NXTMovementCommand(NXTMovementCommand.Command.FORWARD);
				command.setDuration(1000);
			}
			else if (commandName.equals("back"))
			{
				command = new NXTMovementCommand(NXTMovementCommand.Command.BACKWARD);
				command.setDuration(1000);
			}
			else if (commandName.equals("left"))
			{
				command = new NXTMovementCommand(NXTMovementCommand.Command.TURN_LEFT);
				command.setDuration(1000);
			}
			else if (commandName.equals("right"))
			{
				command = new NXTMovementCommand(NXTMovementCommand.Command.TURN_RIGHT);
				command.setDuration(1000);
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		
		return command;
	}
}
