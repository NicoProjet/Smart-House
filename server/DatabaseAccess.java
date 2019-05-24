package database;

// import java.sql;
import java.util.Date;
import java.util.Arrays;
import java.io.FileReader;
import java.io.BufferedReader;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import scheduleControl.Schedule;


public class DatabaseAccess
{
	static private Date _date;
	static private String _user=null;
	static private String _passwd=null;
	static private int _resetTime=0;
	static private String _databaseAddress=null;
	static private String _fileName=new String("./Settings/databaseInfo.txt");
	static final private long ONEDAY=86400000;

	public DatabaseAccess()
	{	try
		{	BufferedReader file = new BufferedReader(new FileReader(_fileName));
			_user=file.readLine();
			_passwd=file.readLine();
			_resetTime=Integer.parseInt(file.readLine())*60;
			_databaseAddress=file.readLine();
			file.close();
		} catch (Exception exception)
		{	System.err.println("Error while reading Settind file : "+exception);
			System.err.println("This program is shutting down.");
			System.exit(0);
		}
		update();
	}

	private Connection connect()
	{	Connection connection=null;
		try
		{	connection=java.sql.DriverManager.getConnection(_databaseAddress,_user,_passwd);
		} catch (SQLException exception)
		{	if (exception.getSQLState().contentEquals("28000"))
			{	System.out.println(String.format("Connection as %s failed.",_user));
				System.out.println("Creating a new user ...");
				if (DatabaseManager.createNewUser(_user,_passwd))
					System.out.println("New user created : "+_user);
				else
					System.exit(0);
				return connect();
			}
			else
			{	System.out.println(exception.getSQLState());
				System.err.println("Unexpected error while accessing database : "+exception);
				System.err.println("This program is shutting down.");
				System.exit(0);
			}
		}
		return connection;
	}

	public boolean update()
	{	try
		{	Connection connection=connect();
			Statement statement=connection.createStatement();
			ResultSet results=statement.executeQuery("SELECT CURDATE();");
			if (!results.next()) return false;
			_date=results.getDate(1);
			statement.close();
			connection.close();
			return true;
		} catch (SQLException exception)
		{	System.err.println("Unexpected error in DatabaseAccess::update : "+exception);
			return false;
		}
	}

	public boolean addUser(String name,boolean isAdmin)
	{	try
		{	Connection connection=connect();
			Statement statement=connection.createStatement();
			ResultSet results=statement.executeQuery(String.format("SELECT COUNT(Users_ID) FROM Users WHERE (Name='%s');",name));
			if (!results.next()) return false;
			if (results.getInt(1)!=0) return false;
			results=statement.executeQuery(String.format("SELECT COUNT(Users_ID) FROM Users WHERE (IsAdmin=%s);",Boolean.toString(isAdmin).toUpperCase()));
			if (!results.next()) return false;
			if (isAdmin && results.getInt(1)!=0) return false;
			statement.executeUpdate(String.format("INSERT INTO Users(Name,IsAdmin) VALUES ('%s',%s);",name,(isAdmin)?"TRUE":"FALSE"));
			statement.close();
			connection.close();
			return true;
		} catch (SQLException exception)
		{	System.err.println("Unexpected error in DatabaseAccess::addUser : "+exception);
			return false;
		}
	}

	public boolean addRoom(String name,int secPerDegree,int userID)
	{	try
		{	Connection connection=connect();
			Statement statement=connection.createStatement();
			ResultSet results=statement.executeQuery(String.format("SELECT COUNT(Rooms_ID) FROM Rooms WHERE (Name='%s');",name));
			if (!results.next()) return false;
			if (results.getInt(1)!=0) return false;
			if (userID!=-1)
				statement.executeUpdate(String.format("INSERT INTO Rooms(Name,SecPerDegree,Users_ID) VALUES ('%s',%d,%d);",name,secPerDegree,userID));
			else
				statement.executeUpdate(String.format("INSERT INTO Rooms(Name,SecPerDegree) VALUES ('%s',%d);",name,secPerDegree));
			statement.close();
			connection.close();
			return true;
		} catch (SQLException exception)
		{	System.err.println("Unexpected error in DatabaseAccess::addRoom : "+exception);
			return false;
		}
	}

	public boolean addRoom(String name,int secPerDegree)
	{	return addRoom(name,secPerDegree,-1);
	}

