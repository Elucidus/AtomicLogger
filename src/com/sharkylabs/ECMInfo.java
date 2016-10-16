package com.sharkylabs;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.sharkylabs.pid.ECT;
import com.sharkylabs.pid.FuelPressure;
import com.sharkylabs.pid.IAC;
import com.sharkylabs.pid.IAT;
import com.sharkylabs.pid.TPS;

public class ECMInfo {
	public static final int ECM_PID = 301;
	
	//row 0 data can0       301   [8]  00 17 05 15 05 44 06 C6
	public ECT ect = new ECT(0); // byte[1], byte[2]
	public IAT iat = new IAT(0); // byte[3], byte[4]
	public FuelPressure fuelPressure; // likely byte[6] 
	public TPS tps = new TPS(0); // byte[7

	//row 1 data can0       301   [8]  01 2B 01 00 00 D8 02 00
	
	//row 2 data can0       301   [8]  02 5D 00 EE 0D 00 00 00
	public IAC iac; // likely byte[1]
	
	//row 3 data can0       301   [8]  03 00 00 00 00 00 00 00
	
	/** 
	 * This method parses a single batch of strings of ECM data (rows 0-3), 
	 * it may be a subset but it has to be wholly formed lines. Elements of the data 
	 * object will be updated.  
	 * @param ecmDataTrim
	 */
	public void parseData(String ecmData) {
		if (ecmData == null || ecmData.trim().isEmpty()) { 
			System.err.println("parseData(Str): invalid input");
			return;
		}
		String ecmDataTrim = ecmData.trim();
		String[] lines = ecmDataTrim.split("\n");
		String[] orderedLines = new String[4];
		
		// parse all the lines in the blob
		// note that the latest data will clobber the earliest data if multiple lines with the
		// same line ID are in the blob. 
		for (String line : lines) {
			String[] lineData = getLineData(line);
			Integer lineID = Integer.decode("0x" + lineData[0]);
			if (lineID < 0 || lineID > 3) { 
				System.err.println("ECM data row ID out of bounds:" + lineID);
				continue;
			}
			
			orderedLines[lineID] = line;
		}
		if (allLinesNull(orderedLines)) {
			System.err.println("No ECM lines found in input");
			return;
		}
		
		parseData(orderedLines);
	}

	private boolean allLinesNull(String[] orderedLines) {
		for (String line : orderedLines) {
			if (line != null) {
				return false;
			}
		}
 		return true;
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
	 * may be null, but otherwies lines must be complete. Updates the object.
	 * @param ecmDataLines
	 */
	public void parseData(String[] ecmDataLines) {
		parseLine0(ecmDataLines[0]);
		parseLine1(ecmDataLines[1]);
		parseLine2(ecmDataLines[2]);
		parseLine3(ecmDataLines[3]);
	}

	protected static String[] getDataTokens(String dataLine) {
		return dataLine.split("]")[1].trim().split(" ");
	}
	
	
	private void parseLine0(String dataLine) {
		if (dataLine == null || dataLine.isEmpty()) { 
			return; 
		}
		String[] s = getDataTokens(dataLine);

		this.ect.setValue(Integer.decode("0x" + s[2] + s[1]));
		this.iat.setValue(Integer.decode("0x" + s[4] + s[3]));
		
		// Fuel pressure appears to be a basic integer representing psi
		this.fuelPressure = new FuelPressure(Integer.decode("0x" + s[6])); 
				
		this.tps.setValue(Integer.decode("0x" + s[7]));
	}

	private void parseLine1(String dataLine) {
		// TODO Auto-generated method stub
		
	}

	private void parseLine2(String dataLine) {
		// TODO Auto-generated method stub
		
	}

	private void parseLine3(String dataLine) {
		if (dataLine == null || dataLine.isEmpty()) { 
			return; 
		}
		String[] s = getDataTokens(dataLine);
		
		this.iac = new IAC(Integer.decode("0x" + s[1])); 
	}

	public void printCurrentData() {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		System.out.println(timeStamp +":" + this.ect + this.iat + " fuel[" + this.fuelPressure + "psi]"
				 + this.tps + " IAC[" + this.iac +"steps]");
	}
	

}
