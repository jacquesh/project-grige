package grige;

public class Color
{
	private float red;
	private float green;
	private float blue;
	private float alpha;
	
	public Color(float r, float g, float b, float a)
	{
		setValues(r,g,b,a);
	}
	
	public Color(float r, float b, float g)
	{
		this(r,g,b,1f);
	}
	
	public Color()
	{
		this(1,1,1,1);
	}
	
	public void setValues(float r, float g, float b, float a)
	{
		red = r;
		green = g;
		blue = b;
		alpha = a;
	}
	
	public void setValues(float r, float g, float b)
	{
		setValues(r,g,b,alpha);
	}
	
	public void setRed(float r) { red = r; }
	public void setGreen(float g) { green = g; }
	public void setBlue(float b) { blue = b; }
	public void setAlpha(float a) {alpha = a; }
	
	public float getRed() { return red; }
	public float getGreen() { return green; }
	public float getBlue() { return blue; }
	public float getAlpha() { return alpha; }
	
	public float[] toFloatArray()
	{
		return new float[]{red, green, blue, alpha};
	}
	
	public float[] toColorFloatArray()
	{
		return new float[]{red, green, blue};
	}
}
