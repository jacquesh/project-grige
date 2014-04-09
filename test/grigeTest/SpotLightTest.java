package grigeTest;

import grige.*;

import com.jogamp.newt.event.KeyEvent;

import javax.media.opengl.GL2;

public class SpotLightTest extends GameBase
{
	
	private SpotLight sl;
	
	public SpotLightTest()
	{
		super();
	}
	
	@Override
	public void initialize(GL2 gl)
	{
		int shader = Graphics.loadShader(gl, "SimpleVertexShader.vsh", "SimpleFragmentShader.fsh");
		int lightingShader = Graphics.loadShader(gl, "SpotLight.vsh", "AttenuatingLight.fsh");
		
		Material backgroundMaterial = Material.load(gl, "test/grigeTest/background.png");
		Material spriteMaterial = Material.load(gl, "test/grigeTest/bluegreengrid.png");
		
		SampleObject background = new SampleObject();
		background.setMaterial(backgroundMaterial);
		background.setDepth(10);
		background.setShader(gl, shader);
		
		SampleObject testSprite = new SampleObject();
		testSprite.setMaterial(spriteMaterial);
		testSprite.setPosition(160,180);
		testSprite.setShader(gl, shader);
		testSprite.setDepth(2);
		
		sl = new SpotLight(90);
		sl.setPosition(160,160);
		sl.setShader(gl, lightingShader);
		
		addObject(background);
		addObject(testSprite);
		addLight(sl);
	}
	
	@Override
	public void update(float deltaTime)
	{
		if(Input.getKey(KeyEvent.VK_RIGHT))
			sl.incRotation(-180*deltaTime);
		if(Input.getKey(KeyEvent.VK_LEFT))
			sl.incRotation(180*deltaTime);
		
		if(Input.getKey(KeyEvent.VK_UP))
			sl.setSpotAngle(sl.getSpotAngle() + 180*deltaTime);
		if(Input.getKey(KeyEvent.VK_DOWN))
			sl.setSpotAngle(sl.getSpotAngle() - 180*deltaTime);
		
		
		Vector2I mouseLoc = Input.getMouseLoc();
		Vector3 lightLoc = camera.screenToWorldLoc(mouseLoc.x, mouseLoc.y, 0);
		sl.setX(lightLoc.x);
		sl.setY(lightLoc.y);
	}
	
	@Override
	public void display(){}
	
	public static void main(String[] args)
	{
		SpotLightTest game = new SpotLightTest();
		game.start();
	}

}
