package grige;

import com.jogamp.opengl.util.texture.Texture;

public class Drawable {
	
	private Texture texture;
	
	protected float x;
	protected float y;
	protected float depth;
	
	protected float scale;
	protected float rotation;
	
	public float x(){ return x; }
	public float y(){ return y; }
	public float depth() { return depth; }
	public float scale() { return scale; }
	public float rotation() { return rotation; }
	
	public Drawable()
	{
		x = 160;
		y = 160;
		scale = 1;
		depth = 0;
		rotation = 45;
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
