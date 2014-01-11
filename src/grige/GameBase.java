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
			//camera.drawLight(l);
		}
		
		//Create shadow geometry
		float[] vertices;
		Vector2 previousLoc = new Vector2(0,0);
		Vector2 currentLoc = new Vector2(0,0);
		Vector2 vertexNormal = new Vector2(0,0);
		Vector2 lightOffsetDir = new Vector2(0,0);
		Vector2 lightProjectionLoc = new Vector2(0, 0);
		float dotProduct;
		for(Light l : worldLights)
		{
			for(GameObject obj : worldObjects)
			{
				ArrayList<Float> vertexList = new ArrayList<Float>();
				int currentVertexIndex = 0;
				
				vertices = obj.getVertices();
				previousLoc.x = vertices[6];
				previousLoc.y = vertices[7];
				for(int index=0; index<8; index+=2)
				{
					currentLoc.x = vertices[index];
					currentLoc.y = vertices[index+1];
					
					lightOffsetDir.x = l.x() - currentLoc.x;
					lightOffsetDir.y = l.y() - currentLoc.y;
					lightOffsetDir.normalise(); //We can normalise here because the magnitude has no effect on the sign of any dot products
					
					//Because we know we're traversing vertices in a counter-clockwise order, we know that the normal for an edge is
					//(dy, -dx)
					vertexNormal.x = currentLoc.y - previousLoc.y;
					vertexNormal.y = -(currentLoc.x - previousLoc.x);
					
					dotProduct = vertexNormal.x*lightOffsetDir.x + vertexNormal.y*lightOffsetDir.y;
					
					if(dotProduct <= 0)
					{
						if(currentVertexIndex == -1)
						{ 	//If the current index has been set to -1 then we moved from light into shadow
							//so we need to add shadow for both the current and previous vertices
							Vector2 previousLightOffsetDir = new Vector2(l.x()-previousLoc.x, l.y()-previousLoc.y);
							previousLightOffsetDir.normalise();
							
							currentVertexIndex = 0;
							vertexList.add(currentVertexIndex, previousLoc.x);
							vertexList.add(currentVertexIndex+1, previousLoc.y);
							vertexList.add(currentVertexIndex+2, 0f); //We need a z-value so we can just cast it to an array and immediately use it in a vertex buffer
							
							lightProjectionLoc.set(previousLightOffsetDir);
							lightProjectionLoc.multiply(-100); //lightOffset is the vector: "vertex -> light", we use (-) because we need to offset "light vertex ->"
							lightProjectionLoc.add(previousLoc);
							
							vertexList.add(currentVertexIndex+3, lightProjectionLoc.x);
							vertexList.add(currentVertexIndex+4, lightProjectionLoc.y);
							vertexList.add(currentVertexIndex+5, 0f);
							
							currentVertexIndex += 6;
						}
						vertexList.add(currentVertexIndex, currentLoc.x);
						vertexList.add(currentVertexIndex+1, currentLoc.y);
						vertexList.add(currentVertexIndex+2, 0f); //We need a z-value so we can just cast it to an array and immediately use it in a vertex buffer
						
						lightProjectionLoc.set(lightOffsetDir);
						lightProjectionLoc.multiply(-100); //lightOffset is the vector: "vertex -> light", we use (-) because we need to offset "light vertex ->"
						lightProjectionLoc.add(currentLoc);
						
						vertexList.add(currentVertexIndex+3, lightProjectionLoc.x);
						vertexList.add(currentVertexIndex+4, lightProjectionLoc.y);
						vertexList.add(currentVertexIndex+5, 0f);
						
						currentVertexIndex += 6;
					}
					else
						currentVertexIndex = -1;
					
					previousLoc.x = currentLoc.x;
					previousLoc.y = currentLoc.y;
				}
				
				float[] vertArray = new float[vertexList.size()];
				for(int i=0; i<vertArray.length; i++)
					vertArray[i] = vertexList.get(i);
				
				camera.drawShadow(vertArray);
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
