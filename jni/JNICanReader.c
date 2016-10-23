#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>

#include <net/if.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/ioctl.h>

#include <linux/can.h>
#include <linux/can/raw.h>

#include <jni.h>

#include "JNICanReader.h"

/*
 * Class:     com_sharkylabs_comm_JNICanReader
 * Method:    poll
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_sharkylabs_comm_JNICanReader_poll(JNIEnv * env, jclass class, jstring interfaceJString) { 

    int g_socketId;
    struct sockaddr_can addr;
    struct ifreq ifr;
    struct can_frame frames[4];
    struct can_frame temp;
    int frameRead[4];
    int bytesRead;
    int i;
    char canFrameBytes[4*8]; // 4 rows of 8 bytes, but contained serially
    const char * interfaceName = (*env)->GetStringUTFChars(env, interfaceJString, NULL);
    
    memset(frameRead, 0, sizeof(int)*4);

    if (NULL == interfaceName) {
        printf("Error opening socket, invalid socket name.\n");
        return NULL;
    }

    if((g_socketId = socket(PF_CAN, SOCK_RAW, CAN_RAW)) < 0) {
        perror("Error while opening socket");
        return NULL;
    }

    strcpy(ifr.ifr_name, interfaceName);
    ioctl(g_socketId, SIOCGIFINDEX, &ifr);

    addr.can_family  = AF_CAN;
    addr.can_ifindex = ifr.ifr_ifindex;

    // printf("%s at index %d\n", interfaceName, ifr.ifr_ifindex);

    if(bind(g_socketId, (struct sockaddr *)&addr, sizeof(addr)) < 0) {
            perror("Error in socket bind");
            return NULL;
    }
   
    while (!frameRead[0] || !frameRead[1] || !frameRead[2] || !frameRead[3]) {
            // printf("Polling...\n");
            bytesRead = read(g_socketId, &temp, sizeof(struct can_frame));
            // printf("Read %d bytes %d.%d\n", bytesRead, temp.can_id, temp.data[0]);
            frames[temp.data[0]] = temp;
            frameRead[temp.data[0]] = 1;
    }

    for (i = 0; i < 4; i++) {
        // printf("frameread[%d]:%d\n", i, frameRead[i]);
        // printf("i %d i*8 %d\n", i, i*8);
        // printf("ID: %d data %d %d %d %d\n", frames[i].can_id, frames[i].data[0], frames[i].data[1], frames[i].data[2], frames[i].data[3]);
	memcpy(canFrameBytes + (i*8), frames[i].data, 8);
        // printf("row: %d data 1 %d 2 %d 3 %d\n", frames[i].data[0], frames[i].data[1], frames[i].data[2], frames[i].data[3]);
    }

    jbyteArray outArray = (*env)->NewByteArray(env, 4*8);
    if (NULL == outArray) {
        printf("Error allocating byte array in poll()\n"); 
        return NULL;
    }
    (*env)->SetByteArrayRegion(env, outArray, 0, 4*8, (jbyte*)canFrameBytes);
    return outArray;
  }
