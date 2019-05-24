#!/bin/bash

#Current user must be into dialout group ---> usermod -a -G dialout
if [ -e /dev/ttyACM* ] ; then
	{
		echo -e "Arduino connected to serial port:"
		echo /dev/ttyACM*


		stty -F /dev/ttyACM* -hupcl
		echo -e "\nchoose your led(0-5) and intensity(0-255).You can turn on several simultaneously\n-->example \"0:255&1:120&2:10\" :"
		read response
		while [ $response != "exit" ]
		do
			echo $response > /dev/ttyACM*
			read response
		done
	}
fi

