package grige;

public abstract class Light extends Drawable
{
	public float radius() { return scale(); }
	public void setRadius(float radius) { setScale(radius); }
}
