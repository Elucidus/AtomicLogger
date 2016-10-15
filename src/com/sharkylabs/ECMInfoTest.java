package com.sharkylabs;

import static org.junit.Assert.*;

import org.junit.Test;

public class ECMInfoTest {
	String infoBlob = "  can0       301   [8]  00 17 05 15 05 44 06 C6\n"
					  + "  can0       301   [8]  01 2B 01 00 00 D8 02 00\n"
					  + "  can0       301   [8]  02 5D 00 EE 0D 00 00 00\n"
					  + "  can0       301   [8]  03 00 00 00 00 00 00 00";
	
	@Test 
	public void testECMInfo() {
		ECMInfo info = new ECMInfo(infoBlob);
		assertEquals(99, info.throttlePosition);
		
		String infoMultiLine00
				  = "  can0       301   [8]  00 17 05 15 05 44 06 C6\n"
				  + "  can0       301   [8]  00 2B 01 00 00 D8 02 00\n"
				  + "  can0       301   [8]  00 5D 00 EE 0D 00 00 00\n"
				  + "  can0       301   [8]  00 00 00 00 00 00 00 04";
		info.parseData(infoMultiLine00);
		assertEquals(2, info.throttlePosition);
	}
	@Test
	public void testDataLines() {
		String[] strings = ECMInfo.getDataTokens(" can0       301   [8]  00 17 05 15 05 44 06 C6");
		assertNotNull(strings);
		assertEquals(8, strings.length);
	}
	
	@Test
	public void testECMInfoTPSChange() {
		ECMInfo info = new ECMInfo(infoBlob);
		assertEquals(99, info.throttlePosition);
		
		//Try 50
		String tps50Blob = "  can0       301   [8]  00 17 05 15 05 44 06 64\n";
		info.parseData(new String[] {tps50Blob, null, null, null});
		assertEquals(50, info.throttlePosition);
		//Try 100
		String tps100Blob = "  can0       301   [8]  00 17 05 15 05 44 06 C8\n";
		info.parseData(new String[] {tps100Blob, null, null, null});
		assertEquals(100, info.throttlePosition);
		//try 25
		String tps25Blob = "  can0       301   [8]  00 17 05 15 05 44 06 32\n";
		info.parseData(new String[] {tps25Blob, null, null, null});
		assertEquals(25, info.throttlePosition);
	}

	@Test
	public void testGetLineData() {
		assertArrayEquals(new String[] {"00", "17", "05", "15", "05", "44", "06", "C8"}, 
				ECMInfo.getLineData("  can0       301   [8]  00 17 05 15 05 44 06 C8\n"));
	}
}
