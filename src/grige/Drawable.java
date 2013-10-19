package grige;

import com.jogamp.opengl.util.texture.Texture;

public class Drawable {
	
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
}
