package com.sharkylabs;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * CANReader takes input from stdin and dumps the CAN data to output. 
 */
public class CANReader {

	public static void main(String[] args) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String line;
			ECMInfo ecmInfo = null;
			while ((line = br.readLine()) != null) {
				if (ecmInfo == null) {
					ecmInfo = new ECMInfo(line);
				} else {
					ecmInfo.parseData(line);
				}
				ecmInfo.printCurrentData();
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
	}
}