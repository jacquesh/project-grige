package grige;

import com.jogamp.newt.opengl.GLWindow;

import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.event.WindowListener;
import com.jogamp.newt.event.WindowUpdateEvent;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLAutoDrawable;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.nulldevice.NullSoundDevice;

import de.lessvoid.nifty.spi.time.impl.FastTimeProvider;

import de.lessvoid.nifty.renderer.jogl.render.batch.JoglBatchRenderBackendCoreProfile;
import de.lessvoid.nifty.batch.BatchRenderDevice;

import java.util.ArrayList;

public abstract class GameBase implements GLEventListener, WindowListener{
	
	static GameBase instance;
	
	//Game State Data
	private boolean running;
	private ArrayList<GameObject> worldObjects;
	private ArrayList<Light> worldLights;

	//Game time data
	private long startTime;
	private long lastFrameTime;
	private float currentDeltaTime;
	
	//Game Managers
	protected Camera camera;
	protected Audio audio;
	
	//OpenGL Data
	private GLProfile glProfile;
	private GLCapabilities glCapabilities;
	private GLWindow gameWindow;
	
	private Nifty nifty;
	
	protected abstract void initialize(GL2 gl);
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
		startTime = System.nanoTime();
		lastFrameTime = startTime;
		
		while(running)
		{	
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
		gameWindow.setTitle("GrIGE");
		
		//Create the various managers for the game
		camera = new Camera(gameWindow.getWidth(),gameWindow.getHeight(),10000);
		
		//Add the required event listeners
		gameWindow.addWindowListener(this);
		gameWindow.addGLEventListener(this);
		
		//Instantiate other structures
		worldObjects = new ArrayList<GameObject>();
		worldLights = new ArrayList<Light>();
	}
	
	public final void init(GLAutoDrawable glad)
	{
		GL2 gl = glad.getGL().getGL2();
		//Initialize internal components
		camera.initialize(gl);
		Audio.initialize();
		Input.initialize(gameWindow);
		
		//Add input listeners
		gameWindow.addKeyListener(Input.getInstance());
		gameWindow.addMouseListener(Input.getInstance());
		
		NullSoundDevice sd = null;
		sd = new NullSoundDevice();
		
		BatchRenderDevice rd = new BatchRenderDevice(new JoglBatchRenderBackendCoreProfile(), 2048, 2048);
		nifty = new Nifty(rd, sd, Input.getInstance(), new FastTimeProvider());
		
		//Run child class initialization
		initialize(gl);
	}
	
	public final void display(GLAutoDrawable glad)
	{
		GL2 gl = glad.getGL().getGL2();
		
		//Update game timer
		long currentTime = System.nanoTime();
		currentDeltaTime = (currentTime - lastFrameTime)/1000000000f;
		lastFrameTime = currentTime;
		
		//Update game state
		internalUpdate(currentDeltaTime);
		
		//Render the game state
		internalDraw(gl);
	}
	
	protected void internalDraw(GL2 gl)
	{
		//Reset the camera for this draw call
		camera.refresh(gl);
		
		camera.drawLightingStart();
		//Draw all our objects into the depth buffer so that our shadows can get depth-tested correctly against objects at the same depth
		for(GameObject obj : worldObjects)
		{
			gl.glDepthMask(true);
			gl.glColorMask(false, false, false, false);
			obj.onDraw(gl, camera);
			gl.glColorMask(true, true, true, true);
			gl.glDepthMask(false);
		}
		
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
			
			camera.drawShadowsToStencil(vertexArrays);
			
			//Draw lighting (where the stencil is empty)
			l.onDraw(gl, camera);
			gl.glClear(GL.GL_STENCIL_BUFFER_BIT);
		}
		gl.glDisable(GL.GL_STENCIL_TEST); //We only use stencil test for rendering lights
		camera.drawLightingEnd();
		
		camera.drawGeometryStart();
		//Draw all the objects now that we've finalized our lighting
		for(GameObject obj : worldObjects)
			obj.onDraw(gl, camera);
		camera.drawGeometryEnd();
		
		//Let the child game class draw any required UI
		camera.drawInterfaceStart();
		display();
		camera.drawInterfaceEnd();
		
		//Commit all drawing thats happened, combining them via their respective framebuffers as needed
		camera.commitDraw();
		
