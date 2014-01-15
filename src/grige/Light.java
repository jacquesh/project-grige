package grige;

public abstract class Light extends Drawable
{
	private float[] colour;
	private float intensity;
	
	public Light()
	{
		super();
		
		colour = new float[]{1f, 1f, 1f};
		intensity = 1f;
	}
	
	public void setColour(float red, float green, float blue)
	{
		colour[0] = red;
		colour[1] = blue;
		colour[2] = green;
	}
	
	public void setIntensity(float newIntensity) { intensity = newIntensity; }
	public void setRadius(float radius) { setScale(radius); }
	
	public float[] getColour() { return colour; }
	public float getIntensity() { return intensity; }
	public float getRadius() { return scale(); }
	
}
