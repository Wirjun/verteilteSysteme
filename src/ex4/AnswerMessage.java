package ex4;

import java.io.Serializable;


public class AnswerMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String searchedName;
	private String searchedIp;
	private int searchedPort;
	
	public AnswerMessage(String _searchedName, String _searchedIp, int _searchedPort){
		searchedName = _searchedName;
		searchedIp = _searchedIp;
		searchedPort = _searchedPort;
	}
	
	public String getName(){
		return searchedName;
	}
	
	public String getIp(){
		return searchedIp;
	}
	
	public int getPort(){
		return searchedPort;
	}
}
