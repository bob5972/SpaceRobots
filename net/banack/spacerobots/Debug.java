package net.banack.spacerobots;

public class Debug extends net.banack.debug.Debug
{
	private static boolean aiWarning=false;
	private static boolean comLog=false;
	private static boolean slowGraphics=false;
	
	public static void setShowAIWarnings(boolean b)
	{
		aiWarning=b;
	}
	
	public static boolean showComLog()
	{
		return comLog;
	}
	
	public static void setShowComLog(boolean b)
	{
		comLog = b;
	}
	
	public static boolean showAIWarnings()
	{
		return aiWarning;
	}
	
	public static void aiwarn(String msg)
	{
		if(showAIWarnings())
			print("AI Warning: "+msg);
	}
	
	public static void comLog(String msg)
	{
		if(showComLog())
			info(msg);
	}
	
	public static boolean isSlowGraphics()
	{
		return slowGraphics;
	}
	
	public static void setSlowGraphics(boolean b)
	{
		slowGraphics=b;
	}
	
	public static void enableSlowGraphics()
	{
		slowGraphics =true;
	}
	
	public static void disableSlowGraphics()
	{
		slowGraphics=false;
	}
	
}