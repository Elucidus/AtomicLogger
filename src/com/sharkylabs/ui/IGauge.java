package com.sharkylabs.ui;

import javafx.scene.canvas.GraphicsContext;

public interface IGauge {
	/**
	 * Called when the object has to be drawn.
	 * @param gc GraphicsContext to draw to. 
	 */
	public void onDraw(GraphicsContext gc);
	
	/**
	 * Called when the object is removed from the panel, used to clear out the area left behind.
	 * @param gc GraphicsContext to draw to. 
	 */
	public void onRemove(GraphicsContext gc);
}
