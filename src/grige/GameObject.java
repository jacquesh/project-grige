package grige;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.util.glsl.ShaderProgram;
import com.jogamp.opengl.util.texture.Texture;

public abstract class GameObject extends Drawable
{
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
	
	private final float[] quadTintColours = {
			1f, 1f, 1f, 1f,
			1f, 1f, 1f, 1f,
			1f, 1f, 1f, 1f,
			1f, 1f, 1f, 1f,
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
	
	//Animation data
	private Animation animation;
	private float timeSinceAnimationUpdate;
	private float animationPlaySpeed;
	private int animationPlayMode;
	private int animationFrame;
	
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
	
	public void setAnimation(Animation newAnimation)
	{
		animation = newAnimation;
		animationFrame = 0;
		animationPlaySpeed = 0;
	}
	
	public void setAnimationSpeed(float playSpeed)
	{
		animationPlaySpeed = playSpeed;
	}
	public void playAnimation(int playMode, float playSpeed)
	{
		animationPlayMode = playMode;
		animationPlaySpeed = playSpeed;
		timeSinceAnimationUpdate = 0;
	}
	public void playAnimation()
	{
		playAnimation(Animation.PLAY_MODE_LOOP, 1);
	}
	public void playAnimation(int playMode)
	{
		playAnimation(playMode, 1);
	}
	public void playAnimationBackwards(int playMode)
	{
		playAnimation(playMode, -1);
	}
	public void pauseAnimation()
	{
		animationPlaySpeed = 0;
	}
	public void stopAnimation()
	{
		if(animationPlaySpeed >= 0)
			animationFrame = 0;
		else if(animationPlaySpeed < 0)
			animationFrame = animation.length()-1;
		
		animationPlaySpeed = 0;
	}
	
	public void setAnimationFrame(int frame)
	{
		animationFrame = frame;
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
	
	protected void internalUpdate(float deltaTime)
	{
		if(animation != null && animationPlaySpeed != 0)
		{
			timeSinceAnimationUpdate += deltaTime;
			int frameIncrement = (int)(timeSinceAnimationUpdate*animationPlaySpeed);
			int newFrame = animationFrame + frameIncrement;
			if(newFrame < 0)
			{
				if(animationPlayMode == Animation.PLAY_MODE_LOOP)
					newFrame += animation.length();
				else if(animationPlayMode == Animation.PLAY_MODE_PINGPONG)
				{
					newFrame = -newFrame;
					setAnimationSpeed(-animationPlaySpeed);
				}
				else if(animationPlayMode == Animation.PLAY_MODE_ONCE)
				{
					stopAnimation();
					newFrame = animationFrame;
				}
			}
			else if(newFrame >= animation.length())
			{
				if(animationPlayMode == Animation.PLAY_MODE_LOOP)
					newFrame = newFrame%animation.length();
				else if(animationPlayMode == Animation.PLAY_MODE_PINGPONG)
				{
					newFrame = animation.length() - (newFrame%(animation.length()-1));
					setAnimationSpeed(-animationPlaySpeed);
				}
				else if(animationPlayMode == Animation.PLAY_MODE_ONCE)
				{
					stopAnimation();
					newFrame = animationFrame;
				}
			}
			
			timeSinceAnimationUpdate -= frameIncrement/animationPlaySpeed;
			setAnimationFrame(newFrame);
		}
		
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
		
		int[] buffers = new int[4];
		
		//Create the vertex array
		gl.glGenVertexArrays(1, buffers, 0);
		geometryVAO = buffers[0];
		gl.glBindVertexArray(geometryVAO);
		
		//Generate and store the required buffers
		gl.glGenBuffers(3, buffers,0);
		int indexBuffer = buffers[0];
		int vertexBuffer = buffers[1];
		int colourBuffer = buffers[2];
		
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
		
		//Buffer the tint colour
		int colourIndex = gl.glGetAttribLocation(shaderProgram, "tintColour");
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, colourBuffer);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, quadTintColours.length*(Float.SIZE/8), FloatBuffer.wrap(quadTintColours), GL.GL_STATIC_DRAW);
		gl.glEnableVertexAttribArray(colourIndex);
		gl.glVertexAttribPointer(colourIndex, 4, GL.GL_FLOAT, false, 0, 0);
		
		gl.glBindVertexArray(0);
		
		gl.glUseProgram(0);
	}
	
	@Override
	protected void onDraw(GL2 gl, Camera cam)
	{
		if(shaderProgram == 0)
		{
			Log.fatal("Attempting to render a shaderless object. Skipping...");
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
		gl.glActiveTexture(GL.GL_TEXTURE0);
		gl.glBindVertexArray(geometryVAO);
		gl.glDepthMask(true);
		
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		
		//Texture specification
		int textureSamplerIndex = gl.glGetUniformLocation(shaderProgram, "textureUnit");
		gl.glUniform1i(textureSamplerIndex, 0);
		
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
		
		int objTex = getMaterial().getDiffuseMap();
		gl.glEnable(objTex);
		gl.glBindTexture(GL.GL_TEXTURE_2D, objTex);

		gl.glDrawElements(GL.GL_TRIANGLE_STRIP, quadIndices.length, GL.GL_UNSIGNED_INT, 0);
		
		gl.glDisable(objTex);
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
		
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
