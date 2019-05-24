import java.lang.Thread;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.ConnectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import database.DatabaseAccess;
import machineLearning.DayPlanner_daemon;
import machineLearning.Scheduler;
import scheduleControl.Update_daemon;
import scheduleControl.Day;
import communication.Request;
import communication.RequestChange;
import communication.RequestChangeSchedule;
import communication.Response;
import communication.ResponseServer;

public class Server
{	// Main class of the program

	static private DayPlanner_daemon _planner;
	static private Update_daemon _updater;
	static private Scheduler _scheduler;
	static private DatabaseAccess _database;


	public static void main(String[] args)
	{	_database=new DatabaseAccess();
		_scheduler=new Scheduler(_database);
		_planner=new DayPlanner_daemon(_database,_scheduler);
		_updater=new Update_daemon(_scheduler);

		// test();
		_database.init();
		launchDaemons();
		listenNetwork();
	}

	private static void launchDaemons()
	{	// Launch daemon threads see their source code for further info
		Thread thread;
		thread=new Thread(_planner);
		thread.setDaemon(true);
		thread.start();
		thread=new Thread(_updater);
		thread.setDaemon(true);
		_updater.init();
		thread.start();
	}

	private static void planSchedule(int time,int user,int room,double temperature)
	{	/*	function to be called whenever a user change something in the schedule
		*/
		_scheduler.planChange(time,user,room,temperature);
		//_updater.wakeUp(); // used to wake up the thread
	}

	private static void changeTemperature(int user,int room,double temperature)
	{	/*	function to be called whenever a user makes a change on the temperature
			to be applied now
		*/
		_scheduler.applyChange(user,room,temperature);
	}

	private static void listenNetwork()
	{	Socket socket=null;
		ServerSocket serverSocket=null;
		boolean flag;
		while (true)
		{	flag=true;
			try
			{	serverSocket=new ServerSocket(3000);
			}
			catch (Exception exception)
			{	System.err.println("Unexpected error in Server::listenNetwork : "+exception);
				flag=false;
				try{serverSocket.close();}catch(Exception e){}
			}

			while (flag)
			{	ObjectOutputStream streamOut=null;
				ObjectInputStream streamIn=null;
				try
				{	socket=serverSocket.accept();
					streamIn=new ObjectInputStream(socket.getInputStream());
					streamOut=new ObjectOutputStream(socket.getOutputStream());
					Response response=manageRequest((Request)streamIn.readObject());
					streamOut.writeObject(response);
					streamOut.close();
					streamIn.close();
					socket.close();
				}
				catch (Exception exception)
				{	System.err.println("Unexpected error in Server::listenNetwork : "+exception);
					try{socket.close();}catch(Exception e){flag=false;}
					try{streamOut.close();}catch(Exception e){flag=false;}
					try{streamIn.close();}catch(Exception e){flag=false;}
				}
			}
		}
	}

	private static Response manageRequest(Request request)
	{	RequestChange rChange;
		RequestChangeSchedule rcSchedule;
		ResponseServer res;
		switch (request.getID())
		{	case 0: // get the day's schedule
				break;
			case 1: // change the temperature of a room
				rChange=(RequestChange)request;
				changeTemperature(rChange.getUser(),rChange.getRoom(),rChange.getTemperature());
				break;
			case 2: // plan a new schedule
				rcSchedule=(RequestChangeSchedule)request;
				rcSchedule.applyTimeShift(_database.getResetTime(),Day.MAXTIME);
				planSchedule(rcSchedule.getTime(),rcSchedule.getUser(),rcSchedule.getRoom(),rcSchedule.getTemperature());
				break;
			default:
				System.err.println("Unknown request : "+request);
		}
		res=new ResponseServer(_scheduler.getDay(),_database);
		return res.getResponse();
	}

	private static void test()
	{	// used to test
	}
}
