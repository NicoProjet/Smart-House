package communication;

import java.util.Arrays;

import scheduleControl.Day;
import database.DatabaseAccess;
import communication.Response;


public class ResponseServer
{
	private static final long serialVersionUID = 2L;

	private static final char ADMIN=3;
	private static final char OWNER=2;
	private static final char STANDARD=1;
	private static final String[] NULLSTRINGARRAY={};
	private static final int[] NULLINTARRAY={};
	private static final double[] NULLDOUBLEARRAY={};
	private Response _response;

	public ResponseServer(Day day,DatabaseAccess database)
	{	int[] rooms=database.getRooms();

		int[][] users=new int[rooms.length][]; //users per room
		int[][] priorities=new int[rooms.length][];
		int[][][] times=new int[rooms.length][][];

		_response=new Response();
		_response.times=new int[rooms.length][];
		_response.rooms=new String[rooms.length];
		_response.users=new String[rooms.length][];
		_response.temperatures=new double[rooms.length][];
		_response.resetTime=database.getResetTime();
		_response.currentTime=(database.getTime()+database.getResetTime())%Day.MAXTIME;

		for (int i=0;i<rooms.length;++i)
		{	_response.times[i]=NULLINTARRAY;
			_response.users[i]=NULLSTRINGARRAY;
			_response.temperatures[i]=NULLDOUBLEARRAY;
		}

		for (int i=0;i<rooms.length;++i)
		{	// we are in room rooms[i]
			_response.rooms[i]=database.getRoomName(rooms[i]);
			users[i]=day.getUsersPerRoom(rooms[i]);
			priorities[i]=new int[users[i].length];
			times[i]=new int[users[i].length][];
			for (int j=0;j<users[i].length;++j)
			{	// in room[i] we are checking times and priority for user[i][j]
				times[i][j]=day.getTimePerUserPerRoom(users[i][j],rooms[i]);
				if (database.getAdmin()==users[i][j])
					manageSchedule(ADMIN,database,day,i,users[i][j],times[i][j]);
				else if (database.getOwner(rooms[i])==users[i][j])
					manageSchedule(OWNER,database,day,i,users[i][j],times[i][j]);
				else
					manageSchedule(STANDARD,database,day,i,users[i][j],times[i][j]);
			}
		}

		for (double[] temperatures : _response.temperatures)
			for (int i=0;i<temperatures.length;++i){
				temperatures[i]=(temperatures[i]<0)?-temperatures[i]:temperatures[i];
				temperatures[i]=(temperatures[i]==Double.MAX_VALUE)?0:temperatures[i];
			}
		System.out.println("ResponseServer.java:66");
		for (int i = 0; i<_response.temperatures.length; i++){
	        for (int j = 0; j<_response.temperatures[i].length; j++){
	            System.out.println("CHANGES SENT:  temp: "+_response.temperatures[i][j]+ "  |  time: "+_response.times[i][j]);
			}
		}
	}

	public Response getResponse() {return _response;}

	private void manageSchedule(int priority,DatabaseAccess database,Day day,int room,int user,int[] times)
	{
		String str = "ResponseServer.java:77 -> room: "+room+" | user: "+user+" | times.length: "+times.length+" | {";
		for (int time : times){str += time+", ";}
		str += "}";
		System.out.println(str);

		int k=0;
		for (;k<times.length && day.get(times[k],user,room)==0;++k);
		int begin=Integer.MAX_VALUE,end=Integer.MAX_VALUE;
		if (k<times.length)
			begin=times[k++];
		for (;k<times.length;++k)
		{	if (begin!=Integer.MAX_VALUE)
			{	end=times[k];
				System.out.println("ResponseServer.java:91 -> begin: "+begin+" | end: "+end+" | room: "+room+" | temperature: "+day.get(begin,user,database.getRoomID(_response.rooms[room])));
				switch (priority)
				{	case ADMIN:
						manageAdminSchedule(begin,end,day.get(begin,user,database.getRoomID(_response.rooms[room])),database.getUserName(user),room);
						break;
					case OWNER:
						manageOwnerSchedule(begin,end,day.get(begin,user,database.getRoomID(_response.rooms[room])),database.getUserName(user),room);
						break;
					case STANDARD:
						manageStandardSchedule(begin,end,day.get(begin,user,database.getRoomID(_response.rooms[room])),database.getUserName(user),room);
						break;
					default:
						System.err.println(String.format("Error : unknown case in Response::manageSchedule (%d given)",priority));
				}
				if (day.get(end,user,room)!=0)
					begin=end;
				else
					begin=Integer.MAX_VALUE;
				end=Integer.MAX_VALUE;
			}
			else
				begin=times[k];
		}
	}

