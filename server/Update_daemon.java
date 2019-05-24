package scheduleControl;

import machineLearning.Scheduler;
import java.lang.Runnable;
import java.lang.Thread;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;


public class Update_daemon implements Runnable
{	/*	this class is a thread used to periodically apply updates on
		the state of the house's heating system
	*/

	static private Scheduler _scheduler;
	static private int _delay;
	static private ReentrantLock _isWaiting,_isRunning;

	public Update_daemon(Scheduler scheduler)
	{	_scheduler=scheduler;
		_isRunning=new ReentrantLock();
		_isWaiting=new ReentrantLock();
	}

	public void init(){_isWaiting.lock();}

	public void run()
	{	/*	This is the run method of this daemon
		*/
		while (true)
		{	try
			{	_isWaiting.tryLock(_delay,TimeUnit.MINUTES); // is sleeping until the end of _delay
															 // or until another thread wakes it up
				_isRunning.lock();

				/*	from now on, no other thread can wake up this thread as it is already
					awake (_isRunning is locked). They will have to wait until this thread
					goes to sleep again
				*/

				_delay=_scheduler.getTimeToWait();
				while (_delay==0)
				{	_scheduler.applyPlannedChanges();
					_delay=_scheduler.getTimeToWait();
				}

				/*	setting up the mutex and preparing to go to sleep
				*/

				_isRunning.unlock();
			} catch (InterruptedException exception)
			{	System.err.println("Update_daemon : "+exception);
			}
		}
	}

	public void wakeUp()
	{	System.out.println("Update_daemon.java:58 -> wakeUp function start");
		System.out.println("Update_daemon.java:58 -> lock isRunning");
		_isRunning.lock(); // wait until the updater thread is asleep
		System.out.println("Update_daemon.java:58 -> unlock isWaiting");
		_isWaiting.unlock(); // wake it up
		System.out.println("Update_daemon.java:58 -> unlock isRunning");
		_isRunning.unlock();
		System.out.println("Update_daemon.java:58 -> lock isWaiting");
		_isWaiting.lock();
		System.out.println("Update_daemon.java:58 -> wakeUp function end");
	}
}
