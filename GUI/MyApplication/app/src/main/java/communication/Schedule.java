package communication;

import android.util.Log;

import communication.Response;
import java.util.Arrays;
import java.util.Comparator;


public class Schedule
{	
	private String[] _rooms={};
	private int[][] _times={{}};
	private String[][] _users={{}};
	private double[][] _temperatures={{}};
	private static final int MAXTIME=1440;
    private int _currentTime;

    public Schedule()
    {}

	public Schedule(Response response)
	{	_rooms=response.getRooms();
		_times=response.getTimes();
		_users=response.getUsers();
		_temperatures=response.getTemperatures();
        _currentTime=response.currentTime;

        for (int i = 0; i<_temperatures.length; i++){
            for (int j = 0; j<_temperatures[i].length; j++){
                Log.d("CHANGES RECEIVED","Schedule.java:31 -> temp: "+_temperatures[i][j]+ "  |  time: "+_times[i][j]);
            }
        }
        // CHECK IF NECESSARY (or already done)
		/*for (int[] times : _times)
			for (int i=0;i<times.length;++i)
				times[i]=(times[i]+response.getResetTime())%MAXTIME;*/
	}

	public String[] getRooms(){
        return _rooms;}
	public int[][] getTimes(){return _times;}
	public int[] getTemps(){
		int[] response=new int[_rooms.length];
        if (_rooms.length==0)
            return response;
        for (int i=0;i<_rooms.length;++i){
            int timeIndex=Arrays.binarySearch(_times[i],_currentTime);
            if (timeIndex<0)
                timeIndex=-timeIndex-2;
            else
                for (;timeIndex<_times[i].length && _times[i][timeIndex]==_currentTime;++timeIndex);
            if (timeIndex>=0)
                response[i]=new Double(_temperatures[i][timeIndex]).intValue();
        }
        return response;
	}
    public TemperatureChange[] getChanges() {
        TemperatureChange[] tempList;
        int length=0;
        for (int[] time : _times) length+=time.length;
        tempList = new TemperatureChange[length--];
        for (int i=0; i<_rooms.length;++i)
            for (int j=0; j<_times[i].length; ++j)
                tempList[length--] = new TemperatureChange(_times[i][j], _temperatures[i][j], _rooms[i]);
        Arrays.sort(tempList, new Comparator<TemperatureChange>() {
            @Override
            public int compare(TemperatureChange o1, TemperatureChange o2) {
                return o1.getTime()-o2.getTime();
            }
        });
        return tempList;
    }

}