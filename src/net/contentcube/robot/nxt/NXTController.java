package net.contentcube.robot.nxt;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class NXTController
{
	private static NXTController mInstance;
	
	private BluetoothSocket mSocket;
	private OutputStream mOutputStream;
	private LinkedList<NXTMovementCommand> mCommandQueue;
	private boolean mRunning = false;
	private NXTMovementCommand.OnCompleteListener mCompleteListener = new NXTMovementCommand.OnCompleteListener() {

		@Override
		public void onComplete()
		{
			Log.e("NXTCommand", "Complete");
			mRunning = false;
			runQueue();
		}
		
	};
	
	public static NXTController getInstance()
	{
		if (mInstance == null)
		{
			mInstance = new NXTController();
		}
		
		return mInstance;
	}
	
	public NXTController()
	{
		mCommandQueue = new LinkedList<NXTMovementCommand>();
	}
	
	public void setBluetoothSocket(BluetoothSocket socket)
	{
		mSocket = socket;
		
		try
		{
			mOutputStream = socket.getOutputStream();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void forward(int duration)
	{
		NXTMovementCommand command = new NXTMovementCommand(NXTMovementCommand.Command.FORWARD);
		command.setDuration(duration);
		
		queue(command);
	}
	
	public void turnRight(int duration)
	{
		NXTMovementCommand command = new NXTMovementCommand(NXTMovementCommand.Command.TURN_RIGHT);
		command.setDuration(duration);
		
		queue(command);
	}
	
	public void turnLeft(int duration)
	{
		NXTMovementCommand command = new NXTMovementCommand(NXTMovementCommand.Command.TURN_LEFT);
		command.setDuration(duration);
		
		queue(command);
	}
	
	public void backward(int duration)
	{
		NXTMovementCommand command = new NXTMovementCommand(NXTMovementCommand.Command.BACKWARD);
		command.setDuration(duration);
		
		queue(command);
	}
	
	public void brake()
	{
		NXTMovementCommand command = new NXTMovementCommand(NXTMovementCommand.Command.BRAKE);
		queue(command);
	}
	
	private void queue(NXTMovementCommand command)
	{
		mCommandQueue.add(command);
		runQueue();
	}
	
	private void runQueue()
	{
		if (mRunning) return;
		mRunning = true;
		
		NXTMovementCommand command = mCommandQueue.poll();
		
		if (command != null)
		{
			command.setOnCompleteListener(mCompleteListener);
			command.run(mOutputStream);
		}
		else
		{
			mRunning = false;
		}
	}
}
