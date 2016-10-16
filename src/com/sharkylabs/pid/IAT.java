package com.sharkylabs.pid;

/**
 * Represents the Intake Air Temperatur(IAT) value. Used to help with air density calculations as well as 
 * modifying spark tables (hotter IATs demand less spark to prevent engine pinging). 
 */
public class IAT extends AbstractTemperatureSensor {
	public IAT(int value) {
		super(value);
		this.name = "IAT";
	}
}
