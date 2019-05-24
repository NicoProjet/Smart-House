package communication;

import communication.Request;
import java.io.Serializable;


public class RequestChange extends Request implements Serializable
{
	protected int _user;
	protected int _room;
	protected double _temperature;
	private static final long serialVersionUID = 1L;

	public RequestChange(int user,int room,double temperature)
	{	_ID=1;
		_user=user;
		_room=room;
		_temperature=temperature;
	}

	public int getUser(){return _user;}
	public int getRoom(){return _room;}
	public double getTemperature(){return _temperature;}
}