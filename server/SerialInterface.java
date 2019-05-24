// to compile: javac -classpath .:/usr/java/jre1.8.0_111/lib/ext/RXTXcomm.jar SerialInterface.java 
//		java -classpath .:/usr/java/jre1.8.0_111/lib/ext/RXTXcomm.jar SerialInterface 
package heatControl;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 
import java.util.Enumeration;


public class SerialInterface implements SerialPortEventListener {
	static SerialPort serialPort;
        /** The port we're normally going to use. */
	private static final String PORT_NAMES[] = { 
			//"/dev/tty.usbserial-A9007UX1", // Mac OS X
                        //"/dev/ttyACM0", // Raspberry Pi
			"/dev/ttyACM0",
			//"/dev/ttyUSB0", // Linux
			//"COM3", // Windows
	};
	/**
	* A BufferedReader which will be fed by a InputStreamReader 
	* converting the bytes into characters 
	* making the displayed results codepage independent
	*/
	private static BufferedReader input;
	/** The output stream to the port */
	private static OutputStream output;
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 9600;

	public static void initialize() {
                // the next line is for Raspberry Pi and 
                // gets us into the while loop and was suggested here was suggested http://www.raspberrypi.org/phpBB3/viewtopic.php?f=81&t=32186
                System.setProperty("gnu.io.rxtx.SerialPorts", PORT_NAMES[0]);

		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		//First, Find an instance of serial port as set in PORT_NAMES.
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					break;
				}
			}
		}
		if (portId == null) {
			System.out.println("Could not find COM port.");
			return;
		}

		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(new SerialInterface().getClass().getName(),
					TIME_OUT);

			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// open the streams
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			output = serialPort.getOutputStream();

			// add event listeners
			serialPort.addEventListener(null);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	/**
	 * This should be called when you stop using the port.
	 * This will prevent port locking on platforms like Linux.
	 */
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	/**
	 * Handle an event on the serial port. Read the data and print it.
	 */
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				String inputLine=input.readLine();
				System.out.println(inputLine);
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}
		// Ignore all the other eventTypes, but you should consider the other ones.
	}

	public static synchronized void writeData(String data)
	{
		// System.out.println("Send --> " + data);
		try{
			output.write(data.getBytes());
			Thread.sleep(1100);
			System.out.println(data + " has been sent");
		}
		catch(Exception e){System.out.println("couldn't write to port message received : "+data+" error : "+e);}
	}
	
	public static void set_room_light(int room, int degree) {
		// self.initialize();
		try {
			Thread.sleep(1500);
			int new_degree = (int)((degree*10)>255?255:degree*10);
			writeData(String.format("%d:%d",room,new_degree));
			//writeData("3:150");
		} catch (InterruptedException ie) {}
	}

	public static void reset() {
		try { 
			Thread.sleep(2000);
			// writeData("0:0&1:0&2:0&3:0&4:0");
			writeData("0:0");
			writeData("1:0");
			writeData("2:0");
			writeData("3:0");
			writeData("4:0");
		} catch (InterruptedException ie) {}
	}

	public static void max() {
		try { 
			Thread.sleep(2000);
			// writeData("0:255&1:255&2:255&3:255&4:255");
			writeData("0:255");
			writeData("1:255");
			writeData("2:255");
			writeData("3:255");
			writeData("4:255");
		} catch (InterruptedException ie) {}
	}

	public SerialInterface(){
		// initialize();
		// Thread t=new Thread() {
		// 	public void run() {
		// 		//the following line will keep this app alive for 1000 seconds,
		// 		//waiting for events to occur and responding to them (printing incoming messages to console).
		// 		//set_room_light(4,25);
		// 		max();
		// 		reset();

		// 	}
		// };
		// t.start();
		// System.out.println("Started");
	}
}

