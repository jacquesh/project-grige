package grige;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.opengl.FBObject;

import com.jogamp.opengl.FBObject.TextureAttachment;
import com.jogamp.opengl.math.FloatUtil;

import java.awt.Font;
import com.jogamp.opengl.util.awt.TextRenderer;

public class Camera {	
	
	private final float[] screenCanvasVertices = {
			-1.0f, -1.0f, 0.0f,
			-1.0f, 1.0f, 0.0f,
			1.0f, -1.0f, 0.0f,
			1.0f,  1.0f, 0.0f,
	};
	
	private final float[] screenCanvasTextureCoords = {
			0.0f, 0.0f,
			0.0f, 1.0f,
			1.0f, 0.0f,
			1.0f, 1.0f,
	};
	
	private final int[] screenCanvasIndices = {
			0, 1, 2, 3,
	};
	
	//Camera attributes
	private Vector2 position;
	private Vector2I size;
	private float depth;
	
	private Color clearColour;
	private Color ambientLight;
	
	//Current transformation matrices
	private float[] projectionMatrix;
	private float[] viewingMatrix;
	
	//Vertex Buffers
	private int screenCanvasVAO;
	private int shadowVertexBuffer;
	
	//Frame Buffers
	private FBObject geometryFBO;
	private FBObject lightingFBO;
	private FBObject interfaceFBO;
	
	//Shader Data
	private int screenCanvasShader;
	private int shadowGeometryShader;
	
	//Text Rendering Data
	private HashMap<Font, TextRenderer> textFonts;
	
	//GL context
	private GL2 gl;
	
	public Camera(int startWidth, int startHeight, int startDepth)
	{
		position = new Vector2(0, 0);
		size = new Vector2I(startWidth, startHeight);
		depth = startDepth;
		
		ambientLight = new Color(0,0,0,0);
		clearColour = new Color(0,0,0,1);
	}
	
	public void setPosition(float newX, float newY)
	{
		position.x = newX;
		position.y = newY;
		
		viewingMatrix = new float[]{
				1,0,0,0,
				0,1,0,0,
				0,0,1,0,
				-position.x-size.x/2f,-position.y-size.y/2f,0,1f
		};
	}
	
	public void setPosition(Vector2 newPosition)
	{
		setPosition(newPosition.x, newPosition.y);
	}
	
	public void setSize(int newWidth, int newHeight, float newDepth)
	{
		size.x = newWidth;
		size.y = newHeight;
		depth = newDepth;
		
		projectionMatrix = new float[]{
				2f/size.x,0,0,0,
				0,2f/size.y,0,0,
				0,0,-2f/depth,0,
				0,0,-1,1
		};
		setPosition(position.x,position.y); //Update the viewing matrix as well, because the size has changed (so we need to translate (0,0) differently)
	}
	
	public void setAmbientLight(float r, float g, float b, float a) { ambientLight = new Color(r, g, b, a); }
	public void setClearColor(float r, float g, float b){ clearColour = new Color(r, g, b, 1); }
	
	public Vector2 getBottomLeft() { return new Vector2(position.x, position.y); }
	public Vector2 getTopLeft() { return new Vector2(position.x, position.y + size.y); }
	public Vector2 getTopRight() { return new Vector2(position.x + size.x, position.y + size.y); }
	public Vector2 getBottomRight() { return new Vector2(position.x + size.x, position.y); }
	public float getX() { return position.x; }
	public float getY() { return position.y; }
	public float getWidth() { return size.x; }
	public float getHeight() { return size.y; }
	public float getDepth() { return depth; }
	public Color getAmbientLight() { return ambientLight; }
	public Color getClearColor() { return clearColour; }
	public float[] getProjectionMatrix() { return projectionMatrix; }
	public float[] getViewingMatrix() { return viewingMatrix; }
	
	public Vector2 screenToWorldLoc(Vector2 screenLoc)
	{
		return new Vector2(screenToWorldLoc(screenLoc.x, screenLoc.y, 0));
	}
	
