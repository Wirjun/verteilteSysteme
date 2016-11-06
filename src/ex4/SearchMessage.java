package ex4;

import java.io.Serializable;


public class SearchMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int id;
	private final String name;
	private final String startIp;
	private final int startPort;
	
	public SearchMessage(int _id, String _name, String _startIp, int _startPort){
		id = _id;
		name = _name;
		startIp = _startIp;
		startPort = _startPort;
	}
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	public String getStartIp(){
		return startIp;
	}
	
	public int getStartPort(){
		return startPort;
	}
}
