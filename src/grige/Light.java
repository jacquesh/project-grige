package grige;

import java.util.ArrayList;

public abstract class Light extends Drawable
{
	private Color colour;
	
	public Light()
	{
		super();
		
		colour = new Color(1,1,1,1);
	}
	
	public void setColour(float red, float green, float blue)
	{
		colour = new Color(red,green,blue, colour.getAlpha());
	}
	
	public void setColour(float red, float green, float blue, float intensity)
	{
		colour = new Color(red,green,blue,intensity);
	}
	
	public void setIntensity(float newIntensity)
	{
		colour = new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), newIntensity);
	}
	public void setRadius(float radius) { setScale(radius); }
	
	public Color getColour() { return colour; }
	public float getRadius() { return scale(); }
	
	
	/*
	 * Compute the vertices for the shadow cast by the given GameObject when lit by this light
	 */
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
			
			currentLightOffsetDir.x = position.x - currentVert.x;
			currentLightOffsetDir.y = position.y - currentVert.y;
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
