package com.sharkylabs;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CANReader {

	public static void main(String[] args) {
		try {
//			BufferedReader br = new BufferedReader(new FileReader(new File("./candump")));
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String line;
//			int c = 0;
//			while ((line = br.readLine()) != null) {
//				String[] toks = line.split(" ");
//				System.out.println("\n" + ++c + ":" + line);
//				for (String s : toks) {
//					if (!s.trim().equals(""))
//						System.out.print("tok:" + s + " ");
//				}
//			}
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