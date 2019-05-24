package communication;

import java.net.Socket;
import java.io.InputStream;
import java.io.IOException;
import java.util.zip.Inflater;
import java.util.zip.DataFormatException;

public class CompressInputStream extends InputStream
{
	private InputStream _inputStream=null;
	private byte[] _buffer=null;
	private int _currentSlot;

	public CompressInputStream(InputStream inputStream) {_inputStream=inputStream;}
	public CompressInputStream(Socket socket) throws IOException {_inputStream=socket.getInputStream();}

	private int readAll() throws IOException
	{	// Read all the input from the stream and uncompress it
		int compressedDataLen=0;
		int length=0;
		_currentSlot=0;
		Inflater inflater=new Inflater(false);

		for (int i=0;i<Integer.SIZE/Byte.SIZE;i++)
		{	byte tmp=(byte)_inputStream.read();
			if (tmp<0) return -1;
			compressedDataLen=compressedDataLen<<Byte.SIZE;
			compressedDataLen+=(int)tmp;
		}
		for (int i=0;i<Integer.SIZE/Byte.SIZE;i++)
		{	byte tmp=(byte)_inputStream.read();
			if (tmp<0) return -1;
			length=length<<Byte.SIZE;
			length+=(int)tmp;
		}

		byte[] compressedData=new byte[compressedDataLen];
		_buffer=new byte[length];

		for (int i=0;i<compressedDataLen;)
		{	int len=_inputStream.read(compressedData,i,compressedDataLen-i);
			if (len<0) return -1;
			i+=len;
		}

		inflater.setInput(compressedData,0,compressedDataLen);
		try
		{	inflater.inflate(_buffer);
		}
		catch(DataFormatException exception)
		{	System.err.println("Error in CompressInputStream::read : "+exception);
			return -1;
		}
		return compressedDataLen;
	}

	public int read() throws IOException
	{	if (_currentSlot==_buffer.length && readAll()==-1) return -1;
		return (int)_buffer[_currentSlot++];
	}

	public int read(byte[] dest, int begin, int length) throws IOException
	{	int count=0;
		while (count<length)
		{	if (_currentSlot==_buffer.length && readAll()==-1) return (count==0)?-1:count;

			int offset=(_buffer.length-_currentSlot<length)?_buffer.length-_currentSlot:length;
			{	count+=offset;
				System.arraycopy(_buffer,_currentSlot,dest,begin,offset);
				begin+=offset;
				_currentSlot+=offset;
			}
		}

		return count;
	}

	public int read(byte[] dest) throws IOException
	{	return read(dest,0,dest.length);
	}

	public int available(){return _buffer.length-_currentSlot;}
	public boolean markSupported(){return false;}

	public long skip(long n)
	{	if (n>_buffer.length-_currentSlot)
		{	_currentSlot=_buffer.length;
			return _buffer.length-_currentSlot;
		}
		else
		{	_currentSlot=_currentSlot+(int)n;
			return n;
		}
	}

	public void close() throws IOException
	{	_inputStream.close();
		_inputStream=null;
		_currentSlot=0;
		_buffer=null;
		System.gc();
		super.close();
	}
}