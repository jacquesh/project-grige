package grige;

public class GameObject extends Drawable {
	
	public GameObject()
	{
		super();
	}
	
	protected void update()
	{
		rotation += 1;
	}
}
