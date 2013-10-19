package grige;


import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;

import java.util.HashMap;

public final class InputManager implements KeyListener, MouseListener{
	
	//private HashMap<KeyCode,Boolean> currentInput; //Input from the latest frame
	//private HashMap<KeyCode,Boolean> previousInput; //Input from the previous frame
	//private HashMap<KeyCode,Boolean> nextInput; //Input for the next frame
	
	protected InputManager()
	{
		
	}
	
	protected void update()
	{
		
	}
	
	public boolean getKey(KeyCode key)
	{
		return false;
	}
	
	public void keyPressed(KeyEvent evt)
	{
		
	}
	
	public void keyReleased(KeyEvent evt)
	{
		
	}
	
	public void mouseWheelMoved(MouseEvent evt)
	{
		
	}
	
	public void mousePressed(MouseEvent evt)
	{
		
	}
	
	public void mouseReleased(MouseEvent evt)
	{
		
	}
	
	public void mouseClicked(MouseEvent evt){}
	public void mouseDragged(MouseEvent evt){}
	public void mouseEntered(MouseEvent evt){}
	public void mouseExited(MouseEvent evt){}
	public void mouseMoved(MouseEvent evt){}
	
}
