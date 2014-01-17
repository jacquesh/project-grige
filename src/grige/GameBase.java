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
		gameWindow.display(); //Draw once before looping to initalize the screen/opengl

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
		
		//Create the various managers for the game
		input = new InputManager(gameWindow.getHeight());
		camera = new Camera(gameWindow.getWidth(),gameWindow.getHeight(),10);
		
		//Add the required event listeners
		gameWindow.addWindowListener(this);
		gameWindow.addGLEventListener(this);
		gameWindow.addKeyListener(input);
		gameWindow.addMouseListener(input);
		
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
		camera.initialize(glad.getGL());
		initialize();
	}
	
	public final void display(GLAutoDrawable glad)
	{
		GL gl = glad.getGL();
		
		//Reset the camera for this draw call
		camera.refresh(gl);
		
		//Draw all our objects into the depth buffer so that our lights can get depth-tested correctly
		for(GameObject obj : worldObjects)
			camera.drawObjectDepthToLighting(obj);
		
		//Draw *all* the lights
		gl.glEnable(GL.GL_STENCIL_TEST); //We need to stencil out bits of light, so enable stencil test while we're drawing lights
		for(Light l : worldLights)
		{
			ArrayList<float[]> vertexArrays = new ArrayList<float[]>();
			for(GameObject obj : worldObjects)
			{
				//Compute/store the vertices of the shadow of this objected, as a result of the current light
				float[] vertices = camera.generateShadowVertices(l, obj);
				vertexArrays.add(vertices);
			}
			
			//Set to draw only to the stencil buffer (no colour/alpha)
			gl.glColorMask(false, false, false, false);
			gl.glStencilFunc(GL.GL_ALWAYS, 1, 1);
			gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_REPLACE);
			
			//Render all shadows from this light into the stencil buffer
			//This is so that when we render the actual light, it doesn't light up the shadows
			for(int i=0; i<vertexArrays.size(); i++)
				camera.drawShadow(vertexArrays.get(i));
			
			//Reset drawing to standard colour/alpha
			gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);
			gl.glStencilFunc(GL.GL_EQUAL, 0, 1);
			gl.glColorMask(true, true, true, true);
			
			//Draw lighting (where the stencil is empty)			
			camera.drawLight(l);
			camera.clearShadowStencil();
		}
		gl.glDisable(GL.GL_STENCIL_TEST); //We only use stencil test for rendering lights
		
		//Draw all the objects now that we've finalized our lighting
		for(GameObject obj : worldObjects)
			camera.drawObject(obj);
		
		//Child-class drawing
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
	
	public static void printOpenGLError(GL gl, boolean displayNoError)
	{
		int error;
		
		do
		{
			error = gl.glGetError();
			switch(error)
			{
			case(GL.GL_NO_ERROR):
				if(displayNoError)
					System.out.println("No Error");
				break;
			
			case(GL.GL_INVALID_ENUM):
				System.out.println("Invalid Enum");
				break;
			
			case(GL.GL_INVALID_VALUE):
				System.out.println("Invalid Value");
				break;
				
			case(GL.GL_INVALID_OPERATION):
				System.out.println("Invalid Operation");
				break;
				
			case(GL.GL_INVALID_FRAMEBUFFER_OPERATION):
				System.out.println("Invalid Framebuffer Operation");
				break;
				
			case(GL.GL_OUT_OF_MEMORY):
				System.out.println("Out of Memory");
				break;
				
			default:
				System.out.println("UNKNOWN OPENGL ERROR: "+error);
			}
		}while(error != 0);
	}
}
