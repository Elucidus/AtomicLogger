package com.sharkylabs.comm;

public class JNICanReader {
    /**
     * Opens a CAN socket to the provided interface. 
     * TODO: make this ony support one open socket interface at a time. 
     */
    public static native int openSocket(String socketId);
    /**
     * Polls the socket until a complete 0x301 frame is read (4 rows of data with unique row IDs).
     */
    public static native byte[] poll();
    /**
     * Closes the last opened socket.
     */
    public static native int closeSocket();

}

