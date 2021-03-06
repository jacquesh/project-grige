package grigeTest;

import com.jogamp.newt.event.KeyEvent;

import grige.*;

import javax.media.opengl.GL2;

public class SelfIlluminationTest extends GameBase
{
	
	private PointLight pl;
	
	public SelfIlluminationTest()
	{
		super();
	}
	
	@Override
	public void initialize(GL2 gl)
	{
		int shader = Graphics.loadShader(gl, "SimpleVertexShader.vsh", "SimpleFragmentShader.fsh");
		int lightingShader = Graphics.loadShader(gl, "Light.vsh", "AttenuatingLight.fsh");
		
		Material backgroundMaterial = Material.load(gl, "test/grigeTest/background.png");
		Material spriteMaterial = Material.load(gl, "test/grigeTest/bluegreengrid.png", null, "test/grigeTest/bluegreenSelfIllu.png");
		
		SampleObject backgroundSprite = new SampleObject();
		backgroundSprite.setMaterial(backgroundMaterial);
		backgroundSprite.setDepth(100);
		backgroundSprite.setShader(gl, shader);
		
		SampleObject testSprite = new SampleObject();
		testSprite.setMaterial(spriteMaterial);
		testSprite.setPosition(160,180);
		testSprite.setShader(gl, shader);
		testSprite.setDepth(2);
		
		pl = new PointLight();
		pl.setRadius(1.5f);
		pl.setPosition(160,140);
		pl.setShader(gl, lightingShader);
		
		addObject(backgroundSprite);
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
		
		Vector2I mouseLoc = Input.getMouseLoc();
		Vector3 lightLoc = camera.screenToWorldLoc(mouseLoc.x, mouseLoc.y, 0);
		pl.setX(lightLoc.x);
		pl.setY(lightLoc.y);
	}
	
	@Override
	public void display(){}
	
	public static void main(String[] args)
	{
		SelfIlluminationTest game = new SelfIlluminationTest();
		game.start();
	}

}