	public Vector2 screenToWorldLoc(float x, float y)
	{
		return new Vector2(screenToWorldLoc(x,y,0));
	}
	
	public Vector3 screenToWorldLoc(float x, float y, float z)
	{
		float[] screenLoc = new float[]{2*x/getWidth()-1, 2*y/getHeight()-1, 2*z/getDepth()-1, 1}; //Transform from pixel space to OpenGL screen space
		float[] worldLoc = new float[4];
		
		float[] inverseViewingMatrix = new float[]{
				1,0,0,0,
				0,1,0,0,
				0,0,1,0,
				position.x+size.x/2f,position.y+size.y/2f,0,1f
		};
		float[] inverseProjectionMatrix = new float[]{
				size.x/2f,0,0,0,
				0,size.y/2f,0,0,
				0,0,-depth/2f,0,
				0,0,-depth/2f,1
		};
		
		float[] transformMatrix = inverseViewingMatrix;
		FloatUtil.multMatrixf(transformMatrix, 0, inverseProjectionMatrix, 0);
		FloatUtil.multMatrixVecf(transformMatrix, screenLoc, worldLoc);
		
		Vector3 result = new Vector3(worldLoc[0], worldLoc[1], worldLoc[2]); //This is in OpenGL screen space, so we need to change it into pixel space
		
		return result;
	}
	
	public Vector2 worldToScreenLoc(Vector2 worldLoc)
	{
		return new Vector2(worldToScreenLoc(worldLoc.x, worldLoc.y, 0));
	}
	
	public Vector2 worldToScreenLoc(float x, float y)
	{
		return new Vector2(worldToScreenLoc(x,y,0));
	}
	
	public Vector3 worldToScreenLoc(float x, float y, float z)
	{
		float[] worldLoc = new float[]{x, y, z, 1};
		float[] screenLoc = new float[4];
		float[] transformMatrix = Arrays.copyOf(projectionMatrix, 16);
		FloatUtil.multMatrixf(transformMatrix, 0, viewingMatrix, 0);
		FloatUtil.multMatrixVecf(transformMatrix, worldLoc, screenLoc);
		
		Vector3 result = new Vector3(screenLoc[0], screenLoc[1], screenLoc[2]); //This is in OpenGL screen space, so we need to change it into pixel space
		
		result.x += 1;
		result.y += 1;
		result.z += 1;
		
		result.x *= getWidth()/2;
		result.y *= getHeight()/2;
		result.z *= getDepth()/2;
		
		return result;
	}
	
	protected void initialize(GL glContext)
	{
		gl = glContext.getGL2();
		
		//Set rendering properties
		gl.glDisable(GL.GL_CULL_FACE);
		gl.glEnable(GL.GL_STENCIL_TEST);
		
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);
		
		//Initialize the various components and data stores
		initializeGeometryData();
		initializeLightingData();
		initializeShadowData();
		initializeInterfaceData();
		initializeScreenCanvas();
		
