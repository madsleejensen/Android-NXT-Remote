package net.contentcube.robot.nxt;

import java.io.OutputStream;

public interface NXTCommand {
	
	public enum Command
	{
		FORWARD,
		BACKWARD,
		BRAKE,
		TURN_RIGHT,
		TURN_LEFT,
		PING
	}
	
	public interface OnCompleteListener
	{
		public void onComplete();
	}
	
	public void run(OutputStream stream);
	public void setOnCompleteListener(OnCompleteListener listener);
}
