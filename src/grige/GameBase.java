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
		
		//Fill the depth buffer
		/*gl.glDepthMask(true);
		gl.glColorMask(false,false,false,false);
		gl.glDisable(GL.GL_BLEND);
		
		for(GameObject obj : worldObjects)
			camera.drawObject(obj);*/
		
		
		//Run a render pass for each light in the scene
		/*for(Light l : worldLights)
		{
			//Clear the alpha buffer
			///gl.glDepthMask(false);
			gl.glColorMask(false,false,false,true);
			gl.glClearColor(0, 0, 0, 0);
			gl.glClear(GL.GL_COLOR_BUFFER_BIT);
			
			//Draw the new alpha from the light
			//gl.glEnable(GL.GL_DEPTH_TEST);
			//gl.glDepthMask(true);
			//gl.glDepthFunc(GL.GL_LEQUAL);
			camera.drawLight(l);
			
			//Draw all the geometry
			//gl.glDisable(GL.GL_DEPTH_TEST);
			//gl.glDepthMask(false);
			gl.glColorMask(true,true,true,false);
			gl.glEnable(GL.GL_BLEND);
			gl.glBlendFunc(GL.GL_DST_ALPHA, GL.GL_ONE);
			
			for(GameObject obj : worldObjects)
			{
				camera.drawObject(obj);
			}
		}*/
		
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
