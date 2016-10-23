package com.sharkylabs.pid;

/**
 * Revolutions per minute - used to measure engine revolutions. Typically indicated 
 * by a tachometer. Critical for determining many engine factors. Part of the Speed Density equation.
 */
public class RPM extends AbstractPID {

	public RPM(int value) {
		this.name = "RPM";
		this.unit = "RPM";
		this.minValue = 0;
		this.maxValue = 8000;
		setValue(value);
	}
	@Override
	public void setValue(int newValue) {
		// current derivation of RPM is unknown, using test value for now
		this.currentValue = newValue; 
	}
	
	/**
	 * Setter for data coming straight from the ECM. 
	 * @param low
	 * @param high
	 */
	public void setValue(byte low, byte high) {
		this.currentValue = (low & 0xFF) + ((high & 0xFF) << 8); 
		
	}
}
