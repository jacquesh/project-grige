package grigeTest;

import grige.*;

import com.jogamp.newt.event.KeyEvent;

import com.jogamp.opengl.math.FloatUtil;

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
		
		Material spriteMaterial = Material.load(gl, "test/grigeTest/bluegreengrid.png");
		
		SampleObject testSprite = new SampleObject();
		testSprite.setMaterial(spriteMaterial);
		testSprite.setPosition(160,180);
		testSprite.setShader(gl, shader);
		testSprite.setDepth(2);
		
		sl = new SpotLight(90);
		sl.setRadius(1.5f);
		sl.setPosition(160,140);
		sl.setIntensity(20);
		sl.setShader(gl, lightingShader);
		
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
