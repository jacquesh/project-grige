package grige;

import com.jogamp.opengl.math.FloatUtil;

public class Vector2
{
	public float x;
	public float y;
	
	public Vector2(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Vector2(Vector2 other)
	{
		this(other.x, other.y);
	}
	
	public Vector2(Vector3 other)
	{
		this(other.x, other.y);
	}
	
	public void set(Vector2 other)
	{
		set(other.x, other.y);
	}
	
	public void set(float newX, float newY)
	{
		x = newX;
		y = newY;
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
	
	public void rotate(float theta)
	{
		float thetaRadians = theta*FloatUtil.PI/180f;
		float tempX = x;
		float tempY = y;
		
		x = tempX*FloatUtil.cos(thetaRadians) - tempY*FloatUtil.sin(thetaRadians);
		y = tempX*FloatUtil.sin(thetaRadians) + tempY*FloatUtil.cos(thetaRadians);
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
		return FloatUtil.sqrt(sqrMagnitude());
	}
	
	@Override
	public String toString()
	{
		return "("+x+"; "+y+")";
	}
	
	public static Vector2 add(Vector2 a, Vector2 b)
	{
		return new Vector2(a.x+b.x, a.y+b.y);
	}
	
	public static Vector2 subtract(Vector2 a, Vector2 b)
	{
		return new Vector2(a.x-b.x, a.y-b.y);
	}
	
	public static Vector2 multiply(Vector2 v, float f)
	{
		return new Vector2(v.x*f, v.y*f);
	}
	
	public static Vector2 rotate(Vector2 v, float theta)
	{
		float thetaRadians = theta*FloatUtil.PI/180f;
		float tempX = v.x;
		float tempY = v.y;
		Vector2 result = new Vector2(0,0);
		
		result.x = tempX*FloatUtil.cos(thetaRadians) - tempY*FloatUtil.sin(thetaRadians);
		result.y = tempX*FloatUtil.sin(thetaRadians) + tempY*FloatUtil.cos(thetaRadians);
		
		return result;
	}
	
	public static float dot(Vector2 a, Vector2 b)
	{
		return a.x*b.x + a.y*b.y;
	}
	
	public static float cross(Vector2 a, Vector2 b)
	{
		return a.x*b.y - a.y*b.x;
	}
}
