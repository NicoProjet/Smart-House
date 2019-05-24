package communication;

import java.net.Socket;
import java.net.InetAddress;
import java.net.ConnectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import communication.Request;
import communication.RequestChange;
import communication.RequestChangeSchedule;
import communication.Response;
import communication.Schedule;
import android.util.Log;
import android.os.AsyncTask;


public class NetworkAccess extends AsyncTask<Request,Void,Schedule>
{
    @Override
    protected Schedule doInBackground(Request... requests)
    {	Schedule res=null;
        for (Request request : requests)
            try
            {	//Socket socket=new Socket(InetAddress.getLocalHost(),3000);
                //Socket socket= new Socket(InetAddress.getByName("192.168.1.56"),3000); // JMF
                //Socket socket= new Socket(InetAddress.getByName("91.178.61.171"),3000);
                Socket socket= new Socket(InetAddress.getByName("51.254.136.169"),3000); // OVH
                ObjectOutputStream streamOut=new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream streamIn=new ObjectInputStream(socket.getInputStream());
                streamOut.writeObject(request);
                res=new Schedule((Response)streamIn.readObject());
                streamIn.close();
                streamOut.close();
                socket.close();
            }
            catch (ConnectException exception)
            {
                Log.d("STATE","Connection error : "+exception);
                //System.err.println("Connection error : "+exception);
                return null;
            }
            catch (Exception exception)
            {	Log.d("STATE","Unexpected error in NetworkAccess::doInBackground : "+exception);
                //System.err.println("Unexpected error in NetworkAccess::genericGet : "+exception);
                return null;
            }
        return res;
    }

	private static Schedule genericGet(Request request)
	{	NetworkAccess na=new NetworkAccess();
        na.execute(request);
        try{
            return na.get();
        }
        catch (Exception exception){
            Log.d("STATE","Unexpected error in NetworkAccess::genericGet : "+exception);
        }
        return new Schedule();
	}

	public static Schedule planSchedule(int time,int user,int room,double temperature)
	{	return genericGet(new RequestChangeSchedule(time,user,room,temperature));
	}

	public static Schedule changeTemperature(int user,int room,double temperature)
	{	Log.d("STATE",String.format("NetworkAccess:68 -> user: %d room: %d temperature: %d",user,room,new Double(temperature).intValue()));
        return genericGet(new RequestChange(user,room,temperature));
	}

	public static Schedule getSchedule()
	{	return genericGet(new Request());
	}
}