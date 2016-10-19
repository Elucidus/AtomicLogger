package com.sharkylabs.util;

public class HexUtils {
	
	
	public static int hexCharArrayToInt(char[] hex) {
		return hexCharArrayToInt(hex, 0, hex.length);
	}
	
	
	/**
	 * Converts a character array to its integer value. Assumes most significant
	 * bytes are subsequent. IE "010A" is 0x0A01. If you call this method with "010A", 
	 * 0,2 you will receive the integer value 0x01 (decimal:1).  
	 * 
	 * @param hex Array to convert.
	 * @param begin Index to start reading from.
	 * @param end Index to read until.
	 * @return Integer representation.
	 */
	public static int hexCharArrayToInt(char[] hex, int begin, int end) {
		//only supports 2 digit hex right now :) 
		if (hex == null) {
			throw new IllegalArgumentException(
					"invalid hex array");
		}
		if ((end-begin+1) % 2 != 0) {
			throw new IllegalArgumentException(
					"Currently only supports longform.");
		}
		if (begin < 0 || begin > end || begin >= hex.length) {
			throw new IllegalArgumentException(
					"begin out of range.");
		}
		
		if (end < 0 || begin > end || end > hex.length) {
			throw new IllegalArgumentException(
					"end out of range.");
		}

		int hexResult = 0;
		for (int i = begin; i < end; i += 2) {
			hexResult+= getIntValue(hex[i+1]) << ((i-begin)*4);
			hexResult+= getIntValue(hex[i]) << ((i-begin+1)*4);
			
		}
		return hexResult;
	}

	private static int getIntValue(char hex) {
		switch (hex) {
			case '0':
				return 0;
			case '1':
				return 1;
			case '2':
				return 2;
			case '3':
				return 3;
			case '4':
				return 4;
			case '5':
				return 5;
			case '6':
				return 6;
			case '7':
				return 7;
			case '8':
				return 8;
			case '9':
				return 9;
			case 'A':
				return 10;
			case 'B':
				return 11;
			case 'C':
				return 12;
			case 'D':
				return 13;
			case 'E':
				return 14;
			case 'F':
				return 15;
			default: 
				throw new IllegalArgumentException("Unsupported value:" + hex);
		}
	}
}
