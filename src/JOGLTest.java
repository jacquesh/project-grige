import grige.GameBase;
import grige.GameObject;
import grige.KeyCode;
import grige.ResourceLoader;

public class JOGLTest extends GameBase
{
	public JOGLTest()
	{
		super();
	}
	
	private GameObject go;
	
	@Override
	public void initialize()
	{
		go = new SampleObject();
		go.setTexture(ResourceLoader.loadImage("textures/sampletex.png"));
	}
	
	@Override
	public void update(float deltaTime)
	{
		go.update(deltaTime);
	}
	
	@Override
	public void display()
	{
		camera.draw(go);
	}
	
	public static void main(String[] args)
	{
		JOGLTest game = new JOGLTest();
		game.start();
	}

}