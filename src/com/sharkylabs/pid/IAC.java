package com.sharkylabs.pid;

/**
 * Represents the Idle Air Control(IAC) valve. This valve is used to control idle RPM 
 * as well as compensate for certain scenarios, such as air conditioning engagement,
 * fan engagement, closed throttle and others to help with engine harshness and prevent
 * stalling. 
 */
public class IAC extends AbstractPID {

	public IAC(int value) {
		this.name = "IAC";
		this.unit = "steps";
		this.minValue = 0;
		this.maxValue = 200; // not sure how many steps
		setValue(value);
	}
	@Override
	public void setValue(int newValue) {
		// The number of steps the IAC valve is open.
		this.currentValue = newValue; 
	}
}
