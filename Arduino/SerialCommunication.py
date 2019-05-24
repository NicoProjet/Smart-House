# dont forget sudo apt install python3-serial

import serial


def allOFF():
	return b'0:0&1:0&2:0&3:0&4:0'

def allON():
	return b'0:255&1:255&2:255&3:255&4:255'


ser = serial.Serial('/dev/ttyACM0')  # open serial port
print(ser.name)         # check which port was really used
# ser.write(b'0:255&1:200&2:150&3:100&4:50')     # write a string
# ser.write(b'0:0&1:0&2:255&3:0&4:0')
#ser.write(allOFF())
ser.write(allON())
ser.close()             # close port


