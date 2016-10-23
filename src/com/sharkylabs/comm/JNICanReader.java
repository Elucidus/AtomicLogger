package com.sharkylabs.comm;

public class JNICanReader {
    static {
        System.loadLibrary("JNICanReader");
    }
    /**
     * Polls the socket until a complete 0x301 frame is read (4 rows of data with unique row IDs).
     */
    public static native byte[] poll(String interfaceName);

    /**
     * Main function used for testing JNI interface. Thread sleep used to throttle polling interval to test GC intervals.
     * TODO: decrease cost by making a static buffer for polling, add as param in poll(). 
     */
    public static void main(String[] args) {
        String interfaceName = "Empty string!";
        if (args.length > 0) {
            interfaceName = args[0];
        }

	long startTime = System.currentTimeMillis();
        long iterationCount = 0;
        byte[] bytes; 
        while (true) {
	    bytes = poll(interfaceName);
            iterationCount++;
            long timePastSec = (System.currentTimeMillis() - startTime) / 1000 ;
            if (timePastSec != 0) {
                System.out.println("cycles per sec:" + (iterationCount / timePastSec));
            }
            try {
                Thread.sleep(7);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

