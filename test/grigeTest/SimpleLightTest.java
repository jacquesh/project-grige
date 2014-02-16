package grigeTest;

import com.jogamp.newt.event.KeyEvent;

import grige.*;

public class SimpleLightTest extends GameBase
{
	
	public SimpleLightTest()
	{
		super();
	}
	
	@Override
	public void initialize()
	{
		PointLight pl = new PointLight();
		pl.setRadius(1.5f);
		pl.setPosition(160,160);
		addLight(pl);
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
	public void display(){}
	
	public static void main(String[] args)
	{
		SimpleLightTest game = new SimpleLightTest();
		game.start();
	}

}
