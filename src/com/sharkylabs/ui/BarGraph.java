package com.sharkylabs.ui;

import com.sharkylabs.pid.AbstractPID;
import com.sharkylabs.pid.TPS;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BarGraph implements IGauge {

	// layout fields
	private double xAnchor;
	private double yAnchor;
	private double width;
	private double height;

	// data fields
	private AbstractPID pid;
	
	public BarGraph(double xAnchor, double yAnchor, double width, double height,
			AbstractPID pid) {
		super();
		this.xAnchor = xAnchor;
		this.yAnchor = yAnchor;
		this.width = width;
		this.height = height;
		this.pid = pid;
	}
	
	

	@Override
	public void onDraw(GraphicsContext gc) {
		gc.setFill(Color.BLACK);
		gc.fillRect(this.xAnchor, this.yAnchor, this.width, this.height);

		gc.setFill(Color.RED);
		// when filling the range, you need to determine the relative position given
		// the min and max value. 
		double ratio = (double)pid.currentValue / (double)(pid.maxValue - pid.minValue);
		gc.fillRect(this.xAnchor, this.yAnchor, ratio * this.width, this.height);
	}

	@Override
	public void onRemove(GraphicsContext gc) {
		// TODO Used to clear out the drawing left by the gauge objects when a gauge is removed from screen. 
	}
	
}
