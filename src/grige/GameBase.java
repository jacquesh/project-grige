package grige;


import com.jogamp.newt.opengl.GLWindow;

import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.event.WindowListener;
import com.jogamp.newt.event.WindowUpdateEvent;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;

import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GL2;

import java.util.ArrayList;

public abstract class GameBase implements GLEventListener, WindowListener{
	
	static GameBase instance;
	
	//Game State Data
	private boolean running;
	private ArrayList<GameObject> objects;
	
	//Game Managers
	protected Camera camera;
	protected InputManager input;
	
	//OpenGL Data
	private GLProfile glProfile;
	private GLCapabilities glCapabilities;
	private GLWindow gameWindow;
	
	protected abstract void initialize();
	protected abstract void update(float deltaTime);
	
	public GameBase()
	{
		GameBase.instance = this;
	}
	
	protected void internalSetup()
	{
		//Initialize the OpenGL profile that the game will use
		glProfile = GLProfile.getDefault();
		glCapabilities = new GLCapabilities(glProfile);
		
		//Create the game window
		gameWindow = GLWindow.create(glCapabilities);
		gameWindow.setSize(320, 320);
		gameWindow.setVisible(true);
		gameWindow.setTitle("JOGL Test");
		gameWindow.addWindowListener(this);
		gameWindow.addGLEventListener(this);
		
		//Create the various managers for the game
		input = new InputManager();
		camera = new Camera(gameWindow.getWidth(),gameWindow.getHeight(),10);
		
		//Create the data structures for holding game data
		objects = new ArrayList<GameObject>();
	}
	
	public final void start()
	{
		internalSetup();
		gameWindow.display(); //Initial draw to initalize the screen
		
		running = true;
		while(running)
		{
			internalUpdate();
			
			update(0);
			gameWindow.display();
		}
		cleanup();
	}
	
	public void addObject(GameObject newObj)
	{
		objects.add(newObj);
	}
	
	private void internalUpdate()
	{
		//Update input data
		input.update();
		
		for(int i=0; i<objects.size(); i++)
		{
			objects.get(i).update();
		}
	}
	
	protected void cleanup()
	{
		gameWindow.destroy();
		GLProfile.shutdown();
	}
	
	GL2 getGLContext()
	{
		return gameWindow.getGL().getGL2();
	}
	
	//GLEvent listener methods
	public final void init(GLAutoDrawable glad)
	{
		camera.initialize(gameWindow.getGL());
		initialize();
	}
	
	public final void display(GLAutoDrawable glad)
	{
		//Clear the screen
		camera.clear();
		
		//Draw all game objects
		for(int i=0; i<objects.size(); i++)
		{
			camera.draw(objects.get(i));
		}
	}
	
	public void reshape(GLAutoDrawable glad, int x, int y, int width, int height)
	{
		camera.setSize(width, height, camera.getDepth());
	}
	public void dispose(GLAutoDrawable glad)
	{
		
	}
	
	
	//Window listener methods
	public void windowDestroyNotify(WindowEvent we)
	{
		//The window and profile get cleaned up automatically in this case anyways
		System.exit(0);
	}
	public void windowDestroyed(WindowEvent we){}
	public void windowGainedFocus(WindowEvent we){}
	public void windowLostFocus(WindowEvent we){}
	public void windowMoved(WindowEvent we){}
	public void windowResized(WindowEvent we){}
	public void windowRepaint(WindowUpdateEvent wue){}
}
