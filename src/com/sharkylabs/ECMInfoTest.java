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
		ECMInfo info = new ECMInfo();
		info.parseData(infoBlob);
		assertEquals(99, info.tps.currentValue);
		
		String infoMultiLine00
				  = "  can0       301   [8]  00 17 05 15 05 44 06 C6\n"
				  + "  can0       301   [8]  00 2B 01 00 00 D8 02 00\n"
				  + "  can0       301   [8]  00 5D 00 EE 0D 00 00 00\n"
				  + "  can0       301   [8]  00 00 00 00 00 00 00 04";
		info.parseData(infoMultiLine00);
		assertEquals(2, info.tps.currentValue);
	}
	@Test
	public void testDataLines() {
		String[] strings = ECMInfo.getDataTokens(" can0       301   [8]  00 17 05 15 05 44 06 C6");
		assertNotNull(strings);
		assertEquals(8, strings.length);
	}
	
	@Test
	public void testECTAndIAT() {
		String ect133Blob = "  can0       301   [8]  00 25 05 15 05 44 06 64\n";
		ECMInfo info = new ECMInfo();
		info.parseData(ect133Blob);
		assertEquals(132, info.ect.currentValue);
		
		String ect275Blob = "  can0       301   [8]  00 60 06 15 05 44 06 64\n";
		info.parseData(ect275Blob);
		assertEquals(275, info.ect.currentValue);
		
		String ect102Blob = "  can0       301   [8]  00 E2 04 15 05 44 06 64\n";
		info.parseData(ect102Blob);
		assertEquals(102, info.ect.currentValue);

		//test IAT 
		//IAT from previous group should be 125
		assertEquals(125, info.iat.currentValue);
		String iat102Blob = "  can0       301   [8]  00 E2 04 E2 04 44 06 64\n";
		info.parseData(iat102Blob);
		assertEquals(102, info.iat.currentValue);
	}
	
	@Test
	public void testECMInfoTPSChange() {
		ECMInfo info = new ECMInfo();
		info.parseData(infoBlob);
		assertEquals(99, info.tps.currentValue);
		
		//Try 50
		String tps50Blob = "  can0       301   [8]  00 17 05 15 05 44 06 64\n";
		info.parseData(new String[] {tps50Blob, null, null, null});
		assertEquals(50, info.tps.currentValue);
		//Try 100
		String tps100Blob = "  can0       301   [8]  00 17 05 15 05 44 06 C8\n";
		info.parseData(new String[] {tps100Blob, null, null, null});
		assertEquals(100, info.tps.currentValue);
		//try 25
		String tps25Blob = "  can0       301   [8]  00 17 05 15 05 44 06 32\n";
		info.parseData(new String[] {tps25Blob, null, null, null});
		assertEquals(25, info.tps.currentValue);
	}

	@Test
	public void testGetLineData() {
		assertArrayEquals(new String[] {"00", "17", "05", "15", "05", "44", "06", "C8"}, 
				ECMInfo.getLineData("  can0       301   [8]  00 17 05 15 05 44 06 C8\n"));
	}
}
