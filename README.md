# AtomicLogger
A Java application that translates CAN data from an aftermarket EFI system into human readable displays.

**How to set up a virtul CAN interface**
$ modprobe can
$ modprobe can_raw
$ modprobe vcan
$ sudo ip link add dev vcan0 type vcan
$ sudo ip link set up vcan0