	private void manageAdminSchedule(int begin,int end,double temperature,String name,int room)
	{	temperature=(temperature>0)?-temperature:temperature; // to mark the priority, we use negative values
		if (temperature!=Double.MAX_VALUE)
			insertSchedule(begin,end,temperature,name,room);
	}

	private void insertSchedule(int begin,int end,double temperature,String name,int room)
	{	int indexBegin=Arrays.binarySearch(_response.times[room],begin);
		int indexEnd=Arrays.binarySearch(_response.times[room],end);
		indexBegin=(indexBegin<0)?-indexBegin-1:indexBegin;
		indexEnd=(indexEnd<0)?-indexEnd-1:indexEnd;
		for (;indexBegin+1<_response.times[room].length && _response.times[room].length!=0 && _response.times[room][indexBegin+1]==begin;++indexBegin);
		for (;indexEnd+1<_response.times[room].length && _response.times[room].length!=0 && _response.times[room][indexEnd+1]==end;++indexEnd);

		while (indexEnd-1>=0 && _response.times[room].length!=0 && _response.times[room][indexEnd-1]==end)
		{	if (_response.temperatures[room][indexEnd]==0)
			{	_response.times[room]=removeRangeInArray(_response.times[room],indexEnd,indexEnd+1);
				_response.temperatures[room]=removeRangeInArray(_response.temperatures[room],indexEnd,indexEnd+1);
				_response.users[room]=removeRangeInArray(_response.users[room],indexEnd,indexEnd+1);
			}
			else
				indexEnd--;
		}

		while (indexBegin-1>=0 && _response.times[room].length!=0 && _response.times[room][indexBegin-1]==begin)
		{	if (_response.temperatures[room][indexBegin]!=0)
			{	int tmp=indexBegin;
				for (;_response.users[room][tmp]!=_response.users[room][indexBegin];++tmp);
				if (_response.times[room][tmp]>=end)
				{	_response.temperatures[room]=insertInArray(_response.temperatures[room],indexEnd,_response.temperatures[room][indexBegin]);
					_response.users[room]=insertInArray(_response.users[room],indexEnd,_response.users[room][indexBegin]);
					_response.times[room]=insertInArray(_response.times[room],indexEnd,end);
				}
				_response.times[room]=removeRangeInArray(_response.times[room],indexBegin,indexBegin+1);
				_response.temperatures[room]=removeRangeInArray(_response.temperatures[room],indexBegin,indexBegin+1);
				_response.users[room]=removeRangeInArray(_response.users[room],indexBegin,indexBegin+1);
			}
			else
				indexBegin--;
		}

		// placing begin values
		for (int i=indexBegin;indexBegin>0 && i!=0 && _response.times[room][i]==_response.times[room][indexBegin-1];--i)
			if (_response.temperatures[room][i-1]!=0)
			{	_response.temperatures[room]=insertInArray(_response.temperatures[room],indexBegin,0);
				_response.users[room]=insertInArray(_response.users[room],indexBegin,_response.users[room][indexBegin-1]);
				_response.times[room]=insertInArray(_response.times[room],indexBegin++,begin);
				i=-1;
			}
		_response.temperatures[room]=insertInArray(_response.temperatures[room],indexBegin,temperature);
		_response.users[room]=insertInArray(_response.users[room],indexBegin,name);
		_response.times[room]=insertInArray(_response.times[room],indexBegin++,begin);

		// placing end values
		for (;indexBegin+1<_response.times[room].length && _response.times[room].length!=0 && _response.times[room][indexBegin+1]==begin;++indexBegin);
		for (int i=indexBegin+1;i<indexEnd;++i)
		{	if (_response.temperatures[room][i]!=0)
			{	int tmp=i;
				for (;_response.users[room][tmp]!=_response.users[room][i];++tmp);
				if (_response.times[room][tmp]>end)
				{	_response.times[room]=insertInArray(_response.times[room],indexEnd,end);
					_response.users[room]=insertInArray(_response.users[room],indexEnd,_response.users[room][i]);
					_response.temperatures[room]=insertInArray(_response.temperatures[room],indexEnd,_response.temperatures[room][i]);
				}
			}
		}
		if (indexEnd!=indexBegin)
		{	_response.times[room]=removeRangeInArray(_response.times[room],indexBegin,indexEnd);
			_response.users[room]=removeRangeInArray(_response.users[room],indexBegin,indexEnd);
			_response.temperatures[room]=removeRangeInArray(_response.temperatures[room],indexBegin,indexEnd);
		}
		indexBegin++;
		_response.temperatures[room]=insertInArray(_response.temperatures[room],indexBegin,0);
		_response.users[room]=insertInArray(_response.users[room],indexBegin,name);
		_response.times[room]=insertInArray(_response.times[room],indexBegin,end);
	}

	private void manageOwnerSchedule(int begin,int end,double temperature,String name,int room)
	{	temperature=(temperature>0)?-temperature:temperature; // to mark the priority, we use negative values
		if (temperature!=Double.MAX_VALUE)
		{	int indexEnd,indexBegin=Arrays.binarySearch(_response.times[room],begin);
			indexBegin=(indexBegin<0)?-indexBegin-1:indexBegin;
			for (;indexBegin+1<_response.times[room].length && _response.times[room].length!=0 && _response.times[room][indexBegin+1]==begin;++indexBegin);
			indexEnd=indexBegin;
			while (_response.times[room][indexEnd]!=end)
			{	if (_response.temperatures[room][indexBegin]<0)
				{	indexBegin++;
					indexEnd++;
				}
				else if (_response.temperatures[room][indexEnd]<0)
				{	insertSchedule(_response.times[room][indexBegin],_response.times[room][indexEnd],temperature,name,room);
					indexEnd++;
					indexBegin=indexEnd;
				}
				else
					indexEnd++;
			}
		}
	}

	private void manageStandardSchedule(int begin,int end,double temperature,String name,int room)
	{	temperature=(temperature<0)?-temperature:temperature; // lowest priority, we use positive values
		if (temperature!=Double.MAX_VALUE)
		{	int indexEnd,indexBegin=Arrays.binarySearch(_response.times[room],begin);
			indexBegin=(indexBegin<0)?-indexBegin-1:indexBegin;
			for (;indexBegin+1<_response.times[room].length && _response.times[room].length!=0 && _response.times[room][indexBegin+1]==begin;++indexBegin);
			indexEnd=indexBegin;
			while (indexEnd<_response.times[room].length && _response.times[room][indexEnd]!=end)
			{	if (indexBegin<_response.temperatures[room].length && (_response.temperatures[room][indexBegin]<0 || _response.temperatures[room][indexBegin]>temperature))
				{	indexBegin++;
					indexEnd++;
				}
				else if (_response.temperatures[room][indexEnd]<0 || _response.temperatures[room][indexEnd]>temperature)
				{	insertSchedule(_response.times[room][indexBegin],_response.times[room][indexEnd],temperature,name,room);
					indexEnd++;
					indexBegin=indexEnd;
				}
				else
					indexEnd++;
			}
		}
	}

	private int[] insertInArray(int[] array,int index,int element)
	{	int[] newArray=Arrays.copyOf(array,array.length+1);
		for (int i=index;i<array.length;++i)
			newArray[i+1]=newArray[i];
		newArray[index]=element;
		return newArray;
	}

	private double[] insertInArray(double[] array,int index,double element)
	{	double[] newArray=Arrays.copyOf(array,array.length+1);
		for (int i=index;i<array.length;++i)
			newArray[i+1]=newArray[i];
		newArray[index]=element;
		return newArray;
	}

	private String[] insertInArray(String[] array,int index,String element)
	{	String[] newArray=Arrays.copyOf(array,array.length+1);
		for (int i=index;i<array.length;++i)
			newArray[i+1]=newArray[i];
		newArray[index]=element;
		return newArray;
	}

	private int[] removeRangeInArray(int[] array,int begin,int end)
	{	int[] newArray=Arrays.copyOfRange(array,0,begin);
		newArray=Arrays.copyOf(newArray,newArray.length+array.length-end);
		for (int i=begin, j=end;j<array.length;++i,++j)
			newArray[i]=array[j];
		return newArray;
	}

	private double[] removeRangeInArray(double[] array,int begin,int end)
	{	double[] newArray=Arrays.copyOfRange(array,0,begin);
		newArray=Arrays.copyOf(newArray,newArray.length+array.length-end);
		for (int i=begin, j=end;j<array.length;++i,++j)
			newArray[i]=array[j];
		return newArray;
	}

	private String[] removeRangeInArray(String[] array,int begin,int end)
	{	String[] newArray=Arrays.copyOfRange(array,0,begin);
		newArray=Arrays.copyOf(newArray,newArray.length+array.length-end);
		for (int i=begin, j=end;j<array.length;++i,++j)
			newArray[i]=array[j];
		return newArray;
	}
}
