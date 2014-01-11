package grige;

import com.jogamp.opengl.util.texture.Texture;

public class Drawable
{
	private Texture texture;
	
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
	
	public void setTexture(Texture newTexture)
	{
		texture = newTexture;
	}
	
	protected Texture getTexture()
	{
		return texture;
	}
	
	public float x() { return position.x; }
	public float y() { return position.y; }
	public float depth() { return depth; }
	public float scale() { return scale; }
	public float rotation() { return rotation; }
	
	public void setX(float newX) { position.x = newX; }
	public void setY(float newY) { position.y = newY; }
	public void setDepth(float newDepth) { depth = newDepth; }
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
	public void setPosition(Vector2 newPosition)
	{
		position.x = newPosition.x;
		position.y = newPosition.y;
	}
	
	public float width()
	{
		if(texture == null)
			return 0;
		
		return texture.getWidth() * scale;
	}
	
	public float height()
	{
		if(texture == null)
			return 0;
		
		return texture.getHeight() * scale;
	}
	
	float[] getVertices()
	{
		float[] result = new float[8];
		float halfWidth = width()/2f;
		float halfHeight = height()/2f;
		float rotationSin = (float)Math.sin(rotation);
		float rotationCos = (float)Math.cos(rotation);
		
		float axisAlignedWidth = halfWidth*rotationCos - halfHeight*rotationSin;
		float axisAlignedHeight = halfHeight*rotationSin + halfHeight*rotationSin;
		
		result[0] = position.x - axisAlignedWidth;
		result[1] = position.y - axisAlignedHeight;
		
		result[2] = position.x - axisAlignedWidth;
		result[3] = position.y + axisAlignedHeight;
		
		result[4] = position.x + axisAlignedWidth;
		result[5] = position.y + axisAlignedHeight;
		
		result[6] = position.x + axisAlignedWidth;
		result[7] = position.y - axisAlignedHeight;
		
		return result;
	}
}
