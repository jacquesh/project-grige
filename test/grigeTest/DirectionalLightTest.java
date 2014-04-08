package grigeTest;

import com.jogamp.newt.event.KeyEvent;

import grige.*;

import javax.media.opengl.GL2;

public class DirectionalLightTest extends GameBase
{
	private DirectionalLight dl;
	
	public DirectionalLightTest()
	{
		super();
	}
	
	@Override
	public void initialize(GL2 gl)
	{
		int shader = Graphics.loadShader(gl, "SimpleVertexShader.vsh", "SimpleFragmentShader.fsh");
		int lightingShader = Graphics.loadShader(gl, "Light.vsh", "NonAttenuatingLight.fsh");
		
		Material backgroundMaterial = Material.load(gl, "test/grigeTest/background.png");
		Material spriteMaterial = Material.load(gl, "test/grigeTest/bluegreengrid.png");
		
		//camera.setAmbientLight(1, 1, 1, 1);
		
		SampleObject backgroundSprite = new SampleObject();
		backgroundSprite.setMaterial(backgroundMaterial);
		backgroundSprite.setShader(gl, shader);
		backgroundSprite.setDepth(100);
		
		SampleObject testSprite = new SampleObject();
		testSprite.setMaterial(spriteMaterial);
		testSprite.setPosition(160,180);
		testSprite.setShader(gl, shader);
		testSprite.setDepth(2);
		
		dl = new DirectionalLight(new Vector2(1,-0.3f));
		dl.setIntensity(0.5f);
		dl.setShader(gl, lightingShader);
		
		addObject(backgroundSprite);
		addObject(testSprite);
		addLight(dl);
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
		DirectionalLightTest game = new DirectionalLightTest();
		game.start();
	}

}
