package grige;

import com.jogamp.opengl.util.texture.Texture;

public class Drawable
{
	private Texture texture;
	
	public float x;
	public float y;
	public float depth;
	
	public float scale;
	public float rotation;
	
	public Drawable()
	{
		x = 160;
		y = 160;
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
	
	public float getWidth()
	{
		if(texture == null)
			return 0;
		
		return texture.getWidth() * scale;
	}
	
	public float getHeight()
	{
		if(texture == null)
			return 0;
		
		return texture.getHeight() * scale;
	}
	
	public float[] getVertices()
	{
		float[] result = new float[8];
		float halfWidth = getWidth()/2f;
		float halfHeight = getHeight()/2f;
		float rotationSin = (float)Math.sin(rotation);
		float rotationCos = (float)Math.cos(rotation);
		
		float axisAlignedWidth = halfWidth*rotationCos - halfHeight*rotationSin;
		float axisAlignedHeight = halfHeight*rotationSin + halfHeight*rotationSin;
		
		result[0] = x - axisAlignedWidth;
		result[1] = y - axisAlignedHeight;
		
		result[2] = x - axisAlignedWidth;
		result[3] = y + axisAlignedHeight;
		
		result[4] = x + axisAlignedWidth;
		result[5] = y + axisAlignedHeight;
		
		result[6] = x + axisAlignedWidth;
		result[7] = y - axisAlignedHeight;
		
		return result;
	}
}
