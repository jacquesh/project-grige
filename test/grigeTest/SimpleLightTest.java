package grigeTest;

import com.jogamp.newt.event.KeyEvent;

import grige.*;

import javax.media.opengl.GL2;

public class SimpleLightTest extends GameBase
{
	
	public SimpleLightTest()
	{
		super();
	}
	
	@Override
	public void initialize(GL2 gl)
	{
		int shader = Graphics.loadShader(gl, "SimpleVertexShader.vsh", "SimpleFragmentShader.fsh").program();
		int lightingShader = Graphics.loadShader(gl, "LightVertexShader.vsh", "LightFragmentShader.fsh").program();
		camera.setAmbientLightAlpha(0);
		
		Material spriteMaterial = Material.load("test/grigeTest/bluegreengrid.png");
		
		SampleObject testSprite = new SampleObject();
		testSprite.setMaterial(spriteMaterial);
		testSprite.setPosition(160,180);
		testSprite.setShader(gl, shader);
		testSprite.setDepth(2);
		
		PointLight pl = new PointLight();
		pl.setRadius(1.5f);
		pl.setPosition(160,140);
		pl.setShader(gl, lightingShader);
		
		addObject(testSprite);
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
