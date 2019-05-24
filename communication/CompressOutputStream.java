package communication;

import java.net.Socket;
import java.io.OutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.Arrays;

public class CompressOutputStream extends OutputStream
{	
	private byte[] _buffer=null;
	private OutputStream _outputStream=null;
	private int _currentSlot=0;
	private int _size=1;


	public CompressOutputStream(OutputStream outputStream){_outputStream=outputStream;}
	public CompressOutputStream(OutputStream outputStream,int size){_outputStream=outputStream;_size=size;_buffer=new byte[size];}
	public CompressOutputStream(Socket socket) throws IOException {this(socket,false);}
	public CompressOutputStream(Socket socket,boolean buffered) throws IOException
	{	_outputStream=socket.getOutputStream();
		_size=socket.getSendBufferSize();
		if (buffered)
			_buffer=new byte[_size];
	}

	private void sendAll(byte[] buffer,int bufferLength) throws IOException
	{	if (bufferLength>0)
		{	Deflater deflater=new Deflater(9,false);
			byte[] compressedData=new byte[bufferLength+(Integer.SIZE/Byte.SIZE)*2];
			int length;

			deflater.setInput(buffer,(Integer.SIZE/Byte.SIZE)*2,bufferLength);
			deflater.finish();
			length=deflater.deflate(compressedData,+(Integer.SIZE/Byte.SIZE)*2,bufferLength,Deflater.SYNC_FLUSH)+(Integer.SIZE/Byte.SIZE)*2;

			int i=(Integer.SIZE/Byte.SIZE)*2-1;
			for(;i>Integer.SIZE/Byte.SIZE;--i)
			{	byte tmp=(byte)(bufferLength>>Byte.SIZE);
				compressedData[i]=tmp;
			}
			for(;i>0;--i)
			{	byte tmp=(byte)(length>>Byte.SIZE);
				compressedData[i]=tmp;
			}

			_outputStream.write(compressedData,0,length);
			buffer=null;
			compressedData=null;
			deflater=null;
			System.gc();
		}
	}

	private void sendAll() throws IOException {sendAll(_buffer,_currentSlot);}

	public void write(int data) throws IOException
	{	if (_buffer==null) _buffer=new byte[_size];
		_buffer[_currentSlot++]=(byte)data;
		if (_currentSlot==_buffer.length)
		{	sendAll();
			_currentSlot=0;
		}
	}

	public void write(byte[] data,int begin,int length) throws IOException
	{	sendAll(Arrays.copyOfRange(data,begin,begin+length),length);
	}

	public void write(byte[] data) throws IOException {write(data,0,data.length);}

	public void flush() throws IOException
	{	if (_currentSlot!=0) sendAll();
		_outputStream.flush();
	}

	public void close() throws IOException
	{	flush();
		_outputStream.close();
	}
}