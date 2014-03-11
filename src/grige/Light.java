package grige;

public abstract class Light extends Drawable
{
	private Color colour;
	private float intensity;
	
	public Light()
	{
		super();
		
		colour = new Color(1,1,1,1);
		intensity = 1f;
	}
	
	public void setColour(float red, float green, float blue)
	{
		colour = new Color(red,green,blue);
	}
	
	public void setIntensity(float newIntensity) { intensity = newIntensity; }
	public void setRadius(float radius) { setScale(radius); }
	
	public Color getColour() { return colour; }
	public float getIntensity() { return intensity; }
	public float getRadius() { return scale(); }
	
}
