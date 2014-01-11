package grige;

public class Vector2I
{
    public int x;
    public int y;
    
    public Vector2I(int x, int y)
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
    
    public void multiply(int f)
    {
        x *= f;
        y *= f;
    }
    
    public int dot(Vector2I other)
    {
        return x*other.x + y*other.y;
    }
    
    public void normalise()
    {
        int mag = magnitude();
        
        if(mag != 0)
        {
            x /= mag;
            y /= mag;
        }
    }
    
    public int sqrMagnitude()
    {
        return x*x + y*y;
    }
    
    public int magnitude()
    {
        return (int)Math.sqrt(sqrMagnitude());
    }
}
