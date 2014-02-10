package grige;

import com.jogamp.openal.*;

import com.jogamp.openal.util.ALut;


import java.nio.ByteBuffer;

public class Audio
{
	private static AL al = ALFactory.getAL();
	
	//Buffers to hold data
	private static int[] bufferStorage = new int[1];
	private static int[] sourceStorage = new int[1];

	private static int[] formatStorage = new int[1];
	private static int[] sizeStorage = new int[1];
	private static int[]frequencyStorage = new int[1];
	private static int[] loopStorage = new int[1];
	private static ByteBuffer[] dataStorage = new ByteBuffer[1];
	
	//Listener data
	private static float[] listenerLoc = new float[]{0f, 0f, 0f};
	private static float[] listenerVelocity = new float[]{0f, 0f, 0f};
	private static float[] listenerOrientation = new float[]{0f, 0f, -1f,  0f, 1f, 0f}; //(forwardX, forwardY, forwardZ, upX, upY, upZ)
	
	private Audio(){}
	
	static void initialize()
	{
		ALut.alutInit();

		al.alListenerfv(AL.AL_POSITION, listenerLoc, 0);
		al.alListenerfv(AL.AL_VELOCITY, listenerVelocity, 0);
		al.alListenerfv(AL.AL_ORIENTATION, listenerOrientation, 0);
	}
	
	public static AudioSource createSource()
	{
		//Generate a new audio source
		al.alGenSources(1, sourceStorage, 0);
		AudioSource source = new AudioSource(sourceStorage[0]);
		
		al.alSourcef(source.getID(), AL.AL_PITCH, 1f);
		al.alSourcef(source.getID(), AL.AL_GAIN, 1f);
		al.alSource3f(source.getID(), AL.AL_POSITION, 0f, 0f, 0f);
		al.alSource3f(source.getID(), AL.AL_VELOCITY, 0f, 0f, 0f);
		al.alSourcei(source.getID(), AL.AL_LOOPING, AL.AL_FALSE);
		
		return source;
	}
	
	public static AudioClip loadClip(String filename)
	{
		//Generate a new buffer into which we can load the clip
		al.alGenBuffers(1, bufferStorage, 0);
		AudioClip clip = new AudioClip(bufferStorage[0]);
		
		ALut.alutLoadWAVFile(filename, formatStorage, dataStorage, sizeStorage, frequencyStorage, loopStorage);
		al.alBufferData(bufferStorage[0], formatStorage[0], dataStorage[0], sizeStorage[0], frequencyStorage[0]);
		
		return clip;
	}
	
	public static void destroySource(AudioSource source)
	{
		sourceStorage[0] = source.getID();
		al.alDeleteSources(1, sourceStorage, 0);
	}
	
	public static void destroyClip(AudioClip clip)
	{
		bufferStorage[0] = clip.getID();
		al.alDeleteBuffers(1, bufferStorage, 0);
	}
	
	public static void play(AudioSource source, AudioClip clip, boolean loop)
	{
		al.alSourcei(source.getID(), AL.AL_LOOPING, loop ? AL.AL_TRUE : AL.AL_FALSE);
		al.alSourcei(source.getID(), AL.AL_BUFFER, clip.getID());
		al.alSourcePlay(source.getID());
	}
	
	public static void pause(AudioSource source)
	{
		al.alSourcePause(source.getID());
	}
	
	public static void stop(AudioSource source)
	{
		al.alSourceStop(source.getID());
	}

	static void cleanup()
	{
		ALut.alutExit();
	}
}