	public boolean addSchedule(int time,int user,int room,double temperature){return addSchedule(time,user,room,temperature,getDate());}

	public boolean addSchedule(int time,int user,int room,double temperature,String date)
	{	update();
		try
		{	Connection connection=connect();
			Statement statement=connection.createStatement();
			ResultSet results;
			results=statement.executeQuery(String.format("SELECT COUNT(Dates_ID) FROM Dates WHERE (DataDate='%s');",date));
			if (!results.next()) return false;
			if (results.getInt(1)==0)
				statement.executeUpdate(String.format("INSERT INTO Dates(DataDate) VALUES ('%s');",date));
			statement.executeUpdate(String.format("INSERT INTO DataTable(Users_ID,Rooms_ID,DesiredDegree,TimeOfChange,Dates_ID) VALUES (%d,%d,%d,%d,(SELECT Dates_ID FROM Dates WHERE DataDate='%s'));",user,room,(int)(temperature*100),(time+_resetTime)%scheduleControl.Day.MAXTIME,date));
			statement.close();
			connection.close();

			System.out.println(String.format("added change user: %d | room: %d | temperature: %d  in DB",user,room,new Double(temperature).intValue()));
			return true;
		} catch (SQLException exception)
		{	System.err.println("Unexpected error in DatabaseAccess::addSchedule : "+exception);
			return false;
		}
	}

	public int getUserID(String name)
	{	try
		{	int res;
			Connection connection=connect();
			Statement statement=connection.createStatement();
			ResultSet results;
			results=statement.executeQuery(String.format("SELECT Users_ID FROM Users WHERE (Name='%s');",name));
			if (!results.next()) return -1;
			res=results.getInt(1);
			statement.close();
			connection.close();
			if (res==0) return -1;
			return res;
		} catch (SQLException exception)
		{	System.err.println("Unexpected error in DatabaseAccess::getUserID : "+exception);
			return -1;
		}
	}

	public int getRoomID(String name)
	{	try
		{	int res;
			Connection connection=connect();
			Statement statement=connection.createStatement();
			ResultSet results;
			results=statement.executeQuery(String.format("SELECT Rooms_ID FROM Rooms WHERE (Name='%s');",name));
			if (!results.next()) return -1;
			res=results.getInt(1);
			statement.close();
			connection.close();
			if (res==0) return -1;
			return res;
		} catch (SQLException exception)
		{	System.err.println("Unexpected error in DatabaseAccess::getRoomID : "+exception);
			return -1;
		}
	}

	public int getOwner(int room)
	{	try
		{	int res;
			Connection connection=connect();
			Statement statement=connection.createStatement();
			ResultSet results;
			results=statement.executeQuery(String.format("SELECT Users_ID FROM Rooms WHERE (Rooms_ID=%d);",room));
			if (!results.next()) return -1;
			res=results.getInt(1);
			statement.close();
			connection.close();
			if (res==0) return -1;
			return res;
		} catch (SQLException exception)
		{	System.err.println("Unexpected error in DatabaseAccess::getOwner : "+exception);
			return -1;
		}
	}

	public int getAdmin()
	{	try
		{	int res;
			Connection connection=connect();
			Statement statement=connection.createStatement();
			ResultSet results;
			results=statement.executeQuery("SELECT Users_ID FROM Users WHERE (IsAdmin=TRUE);");
			if (!results.next()) return -1;
			res=results.getInt(1);
			statement.close();
			connection.close();
			if (res==0) return -1;
			return res;
		} catch (SQLException exception)
		{	System.err.println("Unexpected error in DatabaseAccess::getAdmin : "+exception);
			return -1;
		}
	}

	public String getUserName(int id)
	{	String res=new String("");
		try
		{	Connection connection=connect();
			Statement statement=connection.createStatement();
			ResultSet results;
			results=statement.executeQuery(String.format("SELECT Name FROM Users WHERE (Users_ID=%d);",id));
			if (!results.next()) return res;
			res=results.getString(1);
			statement.close();
			connection.close();
			return res;
		} catch (SQLException exception)
		{	System.err.println("Unexpected error in DatabaseAccess::getUserName : "+exception);
			return res;
		}
	}

