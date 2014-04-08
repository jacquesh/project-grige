package grige;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import java.util.logging.Logger;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.opengl.math.FloatUtil;

public abstract class GameObject extends Animatable
{
	private static final Logger log = Logger.getLogger(GameObject.class.getName());
	
	private final float[] quadVertices = {
			-0.5f, -0.5f, 0.0f,
			-0.5f, 0.5f, 0.0f,
			0.5f, -0.5f, 0.0f,
			0.5f,  0.5f, 0.0f,	
	};
	
	private final float[] defaultTextureCoords = {
			0.0f, 0.0f,
			0.0f, 1.0f,
			1.0f, 0.0f,
			1.0f, 1.0f,
	};
	
	private final int[] quadIndices = {
			0, 1, 2, 3,
	};
	
	//GL data
	private Material material;
	private int geometryVAO;
	private int shaderProgram;
	
	//GL buffers
	private int texCoordBuffer;
	
	public abstract void update(float deltaTime);
	
	public GameObject()
	{
		super();
	}
	
	public void setMaterial(Material newMaterial)
	{
		material = newMaterial;
	}
	protected Material getMaterial()
	{
		return material;
	}
	
	public boolean collidesWith(GameObject other)
	{
		//SAT - http://www.codezealot.org/archives/55
		Vector2[] axes = new Vector2[4];
		int axisCount = 2;
		
		axes[0] = new Vector2(1,0);
		axes[0].rotate(rotation);
		
		axes[1] = new Vector2(0,1);
		axes[1].rotate(rotation);
		
		if(FloatUtil.abs(other.rotation - rotation) > 1f)
		{
			axisCount = 4;
			axes[2] = new Vector2(1,0);
			axes[2].rotate(other.rotation);
			
			axes[3] = new Vector2(0,1);
			axes[3].rotate(other.rotation);
		}
		
		float[] verts = getVertices();
		float[] otherVerts = other.getVertices();
		
		for(int i=0; i<axisCount; i++)
		{
			float thisMin = 1000000;
			float thisMax = -1000000;
			float otherMin = 1000000;
			float otherMax = -1000000;
			
			for(int vert=0; vert<8; vert+=2)
			{
				float dot = verts[vert]*axes[i].x + verts[vert+1]*axes[i].y;
				float otherDot = otherVerts[vert]*axes[i].x + otherVerts[vert+1]*axes[i].y;
				
				if(dot < thisMin)
					thisMin = dot;
				if(dot > thisMax)
					thisMax = dot;
				
				if(otherDot < otherMin)
					otherMin = otherDot;
				if(otherDot > otherMax)
					otherMax = otherDot;
			}
			
			boolean intersects = (
					(thisMin < otherMin && thisMax > otherMin) ||
					(thisMin < otherMax && thisMax > otherMax) ||
					(thisMin > otherMin && thisMax < otherMax)
			);
			if(!intersects)
				return false;
		}
		
		return true;
	}
	
	public float width()
	{
		if(material == null)
			return 0;
		
		if(animation == null)
			return material.getWidth() * scale;
		
		return animation.getFrameBox(animationFrame).size.x * scale;
	}
	
	public float height()
	{
		if(material == null)
			return 0;
		
		if(animation == null)
			return material.getHeight() * scale;
		
		return animation.getFrameBox(animationFrame).size.y * scale;
	}
	
	@Override
	void internalUpdate(float deltaTime)
	{
		super.internalUpdate(deltaTime);
		
		update(deltaTime);
	}
	
	/*
	 * Generate an Axis-aligned boundingbox that entirely encompasses this object
	 */
	public AABB getAABB()
	{
		float w = width();
		float h = height();
		return new AABB(position.x-w/2f, position.y-h/2f, width(), height());
	}
	
	/*
	 * Return the co-ordinates of the vertices of this drawable object in Counterclockwise order;
	 * The order is important as it lets us construct geometry from these vertices without re-arranging anything
	 * 
	 * Primarily used for generating shadow geometry
	 */
	public float[] getVertices()
	{
		float[] result = new float[8];
		float halfWidth = width()/2f;
		float halfHeight = height()/2f;
		float rotationSin = FloatUtil.sin(FloatUtil.PI/180 * rotation);
		float rotationCos = FloatUtil.cos(FloatUtil.PI/180 * rotation);
		
		//Bottom Left
		result[0] = position.x + (-halfWidth*rotationCos + halfHeight*rotationSin);
		result[1] = position.y + (-halfWidth*rotationSin - halfHeight*rotationCos);
		
		//Bottom Right
		result[2] = position.x + (halfWidth*rotationCos + halfHeight*rotationSin);
		result[3] = position.y + (halfWidth*rotationSin - halfHeight*rotationCos);
		
		//Top Right
		result[4] = position.x + (halfWidth*rotationCos - halfHeight*rotationSin);
		result[5] = position.y + (halfWidth*rotationSin + halfHeight*rotationCos);
		
		//Top Left
		result[6] = position.x + (-halfWidth*rotationCos - halfHeight*rotationSin);
		result[7] = position.y + (-halfWidth*rotationSin + halfHeight*rotationCos);
		
		return result;
	}
	
