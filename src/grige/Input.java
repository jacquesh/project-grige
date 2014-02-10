package grige;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;

import java.util.HashMap;

public final class Input implements KeyListener, MouseListener
{
	
	private static HashMap<Short, Boolean> nextInput; //Input currently being executed
	private static HashMap<Short, Boolean> currentInput; //Input from the latest frame
	private static HashMap<Short, Boolean> previousInput; //Input from the previous frame
	
	private static boolean[] nextMouseButtons;
	private static boolean[] currentMouseButtons;
	private static boolean[] previousMouseButtons;
	
	private static int nextMouseWheel;
	private static int currentMouseWheel;
	
	private static Vector2I mouseLoc;
	
	private static int screenHeight;
	
	private static Input instance;
	static Input getInstance()
	{
		if(instance == null)
			instance = new Input();
		return instance;
	}
	
	private Input(){}
	
	static void initialize(int glScreenHeight)
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
	
	public static boolean getKey(short keySymbol)
	{
		return currentInput.get(keySymbol); 
	}
	
	public static boolean getKeyDown(short keySymbol)
	{
		return currentInput.get(keySymbol) && !previousInput.get(keySymbol);
	}
	
	public static boolean getKeyUp(short keySymbol)
	{
		return !currentInput.get(keySymbol) && previousInput.get(keySymbol);
	}
	
	public static int getMouseX() { return mouseLoc.x; }
	public static int getMouseY() { return mouseLoc.y; }
	
	public static boolean getMouseButton(int buttonID)
	{
		return currentMouseButtons[buttonID];
	}
	
	public static boolean getMouseButtonDown(int buttonID)
	{
		return currentMouseButtons[buttonID] && !previousMouseButtons[buttonID];
	}
	
	public static boolean getMouseButtonUp(int buttonID)
	{
		return !currentMouseButtons[buttonID] && previousMouseButtons[buttonID];
	}
	
	
	public static int getMouseWheel()
	{
		return currentMouseWheel;
	}
	
	protected static void update()
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
			Input.nextInput.put(evt.getKeySymbol(), true);
	}
	
	public void keyReleased(KeyEvent evt)
	{
		if(!evt.isAutoRepeat())
			Input.nextInput.put(evt.getKeySymbol(), false);
	}
	
	public void mouseWheelMoved(MouseEvent evt)
	{
		Input.nextMouseWheel += (int)evt.getRotation()[1];
	}
	
	public void mousePressed(MouseEvent evt)
	{
		int button = evt.getButton() - 1; //We minus 1 because for JOGL left mouse is button 1. We want zero-based things dammit!
		Input.nextMouseButtons[button] = true;
	}
	
	public void mouseReleased(MouseEvent evt)
	{
		int button = evt.getButton() - 1;
		Input.nextMouseButtons[button] = false;
	}
	
	public void mouseMoved(MouseEvent evt)
	{
		Input.mouseLoc.x = evt.getX();
		Input.mouseLoc.y = Input.screenHeight - evt.getY();
	}
	
	public void mouseDragged(MouseEvent evt)
	{
		mouseMoved(evt); //We handle mouseDragged events the same way we handle mouseMoved events because for a PC dragged/moved are the same thign
	}
	
	public void mouseClicked(MouseEvent evt){}
	public void mouseEntered(MouseEvent evt){}
	public void mouseExited(MouseEvent evt){}
	
}
