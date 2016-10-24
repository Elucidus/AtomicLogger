package com.sharkylabs;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import com.sharkylabs.thread.CANBusPollingThread;
import com.sharkylabs.thread.HammerThread;
import com.sharkylabs.thread.InputPollingThread;
import com.sharkylabs.ui.BarGraph;
import com.sharkylabs.ui.IGauge;
import com.sharkylabs.ui.Tachometer;
import com.sharkylabs.util.GlobalConstants;

/**
 * GaugeCluster is the entry point for viewing the data in a small dashboard format.
 * Need to add support for changing the sample rate, amongst a LOT of other things. 
 */
public class GaugeCluster extends Application {
	private static ECMInfo ecmInfo;
	private static GaugeCanvas canvas;
	
	public static void main(String[] args) {
		ecmInfo = new ECMInfo();
		
		if (args.length >= 1) {
			if (args[0].equals("-?")) {
				System.out.println("Usage: " 
						+ "\njava com.sharkylabs.GaugeCluster <can interface e.g. \"can0\" <optional: polling time in msec> "
						+ "\njava com.sharkylabs.GaugeCluster \"demo\" <optional: polling time in msec> ");
				System.out.println("System info:" + com.sun.prism.GraphicsPipeline.getPipeline().getClass().getName());
				System.exit(0);
			}
			if (args.length >= 2) {
				GlobalConstants.UPDATE_DELAY_MSEC = Integer.parseInt(args[1]);
			}
			System.out.println("new update delay:" + GlobalConstants.UPDATE_DELAY_MSEC);
			if (args.length >= 1 && args[0].equals("demo")) {
				GlobalConstants.DEMO_MODE = true; 
			} else {
				// if it's not demo, the user specified a CAN interface 
				GlobalConstants.INTERFACE_NAME = args[0];
			}
		}
		
		launch(args);
	}

	public static class ECMUpdateEvent extends Event {
		private static final long serialVersionUID = 1L;

		public ECMUpdateEvent() {
			super(EVENT_TYPE_ECM_UPDATE);
		}

		public static final EventType<ECMUpdateEvent> EVENT_TYPE_ECM_UPDATE = new EventType<>(
				"ECMUpdate");
	}

	@Override
	public void start(Stage stage) throws Exception {
		Group root = new Group();
		Scene scene = new Scene(root, 640, 480, Color.LIGHTGREY);
		canvas = new GaugeCanvas(640, 480, root, ecmInfo);
		canvas.addEventHandler(ECMUpdateEvent.EVENT_TYPE_ECM_UPDATE,
				new EventHandler<ECMUpdateEvent>() {

					@Override
					public void handle(ECMUpdateEvent arg0) {
						canvas.redraw();
					}

				});
		canvas.redraw();

		root.getChildren().add(canvas);
		canvas.doPostInit();
		stage.setScene(scene);
		stage.show();
		
		if (!GlobalConstants.DEMO_MODE) {
			Thread canThread = new CANBusPollingThread(ecmInfo, canvas);
			canThread.start();
		} else {
			System.out.println("Initializing demo mode.");
			Thread hT = new HammerThread();
			hT.start();
			Thread t = new InputPollingThread(ecmInfo, canvas);
			t.start();
		}
	}

	
	
	private class GaugeCanvas extends Canvas {
		//needed to add custom canvases for transforms 
		private BarGraph tpsGraph;
		private BarGraph ectGraph;
		private BarGraph iatGraph;
		private BarGraph fuelPressureGraph;
		private BarGraph iacGraph;
		
		private Tachometer tach;
		
		private long startTime = 0;
		private int drawCount = 0;
		
		private ArrayList<IGauge> gauges = new ArrayList<>(3);
		
		public GaugeCanvas(int width, int height, Group root, ECMInfo ecmInfo) {
			super(width, height);
			this.tpsGraph = new BarGraph(10, 10, 100, 25, ecmInfo.tps);
			this.ectGraph = new BarGraph(10, 40, 100, 25, ecmInfo.ect);
			this.iatGraph = new BarGraph(10, 70, 100, 25, ecmInfo.iat);
			this.fuelPressureGraph = new BarGraph(10, 100, 100, 25, ecmInfo.fuelPressure);
			this.iacGraph = new BarGraph(10, 130, 100, 25, ecmInfo.iac);
			this.tach = new Tachometer(root, 150, 10, ecmInfo.rpm);
			
			gauges.add(tpsGraph);
			gauges.add(ectGraph);
			gauges.add(iatGraph);
			gauges.add(fuelPressureGraph);
			gauges.add(iacGraph);
			gauges.add(tach);
		}

		public void doPostInit() {
			for (IGauge gauge : gauges) {
				gauge.onPostInit();;
			}
		}

		public void redraw() {
			if (startTime == 0) {
				startTime = System.currentTimeMillis();
			}
			GraphicsContext gc = getGraphicsContext2D();
			for (IGauge gauge : gauges) {
				gauge.onDraw(gc);
			}
			
			drawCount++;
			System.out.println("FPS:" + (drawCount * 1000 / (System.currentTimeMillis() - startTime)));
		}
	}

}