		//Initialize and buffer the projection and viewing matrices
		setSize(size.x, size.y, depth);
	}
	
	private void initializeGeometryData()
	{
		//Create the framebuffer
		geometryFBO = new FBObject();
		geometryFBO.reset(gl, size.x, size.y);
		geometryFBO.attachTexture2D(gl, 0, true);
		geometryFBO.attachTexture2D(gl, 1, false);
		geometryFBO.attachTexture2D(gl, 2, true);
		geometryFBO.attachRenderbuffer(gl, FBObject.Attachment.Type.DEPTH, 6);
		
		//Specify all the render targets
		IntBuffer geometryRenderTargets = IntBuffer.wrap(new int[]{GL.GL_COLOR_ATTACHMENT0, GL.GL_COLOR_ATTACHMENT0+1, GL.GL_COLOR_ATTACHMENT0+2});
		gl.glDrawBuffers(3, geometryRenderTargets);
		geometryFBO.unbind(gl);
	}
	
	private void initializeLightingData()
	{
		//Create the framebuffer
		lightingFBO = new FBObject();
		lightingFBO.reset(gl, size.x, size.y);
		lightingFBO.attachTexture2D(gl, 0, true);
		lightingFBO.attachRenderbuffer(gl, FBObject.Attachment.Type.DEPTH_STENCIL, 6);
		lightingFBO.unbind(gl);
	}

	private void initializeShadowData()
	{
		//Load the shaders
		shadowGeometryShader = Graphics.loadShader(gl, "ShadowGeometry.vsh", "ShadowGeometry.fsh");
		
		int[] buffers = new int[1];
		
		gl.glGenBuffers(1, buffers, 0);
		shadowVertexBuffer = buffers[0];
	}
	
	private void initializeInterfaceData()
	{
		//Create the framebuffer
		interfaceFBO = new FBObject();
		interfaceFBO.reset(gl, size.x, size.y);
		interfaceFBO.attachTexture2D(gl, 0, true);
		interfaceFBO.unbind(gl);
		
		//Initialize Text Rendering data
		textFonts = new HashMap<Font, TextRenderer>();
	}
	
	private void initializeScreenCanvas()
	{
		//Load the shader
		screenCanvasShader = Graphics.loadShader(gl, "Canvas.vsh", "Canvas.fsh");
		gl.glUseProgram(screenCanvasShader);
		
		int[] buffers = new int[4];
		
		//Create the vertex array
		gl.glGenVertexArrays(1, buffers, 0);
		screenCanvasVAO = buffers[0];
		gl.glBindVertexArray(screenCanvasVAO);
		
		//Generate and store the required buffers
		gl.glGenBuffers(4, buffers,0); //Indices, VertexLocations, TextureCoordinates
		int indexBuffer = buffers[0];
		int vertexBuffer = buffers[1];
		int texCoordBuffer = buffers[2];
		
		//Buffer the vertex indices
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
		gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, screenCanvasIndices.length*(Integer.SIZE/8), IntBuffer.wrap(screenCanvasIndices), GL.GL_STATIC_DRAW);
		
		//Buffer the vertex locations
		int positionIndex = gl.glGetAttribLocation(screenCanvasShader, "position");
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBuffer);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, screenCanvasVertices.length*(Float.SIZE/8), FloatBuffer.wrap(screenCanvasVertices), GL.GL_STATIC_DRAW);
		gl.glEnableVertexAttribArray(positionIndex);
		gl.glVertexAttribPointer(positionIndex, 3, GL.GL_FLOAT, false, 0, 0);
		
		//Buffer the vertex texture coordinates
		int texCoordIndex = gl.glGetAttribLocation(screenCanvasShader, "texCoord");
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, texCoordBuffer);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, screenCanvasTextureCoords.length*(Float.SIZE/8), FloatBuffer.wrap(screenCanvasTextureCoords), GL.GL_STATIC_DRAW);
		gl.glEnableVertexAttribArray(texCoordIndex);
		gl.glVertexAttribPointer(texCoordIndex, 2, GL.GL_FLOAT, false, 0, 0);
		
		//Buffer input textures
		int geometryTextureSamplerIndex = gl.glGetUniformLocation(screenCanvasShader, "geometryTextureUnit");
		gl.glUniform1i(geometryTextureSamplerIndex, 0);
		int lightingTextureSamplerIndex = gl.glGetUniformLocation(screenCanvasShader, "lightingTextureUnit");
		gl.glUniform1i(lightingTextureSamplerIndex, 1);
		int selfIlluTextureSamplerIndex = gl.glGetUniformLocation(screenCanvasShader, "selfIlluTextureUnit");
		gl.glUniform1i(selfIlluTextureSamplerIndex, 2);
		int interfaceTextureSamplerIndex = gl.glGetUniformLocation(screenCanvasShader, "interfaceTextureUnit");
		gl.glUniform1i(interfaceTextureSamplerIndex, 3);
		
		gl.glBindVertexArray(0);
		gl.glUseProgram(0);
	}
	
	private void rebufferViewingMatrix()
	{
		int viewMatrixIndex;
		
		//Shadow Geometry Shader
		gl.glUseProgram(shadowGeometryShader);
		viewMatrixIndex = gl.glGetUniformLocation(shadowGeometryShader, "viewingMatrix");
		gl.glUniformMatrix4fv(viewMatrixIndex, 1, false, viewingMatrix, 0);
		gl.glUseProgram(0);
	}
	
	private void rebufferProjectionMatrix()
	{
		int projMatrixIndex;
		
		//Shadow Geometry shader
		gl.glUseProgram(shadowGeometryShader);
		projMatrixIndex = gl.glGetUniformLocation(shadowGeometryShader, "projectionMatrix");
		gl.glUniformMatrix4fv(projMatrixIndex, 1, false, projectionMatrix, 0);
		gl.glUseProgram(0);
	}
	
	protected void refresh(GL glContext)
	{
		gl = glContext.getGL2();
		gl.glDepthMask(true);
		
		//Clear the screen
		gl.glClearColor(0, 0, 0, 1);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
		//Clear the geometry buffer
		geometryFBO.bind(gl);
		gl.glClearColor(clearColour.getRed(), clearColour.getGreen(), clearColour.getBlue(), clearColour.getAlpha());
		gl.glClearBufferfv(GL2.GL_COLOR, 0, new float[]{0, 0, 0, 1}, 0);
		gl.glClearBufferfv(GL2.GL_COLOR, 1, new float[]{0.5f, 0.5f, 1, 1}, 0);
		gl.glClearBufferfv(GL2.GL_COLOR, 2, new float[]{0, 0, 0, 0}, 0);
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
		geometryFBO.unbind(gl);
		
		//Clear the lighting buffer
		lightingFBO.bind(gl);
		gl.glClearColor(0, 0, 0, 1);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);
		lightingFBO.unbind(gl);
		
		//Clear the interface buffer
		interfaceFBO.bind(gl);
		gl.glClearColor(0, 0, 0, 0);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		interfaceFBO.unbind(gl);
		
		rebufferViewingMatrix();
		rebufferProjectionMatrix();
		
		gl.glDepthMask(false);
	}
	
	public void drawGeometryStart() { geometryFBO.bind(gl); }
	public void drawGeometryEnd() { geometryFBO.unbind(gl); }
	public void drawLightingStart()
	{
		lightingFBO.bind(gl);
		
		TextureAttachment normalTexture = (TextureAttachment)geometryFBO.getColorbuffer(1);
		gl.glActiveTexture(GL.GL_TEXTURE1);
		gl.glBindTexture(GL.GL_TEXTURE_2D, normalTexture.getName());
	}
	public void drawLightingEnd() { lightingFBO.unbind(gl); }
	public void drawInterfaceStart() { interfaceFBO.bind(gl); }
	public void drawInterfaceEnd() { interfaceFBO.unbind(gl); }
	
	private void drawShadow(float[] shadowVerts)
	{
		gl.glUseProgram(shadowGeometryShader);
		
		int positionIndex = gl.glGetAttribLocation(shadowGeometryShader, "position");
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, shadowVertexBuffer);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, shadowVerts.length*(Float.SIZE/8), FloatBuffer.wrap(shadowVerts), GL.GL_DYNAMIC_DRAW);
		gl.glEnableVertexAttribArray(positionIndex);
		gl.glVertexAttribPointer(positionIndex, 3, GL.GL_FLOAT, false, 0, 0);
		
		gl.glDrawArrays(GL.GL_TRIANGLE_STRIP, 0, shadowVerts.length/3);
		gl.glDisableVertexAttribArray(positionIndex);
		
		gl.glUseProgram(0);
	}
	
	protected void drawShadowsToStencil(ArrayList<float[]> vertexArrays)
	{
		//Set to draw only to the stencil buffer (no colour/alpha)
		gl.glColorMask(false, false, false, false);
		gl.glStencilFunc(GL.GL_ALWAYS, 1, 1);
		gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_REPLACE);
		
		//Render all shadows from this light into the stencil buffer
		//This is so that when we render the actual light, it doesn't light up the shadows
		for(int i=0; i<vertexArrays.size(); i++)
			drawShadow(vertexArrays.get(i));
		
		//Reset drawing to standard colour/alpha
		gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);
		gl.glStencilFunc(GL.GL_EQUAL, 0, 1);
		gl.glColorMask(true, true, true, true);
	}
	
	
	public void drawText(Font font, String str, float x, float y) { drawText(font, str, x, y, 0, new Color(1,1,1,1), 1, false); }
	
	public void drawText(Font font, String str, float x, float y, float z, Color color, float scale, boolean worldSpace)
	{
		//Only create a new TextRenderer if we dont already have one for this font
		TextRenderer renderer = null;
		if(textFonts.containsKey(font))
			renderer = textFonts.get(font);
		else
		{
			renderer = new TextRenderer(font, true, false);
			textFonts.put(font, renderer);
		}
		
		//Transform the given location into world co-ordinates
		float[] worldLoc = new float[]{x, y, -z, 1};
		float[] screenLoc = new float[4];
		float[] transformMatrix = Arrays.copyOf(projectionMatrix, 16);
		
		if(worldSpace)
		{	//If we want the text rendererd in worldspace then multiply by the viewing transform
			FloatUtil.multMatrixf(transformMatrix, 0, viewingMatrix, 0);
			FloatUtil.multMatrixVecf(transformMatrix, worldLoc, screenLoc);
		}
		else
		{	//Otherwise just transform the point and subtract 1 from x & y to move the origin to the bottom left
			FloatUtil.multMatrixVecf(transformMatrix, worldLoc, screenLoc);
			screenLoc[0] -= 1;
			screenLoc[1] -= 1;
		}
		
		gl.glActiveTexture(GL.GL_TEXTURE0); //Bind a texture so that the renderer know what its rendering to
		renderer.begin3DRendering();
		renderer.setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
		
		renderer.draw3D(str, screenLoc[0], screenLoc[1], screenLoc[2], 2*scale/size.x);
		renderer.end3DRendering();
	}
	
	protected void commitDraw()
	{
		gl.glUseProgram(screenCanvasShader);
		gl.glBindVertexArray(screenCanvasVAO);
		
		//Bind the different buffer textures to the relevant GL textures so we can use it in our canvas shader
		gl.glActiveTexture(GL.GL_TEXTURE0);
		TextureAttachment geometryTexture = (TextureAttachment)geometryFBO.getColorbuffer(0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, geometryTexture.getName());
		
		gl.glActiveTexture(GL.GL_TEXTURE1);
		TextureAttachment lightingTexture = (TextureAttachment)lightingFBO.getColorbuffer(0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, lightingTexture.getName());
		
		gl.glActiveTexture(GL.GL_TEXTURE2);
		TextureAttachment selfIlluTexture = (TextureAttachment)geometryFBO.getColorbuffer(2);
		gl.glBindTexture(GL.GL_TEXTURE_2D, selfIlluTexture.getName());
		
		gl.glActiveTexture(GL.GL_TEXTURE3);
		TextureAttachment interfaceTexture = (TextureAttachment)interfaceFBO.getColorbuffer(0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, interfaceTexture.getName());
		
		int ambientLightIndex = gl.glGetUniformLocation(screenCanvasShader, "ambientLight");
		gl.glUniform4fv(ambientLightIndex, 1, ambientLight.toFloat4Array(), 0);
		
		gl.glDrawElements(GL.GL_TRIANGLE_STRIP, screenCanvasIndices.length, GL.GL_UNSIGNED_INT, 0);
		
		gl.glBindVertexArray(0);
		gl.glUseProgram(0);
	}
}
