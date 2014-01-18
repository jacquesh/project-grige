package grige;

import com.jogamp.opengl.math.FloatUtil;

public class Vector3
{
	public float x;
	public float y;
	public float z;
	
	public Vector3(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void set(Vector3 other)
	{
		set(other.x, other.y, other.z);
	}
	
	public void set(float newX, float newY, float newZ)
	{
		x = newX;
		y = newY;
		z = newZ;
	}
	
	public void add(Vector3 other)
	{
		x += other.x;
		y += other.y;
		z += other.z;
	}
	
	public void subtract(Vector3 other)
	{
		x -= other.x;
		y -= other.y;
		z -= other.z;
	}
	
	public void multiply(float f)
	{
		x *= f;
		y *= f;
		z *= f;
	}
	
	public float dot(Vector3 other)
	{
		return x*other.x + y*other.y + z*other.z;
	}
	
	public Vector3 cross(Vector3 other)
	{
		Vector3 result = new Vector3(0,0,0);
		result.x = this.y*other.z - this.z*other.y;
		result.y = this.z*other.x - this.x*other.z;
		result.z = this.x*other.y - this.y*other.x;
		
		return result;
	}
	
	public void normalise()
	{
		float mag = magnitude();
		
		if(mag != 0)
		{
			x /= mag;
			y /= mag;
			z /= mag;
		}
	}
	
	public float sqrMagnitude()
	{
		return x*x + y*y + z*z;
	}
	
	public float magnitude()
	{
		return FloatUtil.sqrt(sqrMagnitude());
	}
	
	@Override
	public String toString()
	{
		return "("+x+"; "+y+"; "+z+")";
	}
}
