package grige;

public class Color
{
	public static final Color WHITE = new Color(1,1,1,1);
	public static final Color BLACK = new Color(0,0,0,1);
	
	private float[] values;
	
	public Color(float r, float g, float b, float a)
	{
		values = new float[]{r,g,b,a};
	}
	
	public Color(float r, float b, float g)
	{
		this(r,g,b,1f);
	}
	
	public float getRed() { return values[0]; }
	public float getGreen() { return values[1]; }
	public float getBlue() { return values[2]; }
	public float getAlpha() { return values[3]; }
	
	public float[] toFloat4Array()
	{
		return new float[]{values[0], values[1], values[2], values[3]};
	}
	
	public float[] toFloat3Array()
	{
		return new float[]{values[0], values[1], values[2]};
	}
}
