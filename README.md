# AtomicLogger
A Java application that translates CAN data from an aftermarket EFI system into human readable displays.

**Usage**
Right now, there clearly isn't a build infrastructure for this. 

1. Build the JNI libraries manually:
> AtomicLogger/jni $ gcc JNICanReader.c -I$JAVA_HOME/include -I$JAVA_HOME/include/linux -shared -o libJNICanReader.so
2. Compile the various class files and set your classpath, then run GaugeCluster:
> AtomicLogger $ java -classpath src com.sharkylabs.GaugeCluster
*Arguments for GaugeCluster*



**How to set up a virtul CAN interface**
$ modprobe can
$ modprobe can_raw
$ modprobe vcan
$ sudo ip link add dev vcan0 type vcan
$ sudo ip link set up vcan0


