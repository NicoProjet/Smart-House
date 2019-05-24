package scheduleControl;

import java.util.Arrays;


public class Day
{
	/*	The ADT used to store a day's schedule. It is actually a three dimensions sparse matrix.
		- the times are stored in _time attribute, when looking for a specific time, one can find the index of the time
		  in _time and use it to get the list of users that have planned a change à that time in _users[timeIndex]
		- _users is a double dimension matrix, once the index of the time is found, the list of users concerned by
		  a change is stored in _users[timeIndex]. In that list are stored the users' ids, once the id of a specific
		  user is found it can be used to find the rooms concerned by the change
		- _rooms is a trhe dimensions matrix, containing rooms ids. Once the time and user index are found a list of
		  rooms concerned by the change can be found in _rooms[timeIndex][userIndex]. the index of specific room in
		  that list can be used to finally find the desired temperature.
		- _temperatures contains the temperatures, using time, user and room index one can find the desired temperature
		  at _temperatures[timeIndex][userIndex][roomIndex]

		This structure is used to mimic the behaviour of a dictionary without being a dictionary and to avoid high
		memory consumption : the time is minute by minute, which would make a huge list of matrices, and if by any
		chance there is many users and/or many rooms, the structure would have been far too large.
	*/


	// constants used, the NULL* attributes are used to avoid creating new dynamic empty arrays
	public static final short MAXTIME=1440;
	private static final int[] NULLINTARRAY={};
	private static final double[] NULLDOUBLEARRAY={};
	private static final int[][] NULLINTMATRIX={};
	private static final double[][] NULLDOUBLEMATRIX={};
	private static final int[][][] NULLINTCUBE={};
	private static final double[][][] NULLDOUBLECUBE={};

	private int[] _time; // contains the time of a change
	private int[][] _users; // for each time, contains the users responsible for the change
	private int[][][] _rooms; // for each time and each user, contains the rooms concerned by the change
	private double[][][] _temperatures; // for each time, user and room, contains the temperature


	public Day()
	{	// initializing attributes
		_time=NULLINTARRAY;
		_users=NULLINTMATRIX;
		_rooms=NULLINTCUBE;
		_temperatures=NULLDOUBLECUBE;
	}


	// the three next methods simply use the built in binary search, possible because of the way we organize the arrays (they are always sorted)
	private int findTime(int time) {return Arrays.binarySearch(_time,time);}
	private int findUser(int timeIndex,int user) {return Arrays.binarySearch(_users[timeIndex],user);}
	private int findRoom(int timeIndex,int userIndex,int room) {return Arrays.binarySearch(_rooms[timeIndex][userIndex],room);}


	public double get(int time,int user,int room) throws IndexOutOfBoundsException
	{	/*	the main get method, used to get the temperature at a certain time for a certain user in a certain room
		*/
		int timeIndex,userIndex,roomIndex;
		for (double[][] matrix : _temperatures)

		// checking if the parameters are usable values
		if (time<0 || time>=MAXTIME)
			throw new IndexOutOfBoundsException(String.format("time must be between 0 and %d (%d given)",MAXTIME-1,time));
		if (user<0 || user>=Integer.MAX_VALUE)
			throw new IndexOutOfBoundsException(String.format("user must be between 0 and %d (%d given)",Integer.MAX_VALUE-1,user));
		if (room<0 || room>=Integer.MAX_VALUE)
			throw new IndexOutOfBoundsException(String.format("room must be between 0 and %d (%d given)",Integer.MAX_VALUE-1,room));

		// converting given parameters in the correct indexes
		// if an index is not found, then -1 is returned. That means there is no record for the given parameters
		timeIndex=findTime(time);
		if (timeIndex<0) return Double.MAX_VALUE;
		userIndex=findUser(timeIndex,user);
		if (userIndex<0) return Double.MAX_VALUE;
		roomIndex=findRoom(timeIndex,userIndex,room);
		if (roomIndex<0) return Double.MAX_VALUE;

		// if everything went well, we return the temperature
		return _temperatures[timeIndex][userIndex][roomIndex];
	}

