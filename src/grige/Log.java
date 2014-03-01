package grige;

public class Log
{
	private Log(){}
	
	public static void info(String message)
	{
		System.out.println(message);
	}
	
	public static void warn(String message)
	{
		System.out.println(message);
	}
	
	public static void fatal(String message)
	{
		System.out.println(message);
	}
}
