package grige;

public abstract class Light extends Drawable
{
	private float[] colour;
	
	public Light()
	{
		super();
		
		colour = new float[]{1f, 1f, 1f};
	}
	
	public void setColour(float red, float green, float blue)
	{
		colour[0] = red;
		colour[1] = blue;
		colour[2] = green;
	}
	
	public float[] getColour() { return colour; }
	
	public float radius() { return scale(); }
	public void setRadius(float radius) { setScale(radius); }
}
