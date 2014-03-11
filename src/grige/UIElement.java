package grige;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.opengl.math.FloatUtil;

public abstract class UIElement extends GameObject
{
	private boolean isFocussed;
	
	public abstract void update(float deltaTime);
	
	public UIElement()
	{
		super();
	}
	
	public void setFocus(boolean focussed)
	{
		isFocussed = focussed;
	}
	public boolean isFocussed()
	{
		return isFocussed;
	}
}
