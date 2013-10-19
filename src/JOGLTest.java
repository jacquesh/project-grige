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
		go = new GameObject();
		go.setTexture(ResourceLoader.loadImage("textures/sampletex.png"));
		
		addObject(go);
	}
	
	@Override
	public void update(float deltaTime)
	{
		
	}
	
	public static void main(String[] args)
	{
		JOGLTest game = new JOGLTest();
		game.start();
	}

}