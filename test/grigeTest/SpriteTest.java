package grigeTest;

import com.jogamp.newt.event.KeyEvent;

import grige.*;

import javax.media.opengl.GL2;

public class SpriteTest extends GameBase
{
	private int shader;
	private Material spriteMaterial;
	private GameObject testSprite;
	
	public SpriteTest()
	{
		super();
	}
	
	@Override
	public void initialize(GL2 gl)
	{
		shader = Graphics.loadShader(gl, "SimpleVertexShader.vsh", "SimpleFragmentShader.fsh");
		camera.setAmbientLightAlpha(1);
		
		spriteMaterial = Material.load(gl, "test/grigeTest/bluegreengrid.png");
		
		testSprite = new SampleObject();
		testSprite.setMaterial(spriteMaterial);
		testSprite.setX(160);
		testSprite.setY(160);
		testSprite.setShader(gl, shader);
		
		addObject(testSprite);
	}
	
	@Override
	public void update(float deltaTime)
	{
		testSprite.incRotation(60 * deltaTime);
		
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
		
	}
	
	public static void main(String[] args)
	{
		SpriteTest game = new SpriteTest();
		game.start();
	}

}