	public String getRoomName(int id)
	{	String res=new String("");
		try
		{	Connection connection=connect();
			Statement statement=connection.createStatement();
			ResultSet results;
			results=statement.executeQuery(String.format("SELECT Name FROM Rooms WHERE (Rooms_ID=%d);",id));
			if (!results.next()) return res;
			res=results.getString(1);
			statement.close();
			connection.close();
			return res;
		} catch (SQLException exception)
		{	System.err.println("Unexpected error in DatabaseAccess::getRoomName : "+exception);
			return res;
		}
	}

	public int getDelay(int room)
	{	// return the necessary time to warm a room up to one more degree
		try
		{	int res;
			Connection connection=connect();
			Statement statement=connection.createStatement();
			ResultSet results;
			results=statement.executeQuery(String.format("SELECT SecPerDegree FROM Rooms WHERE (Rooms_ID=%d);",room));
			statement.close();
			connection.close();
			if (!results.next()) return -1;
			res=results.getInt(1);
			if (res==0) return -1;
			return res;
		} catch (SQLException exception)
		{	System.err.println("Unexpected error in DatabaseAccess::getDelay : "+exception);
			return -1;
		}
	}


	public int[] getPresentUsers(int room)
	{	/*	method returning all users present in a room based on the fact that
			they have not turned down the temperature after having turned it up
		*/
		update();
		try
		{	int[] res;
			Connection connection=connect();
			Statement statement=connection.createStatement();
			ResultSet results;
			results=statement.executeQuery(String.format("SELECT COUNT(DISTINCT DT1.Users_ID) FROM DataTable DT1 WHERE (DT1.Rooms_ID=%d AND DT1.DesiredDegree>0 AND Dates_ID=(SELECT Dates_ID FROM Dates WHERE DataDate='%s') AND DT1.TimeOfChange>ALL(SELECT DT2.TimeOfChange FROM DataTable DT2 WHERE (DT2.Rooms_ID=%d AND DT2.DesiredDegree=0)));",room,getDate(),room));
			if (!results.next())
			{	statement.close();
				connection.close();
				return null;
			}
			res=new int[results.getInt(1)];
			results=statement.executeQuery(String.format("SELECT DISTINCT DT1.Users_ID FROM DataTable DT1 WHERE (DT1.Rooms_ID=%d AND DT1.DesiredDegree>0 AND Dates_ID=(SELECT Dates_ID FROM Dates WHERE DataDate='%s') AND DT1.TimeOfChange>ALL(SELECT DT2.TimeOfChange FROM DataTable DT2 WHERE (DT2.Rooms_ID=%d AND DT2.DesiredDegree=0)));",room,getDate(),room));
			for (int i=0;i<res.length;++i)
			{	if (!results.next()) return null;
				res[i]=results.getInt(1);
			}
			statement.close();
			connection.close();
			Arrays.sort(res);
			return res;
		} catch (SQLException exception)
		{	System.err.println("Unexpected error in DatabaseAccess::getPresentUsers : "+exception);
			return null;
		}
	}

	public int getCurrentDay()
	{	// return the day of the week
		java.util.Calendar calendar=java.util.Calendar.getInstance();
		update();
		calendar.setTime(_date);
		return (7-calendar.getFirstDayOfWeek()+calendar.get(java.util.Calendar.DAY_OF_WEEK))%7;
	}

	private String getDate(Date date) {return new java.text.SimpleDateFormat("yyyy-MM-dd").format(date);}

	public String getDate()
	{	update();
		return getDate(_date);
	}

	public int getTime()
	{	/*	method to get the current time in minutes, must be
			modified if time 0 is not actually midnight
			(if time 0 is 4 o'clock, then this method should return
			1379 (minutes) at 3 o'clock)
		*/
		try
		{	String time;
			int res;
			Connection connection=connect();
			Statement statement=connection.createStatement();
			ResultSet results=statement.executeQuery("SELECT CURTIME();");
			if (!results.next()) return -1;
			time=results.getTime(1).toString();
			// time format : "hh:mm:ss"
			res=Integer.parseInt(time.substring(0,2))*60;
			res+=Integer.parseInt(time.substring(3,5));
			res++;//=(Integer.parseInt(time.substring(6))>=30)?1:0;
			res=(res<_resetTime)?scheduleControl.Day.MAXTIME+res-_resetTime:res-_resetTime;
			statement.close();
			connection.close();
			return res;
		} catch (SQLException exception)
		{	System.err.println("Unexpected error in DatabaseAccess::getTime : "+exception);
			return -1;
		}
	}

