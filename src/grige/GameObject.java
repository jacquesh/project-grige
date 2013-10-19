package grige;

public abstract class GameObject extends Drawable {
	
	public GameObject()
	{
		super();
	}
	
	public abstract void update(float deltaTime);
	
	public boolean collidesWith(GameObject other)
	{
		return false;
	}
}
