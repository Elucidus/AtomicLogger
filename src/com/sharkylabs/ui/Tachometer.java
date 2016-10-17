package com.sharkylabs.ui;

import com.sharkylabs.pid.AbstractPID;

import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.transform.Rotate;

public class Tachometer implements IGauge {

	// layout fields
	private double xAnchor;
	private double yAnchor;
	
	// data fields
	private AbstractPID pid;

	// image cache
	private Image tachBG;
	private Image tachNeedle;
	
	private Group root;
	private Canvas needleCanvas; 
	
	public Tachometer(Group root, double xAnchor, double yAnchor, AbstractPID pid) {
		super();
		this.xAnchor = xAnchor;
		this.yAnchor = yAnchor;
		this.pid = pid;
		String currdir = System.getProperty("user.dir");
		
		//TODO: support desktop working dir (and linux, use File.PathSeparator here), as well as JAR
		this.tachBG = new Image("file:" + currdir + "\\res\\tachometerBG.png");
		this.tachNeedle = new Image("file:" + currdir + "\\res\\tachometerNeedle.png");
		
		this.root = root;
		needleCanvas = new Canvas(640, 480); //TODO: abstract canvas size
	}
	
	double min = 0.0, max = 8000.0; 
	double currentRPM = 0;
	boolean accelerating = true;
	@Override
	public void onDraw(GraphicsContext gc) {
		gc.drawImage(this.tachBG, this.xAnchor, this.yAnchor);
		needleCanvas.getGraphicsContext2D().clearRect(0, 0, 640, 480);

		// range for the image, 0 = -144, redline = 144
		double rpmAngle = (currentRPM / max) * 288 - 144;
		if (accelerating) {
			currentRPM += 20;
		} else {
			currentRPM -= 20;
		}
		if (currentRPM >= 8000 || currentRPM <= 0) {
//			accelerating = !accelerating;
			currentRPM = 4500;
		}
		drawRotatedImage(needleCanvas.getGraphicsContext2D(), this.tachNeedle, rpmAngle, this.xAnchor, this.yAnchor);
	}

	@Override
	public void onRemove(GraphicsContext gc) {
		// TODO Used to clear out the drawing left by the gauge objects when a gauge is removed from screen. 
		// Should remove needle canvas from parent!
	}



	@Override
	public void onPostInit() {
		root.getChildren().add(needleCanvas);
	}
	
	// Needle transform methods taken from http://stackoverflow.com/questions/18260421/how-to-draw-image-rotated-on-javafx-canvas
   /**
     * Sets the transform for the GraphicsContext to rotate around a pivot point.
     *
     * @param gc the graphics context the transform to applied to.
     * @param angle the angle of rotation.
     * @param px the x pivot co-ordinate for the rotation (in canvas co-ordinates).
     * @param py the y pivot co-ordinate for the rotation (in canvas co-ordinates).
     */
    private void rotate(GraphicsContext gc, double angle, double px, double py) {
        Rotate r = new Rotate(angle, px, py);
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
    }

    /**
     * Draws an image on a graphics context.
     *
     * The image is drawn at (tlpx, tlpy) rotated by angle pivoted around the point:
     *   (tlpx + image.getWidth() / 2, tlpy + image.getHeight() / 2)
     *
     * @param gc the graphics context the image is to be drawn on.
     * @param angle the angle of rotation.
     * @param tlpx the top left x co-ordinate where the image will be plotted (in canvas co-ordinates).
     * @param tlpy the top left y co-ordinate where the image will be plotted (in canvas co-ordinates).
     */
    private void drawRotatedImage(GraphicsContext gc, Image image, double angle, double tlpx, double tlpy) {
        gc.save(); // saves the current state on stack, including the current transform
        rotate(gc, angle, tlpx + image.getWidth() / 2, tlpy + image.getHeight() / 2);
        gc.drawImage(image, tlpx, tlpy);
        gc.restore(); // back to original state (before rotation)
    }
}