	public int[] getTimes(int time)
	{	/*	This method returns the times of all the schedules not removed until the specified time
		*/
		int[] res;
		int timeIndex,length=0;

		// checking if the given value is acceptable
		if (time<0 || time>=MAXTIME)
			throw new IndexOutOfBoundsException(String.format("time must be between 0 and %d (%d given)",MAXTIME-1,time));

		timeIndex=findTime(time); // finding timeIndex in times
		timeIndex=(timeIndex<0)?-timeIndex-2:timeIndex;
		// if the time has not been found, we still need to return all the times before the specified time
		res=new int[timeIndex+1]; // this arry will certainly be too large, but its size is set to be the worse case in terms of needed size
		Arrays.fill(res,Integer.MAX_VALUE); // initializing the array

		for (;timeIndex>=0;--timeIndex) // for all previous times
		{	boolean flag=true; // used to break the loop if need be
			for (int i=0;flag && i<_temperatures[timeIndex].length;++i)			// given the time, we are looking for at least
				for (int j=0;flag && j<_temperatures[timeIndex][i].length;++j)	// one temperature different than -1 (meaning
					if (_temperatures[timeIndex][i][j]!=-1)						// that the there is a change to apply)
					{	// rearranging the results array
						int index=Arrays.binarySearch(res,_time[timeIndex]);
						int save;
						index=-index-1;
						save=res[index];
						length++;
						flag=false; // breaking the loop, we just to know if there is at least one planned change at this time
						res[index++]=_time[timeIndex];
						while (save!=Integer.MAX_VALUE && index<res.length)
						{	int tmp=res[index];
							res[index++]=save;
							save=tmp;
						}
					}
			// if (flag)
			// 	timeIndex=-1;
		}
		return (length==res.length)?res:Arrays.copyOf(res,length); // returning an array of the right size
	}

	public int[] getUsers(int time,int room)
	{	int[] res;
		int timeIndex,length=0;

		if (time<0 || time>=MAXTIME)
			throw new IndexOutOfBoundsException(String.format("time must be between 0 and %d (%d given)",MAXTIME-1,time));
		if (room<0 || room>=Integer.MAX_VALUE)
			throw new IndexOutOfBoundsException(String.format("room must be between 0 and %d (%d given)",Integer.MAX_VALUE-1,room));

		timeIndex=findTime(time);
		if (timeIndex<0) return new int[0];

		res=new int[_users[timeIndex].length];
		Arrays.fill(res,Integer.MAX_VALUE);

		for (int i=0;i<_rooms[timeIndex].length;++i)
			for (int elem : _rooms[timeIndex][i])
				if (elem==room)
				{	int index=Arrays.binarySearch(res,_users[timeIndex][i]);
					if (index<0)
					{	index=-index-1;
						length++;
						int save=res[index];
						res[index++]=_users[timeIndex][i];
						while (save!=Integer.MAX_VALUE && index<res.length)
						{	int tmp=res[index];
							res[index++]=save;
							save=tmp;
						}
					}
				}

		return (length==res.length)?res:Arrays.copyOf(res,length);
	}

	public int[] getUsers(int time)
	{	int index;

		if (time<0 || time>=MAXTIME)
			throw new IndexOutOfBoundsException(String.format("time must be between 0 and %d (%d given)",MAXTIME-1,time));

		index=findTime(time);
		if (index<0) return NULLINTARRAY;
		return Arrays.copyOf(_users[index],_users[index].length);
	}

	public int[] getRooms(int time)
	{	int[] res;
		int timeIndex,length=0;

		if (time<0 || time>=MAXTIME)
			throw new IndexOutOfBoundsException(String.format("time must be between 0 and %d (%d given)",MAXTIME-1,time));

		timeIndex=findTime(time);
		if (timeIndex<0) return new int[0];
		res=new int[20];
		Arrays.fill(res,Integer.MAX_VALUE);

		for (int[] line : _rooms[timeIndex])
		{	for (int room : line)
			{	int index=Arrays.binarySearch(res,room);
				if (index<0)
				{	index=-index-1;
					length++;
					if (index==res.length)
					{	res=Arrays.copyOf(res,res.length+10);
						res[index]=room;
						Arrays.fill(res,index+1,res.length,Integer.MAX_VALUE);
					}
					else
					{	int save=res[index];
						res[index++]=room;
						while (save!=Integer.MAX_VALUE && index<res.length)
						{	int tmp=res[index];
							res[index++]=save;
							save=tmp;
						}
					}
				}
			}
		}

		return (length==res.length)?res:Arrays.copyOf(res,length);
	}

