package grige;

import java.util.logging.Logger;

import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

public class DirectionalLight extends Light
{
	private static final Logger log = Logger.getLogger(DirectionalLight.class.getName());
	
	private final float[] defaultTextureCoords = {
			0.0f, 0.0f,
			0.0f, 1.0f,
			1.0f, 0.0f,
			1.0f, 1.0f,
	};
	
	private int positionBuffer;
	private int texCoordBuffer;
	
	private Vector2 direction;
	
	public DirectionalLight(Vector2 startDirection)
	{
		super();
		setDirection(startDirection);
		setDepth(1);
	}
	
	public float width()
	{
		return 0;
	}
	public float height()
	{
		return 0;
	}
	
	public Vector2 getDirection()
	{
		return direction;
	}
	public void setDirection(Vector2 newDirection)
	{
		direction = newDirection;
		direction.normalise();
	}
	
	@Override 
	public void setShader(GL2 gl, int shader)
	{
		super.setShader(gl, shader);
		
		int[] buffers = new int[2];
		gl.glGenBuffers(2, buffers, 0);
		
		positionBuffer = buffers[0];
		texCoordBuffer = buffers[1];
		
		int texCoordIndex = gl.glGetAttribLocation(shaderProgram, "texCoord");
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, texCoordBuffer);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, defaultTextureCoords.length*(Float.SIZE/8), FloatBuffer.wrap(defaultTextureCoords), GL.GL_STATIC_DRAW);
		gl.glEnableVertexAttribArray(texCoordIndex);
		gl.glVertexAttribPointer(texCoordIndex, 2, GL.GL_FLOAT, false, 0, 0);
	}
	
	@Override
	protected void onDraw(GL2 gl, Camera cam)
	{
		if(shaderProgram == 0)
		{
			log.severe("Attempting to render a shaderless light. Skipping...");
			return;
		}
		
		float[] lightVertices = {
				-1.0f, -1.0f, -depth(),
				-1.0f, 1.0f, -depth(),
				1.0f, -1.0f, -depth(),
				1.0f,  1.0f, -depth(),
		};
		
		//Draw the light
		gl.glUseProgram(shaderProgram);
		gl.glBindVertexArray(lightingVAO);
		
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE);
		
		int positionIndex = gl.glGetAttribLocation(shaderProgram, "position");
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, positionBuffer);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, lightVertices.length*(Float.SIZE/8), FloatBuffer.wrap(lightVertices), GL.GL_STATIC_DRAW);
		gl.glEnableVertexAttribArray(positionIndex);
		gl.glVertexAttribPointer(positionIndex, 3, GL.GL_FLOAT, false, 0, 0);
		
		int colourIndex = gl.glGetUniformLocation(shaderProgram, "lightColor");
		gl.glUniform4fv(colourIndex, 1, getColour().toFloat4Array(), 0);
		
		int normalSamplerIndex = gl.glGetUniformLocation(shaderProgram, "normalSampler");
		gl.glUniform1i(normalSamplerIndex, 1);
		
		gl.glDrawArrays(GL.GL_TRIANGLE_STRIP, 0, 4);
		
		gl.glDisable(GL.GL_BLEND);
		
		gl.glBindVertexArray(0);
		gl.glUseProgram(0);
	}
	
	@Override
	protected void onDestroy()
	{
		
	}
	
	@Override
	protected void getLightOffsetDir(Vector2 vertex, Vector2 result)
	{
		result.x = -direction.x;
		result.y = -direction.y;
	}
}
