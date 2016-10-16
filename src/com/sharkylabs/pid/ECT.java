package com.sharkylabs.pid;

/**
 * Engine Coolant Temperature (ECT). Used to trigger fan controls, spark modifiers (like IAT), as well as 
 * assisting in determining when the engine is warmed up and should enter closed-loop oxygen sensor polling. 
 */
public class ECT extends AbstractTemperatureSensor {
	public ECT(int value) {
		super(value);
		this.name = "ECT";
	}
}
