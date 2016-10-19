package com.sharkylabs;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.sharkylabs.pid.ECT;
import com.sharkylabs.pid.FuelPressure;
import com.sharkylabs.pid.IAC;
import com.sharkylabs.pid.IAT;
import com.sharkylabs.pid.RPM;
import com.sharkylabs.pid.TPS;
import com.sharkylabs.util.HexUtils;

public class ECMInfo {
	public static final int ECM_PID = 301;
	
	//row 0 data can0       301   [8]  00 17 05 15 05 44 06 C6
	public ECT ect = new ECT(0); // byte[1], byte[2]
	public IAT iat = new IAT(0); // byte[3], byte[4]
	public FuelPressure fuelPressure = new FuelPressure(0); // likely byte[6] - doesn't seem correct
	public TPS tps = new TPS(0); // byte[7

	//row 1 data can0       301   [8]  01 2B 01 00 00 D8 02 00
	public RPM rpm = new RPM(0); // likely byte[3], byte[4]
	//row 2 data can0       301   [8]  02 5D 00 EE 0D 00 00 00
	public IAC iac = new IAC(0); // likely byte[1] - doesn't seem correct
	
	//row 3 data can0       301   [8]  03 00 00 00 00 00 00 00
	
	/** 
	 * This method parses a single batch of char arrays of ECM data (rows 0-3), 
	 * it may be a subset but it has to be wholly formed lines. Elements of the data 
	 * object will be updated.  
	 * @param ecmDataTrim
	 */
	public void parseData(String ecmData) {
		if (ecmData == null || ecmData.trim().isEmpty()) { 
			System.err.println("parseData(Str): invalid input");
			return;
		}
		
		char[] ecmDataBytes = ecmData.toCharArray();
		char[][] orderedData = new char[4][];

		//sort data into ordered char arrays
		for (int i = 0; i< ecmDataBytes.length; i++) {
			if (ecmDataBytes[i] == ']') {
				i++;
				//start reading out can data for given row
				int bytesRead = 0;
				char[] dataRow = new char[16];
				while (bytesRead != dataRow.length && i < ecmDataBytes.length) {
					if (Character.isLetterOrDigit(ecmDataBytes[i])) {
						dataRow[bytesRead] = ecmDataBytes[i];
						bytesRead++;
					}
					i++;
				}
				if (bytesRead == dataRow.length) {
					//decode first pair and assign to ordered lines
					int rowId = HexUtils.hexCharArrayToInt(dataRow, 0, 2);
					if (rowId < 0 || rowId > 3) { 
						System.err.println("ECM data row ID out of bounds:" + rowId);
						continue;
					}
					orderedData[rowId] = dataRow;
				}
			} 
		}
		
		parseData(orderedData);
	}

	/**
	 * Given a canbus line, return the data segment of the line. 
	 * @param line
	 * @return
	 */
	protected static String[] getLineData(String line) {
		return line.substring(line.lastIndexOf("]") + 1).trim().split(" ");
	}
	
	/**
	 * Expects data in the format of {line0, line1,line2,line3}, any individual line 
	 * may be null, but otherwise lines must be complete. Updates the object.
	 * @param ecmDataLines
	 */
	private void parseData(char[][] ecmDataLines) {
		parseLine0(ecmDataLines[0]);
		parseLine1(ecmDataLines[1]);
		parseLine2(ecmDataLines[2]);
		parseLine3(ecmDataLines[3]);
	}

	protected static String[] getDataTokens(String dataLine) {
		return dataLine.split("]")[1].trim().split(" ");
	}
	
	
	private void parseLine0(char[] dataLine) {
		if (dataLine == null) { 
			return; 
		}

		this.ect.setValue(HexUtils.hexCharArrayToInt(dataLine, 2, 5));
		this.iat.setValue(HexUtils.hexCharArrayToInt(dataLine, 6, 9));
		
		// Fuel pressure appears to be a basic integer representing psi
		// TODO looks wrong though. 
		this.fuelPressure.setValue(HexUtils.hexCharArrayToInt(dataLine, 12, 13)); 
				
		this.tps.setValue(HexUtils.hexCharArrayToInt(dataLine, 14, 15));
	}

	private void parseLine1(char[] dataLine) {
		if (dataLine == null) { 
			return; 
		}
		
		this.rpm.setValue(HexUtils.hexCharArrayToInt(dataLine, 6, 9));//Integer.decode("0x" + s[4] + s[3])); 
	}

	private void parseLine2(char[] dataLine) {
		// TODO Auto-generated method stub
		
	}

	private void parseLine3(char[] dataLine) {
		if (dataLine == null) { 
			return; 
		}
		
		//TODO looks wrong
		this.iac.setValue(HexUtils.hexCharArrayToInt(dataLine, 2, 3)); 
	}

	public void printCurrentData() {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		System.out.println(timeStamp +":" + this.ect + this.iat+ this.fuelPressure + this.tps + this.iac);
	}
	

}
