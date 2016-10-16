package com.sharkylabs.pid;

/**
 * Throttle Position Sensor (TPS), represents how far open the throttle is.
 * @author Steve
 *
 */
public class TPS extends AbstractPID {

	public TPS(int value) {
		this.name = "TPS";
		this.unit = "%";
		this.minValue = 0;
		this.maxValue = 100;
		setValue(value);
	}
	@Override
	public void setValue(int newValue) {
		//TPS range is 0-200, convert to percent
		this.currentValue = newValue / 2; 
	}
}
