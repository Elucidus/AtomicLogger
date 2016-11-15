project_base=/home/pi/devcan/jni/atomic/AtomicLogger
export LD_LIBRARY_PATH=${project_base}/jni
export PATH=${PATH}:${project_base}/res
echo ${LD_LIBRARY_PATH}
# Unfortunately, raspi impl of javafx  does not allow you to capture click events as the default user. Run w/ sudo. :( 
sudo java -classpath $project_base/src:$project_base/res com.sharkylabs.GaugeCluster vcan0 

