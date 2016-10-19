package com.sharkylabs.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class HexUtilstest {
	@Test 
	public void testHexCharArrayToInt() {
		assertEquals(255, HexUtils.hexCharArrayToInt("FF".toCharArray()));
		assertEquals(65535, HexUtils.hexCharArrayToInt("FFFF".toCharArray()));
		assertEquals(16777215, HexUtils.hexCharArrayToInt("FFFFFF".toCharArray()));
		assertEquals(1024255, HexUtils.hexCharArrayToInt("FFA00F".toCharArray()));
		
		assertEquals(255, HexUtils.hexCharArrayToInt("FFA00F".toCharArray(), 0, 1));
		assertEquals(15, HexUtils.hexCharArrayToInt("FFA00F".toCharArray(), 4, 5));
	}
}
