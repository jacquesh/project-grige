package grige;

import javax.media.opengl.GL2;

public abstract class Drawable
{
	protected Vector2 position;
	protected float depth;
	
	protected float scale;
	protected float rotation;
	
	protected boolean markedForDeath;
	
	public abstract void setShader(GL2 gl, int shader);
	protected abstract void onDraw(GL2 gl, Camera cam);
	protected abstract void onDestroy();
	
	public abstract float width();
	public abstract float height();
	
	public Drawable()
	{
		position = new Vector2(160, 160);
		scale = 1;
		depth = 0;
		markedForDeath = false;
	}
	
	public float x() { return position.x; }
	public float y() { return position.y; }
	public float depth() { return depth; }
	public float scale() { return scale; }
	public float rotation() { return rotation; }
	
	public void setX(float newX) { position.x = newX; }
	public void setY(float newY) { position.y = newY; }
	public void setDepth(int newDepth) { depth = (float)newDepth; }
	public void setScale(float newScale) { scale = newScale; }
	public void setRotation(float newRotation) { rotation = newRotation; }
	
	public void incX(float extraX) { position.x += extraX; }
	public void incY(float extraY) { position.y += extraY; }
	public void incDepth(float extraDepth) { depth += extraDepth; }
	public void incScale(float extraScale) { scale += extraScale; }
	public void incRotation(float extraRotation) { rotation += extraRotation; }
	
	public Vector2 position()
	{
		return new Vector2(position.x, position.y);
	}
	public void setPosition(float x, float y)
	{
		position.x = x;
		position.y = y;
	}
	public void setPosition(Vector2 newPosition)
	{
		setPosition(newPosition.x, newPosition.y);
	}
}
