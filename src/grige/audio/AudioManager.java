package grige.audio;

import com.jogamp.openal.*;

import com.jogamp.openal.util.ALut;

import java.nio.ByteBuffer;

public class AudioManager
{
	static AL al = ALFactory.getAL();
	
	//Buffers to hold data
	static int[] buffer = new int[1];
	
	//Default Source data
	private float[] defaultSourceLoc = new float[]{0f, 0f, 0f};
	private float[] defaultSourceVelocity = new float[]{0f, 0f, 0f};
	
	//Listener data
	private float[] listenerLoc = new float[]{0f, 0f, 0f};
	private float[] listenerVelocity = new float[]{0f, 0f, 0f};
	private float[] listenerOrientation = new float[]{0f, 0f, -1f,  0f, 1f, 0f}; //(atX, atY, atZ, upX, upY, upZ)
	
	public void initialize()
	{
		ALut.alutInit();

		al.alListenerfv(AL.AL_POSITION, listenerLoc, 0);
		al.alListenerfv(AL.AL_VELOCITY, listenerVelocity, 0);
		al.alListenerfv(AL.AL_ORIENTATION, listenerOrientation, 0);
	}
	
	public Source createSource()
	{
		//Generate a new audio source
		al.alGenSources(1, buffer, 0);
		Source source = new Source(buffer[0]);
		
		al.alSourcei(source.getID(), AL.AL_BUFFER, buffer[0]);
		al.alSourcef(source.getID(), AL.AL_PITCH, 1f);
		al.alSourcef(source.getID(), AL.AL_GAIN, 1f);
		al.alSourcefv(source.getID(), AL.AL_POSITION, defaultSourceLoc, 0);
		al.alSourcefv(source.getID(), AL.AL_VELOCITY, defaultSourceVelocity, 0);
		al.alSourcei(source.getID(), AL.AL_LOOPING, AL.AL_FALSE);
		
		return source;
	}
	
	public Clip loadClip(String filename)
	{
		//Generate a new buffer into which we can load the clip
		al.alGenBuffers(1, buffer, 0);
		Clip clip = new Clip(buffer[0]);
		
		//variables to load into
		int[] format = new int[1];
		int[] size = new int[1];
		ByteBuffer[] data = new ByteBuffer[1];
		int[] frequency = new int[1];
		int[] loop = new int[1];
		
		ALut.alutLoadWAVFile(filename, format, data, size, frequency, loop);
		al.alBufferData(buffer[0], format[0], data[0], size[0], frequency[0]);
		
		return clip;
	}
	
	public static void play(Source source)
	{
		al.alSourcePlay(source.getID());
	}
	
	public static void pause(Source source)
	{
		al.alSourcePause(source.getID());
	}
	
	public static void stop(Source source)
	{
		al.alSourceStop(source.getID());
	}
	
	/*static void killALData()
	{
		al.alDeleteBuffers(1, buffer, 0);
		al.alDeleteSources(1, source, 0);
		ALut.alutExit();
	}*/
}
