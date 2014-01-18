package grige;

public class AudioSource
{
	private int id;
	private float volume;
	
	AudioSource(int sourceID)
	{
		id = sourceID;
		volume = 1;
	}
	
	public int getID(){ return id; }
}
