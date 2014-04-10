package grige;

import java.util.logging.Logger;

import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.opengl.math.FloatUtil;

public class SpotLight extends Light
{
	private static final Logger log = Logger.getLogger(PointLight.class.getName());
	
	private int positionBuffer;
	
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
	
	public float getSpotAngle()
	{
		return spotAngle;
	}
	
	public void setSpotAngle(float angle)
	{
		if(angle > 360)
			angle = 360;
		else if(angle < 0)
			angle = 0;
		spotAngle = angle;
	}
	
	@Override 
	public void setShader(GL2 gl, int shader)
	{
		super.setShader(gl, shader);
		
		//Check if we've run this initialization before, if we have then dont generate obejcts
		if(positionBuffer == 0)
		{
			int[] buffers = new int[1];
			gl.glGenBuffers(1, buffers, 0);
			
			positionBuffer = buffers[0];
		}
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

		Vector2 lightEdge1 = new Vector2(1, 0);
		Vector2 lightEdge2 = new Vector2(1, 0);
		lightEdge1.rotate(spotAngle/2);
		lightEdge2.rotate(-spotAngle/2);
		
		//Compute the object transform matrix
		float rotationSin = FloatUtil.sin(FloatUtil.PI/180 * rotation);
		float rotationCos = FloatUtil.cos(FloatUtil.PI/180 * rotation);
		
		float[] objectTransformMatrix = new float[]{
				 rotationCos, rotationSin,0,0,
				-rotationSin, rotationCos,0,0,
				0,0,1,0,
				x(), y(), -depth(), 1
		};
		
		float[] lightVertices = {
				0, 0, -depth(),
				1000*lightEdge1.x, 1000*lightEdge1.y, -depth(),
				1000*lightEdge2.x, 1000*lightEdge2.y, -depth(),
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
		
		int lightLocIndex = gl.glGetUniformLocation(shaderProgram, "lightLoc");
		gl.glUniform3f(lightLocIndex, transformedLightLoc.x, transformedLightLoc.y, transformedLightLoc.z);
		
		int colourIndex = gl.glGetUniformLocation(shaderProgram, "lightColor");
		gl.glUniform4fv(colourIndex, 1, getColour().toFloat4Array(), 0);
		
		int falloffIndex = gl.glGetUniformLocation(shaderProgram, "falloff");
		gl.glUniform3f(falloffIndex, 0.4f, 3, 20);
		
		int objectTransformIndex = gl.glGetUniformLocation(shaderProgram, "objectTransform");
		gl.glUniformMatrix4fv(objectTransformIndex, 1, false, objectTransformMatrix, 0);
		
		int viewMatrixIndex = gl.glGetUniformLocation(shaderProgram, "viewingMatrix");
		gl.glUniformMatrix4fv(viewMatrixIndex, 1, false, cam.getViewingMatrix(), 0);
		
		int projMatrixIndex = gl.glGetUniformLocation(shaderProgram, "projectionMatrix");
		gl.glUniformMatrix4fv(projMatrixIndex, 1, false, cam.getProjectionMatrix(), 0);
		
		int normalIndex = gl.glGetUniformLocation(shaderProgram, "normalSampler");
		gl.glUniform1i(normalIndex, 1);
		
		int resolutionIndex = gl.glGetUniformLocation(shaderProgram, "resolution");
		gl.glUniform2f(resolutionIndex, cam.getWidth(), cam.getHeight());
		
		gl.glDrawArrays(GL.GL_TRIANGLES, 0, 3);
		
		gl.glDisable(GL.GL_BLEND);
		
		gl.glBindVertexArray(0);
		gl.glUseProgram(0);
	}
	
	@Override
	protected void onDestroy(GL2 gl)
	{
		gl.glDeleteBuffers(1, new int[]{positionBuffer}, 0);
		super.onDestroy(gl);
	}
}
