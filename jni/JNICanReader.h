/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_sharkylabs_comm_JNICanReader */

#ifndef _Included_com_sharkylabs_comm_JNICanReader
#define _Included_com_sharkylabs_comm_JNICanReader
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_sharkylabs_comm_JNICanReader
 * Method:    openSocket
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_sharkylabs_comm_JNICanReader_openSocket
  (JNIEnv *, jclass, jstring);

/*
 * Class:     com_sharkylabs_comm_JNICanReader
 * Method:    poll
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_sharkylabs_comm_JNICanReader_poll
  (JNIEnv *, jclass);

/*
 * Class:     com_sharkylabs_comm_JNICanReader
 * Method:    closeSocket
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_sharkylabs_comm_JNICanReader_closeSocket
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif