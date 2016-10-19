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
	private static int UPDATE_DELAY_MSEC = 20;
	private static final boolean DEMO_MODE = false;
	
	// used for performance evaluation
	private static long START_TIME_MSEC;
	private static long OVERRUN_COUNT = 0, UNDERRUN_COUNT = 0;

	public static void main(String[] args) {
		ecmInfo = new ECMInfo();
		
		if (args.length >= 1) {
			if (args[0].equals("-?")) {
				System.out.println("Usage: java com.sharkylabs.GaugeCluster <polling time in msec> <demo>"
						+ "\nArguments are optional but you must specify a polling time to run \"demo\"");
				System.out.println("System info:" + com.sun.prism.GraphicsPipeline.getPipeline().getClass().getName());
				System.exit(0);
			}
			UPDATE_DELAY_MSEC = Integer.parseInt(args[0]);
		}
		
		if (DEMO_MODE || (args.length >= 2 && args[2].equals("demo"))) {
			START_TIME_MSEC = System.currentTimeMillis();
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
			boolean pastFirstOverrun = false;
			
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
						if (!pastFirstOverrun) {
							System.out.println("First overrun after: " + (System.currentTimeMillis() - START_TIME_MSEC));
							pastFirstOverrun = true;
						}
						
						System.out.println("OR:" + (++OVERRUN_COUNT - UNDERRUN_COUNT));
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
						long currentTime = System.currentTimeMillis();
						if ((currentTime - lastFiredEvent) > UPDATE_DELAY_MSEC) {
							//read 4 lines
							String line = null;
							synchronized (dataInput) {
								if (GaugeCluster.dataInput.size() < 4) {
									System.out.println("UR:" + (OVERRUN_COUNT - ++UNDERRUN_COUNT));
									continue;
								}
								line = dataInput.remove(0) + dataInput.remove(0) + dataInput.remove(0) + dataInput.remove(0);
							}
							if (canvas != null)  {
								lastFiredEvent = currentTime;
								ecmInfo.parseData(line);
								canvas.fireEvent(new ECMUpdateEvent());
								ecmInfo.printCurrentData();
							}
						} else {
							//dump up to 4 lines
							synchronized (dataInput) {
								for (int i = 0; i < 5000 && i < dataInput.size(); i++) {
									dataInput.remove(0);
								}
							}
						}
					}
				} else {
					BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
					while (true) {
						long currentTime = System.currentTimeMillis();
						if ((currentTime - lastFiredEvent) > UPDATE_DELAY_MSEC) {
							// reset the counter
							lastFiredEvent = currentTime;
							// get a full frame of data
							String line = br.readLine() + "\n" + br.readLine() + "\n"
									 + br.readLine() + "\n"  + br.readLine();
							ecmInfo.parseData(line);
							if (canvas != null)  {
								// TODO move code to kick off thread to stage init
								canvas.fireEvent(new ECMUpdateEvent());
								ecmInfo.printCurrentData();
							}
						} else {
							// dump data
							br.readLine();
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
