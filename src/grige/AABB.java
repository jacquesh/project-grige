package grige;

public class AABB
{
	public Vector2 position;
	public Vector2 size;
	
	public Vector2 bottomLeft() { return new Vector2(position.x, position.y); }
	public Vector2 bottomRight() { return new Vector2(position.x+size.x, position.y); }
	public Vector2 topLeft() { return new Vector2(position.x, position.y+size.y); }
	public Vector2 topRight() { return new Vector2(position.x+size.x, position.y+size.y); }
	
	public AABB()
	{
		this(0,0,0,0);
	}
	
	public AABB(Vector2 position, Vector2 size)
	{
		this(position.x, position.x, size.x, size.y);
	}
	
	public AABB(float x, float y, float width, float height)
	{
		position = new Vector2(x, y);
		size = new Vector2(width, height);
	}
	
	public boolean contains(Vector2 point)
	{
		if(point.x > position.x && point.x < position.x+size.x)
			if(point.y > position.y && point.y < position.y+size.y)
				return true;
		return false;
	}
	
	public boolean intersects(AABB other)
	{
		if(other.position.x > position.x+size.x)
			return false;
		if(other.position.x+other.size.x < position.x)
			return false;
		if(other.position.y > position.y+size.y)
			return false;
		if(other.position.y+other.size.y < position.y)
			return false;
		return true;
	}
}
