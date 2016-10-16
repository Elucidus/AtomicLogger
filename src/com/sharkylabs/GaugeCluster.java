package com.sharkylabs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.sharkylabs.ui.BarGraph;
import com.sharkylabs.ui.IGauge;

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

/**
 * GaugeCluster is the entry point for viewing the data in a small dashboard format.
 * Need to add support for changing the sample rate, amongst a LOT of other things. 
 */
public class GaugeCluster extends Application {
	private static ECMInfo ecmInfo;
	private static GaugeCanvas canvas;
	
	/**
	 * The update delay in ms - controls how frequently to fire UX update events.
	 */
	private static final int UPDATE_DELAY_MSEC = 50;

	public static void main(String[] args) {
//		Thread t = new HammerThread();
		Thread t = new PollingThread();
		t.start();
		launch(args);
	}

	private static class ECMUpdateEvent extends Event {
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
		canvas = new GaugeCanvas(640, 480, ecmInfo);
		canvas.addEventHandler(ECMUpdateEvent.EVENT_TYPE_ECM_UPDATE,
				new EventHandler<ECMUpdateEvent>() {

					@Override
					public void handle(ECMUpdateEvent arg0) {
						canvas.redraw();
					}

				});
		canvas.redraw();

		root.getChildren().add(canvas);
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * This thread hammers random TPS data - for testing UI latency.
	 */
	@SuppressWarnings("unused")
	private static class HammerThread extends Thread {
		String[] lines = { "can0       301   [8]  00 17 05 15 05 44 06 C6",
				"can0       301   [8]  00 17 05 15 05 44 06 A2",
				"can0       301   [8]  00 17 05 15 05 44 06 20",
				"can0       301   [8]  00 17 05 15 05 44 06 0",
				"can0       301   [8]  00 17 05 15 05 44 06 20",
				"can0       301   [8]  00 17 05 15 05 44 06 80",
				"can0       301   [8]  00 17 05 15 05 44 06 B2", };

		@Override
		public void run() {
			int currentLine = 0;
			long lastFiredEvent = 0;
			
			if (ecmInfo == null) {
				ecmInfo = new ECMInfo();
			} 
			while (true) {
				ecmInfo.parseData(lines[currentLine]);
				currentLine++;
				if (currentLine == lines.length) {
					currentLine = 0;
				}
				long currentTime = System.currentTimeMillis();
				if (canvas != null && ((currentTime - lastFiredEvent) > UPDATE_DELAY_MSEC)) {
					// TODO move code to kick off thread to stage init
					lastFiredEvent = currentTime;
					canvas.fireEvent(new ECMUpdateEvent());
				}
				try {
					sleep(UPDATE_DELAY_MSEC);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * This thread polls stdin for CAN data line by line.
	 */
	private static class PollingThread extends Thread {
		@Override
		public void run() {
			try {
				long lastFiredEvent = 0;
				BufferedReader br = new BufferedReader(new InputStreamReader(
						System.in));
				String line;
				if (ecmInfo == null) {
					ecmInfo = new ECMInfo();
				} 
				while ((line = br.readLine()) != null) {
					ecmInfo.parseData(line);
					long currentTime = System.currentTimeMillis();
					if (canvas != null && ((currentTime - lastFiredEvent) > UPDATE_DELAY_MSEC)) {
						// TODO move code to kick off thread to stage init
						lastFiredEvent = currentTime;
						canvas.fireEvent(new ECMUpdateEvent());
						ecmInfo.printCurrentData();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class GaugeCanvas extends Canvas {
		private BarGraph tpsGraph;
		private BarGraph ectGraph;
		private BarGraph iatGraph;
		private BarGraph fuelPressureGraph;
		private BarGraph iacGraph;
		
		private ArrayList<IGauge> gauges = new ArrayList<>(3);
		
		public GaugeCanvas(int width, int height, ECMInfo ecmInfo) {
			super(width, height);
			this.tpsGraph = new BarGraph(10, 10, 100, 25, ecmInfo.tps);
			this.ectGraph = new BarGraph(10, 40, 100, 25, ecmInfo.ect);
			this.iatGraph = new BarGraph(10, 70, 100, 25, ecmInfo.iat);
			this.fuelPressureGraph = new BarGraph(10, 100, 100, 25, ecmInfo.fuelPressure);
			this.iacGraph = new BarGraph(10, 130, 100, 25, ecmInfo.iac);
			
			gauges.add(tpsGraph);
			gauges.add(ectGraph);
			gauges.add(iatGraph);
			gauges.add(fuelPressureGraph);
			gauges.add(iacGraph);
		}

		public void redraw() {
			GraphicsContext gc = getGraphicsContext2D();
			for (IGauge gauge : gauges) {
				gauge.onDraw(gc);
			}
		}
	}

}
