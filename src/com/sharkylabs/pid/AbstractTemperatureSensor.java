package com.sharkylabs.pid;


/**
 * It seems that all of the temperature parameters (OK, there's only 2) use the same function for deriving temperature.
 * A single base class can cover the conversion logic.
 */
public abstract class AbstractTemperatureSensor extends AbstractPID {

	public AbstractTemperatureSensor(int value) {
		this.name = "Temp";
		this.unit = "*F";
		
		// Min and max values determined from looking at the spec-sheet for the temperature sensors used in GM cars. 
		this.minValue = -40;
		this.maxValue = 275; 
		
		setValue(value);
	}
	
	@Override
	public final void setValue(int newValue) {
		// both IAT and ECT seem to follow y=mx+b, where m = 0.25 and b = (-273)
		// then convert from C to F
		this.currentValue = ((newValue / 4) - 273) * 9 / 5 + 32; 
	}
}
