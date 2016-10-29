i=10
add="true"
while (true) 
do
	if [ $add == "true" ]
		then
			i=$(( $i + 10))
	fi
	if [ $add == "false" ]
		then
			i=$(( $i - 10))
	fi
	if [ $i -eq 90 ] 
		then
			add=false
	fi
	if [ $i -eq 10 ]
		then
			add=true
	fi
	cansend vcan0 301#00010203040506${i}
	cansend vcan0 301#010102${i}04050607
	cansend vcan0 301#0201020304050607
	cansend vcan0 301#0301020304050607
done
