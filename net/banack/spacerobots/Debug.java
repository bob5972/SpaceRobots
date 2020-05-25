/*
 * This file is part of SpaceRobots. Copyright (c)2009 Michael Banack <github@banack.net>
 * 
 * SpaceRobots is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * SpaceRobots is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with SpaceRobots. If not, see
 * <http://www.gnu.org/licenses/>.
 */

package net.banack.spacerobots;

public class Debug extends net.banack.debug.Debug
{
	private static boolean aiWarning = false;
	private static boolean comLog = false;
	private static boolean slowGraphics = false;
	
	public static void setShowAIWarnings(boolean b)
	{
		aiWarning = b;
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
		if (showAIWarnings())
			print("AI Warning: " + msg);
	}
	
	public static void comLog(String msg)
	{
		if (showComLog())
			info(msg);
	}
	
	public static boolean isSlowGraphics()
	{
		return slowGraphics;
	}
	
	public static void setSlowGraphics(boolean b)
	{
		slowGraphics = b;
	}
	
	public static void enableSlowGraphics()
	{
		slowGraphics = true;
	}
	
	public static void disableSlowGraphics()
	{
		slowGraphics = false;
	}
	
}
