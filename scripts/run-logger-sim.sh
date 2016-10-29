project_base=/home/pi/devcan/jni/atomic/AtomicLogger
export LD_LIBRARY_PATH=${project_base}/jni
export PATH=${PATH}:${project_base}/res
echo ${LD_LIBRARY_PATH}
java -classpath $project_base/src:$project_base/res com.sharkylabs.GaugeCluster vcan0 

