package net.contentcube.robot.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StringHelper {

	public static String createByInputStream(InputStream inputStream)
	{
		InputStreamReader streamReader = new InputStreamReader(inputStream);
		BufferedReader reader = new BufferedReader(streamReader, 256);
		
		StringBuilder builder = new StringBuilder();
		String line;
		try
		{
			while ((line = reader.readLine()) != null)
			{
				builder.append(line);
			}
			
			reader.close();
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	
		return builder.toString();
	}
	
}
