package grigeTest;

import com.jogamp.newt.event.KeyEvent;

import grige.*;

import javax.media.opengl.GL2;

public class AnimationTest extends GameBase
{
	private int shader;
	private Material walkMaterial;
	private Animation walkAnimation;
	private GameObject walker;
	
	public AnimationTest()
	{
		super();
	}
	
	@Override
	public void initialize(GL2 gl)
	{
		shader = Graphics.loadShader(gl, "SimpleVertexShader.vsh", "SimpleFragmentShader.fsh").program();
		camera.setAmbientLightAlpha(1);
		
		walkMaterial = Material.load("test/grigeTest/playerWalk.png");
		walkAnimation = Animation.load("test/grigeTest/playerWalk.txt");
		
		walker = new SampleObject();
		walker.setMaterial(walkMaterial);
		walker.setAnimation(walkAnimation);
		walker.playAnimation(Animation.PLAY_MODE_LOOP, 30);
		walker.setX(160);
		walker.setY(160);
		walker.setShader(gl, shader);
		
		addObject(walker);
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
		
	}
	
	public static void main(String[] args)
	{
		AnimationTest game = new AnimationTest();
		game.start();
	}

}
