package grige;

public class AABBI
{
	public Vector2I position;
	public Vector2I size;
	
	public Vector2I bottomLeft() { return new Vector2I(position.x, position.y); }
	public Vector2I bottomRight() { return new Vector2I(position.x+size.x, position.y); }
	public Vector2I topLeft() { return new Vector2I(position.x, position.y+size.y); }
	public Vector2I topRight() { return new Vector2I(position.x+size.x, position.y+size.y); }
	
	public AABBI()
	{
		this(0,0,0,0);
	}
	
	public AABBI(Vector2I position, Vector2I size)
	{
		this(position.x, position.x, size.x, size.y);
	}
	
	public AABBI(int x, int y, int width, int height)
	{
		position = new Vector2I(x, y);
		size = new Vector2I(width, height);
	}
	
	public boolean contains(Vector2I point)
	{
		if(point.x > position.x && point.x < position.x+size.x)
			if(point.y > position.y && point.y < position.y+size.y)
				return true;
		return false;
	}
	
	public boolean intersects(AABBI other)
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
