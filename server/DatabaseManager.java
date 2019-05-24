package database;

// import java.sql;
import java.sql.Statement;
import java.util.GregorianCalendar;
import java.util.Arrays;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;


class DatabaseManager
{
	private final static String SETTINGSFILE="./Settings/rootDBIDs.txt";
	private final static String DBADDRESS="jdbc:mysql://127.0.0.1:3306/printemps";
	private final static String SQLSCRIPT="./DB_creation.sql";
	private final static String SQLINSERT="./DB_insert_dataSet.sql";
	private final static int DAYS_IN_MONTH=28;
	private final static int MONTHS_IN_YEAR=12;

	private static java.sql.Connection connect()
	{	String root;
		String rootpasswd;
		java.sql.Connection connection=null;

		try
		{	BufferedReader file = new BufferedReader(new FileReader(SETTINGSFILE));
			root=file.readLine();
			rootpasswd=file.readLine();
			file.close();
		} catch (Exception exception)
		{	System.err.println("Error while reading Settind file : "+exception);
			System.err.println("This program is about to shut down.");
			return null;
		}
			
		try
		{	connection= java.sql.DriverManager.getConnection(DBADDRESS,root,rootpasswd);
		} catch (java.sql.SQLException exception)
		{	if (exception.getSQLState()=="28000")
			{	System.err.println("Connection as root failed, please check root name and password.");
				System.err.println("This program is about to shut down.");
			}
			else
			{	System.err.println("Unexpected error while accessing database as root : "+exception);
				System.err.println("This program is about to shut down.");
			}
		}
		return connection;
	}

	protected static boolean createNewUser(String user,String passwd)
	{	java.sql.Connection connection=connect();
		
		if (connection==null)
			return false;

		try
		{	java.sql.Statement statement=connection.createStatement();
			statement.execute(String.format("CREATE USER '%s' IDENTIFIED BY '%s';",user,passwd));
			statement.execute(String.format("GRANT DELETE,INSERT,SELECT,UPDATE ON printemps.Users TO '%s';",user));
			statement.execute(String.format("GRANT DELETE,INSERT,SELECT,UPDATE ON printemps.Rooms TO '%s';",user));
			statement.execute(String.format("GRANT DELETE,INSERT,SELECT,UPDATE ON printemps.Dates TO '%s';",user));
			statement.execute(String.format("GRANT DELETE,INSERT,SELECT,UPDATE ON printemps.DataTable TO '%s';",user));
			statement.execute("FLUSH PRIVILEGES;");
			return true;
		} catch (java.sql.SQLException exception)
		{	System.err.println("Unexpected error while creating new user : "+exception);
			System.err.println("This program is about to shut down.");
			return false;
		}
	}
	
	protected static boolean executeScript(String fileName) {
		java.sql.Connection connection=connect();
		String command=null;

		if (connection==null)
			return false;

		try
		{	java.sql.Statement statement=connection.createStatement();
			BufferedReader file = new BufferedReader(new FileReader(fileName));
			while ((command=file.readLine())!=null) 
			{	while (!command.endsWith(";"))
					command+=file.readLine();
				statement.execute(command);
			}
			file.close();
		}
		catch (IOException exception)
		{	System.err.println("Error while reading sql script : "+exception);
			System.err.println("This program is about to shut down.");
			return false;
		}
		catch (java.sql.SQLException exception)
		{	System.err.println(String.format("Error while executing command '%s'\n\n%s",command,exception));
			System.err.println("This program is about to shut down.");
			return false;
		}
		catch (Exception exception)
		{	System.err.println("Unexpected error while initializing database : "+exception);
			System.err.println("This program is about to shut down.");
			return false;
		}
		return true;
	}

	protected static boolean init() {	
		String fileName = SQLSCRIPT;
		return (executeScript(fileName) && insert());
	}
	
	protected static boolean insert() {
		try {
			// insert dates
			// récupération de la date actuelle
			java.sql.Connection connection=connect();
			java.sql.Statement statement=connection.createStatement();
			java.sql.ResultSet results=statement.executeQuery("SELECT CURDATE();");
			if (!results.next()) return false;
			String date = new java.text.SimpleDateFormat("yyyy-MM-dd").format(results.getDate(1));
			
			// insert les 28 jours à venir
			// date format = "YYYY-MM-DD"
			int sizeOfDate = date.length();
			int actualDay = Integer.parseInt(date.substring(sizeOfDate-2));
			int actualMonth = Integer.parseInt(date.substring(sizeOfDate-5,sizeOfDate-3));
			int actualYear = Integer.parseInt(date.substring(0,4));
			// roll one month back
			actualDay++; actualMonth--;
			String query = "INSERT INTO Dates(DataDate) VALUES";
			for (int i = 0; i<DAYS_IN_MONTH; i++){
				if (actualDay >DAYS_IN_MONTH){actualDay=1;actualMonth++;}
				if (actualMonth >MONTHS_IN_YEAR){actualMonth = 1; actualYear++;}
				query += "('"+actualYear+"-"+actualMonth+"-"+actualDay+"'),";
				actualDay = ++actualDay;
			}
			query = query.substring(0,query.length()-1) + ";";
			statement.execute(query);
			statement.close();
			connection.close();		
			
			
			// insert other tables
			String fileName = SQLINSERT;
			return executeScript(fileName);
		} catch (java.sql.SQLException e) {
			System.err.println("Error in DatabaseManager::insert : "+e);
		}
		return false;
	}

}