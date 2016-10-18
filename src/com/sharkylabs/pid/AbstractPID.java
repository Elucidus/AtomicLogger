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
	
	protected StringBuffer displayBuffer = new StringBuffer(20);
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
		if (displayBuffer.length() > 0) {
			displayBuffer.delete(this.name.length() + 1, displayBuffer.length());
		} else {
			displayBuffer.append(this.name);
			displayBuffer.append('[');
		}
		displayBuffer.append(this.currentValue);
		displayBuffer.append(this.unit);
		displayBuffer.append(']');
		
		return displayBuffer.toString();
	}
}
