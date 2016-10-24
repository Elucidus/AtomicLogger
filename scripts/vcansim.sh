add="true"
i=10
while (true) 
	do 
	if [ $add == "true" ]
	then
		i=$(( i + 10 ))
	fi
	if [ $add == "false" ]
	then
		i=$(( i - 10))
	fi
	if [ $i -eq 90 ]
	then
		add="false"
	fi
	if [ $i -eq 10 ]
	then
		add="true"
	fi
	cansend vcan0 301#00${i}05${i}060600${i} 
	cansend vcan0 301#010102${i}04050607
	cansend vcan0 301#0201020304050607
	cansend vcan0 301#0301020304050607
done

