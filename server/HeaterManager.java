package heatControl;

import heatControl.SerialInterface;


public class HeaterManager
{	/*	this class represents the heating system of the house, it is used by the Scheduler
		to set the right temperature in the right room at the right time
	*/



	public HeaterManager()
	{SerialInterface.initialize();}

	public void changeTemperature(int room, int temperature) {
		SerialInterface.set_room_light(room,temperature);
	}
}
