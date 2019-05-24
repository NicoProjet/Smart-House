package machineLearning;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import java.util.concurrent.locks.ReentrantLock;
import java.lang.Runnable;
import java.util.Arrays;

import scheduleControl.Day;
import heatControl.HeaterManager;
import database.DatabaseAccess;


public class Scheduler
{	/*	this class is scheduling one day, this is the "core" of the program
		where decisions concerning temperature and time to heat the house are taken
	*/

	static private HeaterManager _heater; // interface to the house's heater system
	static private DatabaseAccess _database; // interface to the database
	static private Day _day; // schedule of the day
	static private ReentrantLock _mutex; // mutex used to access the schedule

	public Scheduler(DatabaseAccess database)
	{	_heater=new HeaterManager();
		_day=new Day();
		_database=database;
		_mutex=new ReentrantLock();
	}

	protected void updateSchedule()
	{	/*	this method is used ONLY by the planner daemon
			used once the changes have been made
			to the schedule by the machine learning algorithm
		*/
		_day.cleanUp();
		_mutex.unlock();
	}

	public Day getDay(){return _day;}

	protected Day finishDay()
	{	/*	method used to acquire exclusive access to the schedule in order
			to update the schedule for a new day. Must be used ONLY by the
			planner daemon
		*/

		_mutex.lock();
		_day.clear();
		return _day;
	}

	public int getTimeToWait()
	{	/*	method used by the the updater thread to know how much to wait
			until the next scheduled change (it can also be awaken on a user's request)
		*/

		int delay,currentTime;
		_mutex.lock();
		currentTime=_database.getTime();
		delay=_day.getNextTime(currentTime);
		delay=(delay==Day.MAXTIME)?delay:delay-currentTime;
		_mutex.unlock();
		return delay;
	}

	public void planChange(int time,int user,int room,double temperature)
	{	// plan a change
		_mutex.lock();
		_database.addSchedule(time,user,room,temperature);
		_day.set(time,user,room,temperature);
		_mutex.unlock();
	}

	private double determineTemperature(int time,int room,int[] presentUsers)
	{	/*	used to determine a room temperature based upon the time, the room
			and the present users
		*/

		double res;
		int index;

		if (presentUsers.length==0) return 0;

		index=Arrays.binarySearch(presentUsers,_database.getAdmin());
		if (index>=0) // the admin has always the priority
			res=_day.get(time,presentUsers[index],room);
		else
		{	index=Arrays.binarySearch(presentUsers,_database.getOwner(room));
			if (index>=0) // if the admin did not decide anything, then the owner has the priority
				res=_day.get(time,presentUsers[index],room);
			else
			{	// if there is no room's owner nor admin in the room, a compromise is made between all preferences
				res=0;
				for (int user : presentUsers)
					res+=(_day.get(time,room,user)<0)?-_day.get(time,room,user):_day.get(time,room,user);
				res/=presentUsers.length;
			}
		}
		return res;
	}

	public void applyChange(int user,int room,double temperature)
	{	// apply a change now
		int time=_database.getTime();
		_database.addSchedule((time+_database.getResetTime())%Day.MAXTIME,user,room,temperature);
		_day.set(time,user,room,temperature);
		// _heater.changeTemperature(room,(int)determineTemperature(time,room,_database.getPresentUsers(room)));
		_heater.changeTemperature(room,(int)temperature);
	}

	public void applyPlannedChanges()
	{	/*	method used to apply all planned changes, meaning writing in the database and changing temperature
		*/
		_mutex.lock();
		for (int time : _day.getTimes(_database.getTime())) // get the time of the change between last change and now
		{	for (int room : _day.getRooms(time)) // for each time, get rooms concerned by the change
			{	int[] users=_day.getUsers(time,room);
				int index;
				for (int user : users) // for each time and room, get the present users
				{	double temperature=_day.get(time,user,room);
					if (temperature>=0)
						// time+_database.getDelay(room) because now is not the time when the room is warm, it is the time we need to start heating
						_database.addSchedule(time+_database.getDelay(room),user,room,temperature); // write in the database
				}
				_heater.changeTemperature(room,(int)determineTemperature(time,room,_database.getPresentUsers(room))); // effectively applying change in the house
			}
			_day.removeSchedule(time);
		}
		_mutex.unlock();
	}


	/*	TODO :
		-méthodes de test du scheduler pour trouver le temps
		 de chauffage d'une pièce
	*/
}
