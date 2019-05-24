package communication;

import communication.RequestChange;
import java.io.Serializable;


public class RequestChangeSchedule extends RequestChange implements Serializable
{
	protected int _time;
	private static final long serialVersionUID = 1L;

	public RequestChangeSchedule(int time,int user,int room,double temperature)
	{	super(user,room,temperature);
		_ID=2;
		_time=time;
	}

	public int getTime(){return _time;}

	public void applyTimeShift(int resetTime,int maxtime){_time=(_time>resetTime)?_time-resetTime:maxtime+_time-resetTime;}
}