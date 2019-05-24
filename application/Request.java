package communication;

import java.io.Serializable;


public class Request implements Serializable
{
	protected char _ID=0;
	private static final long serialVersionUID = 1L;

	public char getID(){return _ID;}
}