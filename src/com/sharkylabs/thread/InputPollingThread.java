package com.sharkylabs.thread;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javafx.scene.canvas.Canvas;

import com.sharkylabs.ECMInfo;
import com.sharkylabs.GaugeCluster;
import com.sharkylabs.util.GlobalConstants;

/**
 * This thread polls stdin for CAN data line by line.
 * TODO: This and its peer HammerThread are for simulations, so the code is a fair bit 
 * messier than others.
 */
public class InputPollingThread extends Thread {
	// Streams for demo mode
	protected static final int DATA_INPUT_SIZE = 10000;
	protected static ArrayList<String> dataInput;
	
	private ECMInfo ecmInfo;
	private Canvas canvas; 
		
	public InputPollingThread(ECMInfo ecmInfo, Canvas canvas) {
		this.ecmInfo = ecmInfo;
		this.canvas = canvas; 
	}

		@Override
		public void run() {
			try {
				sleep(1000); // give UI time to start
				long lastFiredEvent = 0;
				int lineCount = 0; 
				if (GlobalConstants.DEMO_MODE) {
					while (true) {
						long currentTime = System.currentTimeMillis();
						if ((currentTime - lastFiredEvent) > GlobalConstants.UPDATE_DELAY_MSEC) {
							//read 4 lines
//							String line = null;
//							synchronized (dataInput) {
//								if (GaugeCluster.dataInput.size() < 4) {
////									System.out.println("UR:" + (OVERRUN_COUNT - ++UNDERRUN_COUNT));
//									continue;
//								}
//								line = dataInput.remove(0) + dataInput.remove(0) + dataInput.remove(0) + dataInput.remove(0);
//							}
							if (canvas != null)  {
								lastFiredEvent = currentTime;
								ecmInfo.parseData(HammerThread.lines[lineCount]);
								lineCount = ++lineCount == HammerThread.lines.length ? 0 : lineCount;
								canvas.fireEvent(new GaugeCluster.ECMUpdateEvent());
								ecmInfo.printCurrentData();
							}
						} else {
							//dump up to 4 lines
							continue;
//							synchronized (dataInput) {
//								for (int i = 0; i < 5000 && i < dataInput.size(); i++) {
//									dataInput.remove(0);
//								}
//							}
						}
					}
				} else {
					BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
					while (true) {
						long currentTime = System.currentTimeMillis();
						if ((currentTime - lastFiredEvent) > GlobalConstants.UPDATE_DELAY_MSEC) {
							// reset the counter
							lastFiredEvent = currentTime;
							// get a full frame of data
							String line = br.readLine() + "\n" + br.readLine() + "\n"
									 + br.readLine() + "\n"  + br.readLine();
							ecmInfo.parseData(line);
							if (canvas != null)  {
								// TODO move code to kick off thread to stage init
								canvas.fireEvent(new GaugeCluster.ECMUpdateEvent());
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