package grigeTest;

import grige.GameBase;
import grige.Audio;
import grige.AudioClip;
import grige.AudioSource;
import grige.Input;

import javax.media.opengl.GL2;

import com.jogamp.newt.event.KeyEvent;

public class SingleAudioTest extends GameBase
{
	private AudioClip clip;
	private AudioSource source;
	
	public void initialize(GL2 gl)
	{
		clip = Audio.loadClip("test/grigeTest/robot.wav");
		source = Audio.createSource();
	}
	
	public void update(float deltaTime)
	{
		if(Input.getKeyDown(KeyEvent.VK_SPACE))
		{
			Audio.play(source, clip, false);
		}
	}
	
	public void display()
	{
		
	}
	
	public static void main(String[] args)
	{
		SingleAudioTest sat = new SingleAudioTest();
		sat.start();
	}
}
