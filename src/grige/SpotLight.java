package grige;

import java.util.logging.Logger;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.opengl.math.FloatUtil;

public class SpotLight extends Light
{
	private static final Logger log = Logger.getLogger(PointLight.class.getName());
	
	private final float[] screenCanvasVertices = {
			-1.0f, -1.0f, 0.0f,
			-1.0f, 1.0f, 0.0f,
			1.0f, -1.0f, 0.0f,
			1.0f,  1.0f, 0.0f,
	};
	
	private final int[] screenCanvasIndices = {
			0, 1, 2, 3,
	};
	
	private int shaderProgram;
	private int lightingVAO;
	
	private float spotAngle;
	
	public SpotLight(float angle)
	{
		super();
		setSpotAngle(angle);
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
	
	public void setSpotAngle(float angle)
	{
		spotAngle = angle*FloatUtil.PI/180;
	}
	
	@Override
	public void setShader(GL2 gl, int shader)
	{
		shaderProgram = shader;
		
		//Load and bind the shader
		gl.glUseProgram(shaderProgram);
		
		int[] buffers = new int[2];
		//Create the vertex array
		gl.glGenVertexArrays(1, buffers, 0);
		lightingVAO = buffers[0];
		gl.glBindVertexArray(lightingVAO);
		
		gl.glGenBuffers(2, buffers ,0);
		int indexBuffer = buffers[0];
		int vertexBuffer = buffers[1];
		
		//Buffer the vertex indices
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
		gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, screenCanvasIndices.length*(Integer.SIZE/8), IntBuffer.wrap(screenCanvasIndices), GL.GL_STATIC_DRAW);
		
		//Buffer the vertex locations
		int positionIndex = gl.glGetAttribLocation(shaderProgram, "position");
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBuffer);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, screenCanvasVertices.length*(Float.SIZE/8), FloatBuffer.wrap(screenCanvasVertices), GL.GL_STATIC_DRAW);
		gl.glEnableVertexAttribArray(positionIndex);
		gl.glVertexAttribPointer(positionIndex, 3, GL.GL_FLOAT, false, 0, 0);
		
		gl.glBindVertexArray(0);
		gl.glUseProgram(shaderProgram);
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
		
		int[] buffer = new int[1];
		gl.glGenBuffers(1, buffer, 0);
		
		int positionIndex = gl.glGetAttribLocation(shaderProgram, "position");
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, buffer[0]);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, lightVertices.length*(Float.SIZE/8), FloatBuffer.wrap(lightVertices), GL.GL_STATIC_DRAW);
		gl.glEnableVertexAttribArray(positionIndex);
		gl.glVertexAttribPointer(positionIndex, 3, GL.GL_FLOAT, false, 0, 0);
		
		int lightLocIndex = gl.glGetUniformLocation(shaderProgram, "lightLoc");
		gl.glUniform3f(lightLocIndex, transformedLightLoc.x, transformedLightLoc.y, transformedLightLoc.z);
		
		int lightRotationIndex = gl.glGetUniformLocation(shaderProgram, "rotation");
		gl.glUniform1f(lightRotationIndex, rotation());
		
		int spotAngleIndex = gl.glGetUniformLocation(shaderProgram, "spotAngle");
		gl.glUniform1f(spotAngleIndex, spotAngle);
		
		int radiusIndex = gl.glGetUniformLocation(shaderProgram, "radius");
		gl.glUniform1f(radiusIndex, getRadius());
		
		int colourIndex = gl.glGetUniformLocation(shaderProgram, "lightColour");
		gl.glUniform3fv(colourIndex, 1, getColour().toFloat3Array(), 0);
		
		int intensityIndex = gl.glGetUniformLocation(shaderProgram, "intensity");
		gl.glUniform1f(intensityIndex, getIntensity());
		
		int viewMatrixIndex = gl.glGetUniformLocation(shaderProgram, "viewingMatrix");
		gl.glUniformMatrix4fv(viewMatrixIndex, 1, false, cam.getViewingMatrix(), 0);
		
		int projMatrixIndex = gl.glGetUniformLocation(shaderProgram, "projectionMatrix");
		gl.glUniformMatrix4fv(projMatrixIndex, 1, false, cam.getProjectionMatrix(), 0);
		
		gl.glDrawElements(GL.GL_TRIANGLE_STRIP, screenCanvasIndices.length, GL.GL_UNSIGNED_INT, 0);
		
		gl.glDisable(GL.GL_BLEND);
		
		gl.glBindVertexArray(0);
		gl.glUseProgram(0);
	}
	
	@Override
	protected void onDestroy()
	{
		
	}
}
