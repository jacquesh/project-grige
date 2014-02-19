package grige;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import java.util.ArrayList;

public class Animation
{
	public static final int PLAY_MODE_NONE = 0;
	public static final int PLAY_MODE_LOOP = 1;
	public static final int PLAY_MODE_PINGPONG = 2;
	public static final int PLAY_MODE_ONCE = 3;
	
	
	private ArrayList<AABBI> frameQuads;
	
	private Animation()
	{
		frameQuads = new ArrayList<AABBI>();
	}
	
	private void addFrame(int x, int y, int w, int h)
	{
		frameQuads.add(new AABBI(x,y,w,h));
	}
	
	public AABBI getFrameBox(int frame)
	{
		if(frame >= frameQuads.size())
			return null;
		return frameQuads.get(frame);
	}
	
	public int length()
	{
		return frameQuads.size();
	}
	
	public static Animation load(String filepath)
	{
		File sourceFile = new File(filepath);
		
		System.out.println("Loading animation "+sourceFile.getAbsolutePath());
		if(!sourceFile.exists())
		{
			System.out.println("Error, attempt to load non-existant file: "+filepath);
			return null;
		}
		
		try
		{
			FileReader fileIn = new FileReader(sourceFile);
			BufferedReader reader = new BufferedReader(fileIn);
			
			Animation anim = new Animation();
			
			while(reader.ready())
			{
				String newLine = reader.readLine();
				
				String[] lineElements = newLine.split(" ");
				
				anim.addFrame( 
						Integer.parseInt(lineElements[2]),
						Integer.parseInt(lineElements[3]),
						Integer.parseInt(lineElements[4]),
						Integer.parseInt(lineElements[5])
				);
			}
			
			reader.close();
			fileIn.close();
			
			return anim;
		
		}
		catch(FileNotFoundException fnfe)
		{
			fnfe.printStackTrace();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
		
		return null;
	}
}
