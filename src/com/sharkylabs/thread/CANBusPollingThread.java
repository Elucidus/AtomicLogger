package com.sharkylabs.thread;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javafx.scene.canvas.Canvas;

import com.sharkylabs.ECMInfo;
import com.sharkylabs.GaugeCluster;
import com.sharkylabs.comm.JNICanReader;
import com.sharkylabs.util.GlobalConstants;

/**
 * This thread polls socketcan for CAN data line by line.
 */
public class CANBusPollingThread extends Thread {
	// Streams for demo mode
	private ECMInfo ecmInfo;
	private Canvas canvas; 
	private byte[] byteBuffer = new byte[ECMInfo.DATA_ROW_WIDTH * ECMInfo.DATA_ROW_COUNT];
		
	public CANBusPollingThread(ECMInfo ecmInfo, Canvas canvas) {
		this.ecmInfo = ecmInfo;
		this.canvas = canvas; 
	}

		@Override
		public void run() {
			try {
				long lastFiredEvent = 0;
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				while (true) {
					long currentTime = System.currentTimeMillis();
					if ((currentTime - lastFiredEvent) > GlobalConstants.UPDATE_DELAY_MSEC) {
						// reset the counter
						lastFiredEvent = currentTime;
						// get a full frame of data
						ecmInfo.parseData(JNICanReader.poll(GlobalConstants.INTERFACE_NAME, byteBuffer));
						if (canvas != null)  {
							canvas.fireEvent(new GaugeCluster.ECMUpdateEvent());
							ecmInfo.printCurrentData();
						}
					} else {
						// dump data
						br.readLine();
					}
					
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}