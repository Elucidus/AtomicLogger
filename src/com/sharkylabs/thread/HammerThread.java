package com.sharkylabs.thread;

/**
 * This thread hammers random data - for testing UI latency.
 * TODO: This and its peer InputPollingThread are for simulations, so the code is a fair bit 
 * messier than others.
 */
public class HammerThread extends Thread {
	static String[] lines = { "can0       301   [8]  00 17 05 15 05 44 06 C6\n" + "  can0       301   [8]  01 2B 01 BC 02 D8 02 00\n",
		"can0       301   [8]  00 17 05 15 05 44 06 A2\n" + "  can0       301   [8]  01 2B 01 EE 02 D8 02 00\n",
		"can0       301   [8]  00 17 05 15 05 44 06 20\n" + "  can0       301   [8]  01 2B 01 BC 02 D8 02 00\n",
		"can0       301   [8]  00 17 05 15 05 44 06 00\n" + "  can0       301   [8]  01 2B 01 EE 02 D8 02 00\n",
		"can0       301   [8]  00 17 05 15 05 44 06 20\n" + "  can0       301   [8]  01 2B 01 BC 02 D8 02 00\n",
		"can0       301   [8]  00 17 05 15 05 44 06 80\n" + "  can0       301   [8]  01 2B 01 EE 02 D8 02 00\n",
		"can0       301   [8]  00 17 05 15 05 44 06 B2\n" + "  can0       301   [8]  01 2B 01 BC 02 D8 02 00\n", };
	
	@Override
	public void run() {
		int currentLine = 0;
		boolean pastFirstOverrun = false;

		try {
			// it takes a small amount of time for the UI to init - make sure
			// not to hammer the UI before it's done initializing
			sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		while (true) {
			currentLine++;
			if (currentLine == lines.length) {
				currentLine = 0;
			}
			synchronized (InputPollingThread.dataInput) {
				if (InputPollingThread.dataInput.size() == InputPollingThread.DATA_INPUT_SIZE) {
					if (!pastFirstOverrun) {
						// System.out.println("First overrun after: " +
						// (System.currentTimeMillis() - START_TIME_MSEC));
						pastFirstOverrun = true;
					}

					// System.out.println("OR:" + (++OVERRUN_COUNT -
					// UNDERRUN_COUNT));
					continue;
				} else {
					InputPollingThread.dataInput.add(lines[currentLine]);
				}
			}
		}
	}
}
