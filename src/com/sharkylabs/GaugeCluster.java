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

public class GaugeCluster extends Application {
	private static ECMInfo ecmInfo;
	private static GaugeCanvas canvas; 
	
	public static void main(String[] args) {
		Thread t = new Thread() {

			@Override
			public void run() {
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(
							System.in));
					String line;
					while ((line = br.readLine()) != null) {
						if (ecmInfo == null) {
							ecmInfo = new ECMInfo(line);
						} else {
							ecmInfo.parseData(line);
						}
						canvas.fireEvent(new ECMUpdateEvent());
						ecmInfo.printCurrentData();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
		launch(args);
	}

	private static class ECMUpdateEvent extends Event {
		public ECMUpdateEvent() {
			super(EVENT_TYPE_ECM_UPDATE);
		}
	
		public static final EventType<ECMUpdateEvent> EVENT_TYPE_ECM_UPDATE = new EventType<>("ECMUpdate");
	}
	@Override
	public void start(Stage stage) throws Exception {
		Group root = new Group();
		Scene scene = new Scene(root, 300, 300, Color.LIGHTGREY);
		canvas = new GaugeCanvas(640, 480);
		canvas.addEventHandler(ECMUpdateEvent.EVENT_TYPE_ECM_UPDATE, new EventHandler<ECMUpdateEvent>() {

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