	public int[] getUsedRooms(int user,int day) {
		update();
		Date date=new Date(_date.getTime()-day*ONEDAY);
		try {
			int[] res;
			Connection connection=connect();
			Statement statement=connection.createStatement();
			ResultSet results;
			results=statement.executeQuery(String.format("SELECT COUNT(DISTINCT Rooms_ID) FROM DataTable WHERE (Users_ID=%d AND Dates_ID=(SELECT Dates_ID FROM Dates WHERE DataDate='%s'));",user,getDate(date)));
			if (!results.next())
			{	statement.close();
				connection.close();
				return null;
			}
			res=new int[results.getInt(1)];
			results=statement.executeQuery(String.format("SELECT DISTINCT Rooms_ID FROM DataTable WHERE (Users_ID=%d AND Dates_ID=(SELECT Dates_ID FROM Dates WHERE DataDate='%s'));",user,getDate(date)));
			for (int i=0;i<res.length;++i)
			{	if (!results.next()) return null;
				res[i]=results.getInt(1);
			}
			statement.close();
			connection.close();
			return res;
		} catch (SQLException exception) {
			System.err.println("Unexpected error in DatabaseAccess::getUsedRooms : "+exception);
			return null;
		}
	}

	public Schedule[] getDaySchedules(int day,int user,int room) {
		update();
		Date date=new Date(_date.getTime()-day*ONEDAY);
		try {
			Schedule[] res;
			Connection connection=connect();
			Statement statement=connection.createStatement();
			ResultSet results;
			results=statement.executeQuery(String.format("SELECT COUNT(DISTINCT TimeOfChange, DesiredDegree) FROM DataTable WHERE (Users_ID=%d AND Rooms_ID=%d AND Dates_ID=(SELECT Dates_ID FROM Dates WHERE DataDate='%s'));",user,room,getDate(date)));
			if (!results.next())
			{	statement.close();
				connection.close();
				return null;
			}
			res=new Schedule[results.getInt(1)];
			results=statement.executeQuery(String.format("SELECT DISTINCT TimeOfChange, DesiredDegree FROM DataTable WHERE (Users_ID=%d AND Rooms_ID=%d AND Dates_ID=(SELECT Dates_ID FROM Dates WHERE DataDate='%s'));",user,room,getDate(date)));
			for (int i=0;i<res.length;++i)
			{	if (!results.next()) return null;
				res[i]=new Schedule(results.getInt(1),results.getInt(2)/100,user,room);
			}
			statement.close();
			connection.close();
			return res;
		} catch (SQLException exception) {
			System.err.println("Unexpected error in DatabaseAccess::getDaySchedules : "+exception);
			return null;
		}
	}

	public int[] getRooms() {
		try {
			int[] res;
			Connection connection=connect();
			Statement statement=connection.createStatement();
			ResultSet results;
			results=statement.executeQuery("SELECT COUNT(Rooms_ID) FROM Rooms;");
			if (!results.next())
			{	statement.close();
				connection.close();
				return null;
			}
			res=new int[results.getInt(1)];
			results=statement.executeQuery("SELECT DISTINCT Rooms_ID FROM Rooms;");
			for (int i=0;i<res.length;++i)
			{	if (!results.next()) return null;
				res[i]=results.getInt(1);
			}
			statement.close();
			connection.close();
			return res;
		} catch (SQLException exception) {
			System.err.println("Unexpected error in DatabaseAccess::getRooms : "+exception);
			return null;
		}
	}

	public int[] getUsers() {
		int[] res={};
		try {
			Connection connection=connect();
			Statement statement=connection.createStatement();
			ResultSet results;
			results=statement.executeQuery("SELECT COUNT(Users_ID) FROM Users;");
			if (!results.next())
			{	statement.close();
				connection.close();
				return res;
			}
			res=new int[results.getInt(1)];
			results=statement.executeQuery("SELECT DISTINCT Users_ID FROM Users;");
			for (int i=0;i<res.length;++i)
			{	if (!results.next()) return res;
				res[i]=results.getInt(1);
			}
			statement.close();
			connection.close();
			return res;
		} catch (SQLException exception) {
			System.err.println("Unexpected error in DatabaseAccess::getUsers : "+exception);
			return res;
		}
	}

	public int getResetTime(){return _resetTime;}

	public void init()
	{	if (!DatabaseManager.init())
			System.exit(0);
	}
}
