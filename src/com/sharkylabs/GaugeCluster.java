package com.sharkylabs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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

import com.sharkylabs.ui.BarGraph;
import com.sharkylabs.ui.IGauge;
import com.sharkylabs.ui.Tachometer;

/**
 * GaugeCluster is the entry point for viewing the data in a small dashboard format.
 * Need to add support for changing the sample rate, amongst a LOT of other things. 
 */
public class GaugeCluster extends Application {
	private static ECMInfo ecmInfo;
	private static GaugeCanvas canvas;
	
	// Streams for demo mode
	private static final int DATA_INPUT_SIZE = 10000;
	private static ArrayList<String> dataInput;

	/**
	 * The update delay in ms - controls how frequently to fire UX update events.
	 */
	private static final int UPDATE_DELAY_MSEC = 20;
	private static final boolean DEMO_MODE = false;

	public static void main(String[] args) {
		ecmInfo = new ECMInfo();
		
		if (DEMO_MODE) {
			dataInput = new ArrayList<>(DATA_INPUT_SIZE);
			Thread hT = new HammerThread();
			hT.start();
		}
		
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
	}

	/**
	 * This thread hammers random TPS data - for testing UI latency.
	 */
	@SuppressWarnings("unused")
	private static class HammerThread extends Thread {
		String[] lines = { "can0       301   [8]  00 17 05 15 05 44 06 C6\n" + "  can0       301   [8]  01 2B 01 BC 02 D8 02 00\n",
				"can0       301   [8]  00 17 05 15 05 44 06 A2\n" + "  can0       301   [8]  01 2B 01 EE 02 D8 02 00\n",
				"can0       301   [8]  00 17 05 15 05 44 06 20\n" + "  can0       301   [8]  01 2B 01 BC 02 D8 02 00\n",
				"can0       301   [8]  00 17 05 15 05 44 06 00\n" + "  can0       301   [8]  01 2B 01 EE 02 D8 02 00\n",
				"can0       301   [8]  00 17 05 15 05 44 06 20\n" + "  can0       301   [8]  01 2B 01 BC 02 D8 02 00\n",
				"can0       301   [8]  00 17 05 15 05 44 06 80\n" + "  can0       301   [8]  01 2B 01 EE 02 D8 02 00\n",
				"can0       301   [8]  00 17 05 15 05 44 06 B2\n" + "  can0       301   [8]  01 2B 01 BC 02 D8 02 00\n", };

		@Override
		public void run() {
			int currentLine = 0;
			long lastFiredEvent = 0;
			try {
				//it takes a small amount of time for the UI to init - make sure not to hammer the UI before it's done initializing 
				sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			while (true) {
				currentLine++;
				if (currentLine == lines.length) {
					currentLine = 0;
				}
				synchronized (dataInput) {
					if (dataInput.size() == DATA_INPUT_SIZE) {
						try {
							System.out.println("Buffer overrun");
							sleep(5);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						continue;
					} else {
						dataInput.add(lines[currentLine]);
					}	
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
				if (DEMO_MODE) {
					while (true) {
						String line = null;
						synchronized (dataInput) {
							if (GaugeCluster.dataInput.isEmpty()) {
								System.out.println("Buffer underrun");
								sleep(5);
								continue;
							}
							line = dataInput.get(0);
							GaugeCluster.dataInput.remove(line);	
						}
						if (line == null) throw new NullPointerException("Demo failed, NPE"); 
						ecmInfo.parseData(line);
						long currentTime = System.currentTimeMillis();
						if (canvas != null && ((currentTime - lastFiredEvent) > UPDATE_DELAY_MSEC)) {
							// TODO move code to kick off thread to stage init
							lastFiredEvent = currentTime;
							canvas.fireEvent(new ECMUpdateEvent());
							ecmInfo.printCurrentData();
						}
					}
				} else {
					BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
					String line = null;
					
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
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
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
			GraphicsContext gc = getGraphicsContext2D();
			for (IGauge gauge : gauges) {
				gauge.onDraw(gc);
			}
		}
	}

}
