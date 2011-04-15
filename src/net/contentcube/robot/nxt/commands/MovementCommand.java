package net.contentcube.robot.nxt.commands;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import net.contentcube.robot.nxt.NXTCommand;

import org.json.JSONException;
import org.json.JSONObject;

public class MovementCommand implements NXTCommand
{
	private Timer mBrakeTimer;
	private OutputStream mStream;
	private OnCompleteListener mCompleteListener;
	private MovementCommand.Command mCommand;
	private int mDuration = -1; // -1 == infinity.
	
	public MovementCommand(NXTCommand.Command command)
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
		byte[][] buffers = Factory.build(mCommand);
		
		writeBuffers(buffers);
		
		if (mDuration != -1)
		{
			scheduleBrakeAction();
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
	
	private void scheduleBrakeAction()
	{
		mBrakeTimer = new Timer("completion");
		mBrakeTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				
				writeBuffers(Factory.build(NXTCommand.Command.BRAKE));
				
				if (mCompleteListener != null)
				{
					mCompleteListener.onComplete();
				}
			}
			
		}, mDuration);
	}
	
	public static MovementCommand parseByJSONString(String jsonString)
	{
		MovementCommand command = null;
		String commandName;
		
		try
		{
			JSONObject data = new JSONObject(jsonString);
			
			commandName = data.getString("command");
			
			if (commandName.equals("forward"))
			{
				command = new MovementCommand(NXTCommand.Command.FORWARD);
				command.setDuration(500);
			}
			else if (commandName.equals("back"))
			{
				command = new MovementCommand(NXTCommand.Command.BACKWARD);
				command.setDuration(500);
			}
			else if (commandName.equals("left"))
			{
				command = new MovementCommand(NXTCommand.Command.TURN_LEFT);
				command.setDuration(500);
			}
			else if (commandName.equals("right"))
			{
				command = new MovementCommand(NXTCommand.Command.TURN_RIGHT);
				command.setDuration(500);
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		
		return command;
	}
}
