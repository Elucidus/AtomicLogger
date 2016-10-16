package com.sharkylabs.pid;

/** 
 * Represents the injector rail pressure of the fuel system.
 */
public class FuelPressure extends AbstractPID {

	public FuelPressure(int value) {
		this.name = "Press";
		this.unit = "psi";
		this.minValue = 0;
		this.maxValue = 100;
		setValue(value);
	}
	@Override
	public void setValue(int newValue) {
		// Fuel pressure appears to be directly represented as PSI.
		this.currentValue = newValue; 
	}
}
