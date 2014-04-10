package grige;

import java.util.ArrayList;
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
	protected float[] generateShadowVertices(GameObject obj)
	{
		ArrayList<Float> vertexList = new ArrayList<Float>();
		int currentVertexIndex = 0;
		
		float[] vertices = obj.getVertices();
		
		Vector2 vertexNormal = new Vector2(0,0);
		Vector2 projectedLightVertex = new Vector2(0, 0);
		
		Vector2 currentVert = new Vector2(0,0);
		Vector2 currentLightOffsetDir = new Vector2(0,0);
		
		Vector2 previousVert = new Vector2(vertices[6], vertices[7]);
		Vector2 previousLightOffsetDir = new Vector2(position.x-previousVert.x, position.y-previousVert.y);
		
		for(int index=0; index<8; index+=2)
		{
			currentVert.x = vertices[index];
			currentVert.y = vertices[index+1];
			
			//This is the only difference between DirectionalLight.generateShadowVertices() and Light.generateShadowVertices()
			//All lights except directional ones use the position of the light to find out where the shadows must fall,
			//However Directional lights use only the light direction
			currentLightOffsetDir.x = -1f*direction.x;
			currentLightOffsetDir.y = -1f*direction.y;
			currentLightOffsetDir.normalise(); //We can normalise here because the magnitude has no effect on the sign of any dot products
			
			//Because we know we're traversing vertices in a counter-clockwise order
			//we know that the normal for an edge is (dy, -dx)
			vertexNormal.x = currentVert.y - previousVert.y;
			vertexNormal.y = -(currentVert.x - previousVert.x);
			
			//Check if the normal and the light are facing the same general direction
			float dotProduct = vertexNormal.x*currentLightOffsetDir.x + vertexNormal.y*currentLightOffsetDir.y; 
			if(dotProduct <= 0)
			{
				if(currentVertexIndex == -1 || index == 0) //If its the first index then we would have never had a chance to set currentVertexIndex to -1
				{ 	//If the current index has been set to -1 then we moved from light into shadow
					//so we need to add shadow for both the current and previous vertices
					currentVertexIndex = 0;
					vertexList.add(currentVertexIndex, previousVert.x);
					vertexList.add(currentVertexIndex+1, previousVert.y);
					vertexList.add(currentVertexIndex+2, -obj.depth()-0.5f); //Z-value for depth testing and for ease of use in vertex buffers, negative because we don't use object transform matrices for shadow geometry
																			 //We subtract 0.5 so that the shadow lies just below the current depth, this prevents odd effects (e.g when objects overlap) and I guess is more physically accurate
					
					projectedLightVertex.set(previousLightOffsetDir);
					projectedLightVertex.multiply(-1000); //lightOffset is the vector: "vertex -> light", we use (-) because we need to offset "light vertex ->"
					projectedLightVertex.add(previousVert);
					
					vertexList.add(currentVertexIndex+3, projectedLightVertex.x);
					vertexList.add(currentVertexIndex+4, projectedLightVertex.y);
					vertexList.add(currentVertexIndex+5, -obj.depth()-0.5f);
					
					currentVertexIndex += 6;
				}
				
				//Add the current vertex and its projection away from the light
				vertexList.add(currentVertexIndex, currentVert.x);
				vertexList.add(currentVertexIndex+1, currentVert.y);
				vertexList.add(currentVertexIndex+2, -obj.depth()-0.5f);
				
				projectedLightVertex.set(currentLightOffsetDir);
				projectedLightVertex.multiply(-1000); //lightOffset is the vector: "vertex -> light", we use (-) because we need to offset "light vertex ->"
				projectedLightVertex.add(currentVert);
				
				vertexList.add(currentVertexIndex+3, projectedLightVertex.x);
				vertexList.add(currentVertexIndex+4, projectedLightVertex.y);
				vertexList.add(currentVertexIndex+5, -obj.depth()-0.5f);
				
				currentVertexIndex += 6;
			}
			else
				currentVertexIndex = -1;
			
			//Shift our data backwards, so what used to be our "current" data, is now our "previous" data
			previousVert.set(currentVert);
			previousLightOffsetDir.set(currentLightOffsetDir);
		}
		
		float[] vertArray = new float[vertexList.size()];
		for(int i=0; i<vertArray.length; i++)
			vertArray[i] = vertexList.get(i);
		
		return vertArray;
	}
}
