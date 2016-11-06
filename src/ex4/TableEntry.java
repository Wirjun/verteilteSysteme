package ex4;

import java.io.Serializable;


public class TableEntry implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ip;
	private int port;
	
	public TableEntry(String _ip, int _port){
		ip = _ip;
		port = _port;
	}

	public String getIp() {
		return ip;
	}

	public int getPort(){
		return port;
	}
	
	
	
}
