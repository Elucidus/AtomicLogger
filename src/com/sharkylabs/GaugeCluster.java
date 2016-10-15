package com.sharkylabs;

import java.io.BufferedReader;
import java.io.InputStreamReader;

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
		Scene scene = new Scene(root, 300, 300, Color.LIGHTGREY);
		canvas = new GaugeCanvas(640, 480);
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
			while (true) {
				if (ecmInfo == null) {
					ecmInfo = new ECMInfo(lines[currentLine]);
				} else {
					ecmInfo.parseData(lines[currentLine]);
				}
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
				while ((line = br.readLine()) != null) {
					if (ecmInfo == null) {
						ecmInfo = new ECMInfo(line);
					} else {
						ecmInfo.parseData(line);
					}
					long currentTime = System.currentTimeMillis();
					if (canvas != null && ((currentTime - lastFiredEvent) > UPDATE_DELAY_MSEC)) {
						// TODO move code to kick off thread to stage init
						lastFiredEvent = currentTime;
						canvas.fireEvent(new ECMUpdateEvent());
					}
//					ecmInfo.printCurrentData();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class GaugeCanvas extends Canvas {
		public GaugeCanvas(int width, int height) {
			super(width, height);
		}

		public void redraw() {
			GraphicsContext gc = getGraphicsContext2D();
			gc.setFill(Color.BLACK);
			gc.fillRect(10, 10, 100, 25);

			if (ecmInfo != null) {
				gc.setFill(Color.RED);
				gc.fillRect(10, 10, ecmInfo.throttlePosition, 25);
			}
		}
	}

}