	public int[] getAllRooms()
	{	int[] res=new int[20];
		int length=0;
		Arrays.fill(res,Integer.MAX_VALUE);
		for (int[][] matrix : _rooms)
			for (int[] line : matrix)
				for (int room : line)
				{
					int i=Arrays.binarySearch(res,room);
					i=(i<0)?-i-1:i;
					if (res[i]!=room)
					{	int save=res[i];
						length++;
						res[i++]=room;
						if (length==res.length)
						{	res=Arrays.copyOf(res,res.length+1);
							res[res.length-1]=Integer.MAX_VALUE;
						}
						for (;save!=Integer.MAX_VALUE;++i)
						{	int tmp=res[i];
							res[i]=save;
							save=tmp;
						}
					}
				}
		return (length==res.length)?res:Arrays.copyOf(res,length);
	}

	public int[] getUsersPerRoom(int room)
	{	int[] res=new int[10];
		int length=0;
		Arrays.fill(res,Integer.MAX_VALUE);
		for (int i=0;i<_time.length;++i)
			for (int j=0;j<_users[i].length;++j)
				for (int k=0;k<_rooms[i][j].length;++k)
					if (_rooms[i][j][k]==room)
					{	int l=Arrays.binarySearch(res,_users[i][j]);
						l=(l<0)?-l-1:l;
						if (res[l]!=_users[i][j])
						{	int save=res[l];
							length++;
							res[l++]=_users[i][j];
							if (length==res.length)
							{	res=Arrays.copyOf(res,res.length+1);
								res[res.length-1]=Integer.MAX_VALUE;
							}
							for (;save!=Integer.MAX_VALUE;++l)
							{	int tmp=res[l];
								res[l]=save;
								save=tmp;
							}
						}
					}
		return (length==res.length)?res:Arrays.copyOf(res,length);
	}

	public int[] getTimePerUserPerRoom(int user,int room)
	{	int[] res=new int[20];
		int length=0;
		Arrays.fill(res,Integer.MAX_VALUE);
		for (int i=0;i<_time.length;++i)
			for (int j=0;j<_users[i].length;++j)
				for (int k=0;k<_rooms[i][j].length;++k)
					if (_users[i][j]==user && _rooms[i][j][k]==room)
					{	int l=Arrays.binarySearch(res,_time[i]);
						l=(l<0)?-l-1:l;
						if (res[l]!=_time[i])
						{	int save=res[l];
							length++;
							res[l++]=_time[i];
							if (length==res.length)
							{	res=Arrays.copyOf(res,res.length+1);
								res[res.length-1]=Integer.MAX_VALUE;
							}
							for (;save!=Integer.MAX_VALUE;++l)
							{	int tmp=res[l];
								res[l]=save;
								save=tmp;
							}
						}
					}
		return (length==res.length)?res:Arrays.copyOf(res,length);
	}

	public int[] getRooms(int time,int user)
	{	int timeIndex,userIndex;

		if (time<0 || time>=MAXTIME)
			throw new IndexOutOfBoundsException(String.format("time must be between 0 and %d (%d given)",MAXTIME-1,time));
		if (user<0 || user>=Integer.MAX_VALUE)
			throw new IndexOutOfBoundsException(String.format("user must be between 0 and %d (%d given)",Integer.MAX_VALUE-1,user));

		timeIndex=findTime(time);
		if (timeIndex<0) return NULLINTARRAY;
		userIndex=findUser(timeIndex,user);
		if (userIndex<0) return NULLINTARRAY;

		return Arrays.copyOf(_rooms[timeIndex][userIndex],_rooms[timeIndex][userIndex].length);
	}

	public int getNextTime(int currentTime)
	{	int index=findTime(currentTime)+1;
		index=(index<0)?-index:index;
		return (index==_time.length)?MAXTIME:_time[index];
	}

