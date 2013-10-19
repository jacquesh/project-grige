package grige;

public abstract class GameObject extends Drawable {
	
	public GameObject()
	{
		super();
	}
	
	public abstract void update(float deltaTime);
}
