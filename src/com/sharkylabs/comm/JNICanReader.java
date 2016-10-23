package com.sharkylabs.comm;

public class JNICanReader {
    static {
        System.loadLibrary("JNICanReader");
    }
    /**
     * Polls the socket until a complete 0x301 frame is read (4 rows of data with unique row IDs)
     * @param interfaceName the name of the CAN interface to poll (e.g. "can0")
     * @param byteArray A 32-bit wide (4 rows, 8 bytes each) to store CAN data in. 
     * @return byteArray Same array as the parameter, for convenience. 
     */
    public static native byte[] poll(String interfaceName, byte[] byteArray);

    /**
     * Main function used for testing JNI interface. Thread sleep used to throttle polling interval to test GC intervals.
     */
    public static void main(String[] args) {
        String interfaceName = "Empty string!";
        if (args.length > 0) {
            interfaceName = args[0];
        }

	long startTime = System.currentTimeMillis();
        long iterationCount = 0;
        byte[] bytes = new byte[4*8]; 
        while (true) {
            System.out.println(bytes);
	    poll(interfaceName, bytes);
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

