package net.banack.spacerobots;

public class Debug extends net.banack.debug.Debug
{
	private static boolean aiWarning=false;
	
	public static void setShowAIWarnings(boolean b)
	{
		aiWarning=b;
	}
	
	public static boolean showAIWarnings()
	{
		return aiWarning;
	}
	
	public static void aiwarn(String msg)
	{
		if(showAIWarnings())
			warn("AI Warning: "+msg);
	}
	
}