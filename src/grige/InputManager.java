package grige;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;

import java.util.HashMap;

public final class InputManager implements KeyListener, MouseListener{
	
	private HashMap<Short, Boolean> nextInput; //Input currently being executed
	private HashMap<Short, Boolean> currentInput; //Input from the latest frame
	private HashMap<Short, Boolean> previousInput; //Input from the previous frame
	
	private boolean[] nextMouseButtons;
	private boolean[] currentMouseButtons;
	private boolean[] previousMouseButtons;
	
	private int nextMouseWheel;
	private int currentMouseWheel;
	
	private Vector2I mouseLoc;
	
	public int getMouseX() { return mouseLoc.x; }
	public int getMouseY() { return mouseLoc.y; }
	
	private int screenHeight;
	
	protected InputManager(int glScreenHeight)
	{
		mouseLoc = new Vector2I(0,0);
		screenHeight = glScreenHeight;
		
		//Create the input storage structures
		currentInput = new HashMap<Short, Boolean>();
		previousInput = new HashMap<Short, Boolean>();
		nextInput = new HashMap<Short, Boolean>();
		
		nextMouseButtons = new boolean[MouseEvent.BUTTON_NUMBER];
		currentMouseButtons = new boolean[MouseEvent.BUTTON_NUMBER];
		previousMouseButtons = new boolean[MouseEvent.BUTTON_NUMBER];
		
		nextMouseWheel = 0;
		currentMouseWheel = 0;
		
		//Populate each hashmap with all the virtual key fields from KeyEvent via reflection
		try
		{
			Field[] keyEventFields = KeyEvent.class.getFields();
			
			for(Field keyField : keyEventFields)
			{
				int modifier = keyField.getModifiers();
				
				if(Modifier.isStatic(modifier) && Modifier.isPublic(modifier) && Modifier.isFinal(modifier))
					if(keyField.getType().equals(short.class) && keyField.getName().startsWith("VK_"))
					{
						short keyValue = keyField.getShort(null); //We use null here because its a static field
						nextInput.put(keyValue, false);
						currentInput.put(keyValue, false);
						previousInput.put(keyValue, false);
					}
			}
		}
		catch(IllegalAccessException iae)
		{
		}
	}
	
	public boolean getKey(short keySymbol)
	{
		return currentInput.get(keySymbol); 
	}
	
	public boolean getKeyDown(short keySymbol)
	{
		return currentInput.get(keySymbol) && !previousInput.get(keySymbol);
	}
	
	public boolean getKeyUp(short keySymbol)
	{
		return !currentInput.get(keySymbol) && previousInput.get(keySymbol);
	}
	
	public boolean getMouseButton(int buttonID)
	{
		return currentMouseButtons[buttonID];
	}
	
	public boolean getMouseButtonDown(int buttonID)
	{
		return currentMouseButtons[buttonID] && !previousMouseButtons[buttonID];
	}
	
	public boolean getMouseButtonUp(int buttonID)
	{
		return !currentMouseButtons[buttonID] && previousMouseButtons[buttonID];
	}
	
	public int getMouseWheel()
	{
		return currentMouseWheel;
	}
	
	protected void update()
	{
		//Keyboard input
		for(short key : currentInput.keySet())
		{
			previousInput.put(key, currentInput.get(key));
			currentInput.put(key, nextInput.get(key));
		}	
		
		//Mouse button input
		for(int i=0; i<MouseEvent.BUTTON_NUMBER; i++)
		{
			previousMouseButtons[i] = currentMouseButtons[i];
			currentMouseButtons[i] = nextMouseButtons[i];
		}
		
		//Mouse wheel input
		currentMouseWheel = nextMouseWheel;
		nextMouseWheel = 0;
	}
	
	public void keyPressed(KeyEvent evt)
	{
		if(!evt.isAutoRepeat())
			nextInput.put(evt.getKeySymbol(), true);
	}
	
	public void keyReleased(KeyEvent evt)
	{
		if(!evt.isAutoRepeat())
			nextInput.put(evt.getKeySymbol(), false);
	}
	
	public void mouseWheelMoved(MouseEvent evt)
	{
		nextMouseWheel += (int)evt.getRotation()[1];
	}
	
	public void mousePressed(MouseEvent evt)
	{
		int button = evt.getButton() - 1; //We minus 1 because for JOGL left mouse is button 1. We want zero-based things dammit!
		nextMouseButtons[button] = true;
	}
	
	public void mouseReleased(MouseEvent evt)
	{
		int button = evt.getButton() - 1;
		nextMouseButtons[button] = false;
	}
	
	public void mouseMoved(MouseEvent evt)
	{
		mouseLoc.x = evt.getX();
		mouseLoc.y = screenHeight - evt.getY();
	}
	
	public void mouseDragged(MouseEvent evt)
	{
		mouseMoved(evt); //We handle mouseDragged events the same way we handle mouseMoved events because for a PC dragged/moved are the same thign
	}
	
	public void mouseClicked(MouseEvent evt){}
	public void mouseEntered(MouseEvent evt){}
	public void mouseExited(MouseEvent evt){}
	
}
