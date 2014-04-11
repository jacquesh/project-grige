package grige;

import com.jogamp.openal.AL;
import com.jogamp.openal.ALC;
import com.jogamp.openal.ALCdevice;
import com.jogamp.openal.ALCcontext;
import com.jogamp.openal.ALFactory;
import com.jogamp.openal.ALException;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.nio.ByteBuffer;


public class Audio
{
	private static final Logger log = Logger.getLogger(Audio.class.getName());
	
	private static ALC alc;
	private static AL al;
	
	private static ALCdevice device;
	private static ALCcontext context;
	
	//Buffers to hold data
	private static int[] bufferStorage = new int[1];
	private static int[] sourceStorage = new int[1];
	
	//Listener data
	private static float[] listenerLoc = new float[]{0f, 0f, 0f};
	private static float[] listenerVelocity = new float[]{0f, 0f, 0f};
	private static float[] listenerOrientation = new float[]{0f, 0f, -1f,  0f, 1f, 0f}; //(forwardX, forwardY, forwardZ, upX, upY, upZ)
	
	private Audio(){}
	
	static void initialize()
	{
		alc = ALFactory.getALC();
		device = alc.alcOpenDevice(null);
		context = alc.alcCreateContext(device, null);
		alc.alcMakeContextCurrent(context);
		
		String[] s = alc.alcGetDeviceSpecifiers();
		for(int i=0; i<s.length; i++)
			System.out.println(s[i]);
		System.out.println(alc.alcGetString(device, ALC.ALC_DEVICE_SPECIFIER));
		
		al = ALFactory.getAL();
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
		String extension = filename.substring(filename.lastIndexOf(".")+1);
		
		if(extension.equalsIgnoreCase("wav"))
			return loadWAV(filename);
		
		log.warning("Unsupported audio file type "+extension);
		return null;
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
		alc.alcMakeContextCurrent(null);
		alc.alcDestroyContext(context);
		alc.alcCloseDevice(device);
	}
	
	private static int intFromBytes(byte b1, byte b2, byte b3, byte b4)
	{
		int result = 0;
		result |= ((b1 & 0xFF) << 0);
		result |= ((b2 & 0xFF) << 8);
		result |= ((b3 & 0xFF) << 16);
		result |= ((b4 & 0xFF) << 24);
		return result;
	}
	
	private static short shortFromBytes(byte b1, byte b2)
	{
		short result = 0;
		result |= ((b1 & 0xFF) << 0);
		result |= ((b2 & 0xFF) << 8);
		return result;
	}
	
	/*
	 * Wave Format: https://ccrma.stanford.edu/courses/422/projects/WaveFormat/
	 * Extra loading info: http://www.dunsanyinteractive.com/blogs/oliver/?p=72
	 * The wave format by default assumes little-endian
	 */
	private static AudioClip loadWAV(String filename)
	{
		try
		{
			File inFile = new File(filename);
			FileInputStream inStream = new FileInputStream(inFile);
			byte[] data = new byte[44];
			int byteCount = inStream.read(data, 0, 44);
			
			if(byteCount < 44)
			{
				inStream.close();
				throw new IllegalArgumentException("Attempting to load file "+inFile.getAbsolutePath()+" which is not large enough to be a wave");
			}

			//Read all the different bits of data
			int subChunk1Size = intFromBytes(data[16], data[17], data[18], data[19]);
			short audioFormat = shortFromBytes(data[20], data[21]); //Compression format. Is the data compressed? Not used, but we do check it
			short numChannels = shortFromBytes(data[22], data[23]);
			int sampleRate = intFromBytes(data[24], data[25], data[26], data[27]);
			int byteRate = intFromBytes(data[28], data[29], data[30], data[31]); //Bytes per second. Not used
			short blockAlign = shortFromBytes(data[32], data[33]); //Block alignment. Not used
			short bitsPerSample = shortFromBytes(data[34], data[35]);
			int subChunk2Size = intFromBytes(data[40], data[41], data[42], data[43]);
			
			//Check for the headers
			if(data[0] != 'R' || data[1] != 'I' || data[2] != 'F' || data[3] != 'F'
					|| data[8] != 'W' || data[9] != 'A' || data[10] != 'V' || data[11] != 'E'
					|| data[12] != 'f' || data[13] != 'm' || data[14] != 't'
					|| data[36] != 'd' || data[37] != 'a' || data[38] != 't' || data[39] != 'a'
					|| subChunk1Size != 16
					|| audioFormat != 1
					|| numChannels < 1 || numChannels > 2
					|| (bitsPerSample != 8 && bitsPerSample != 16))
			{
				inStream.close();
				throw new IllegalArgumentException("Attempting to load non-wave file "+inFile.getAbsolutePath()+" as a wave file");
			}

			//Determine the which ALConstant we need as the format
			int format = 0;
			if(numChannels == 1)
			{
				if(bitsPerSample == 8)
					format = AL.AL_FORMAT_MONO8;
				
				else if(bitsPerSample == 16)
					format = AL.AL_FORMAT_MONO16;
			}
			else if(numChannels == 2)
			{
				if(bitsPerSample == 8)
					format = AL.AL_FORMAT_STEREO8;
				
				else if(bitsPerSample == 16)
					format = AL.AL_FORMAT_STEREO16;
			}
			
			byte[] audioData = new byte[subChunk2Size];
			inStream.read(audioData);
			
			al.alGenBuffers(1, bufferStorage, 0);
			al.alBufferData(bufferStorage[0], format, ByteBuffer.wrap(audioData), subChunk2Size, sampleRate);
			
			AudioClip clip = new AudioClip(bufferStorage[0]);
			
			inStream.close();
			
			return clip;
		}
		catch(FileNotFoundException fnfe)
		{
			log.log(Level.WARNING, "", fnfe);
		}
		catch(IOException ioe)
		{
			log.log(Level.WARNING, "", ioe);
		}
		
		return null;
	}
	
	public static void printOpenALError(AL gl, boolean displayNoError)
	{
		int error = al.alGetError();
		switch(error)
		{
		case(AL.AL_NO_ERROR):
			if(displayNoError)
				log.info("No OpenAL Error");
			break;
		
		case(AL.AL_INVALID_NAME):
			log.log(Level.WARNING, "OpenAL Error:", new ALException("Invalid Name"));
			
		case(AL.AL_INVALID_ENUM):
			log.log(Level.WARNING, "OpenAL Error:", new ALException("Invalid Enum"));
			break;
		
		case(AL.AL_INVALID_VALUE):
			log.log(Level.WARNING, "OpenAL Error:", new ALException("Invalid Value"));
			break;
			
		case(AL.AL_INVALID_OPERATION):
			log.log(Level.WARNING, "OpenAL Error:", new ALException("Invalid Operation"));
			break;
			
		case(AL.AL_OUT_OF_MEMORY):
			log.log(Level.WARNING, "OpenAL Error:", new ALException("Out of Memory"));
			break;
			
		default:
			log.log(Level.WARNING, "OpenAL Error:", new ALException("Unrecognised OpenGL Error: "+error));
		}
	}
}
