package grigeTest;

import java.awt.Font;

import com.jogamp.newt.event.KeyEvent;

import grige.*;



public class Text2D3DTest extends GameBase
{
	
	private Font font;
	
	public Text2D3DTest()
	{
		super();
	}
	
	@Override
	public void initialize()
	{
		font = new java.awt.Font("Serif", java.awt.Font.PLAIN, 16);
	}
	
	@Override
	public void update(float deltaTime)
	{
		if(Input.getKey(KeyEvent.VK_RIGHT))
			camera.setPosition(camera.getX()+100f*deltaTime, camera.getY());
		if(Input.getKey(KeyEvent.VK_LEFT))
			camera.setPosition(camera.getX()-100f*deltaTime, camera.getY());
		if(Input.getKey(KeyEvent.VK_UP))
			camera.setPosition(camera.getX(), camera.getY()+100f*deltaTime);
		if(Input.getKey(KeyEvent.VK_DOWN))
			camera.setPosition(camera.getX(), camera.getY()-100f*deltaTime);
	}
	
	@Override
	public void display()
	{
		camera.drawText(font, "This is 2D text", 160, 160, 0, Color.WHITE, 1, false);
		camera.drawText(font, "This is 3D text", 160, 140, 0, Color.WHITE, 1, true);
	}
	
	public static void main(String[] args)
	{
		Text2D3DTest game = new Text2D3DTest();
		game.start();
	}

}