	private int allocateNewTime(int timeIndex,int time)
	{	timeIndex=-timeIndex-1;
		if (timeIndex<_time.length && _time[timeIndex]==MAXTIME)
		/*	In this case the array has been cleared (see clear) and
			we do not need to effectively allocate anything we can reuse
			the space previously cleared
		*/
		{	_time[timeIndex]=time;
			return timeIndex;
		}

		_temperatures=Arrays.copyOf(_temperatures,_temperatures.length+1);
		_rooms=Arrays.copyOf(_rooms,_rooms.length+1);
		_users=Arrays.copyOf(_users,_users.length+1);
		_time=Arrays.copyOf(_time,_time.length+1);

		for (int i=_time.length-1;i>timeIndex;--i)
		{	_temperatures[i]=_temperatures[i-1];
			_rooms[i]=_rooms[i-1];
			_users[i]=_users[i-1];
			_time[i]=_time[i-1];
		}
		_temperatures[timeIndex]=NULLDOUBLEMATRIX;
		_rooms[timeIndex]=NULLINTMATRIX;
		_users[timeIndex]=NULLINTARRAY;
		_time[timeIndex]=time;

		return timeIndex;
	}

	private int allocateNewUser(int timeIndex,int userIndex,int user)
	{	userIndex=-userIndex-1;
		if (userIndex<_users[timeIndex].length && _users[timeIndex][userIndex]==Integer.MAX_VALUE)
		/*	In this case the array has been cleared (see clear) and
			we do not need to effectively allocate anything we can reuse
			the space previously cleared
		*/
		{	_users[timeIndex][userIndex]=user;
			return userIndex;
		}

		_temperatures[timeIndex]=Arrays.copyOf(_temperatures[timeIndex],_temperatures[timeIndex].length+1);
		_rooms[timeIndex]=Arrays.copyOf(_rooms[timeIndex],_rooms[timeIndex].length+1);
		_users[timeIndex]=Arrays.copyOf(_users[timeIndex],_users[timeIndex].length+1);

		for (int i=_users[timeIndex].length-1;i>userIndex;--i)
		{	_temperatures[timeIndex][i]=_temperatures[timeIndex][i-1];
			_rooms[timeIndex][i]=_rooms[timeIndex][i-1];
			_users[timeIndex][i]=_users[timeIndex][i-1];
		}
		_temperatures[timeIndex][userIndex]=NULLDOUBLEARRAY;
		_rooms[timeIndex][userIndex]=NULLINTARRAY;
		_users[timeIndex][userIndex]=user;


		return userIndex;
	}

	private int allocateNewRoom(int timeIndex,int userIndex,int roomIndex,int room)
	{	roomIndex=-roomIndex-1;
		if (roomIndex<_rooms[timeIndex][userIndex].length && _rooms[timeIndex][userIndex][roomIndex]==Integer.MAX_VALUE)
		/*	In this case the array has been cleared (see clear) and
			we do not need to effectively allocate anything we can reuse
			the space previously cleared
		*/
		{	_temperatures[timeIndex][userIndex][roomIndex]=0;
			_rooms[timeIndex][userIndex][roomIndex]=room;
			return roomIndex;
		}

		_temperatures[timeIndex][userIndex]=Arrays.copyOf(_temperatures[timeIndex][userIndex],_temperatures[timeIndex][userIndex].length+1);
		_rooms[timeIndex][userIndex]=Arrays.copyOf(_rooms[timeIndex][userIndex],_rooms[timeIndex][userIndex].length+1);

		for (int i=_rooms[timeIndex][userIndex].length-1;i>roomIndex;--i)
		{	_temperatures[timeIndex][userIndex][i]=_temperatures[timeIndex][userIndex][i-1];
			_rooms[timeIndex][userIndex][i]=_rooms[timeIndex][userIndex][i-1];
		}
		_temperatures[timeIndex][userIndex][roomIndex]=0; // ???
		_rooms[timeIndex][userIndex][roomIndex]=room;


		return roomIndex;
	}