		//To render the UI on top of all of that, we need to set the 0-texture to be active and enable blending
		gl.glActiveTexture(GL.GL_TEXTURE0);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		nifty.render(false);
		gl.glDisable(GL.GL_BLEND);
	}
	
	private void internalUpdate(float deltaTime)
	{
		ArrayList<GameObject> deathList = new ArrayList<GameObject>();
		
		//Update input data
		Input.update();
		nifty.update();
		
		//Run an update on all objects
		for(GameObject obj : worldObjects)
		{
			if(obj.markedForDeath)
				deathList.add(obj);
			else
				obj.internalUpdate(deltaTime);
		}
		
		//Remove all objects that are marked for death
		for(GameObject obj : deathList)
		{
			obj.onDestroy();
			worldObjects.remove(obj);
		}
		
		//Call the user-defined game update
		update(deltaTime);
	}
	
	public float getFPS()
	{
		return 1f/currentDeltaTime;
	}
	
	public float getRunningTime()
	{
		return (System.nanoTime() - startTime)/1000000000f;
	}
	
	public Nifty getNifty()
	{
		return nifty;
	}
	
	public void addObject(GameObject obj)
	{
		worldObjects.add(obj);
	}
	
	public void addLight(Light l)
	{
		worldLights.add(l);
	}
	
	public GameObject[] getObjectsAtLocation(Vector2 loc)
	{
		ArrayList<GameObject> objList = new ArrayList<GameObject>();
		loc = camera.screenToWorldLoc(loc);
		
		for(GameObject obj : worldObjects)
		{
			AABB bounds = obj.getAABB();
			if(bounds.contains(loc))
				objList.add(obj);
		}
		
		return objList.toArray(new GameObject[objList.size()]);
	}
	
	public boolean raycast(Vector2 origin, Vector2 direction)
	{
		for(GameObject obj : worldObjects)
		{
			float[] verts = obj.getVertices();
			for(int i=0; i<verts.length; i+=2)
			{
				float x1 = verts[i]-origin.x;
				float y1 = verts[i+1]-origin.y;
				
				float x2 = verts[(i+2)%verts.length]-origin.x;
				float y2 = verts[(i+3)%verts.length]-origin.y;
				
				//v1 cross direction and v2 cross direction (they must have opposite sign
				float cross1 = direction.x*y1 - direction.y*x1;
				float cross2 = direction.x*y2 - direction.y*x2;
				
				if(cross1*cross2 <= 0)
					return true;
			}
		}
		
		return false;
	}
	
	public void destroy(GameObject obj)
	{
		obj.markedForDeath = true;
	}
	
	protected void cleanup()
	{
		Audio.cleanup();
		
		gameWindow.destroy();
		GLProfile.shutdown();
	}
	
	//Window utility functions
	public String getWindowTitle() { return gameWindow.getTitle(); }
	public boolean isFullscreen() { return gameWindow.isFullscreen(); }
	public Vector2I getWindowSize() { return new Vector2I(gameWindow.getWidth(), gameWindow.getHeight()); }
	public int getWindowWidth() { return gameWindow.getWidth(); }
	public int getWindowHeight() { return gameWindow.getHeight(); }
	
	public void setWindowTitle(String title)
	{
		gameWindow.setTitle(title);
	}
	
	public void setWindowSize(Vector2I size)
	{
		setWindowSize(size.x, size.y);
	}
	
	public void setWindowSize(int width, int height)
	{
		gameWindow.setSize(width, height);
	}
	
	public void setFullscreen(boolean fullscreen)
	{
		gameWindow.setFullscreen(fullscreen);
	}
	
	//Window event listeners
	public void reshape(GLAutoDrawable glad, int x, int y, int width, int height)
	{
		camera.setSize(width, height, camera.getDepth());
	}

	public void dispose(GLAutoDrawable glad){}
	
	//Window listener methods
	public void windowDestroyNotify(WindowEvent we)
	{
		running = false;
	}
	
	public void windowDestroyed(WindowEvent we){}
	public void windowGainedFocus(WindowEvent we){}
	public void windowLostFocus(WindowEvent we){}
	public void windowMoved(WindowEvent we){}
	public void windowResized(WindowEvent we){}
	public void windowRepaint(WindowUpdateEvent wue){}
	
	public static void printOpenGLError(GL gl, boolean displayNoError)
	{
		int error = gl.glGetError();
		switch(error)
		{
		case(GL.GL_NO_ERROR):
			if(displayNoError)
				Log.info("No Error");
			break;
		
		case(GL.GL_INVALID_ENUM):
			Log.info("Invalid Enum");
			break;
		
		case(GL.GL_INVALID_VALUE):
			Log.info("Invalid Value");
			break;
			
		case(GL.GL_INVALID_OPERATION):
			Log.info("Invalid Operation");
			break;
			
		case(GL.GL_INVALID_FRAMEBUFFER_OPERATION):
			Log.info("Invalid Framebuffer Operation");
			break;
			
		case(GL.GL_OUT_OF_MEMORY):
			Log.info("Out of Memory");
			break;
			
		default:
			Log.info("UNKNOWN OPENGL ERROR: "+error);
		}
	}
}