	@Override
	public void setShader(GL2 gl, int shader)
	{
		shaderProgram = shader;
		gl.glUseProgram(shader);
		
		int[] buffers = new int[3];
		
		//Create the vertex array
		gl.glGenVertexArrays(1, buffers, 0);
		geometryVAO = buffers[0];
		gl.glBindVertexArray(geometryVAO);
		
		//Generate and store the required buffers
		gl.glGenBuffers(2, buffers,0);
		int indexBuffer = buffers[0];
		int vertexBuffer = buffers[1];
		
		//Generate and store the buffers that we may have generated previously
		if(texCoordBuffer == 0)
		{
			gl.glGenBuffers(1, buffers, 0);
			texCoordBuffer = buffers[0];
		}
		
		//Buffer the vertex indices
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
		gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, quadIndices.length*(Integer.SIZE/8), IntBuffer.wrap(quadIndices), GL.GL_STATIC_DRAW);
		
		//Buffer the vertex locations
		int positionIndex = gl.glGetAttribLocation(shaderProgram, "position");
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBuffer);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, quadVertices.length*(Float.SIZE/8), FloatBuffer.wrap(quadVertices), GL.GL_STATIC_DRAW);
		gl.glEnableVertexAttribArray(positionIndex);
		gl.glVertexAttribPointer(positionIndex, 3, GL.GL_FLOAT, false, 0, 0);
		
		gl.glBindVertexArray(0);
		gl.glUseProgram(0);
	}
	
	@Override
	protected void onDraw(GL2 gl, Camera cam)
	{
		if(shaderProgram == 0)
		{
			log.severe("Attempting to render a shaderless object. Skipping...");
			return;
		}
		
		//Compute the object transform matrix
		float objWidth = width();
		float objHeight = height();
		float rotationSin = FloatUtil.sin(FloatUtil.PI/180 * rotation);
		float rotationCos = FloatUtil.cos(FloatUtil.PI/180 * rotation);
		
		float[] objectTransformMatrix = new float[]{
				 objWidth*rotationCos, objHeight*rotationSin,0,0,
				-objWidth*rotationSin, objHeight*rotationCos,0,0,
				0,0,1,0,
				x(), y(), -depth(), 1
		};
		
		//Compute texture coordinates
		float[] textureCoords;
		
		if(animation == null)
			textureCoords = defaultTextureCoords;
		else
		{
			AABBI currentAnimQuad = animation.getFrameBox(animationFrame);
			float sizeX = material.getWidth();
			float sizeY = material.getHeight();
			
			//Here we need to transform from the coordinates of the image [(0,0) at top left, (x,y,w,h)], to that of opengl [(0,0) bottom left, (x,y) for each point]
			textureCoords = new float[]{
					currentAnimQuad.position.x/sizeX, 1-(currentAnimQuad.position.y + currentAnimQuad.size.y)/sizeY,
					currentAnimQuad.position.x/sizeX, 1-currentAnimQuad.position.y/sizeY,
					(currentAnimQuad.position.x + currentAnimQuad.size.x)/sizeX, 1-(currentAnimQuad.position.y + currentAnimQuad.size.y)/sizeY,
					(currentAnimQuad.position.x + currentAnimQuad.size.x)/sizeX, 1-currentAnimQuad.position.y/sizeY
			};
		}
		
		//Draw geometry
		if(getMaterial() == null)
			return;
		
		gl.glUseProgram(shaderProgram);
		gl.glBindVertexArray(geometryVAO);
		gl.glDepthMask(true);
		
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		
		//Texture coordinates
		int texCoordIndex = gl.glGetAttribLocation(shaderProgram, "texCoord");
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, texCoordBuffer);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, textureCoords.length*(Float.SIZE/8), FloatBuffer.wrap(textureCoords), GL.GL_STATIC_DRAW);
		gl.glEnableVertexAttribArray(texCoordIndex);
		gl.glVertexAttribPointer(texCoordIndex, 2, GL.GL_FLOAT, false, 0, 0);
		
		//Transforms
		int projMatrixIndex = gl.glGetUniformLocation(shaderProgram, "projectionMatrix");
		gl.glUniformMatrix4fv(projMatrixIndex, 1, false, cam.getProjectionMatrix(), 0);
		
		int viewMatrixIndex = gl.glGetUniformLocation(shaderProgram, "viewingMatrix");
		gl.glUniformMatrix4fv(viewMatrixIndex, 1, false, cam.getViewingMatrix(), 0);
		
		int geometryObjTransformIndex = gl.glGetUniformLocation(shaderProgram, "objectTransform");
		gl.glUniformMatrix4fv(geometryObjTransformIndex, 1, false, objectTransformMatrix, 0);
		
		//Texture specification
		//Diffuse Texture
		int textureSamplerIndex = gl.glGetUniformLocation(shaderProgram, "textureUnit");
		gl.glUniform1i(textureSamplerIndex, 0);
		
		int objTex = getMaterial().getDiffuseMap();
		gl.glActiveTexture(GL.GL_TEXTURE0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, objTex);
		
		//Normal Texture
		int normalSamplerIndex = gl.glGetUniformLocation(shaderProgram, "normalUnit");
		int objNormal = getMaterial().getNormalMap();
		if(objNormal != 0)
		{
			gl.glUniform1i(normalSamplerIndex, 1);
			
			gl.glActiveTexture(GL.GL_TEXTURE1);
			gl.glBindTexture(GL.GL_TEXTURE_2D, objNormal);
		}
		else
			gl.glUniform1i(normalSamplerIndex, -1);
		
		gl.glDrawElements(GL.GL_TRIANGLE_STRIP, quadIndices.length, GL.GL_UNSIGNED_INT, 0);
		
		gl.glDisable(GL.GL_BLEND);
		
		gl.glDepthMask(false);
		gl.glBindVertexArray(0);
		gl.glUseProgram(0);
	}
	
	protected void onDrawToLighting(GL2 gl, Camera cam)
	{
		if(shaderProgram == 0)
		{
			log.severe("Attempting to render a shaderless object. Skipping...");
			return;
		}
		
		//Compute the object transform matrix
		float objWidth = width();
		float objHeight = height();
		float rotationSin = FloatUtil.sin(FloatUtil.PI/180 * rotation);
		float rotationCos = FloatUtil.cos(FloatUtil.PI/180 * rotation);
		
		float[] objectTransformMatrix = new float[]{
				 objWidth*rotationCos, objHeight*rotationSin,0,0,
				-objWidth*rotationSin, objHeight*rotationCos,0,0,
				0,0,1,0,
				x(), y(), -depth(), 1
		};
		
		//Draw geometry
		if(getMaterial() == null)
			return;
		
		gl.glUseProgram(shaderProgram);
		gl.glBindVertexArray(geometryVAO);
		gl.glDepthMask(true);
		
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		
		//Transforms
		int projMatrixIndex = gl.glGetUniformLocation(shaderProgram, "projectionMatrix");
		gl.glUniformMatrix4fv(projMatrixIndex, 1, false, cam.getProjectionMatrix(), 0);
		
		int viewMatrixIndex = gl.glGetUniformLocation(shaderProgram, "viewingMatrix");
		gl.glUniformMatrix4fv(viewMatrixIndex, 1, false, cam.getViewingMatrix(), 0);
		
		int geometryObjTransformIndex = gl.glGetUniformLocation(shaderProgram, "objectTransform");
		gl.glUniformMatrix4fv(geometryObjTransformIndex, 1, false, objectTransformMatrix, 0);
		
		//Texture specification
		int objTex = getMaterial().getSelfIlluminationMap();
		if(objTex > 0)
		{
			int textureSamplerIndex = gl.glGetUniformLocation(shaderProgram, "textureUnit");
			gl.glUniform1i(textureSamplerIndex, 0);
			gl.glActiveTexture(GL.GL_TEXTURE0);
			gl.glBindTexture(GL.GL_TEXTURE_2D, objTex);
			
			//Texture coordinates
			int texCoordIndex = gl.glGetAttribLocation(shaderProgram, "texCoord");
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, texCoordBuffer);
			gl.glBufferData(GL.GL_ARRAY_BUFFER, defaultTextureCoords.length*(Float.SIZE/8), FloatBuffer.wrap(defaultTextureCoords), GL.GL_STATIC_DRAW);
			gl.glEnableVertexAttribArray(texCoordIndex);
			gl.glVertexAttribPointer(texCoordIndex, 2, GL.GL_FLOAT, false, 0, 0);
			
			gl.glDrawElements(GL.GL_TRIANGLE_STRIP, quadIndices.length, GL.GL_UNSIGNED_INT, 0);
			
			gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
		}
		else
		{
			gl.glColorMask(false, false, false, false);
			gl.glDrawElements(GL.GL_TRIANGLE_STRIP, quadIndices.length, GL.GL_UNSIGNED_INT, 0);
			gl.glColorMask(false, false, false, true);
		}
		
		gl.glDisable(GL.GL_BLEND);
		
		gl.glDepthMask(false);
		gl.glBindVertexArray(0);
		gl.glUseProgram(0);
	}
	
	@Override
	protected void onDestroy()
	{
		
	}
}
