package grige;

public class Vector2
{
	public float x;
	public float y;
	
	public Vector2(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	public void add(Vector2 other)
	{
		x += other.x;
		y += other.y;
	}
	
	public void subtract(Vector2 other)
	{
		x -= other.x;
		y -= other.y;
	}
	
	public void multiply(float f)
	{
		x *= f;
		y *= f;
	}
	
	public float dot(Vector2 other)
	{
		return x*other.x + y*other.y;
	}
	
	public void normalise()
	{
		float mag = magnitude();
		
		if(mag != 0)
		{
			x /= mag;
			y /= mag;
		}
	}
	
	public float sqrMagnitude()
	{
		return x*x + y*y;
	}
	
	public float magnitude()
	{
		return (float)Math.sqrt(sqrMagnitude());
	}
}
