package grige;

import java.util.logging.Logger;

import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

public class PointLight extends Light
{
	private static final Logger log = Logger.getLogger(PointLight.class.getName());
	
	private final float[] defaultTextureCoords = {
			0.0f, 0.0f,
			0.0f, 1.0f,
			1.0f, 0.0f,
			1.0f, 1.0f,
	};
	
	public PointLight()
	{
		super();
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
	
	@Override
	protected void onDraw(GL2 gl, Camera cam)
	{
		if(shaderProgram == 0)
		{
			log.severe("Attempting to render a shaderless light. Skipping...");
			return;
		}
		
		//Compute the transformed light location (for lighting)
		Vector3 transformedLightLoc = cam.worldToScreenLoc(x(), y(), depth());

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
		
		int[] buffer = new int[2];
		gl.glGenBuffers(2, buffer, 0);
		
		int positionIndex = gl.glGetAttribLocation(shaderProgram, "position");
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, buffer[0]);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, lightVertices.length*(Float.SIZE/8), FloatBuffer.wrap(lightVertices), GL.GL_STATIC_DRAW);
		gl.glEnableVertexAttribArray(positionIndex);
		gl.glVertexAttribPointer(positionIndex, 3, GL.GL_FLOAT, false, 0, 0);
		
		int texCoordIndex = gl.glGetAttribLocation(shaderProgram, "texCoord");
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, buffer[1]);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, defaultTextureCoords.length*(Float.SIZE/8), FloatBuffer.wrap(defaultTextureCoords), GL.GL_STATIC_DRAW);
		gl.glEnableVertexAttribArray(texCoordIndex);
		gl.glVertexAttribPointer(texCoordIndex, 2, GL.GL_FLOAT, false, 0, 0);
		
		int lightLocIndex = gl.glGetUniformLocation(shaderProgram, "lightLoc");
		gl.glUniform3f(lightLocIndex, transformedLightLoc.x, transformedLightLoc.y, transformedLightLoc.z);
		
		int colourIndex = gl.glGetUniformLocation(shaderProgram, "lightColor");
		gl.glUniform4fv(colourIndex, 1, getColour().toFloat4Array(), 0);
		
		int falloffIndex = gl.glGetUniformLocation(shaderProgram, "falloff");
		gl.glUniform3f(falloffIndex, 0.4f, 3, 20);
		
		int normalSamplerIndex = gl.glGetUniformLocation(shaderProgram, "normalSampler");
		gl.glUniform1i(normalSamplerIndex, 1);
		
		int resolutionIndex = gl.glGetUniformLocation(shaderProgram, "resolution");
		gl.glUniform2f(resolutionIndex, cam.getWidth(), cam.getHeight());
		
		gl.glDrawArrays(GL.GL_TRIANGLE_STRIP, 0, 4);
		
		gl.glDisable(GL.GL_BLEND);
		
		gl.glBindVertexArray(0);
		gl.glUseProgram(0);
	}
	
	@Override
	protected void onDestroy()
	{
		
	}
}
