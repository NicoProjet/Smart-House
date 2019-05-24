package machineLearning;

import java.lang.Runnable;
import database.DatabaseAccess;
import scheduleControl.Day;
import scheduleControl.Schedule;
import machineLearning.ADT.Point;
import machineLearning.ADT.Centroid;
import machineLearning.Algorithms.Diana;
import java.util.ArrayList;
import java.lang.Thread;


public class DayPlanner_daemon implements Runnable
{
	private static DatabaseAccess _database;
	private static Scheduler _scheduler;
	private static final int NUMUSEDWEEKS=4;

	public DayPlanner_daemon(DatabaseAccess database,Scheduler scheduler)
	{	_database=database;
		_scheduler=scheduler;
	}

	private void updateSchedule()
	{	Day day=_scheduler.finishDay();

		_database.update();
		
		ArrayList<Schedule> data=new ArrayList<Schedule>();
		int[] users=_database.getUsers();
		for (int user : users)
			for (int i=1;i<=NUMUSEDWEEKS;++i)
				for (int room : _database.getUsedRooms(user,i*7))
					data.addAll(generateData(user,i*7,room));
		
		while (data.size()!=0) {
			ArrayList<Point> tmp=new ArrayList<Point>();
			int currentRoom=data.get(0).getRoom();
			int currentUser=data.get(0).getUser();
			for (Schedule schedule : data)
				if (schedule.getRoom()==currentRoom && schedule.getUser()==currentUser)
					tmp.add(schedule);

			Diana diana=new Diana(tmp);
			for (Centroid result : diana.getCentroids()) {
				System.out.println(String.format("%s in %s : %s",_database.getUserName(currentUser),_database.getRoomName(currentRoom),result));
				day.set((int)result.getX(),currentUser,currentRoom,result.getY());
			}
			for (Point schedule : tmp) data.remove((Schedule)schedule);
		}

		_scheduler.updateSchedule();
	}

	private ArrayList<Schedule> generateData(int user, int day, int room) {
		Schedule[] data=_database.getDaySchedules(day,user,room);
		ArrayList<Schedule> result=new ArrayList<Schedule>(data.length);
		Point.resetCounter();
		for (Schedule schedule : data) result.add(schedule);
		return result;
	}

	public void run()
	{	while(true)
		{	try
			{	updateSchedule();
				Thread.sleep((scheduleControl.Day.MAXTIME-_database.getTime())*60000);
			} catch (InterruptedException exception)
			{	System.err.println("DayPlanner_daemon : "+exception);
			}
		}
	}
}
/*	TODO:
		-ATTENTION : Day doit inclure le délai nécessaire à chauffer une pièce
		 dans son horaire sinon on risque d'avoir des clash entre les temps
*/