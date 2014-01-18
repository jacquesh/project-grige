package grige;

public class AudioClip
{
	private int bufferId;
	
	AudioClip(int clipBuffer)
	{
		bufferId = clipBuffer;
	}
	
	public int getID() { return bufferId; }
}