	public double set(int time,int user,int room,double temperature) throws IndexOutOfBoundsException
	{	int timeIndex,userIndex,roomIndex;
		double res;
		if (time==1440){time = time%1440;}

		if (time<0 || time>=MAXTIME)
			throw new IndexOutOfBoundsException(String.format("time must be between 0 and %d (%d given)",MAXTIME-1,time));
		if (user<0 || user>=Integer.MAX_VALUE)
			throw new IndexOutOfBoundsException(String.format("user must be between 0 and %d (%d given)",Integer.MAX_VALUE-1,user));
		if (room<0 || room>=Integer.MAX_VALUE)
			throw new IndexOutOfBoundsException(String.format("room must be between 0 and %d (%d given)",Integer.MAX_VALUE-1,room));
		if (temperature<0)
			throw new IndexOutOfBoundsException(String.format("temperature must be above or equal to 0 (%d given)",temperature));

			// DEBUT TESTS
			System.out.println("Day.java:414 -> function set, before set");
			int [] test = getTimes(1339);
			for (int timeTest : test){System.out.print(timeTest + " | ");}
			System.out.print("\n");
			// FIN TESTS

		timeIndex=findTime(time);
		if (timeIndex<0) // if no time has been found, then we must allocate space for all the new variables
		{	timeIndex=allocateNewTime(timeIndex,time);
			userIndex=allocateNewUser(timeIndex,-1,user);
			roomIndex=allocateNewRoom(timeIndex,userIndex,-1,room);
		}
		else
		{	userIndex=findUser(timeIndex,user);
			if (userIndex<0) // time found but no user, allocating space for user and room and temperature
			{	userIndex=allocateNewUser(timeIndex,userIndex,user);
				roomIndex=allocateNewRoom(timeIndex,userIndex,-1,room);
			}
			else
			{	roomIndex=findRoom(timeIndex,userIndex,room);
				if (roomIndex<0) // time and user found but no room, allocating space for room and temperature
				{	roomIndex=allocateNewRoom(timeIndex,userIndex,roomIndex,room);
				}
			}
		}

		res=_temperatures[timeIndex][userIndex][roomIndex];
		_temperatures[timeIndex][userIndex][roomIndex]=temperature;

		// DEBUT TESTS
		System.out.println("Day.java:443 -> function set, after set");
		test = getTimes(1339);
		for (int timeTest : test){System.out.print(timeTest + " | ");}
		System.out.print("\n");
		// FIN TESTS
		return res;
	}

	public void clear()
	{	for (int i=0;i<_time.length;++i)
		{	_time[i]=MAXTIME;
			for (int j=0;j<_users[i].length;++j)
			{	_users[i][j]=Integer.MAX_VALUE;
				for (int k=0;k<_rooms[i][j].length;++k)
				{	_rooms[i][j][k]=Integer.MAX_VALUE;
					_temperatures[i][j][k]=-1;
				}
			}
		}
	}

	public void cleanUp()
	{	int timeIndex,userIndex,roomIndex;

		timeIndex=findTime(MAXTIME); // cleaning "dead" times
		if (timeIndex<0)
			timeIndex=_time.length;
		for (int i=0;i<timeIndex;)
		{	userIndex=findUser(i,Integer.MAX_VALUE); // for each clean time, cleaning "dead" users
			if (userIndex<0)
				userIndex=_users[i].length;
			for (int j=0;j<userIndex;)
			{	roomIndex=findRoom(i,j,Integer.MAX_VALUE); // for each clean time and user, cleaning "dead" rooms
				if (roomIndex<0)
					roomIndex=_rooms[i][j].length;

				// checking temperatures to clean those equal to 0
				// rearranging the arrays accordingly, setting up lengths of the new arrays
				for (int k=0;k<roomIndex;)
				{	if (_temperatures[i][j][k]<0)
					{	roomIndex--;
						for (int l=k;l<roomIndex;++l)
						{	_rooms[i][j][l]=_rooms[i][j][l+1];
							_temperatures[i][j][l]=_temperatures[i][j][l+1];
						}
					}
					else
						k++;
				}

				if (roomIndex==0) // we rearrange arrays to move those unused in the "to be deleted" zone
				{	userIndex--;
					for (int k=j;k<userIndex;++k)
					{	_users[i][k]=_users[i][k+1];
						_rooms[i][k]=_rooms[i][k+1];
						_temperatures[i][k]=_temperatures[i][k+1];
					}
				}
				else if (roomIndex<_rooms[i][j].length) // reallocating a smaller array, getting rid of the useless ones
				{	_rooms[i][j]=Arrays.copyOf(_rooms[i][j],roomIndex);
					_temperatures[i][j]=Arrays.copyOf(_temperatures[i][j],roomIndex);
					j++;
				}
				else
					j++;
			}
			if (userIndex==0)
			{	timeIndex--;
				for (int j=i;j<timeIndex;++j)
				{	_time[j]=_time[j+1];
					_users[j]=_users[j+1];
					_rooms[j]=_rooms[j+1];
					_temperatures[j]=_temperatures[j+1];
				}
			}
			else if (userIndex<_users[i].length)
			{	_users[i]=Arrays.copyOf(_users[i],userIndex);
				_rooms[i]=Arrays.copyOf(_rooms[i],userIndex);
				_temperatures[i]=Arrays.copyOf(_temperatures[i],userIndex);
				i++;
			}
			else
				i++;
		}
		if (timeIndex==0)
		{	_time=NULLINTARRAY;
			_users=NULLINTMATRIX;
			_rooms=NULLINTCUBE;
			_temperatures=NULLDOUBLECUBE;
		}
		else if (timeIndex<_time.length)
		{	_time=Arrays.copyOf(_time,timeIndex);
			_users=Arrays.copyOf(_users,timeIndex);
			_rooms=Arrays.copyOf(_rooms,timeIndex);
			_temperatures=Arrays.copyOf(_temperatures,timeIndex);
		}

		System.gc(); // calling garbage collector to effectively delete cleaned variables
	}

