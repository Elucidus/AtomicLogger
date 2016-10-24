package com.sharkylabs.util;

public class GlobalConstants {

	/**
	 * Defines the name of the socket interface to use when opening up socketcan.
	 * Default is "can0".
	 */
	public static String INTERFACE_NAME = "can0";
	/**
	 * Represents the amount of time between UI update attempts or polling the main interface. 
	 */
	public static long UPDATE_DELAY_MSEC = 15;
	
	/**
	 * Behavior changes for simulations when this is true.
	 */
	public static boolean DEMO_MODE = false; 
}
