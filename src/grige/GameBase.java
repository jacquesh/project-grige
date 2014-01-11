package grige;


import com.jogamp.newt.opengl.GLWindow;

import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.event.WindowListener;
import com.jogamp.newt.event.WindowUpdateEvent;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;

import javax.media.opengl.GL;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GL2;

import java.util.ArrayList;

public abstract class GameBase implements GLEventListener, WindowListener{
	
	static GameBase instance;
	
	//Game State Data
	private boolean running;
	private ArrayList<GameObject> worldObjects;
	private ArrayList<Light> worldLights;
	
	//Game Managers
	protected Camera camera;
	protected InputManager input;
	
	//OpenGL Data
	private GLProfile glProfile;
	private GLCapabilities glCapabilities;
	private GLWindow gameWindow;
	
	protected abstract void initialize();
	protected abstract void update(float deltaTime);
	protected abstract void display();
	
	public GameBase()
	{
		GameBase.instance = this;
	}
	
	public final void start()
	{
		internalSetup();
		gameWindow.display(); //Initial draw to initalize the screen
		
		running = true;
		while(running)
		{
			internalUpdate(0);
			
			gameWindow.display();
		}
		cleanup();
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
		
		//Instantiate other structures
		worldObjects = new ArrayList<GameObject>();
		worldLights = new ArrayList<Light>();
	}
	
	public void addObject(GameObject obj)
	{
		worldObjects.add(obj);
	}
	
	public void addLight(Light l)
	{
		worldLights.add(l);
	}
	
	private void internalUpdate(float deltaTime)
	{
		//Update input data
		input.update();
		
		for(GameObject obj : worldObjects)
		{
			obj.update(deltaTime);
		}
		
		//Call the user-defined update
		update(deltaTime);
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
		GL gl = glad.getGL();
		
		//Reset the camera for this draw call
		camera.refresh();
		
		//Just draw all the things for testing
		for(GameObject obj : worldObjects)
		{
			camera.drawObject(obj);
		}
		for(Light l : worldLights)
		{
			camera.drawLight(l);
		}
		
		//Create shadow geometry
		for(Light l : worldLights)
		{
			for(GameObject obj : worldObjects)
			{
				float[] vertices = obj.getVertices();
				float previousX = vertices[6];
				float previousY = vertices[7];
				float currentX = 0;
				float currentY = 0;
				float offsetX = 0;
				float offsetY = 0;
				for(int index=0; index<8; index+=2)
				{
					currentX = vertices[index];
					currentY = vertices[index+1];
					
					offsetX = currentX - previousX;
					offsetY = currentY - previousY;
					
					
				}
			}
		}
		
		//Call child-class rendering
		display();
		
		//Commit all drawing thats happened, combining them via their respective framebuffers as needed
		camera.commitDraw();
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
