package ex4;

import java.io.Serializable;


public class BroadcastMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int id;
	private final String message;
	private final String startIp;
	private final int startPort;
	
	public BroadcastMessage(int _id, String _message, String _startIp, int _startPort){
		id = _id;
		message = _message;
		startIp = _startIp;
		startPort = _startPort;
	}
	
	public int getId(){
		return id;
	}
	
	public String getMessage(){
		return message;
	}
	
	public String getStartIp(){
		return startIp;
	}
	
	public int getStartPort(){
		return startPort;
	}
	
}