	public double remove(int time,int user,int room) throws IndexOutOfBoundsException
	{	double res;
		int timeIndex,userIndex,roomIndex;

		if (time<0 || time>=MAXTIME)
			throw new IndexOutOfBoundsException(String.format("time must be between 0 and %d (%d given)",MAXTIME-1,time));
		if (user<0 || user>=Integer.MAX_VALUE)
			throw new IndexOutOfBoundsException(String.format("user must be between 0 and %d (%d given)",Integer.MAX_VALUE-1,user));
		if (room<0 || room>=Integer.MAX_VALUE)
			throw new IndexOutOfBoundsException(String.format("room must be between 0 and %d (%d given)",Integer.MAX_VALUE-1,room));

		timeIndex=findTime(time);
		if (timeIndex<0) return -1;
		userIndex=findUser(timeIndex,user);
		if (userIndex<0) return -1;
		roomIndex=findRoom(timeIndex,userIndex,room);
		if (roomIndex<0) return -1;

		res=_temperatures[timeIndex][userIndex][roomIndex];
		_temperatures[timeIndex][userIndex][roomIndex]=-res;
		return res;
	}

	public void removeAll()
	{	for (double[][] matrix : _temperatures)
			for (double[] line : matrix)
				for (int i=0;i<line.length;++i)
					line[i]=-line[i];
	}

	public void removeUserSchedule(int time,int user)
	{	int timeIndex,userIndex;

		if (time<0 || time>=MAXTIME)
			throw new IndexOutOfBoundsException(String.format("time must be between 0 and %d (%d given)",MAXTIME-1,time));
		if (user<0 || user>=Integer.MAX_VALUE)
			throw new IndexOutOfBoundsException(String.format("user must be between 0 and %d (%d given)",Integer.MAX_VALUE-1,user));

		timeIndex=findTime(time);
		if (timeIndex>=0)
		{	userIndex=findUser(timeIndex,user);
			if (userIndex>=0)
				for (int i=0;i<_temperatures[timeIndex][userIndex].length;++i)
					_temperatures[timeIndex][userIndex][i]=-_temperatures[timeIndex][userIndex][i];
		}
	}

	public void removeSchedule(int time)
	{	int timeIndex;

		if (time<0 || time>=MAXTIME)
			throw new IndexOutOfBoundsException(String.format("time must be between 0 and %d (%d given)",MAXTIME-1,time));

		timeIndex=findTime(time);
		if (timeIndex>=0)
			for (double[] line : _temperatures[timeIndex])
				for (int i=0;i<line.length;++i)
					line[i]=-line[i];
	}

	public int size()
	{	return _time.length;
	}
}
