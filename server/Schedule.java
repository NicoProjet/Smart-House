package scheduleControl;

import machineLearning.ADT.Point;

public class Schedule extends Point {

	private int _user;
	private int _room;

	// public Schedule() {}
	public Schedule(int time, int temperature, int user, int room) {
		super(time,temperature);
		_room=room;
		_user=user;
	}

	public int getUser() {return _user;}
	public int getRoom() {return _room;}
	public int getTime() {return (int)getX();}
	public double getTemperature() {return getY();}

	public void setTime(int time) {setX(time);}
	public void setTemperature(double temperature) {setY(temperature);}
}