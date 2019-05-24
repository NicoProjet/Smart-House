package communication;

import java.io.Serializable;

public class Response implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	protected int resetTime;
	protected String[] rooms;
	protected int[][] times; // schedules per rooms
	protected String[][] users; // users per room (size of lists should be half the size of lists in times)(only the user responsible of the actual change)
	protected double[][] temperatures; // temperature in the rooms
	protected int currentTime;

	protected Response(){}

	public String[] getRooms() {return rooms;}
	public int getResetTime() {return resetTime;}
	public int[][] getTimes() {return times;}
	public String[][] getUsers() {return users;}
	public double[][] getTemperatures() {return temperatures;}
	public int getCurrentTime() {return currentTime;}
}