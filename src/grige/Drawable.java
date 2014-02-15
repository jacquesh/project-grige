package grige;

import com.jogamp.opengl.math.FloatUtil;

public class Drawable
{
	private Material material;
	
	private Vector2 position;
	private float depth;
	
	private float scale;
	private float rotation;
	
	public Drawable()
	{
		position = new Vector2(160, 160);
		scale = 1;
		depth = 0;
	}
	
	public void setMaterial(Material newMaterial)
	{
		material = newMaterial;
	}
	
	protected Material getMaterial()
	{
		return material;
	}
	
	public AABB getAABB()
	{
		return new AABB(position.x, position.y, width(), height());
	}
	
	public float x() { return position.x; }
	public float y() { return position.y; }
	public float depth() { return depth; }
	public float scale() { return scale; }
	public float rotation() { return rotation; }
	
	public void setX(float newX) { position.x = newX; }
	public void setY(float newY) { position.y = newY; }
	public void setDepth(int newDepth) { depth = (float)newDepth; }
	public void setScale(float newScale) { scale = newScale; }
	public void setRotation(float newRotation) { rotation = newRotation; }
	
	public void incX(float extraX) { position.x += extraX; }
	public void incY(float extraY) { position.y += extraY; }
	public void incDepth(float extraDepth) { depth += extraDepth; }
	public void incScale(float extraScale) { scale += extraScale; }
	public void incRotation(float extraRotation) { rotation += extraRotation; }
	
	public Vector2 position()
	{
		return new Vector2(position.x, position.y);
	}
	public void setPosition(float x, float y)
	{
		position.x = x;
		position.y = y;
	}
	public void setPosition(Vector2 newPosition) { setPosition(newPosition.x, newPosition.y); }
	
	public float width()
	{
		if(material == null)
			return 0;
		
		return material.getWidth() * scale;
	}
	
	public float height()
	{
		if(material == null)
			return 0;
		
		return material.getHeight() * scale;
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
		float rotationSin = FloatUtil.sin(rotation);
		float rotationCos = FloatUtil.cos(rotation);
		
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
}
