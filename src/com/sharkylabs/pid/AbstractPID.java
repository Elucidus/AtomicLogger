package com.sharkylabs.pid;

/**
 * Represents a broadcast engine value. 
 */
public abstract class AbstractPID {
	public String name;
	public String unit; 
	
	public int currentValue;
	public int minValue;
	public int maxValue;
	
	
	/**
	 * Used to set the value of a given PID. This method will convert the CAN 
	 * encoded representation to a real world value.
	 * @param newValue
	 */
	public abstract void setValue(int newValue);
	
	/**
	 * PIDs generate a string in the format of: $name[$value$unit]
	 */
	public String toString() {
		return this.name + "[" + this.currentValue + this.unit + "]";
	}
}
