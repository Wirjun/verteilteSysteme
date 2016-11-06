package ex4;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Random;


public class NodeTable implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String ownerIP;
	private int ownerPort;
	private volatile LinkedList<TableEntry> entries = new LinkedList<TableEntry>();
	private int size;
	private Random generator = new Random();
	
	public NodeTable(String ip, int port, int _size){
		ownerIP = ip;
		ownerPort = port;
		size = _size;
	}
	
	/**
	 * determines if the table is full or not
	 * @return true if full, false if not
	 */
	public boolean isFull(){
		if(entries.size() == size){
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * determines if the table is empty or not
	 * @return true if full, false if not
	 */
	public boolean isEmpty(){
		if(entries.isEmpty()){
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * returns a random node from the table
	 * @return the random node
	 */
	public synchronized String[] getRandomNode(){
		int nodeInt = generator.nextInt(entries.size());
		String node[] = new String[2];
		node[0] = entries.get(nodeInt).getIp();
		node[1] = "" + entries.get(nodeInt).getPort();
		return node;
	}
	
	
	
	/**
	 * adds a new node to the table
	 * @param ip ip of the new node
	 * @param port port of the new node
	 * @param name name of the new node
	 */
	public synchronized void addNode(String ip, int port){
		entries.add(new TableEntry(ip, port));
		System.out.println(ownerIP + ":" + ownerPort + " added new node: " + ip + ":" + port);
	}
	

	/**
	 * looks for a certain node and removes it
	 * @param ip ip of the node
	 * @param port port of the node
	 */
	public synchronized void removeNode(String ip, int port){
		for(int i = 0; i < entries.size(); i++){
			if((entries.get(i).getIp()).equals(ip)){
				if(entries.get(i).getPort() == port){
					entries.remove(i);
					System.out.println(ownerIP + ":" + ownerPort + " removed node " + ip + ":" + port);
				}
			}
		}
	}
	
	public String[] getOwner(){
		String owner[] = new String[2];
		owner[0] = ownerIP;
		owner[1] = "" + ownerPort;
		return owner;
	}
	
	public synchronized void printTableInformation(){
		String information = ("------------------------------------------\n" +
						   	  "|Owner\t| " + ownerIP + "\t| " + ownerPort + "\t|\n" + 
						   	  "------------------------------------------\n");
		for(int i = 0; i < entries.size(); i++){
			information += "|" + (i+1) + "\t| " + entries.get(i).getIp() + "\t| " + entries.get(i).getPort() + "\t|\n";
		}
		information += "------------------------------------------";
		System.out.println(information);
	}
	
	public LinkedList<TableEntry> getOtherNodes(){
		return entries;
	}
	
	public LinkedList<TableEntry> getTableWithOwner(){
		LinkedList<TableEntry> tableCopy = new LinkedList<TableEntry>();
		for(TableEntry entry : entries){
			tableCopy.add(entry);
		}
		tableCopy.add(new TableEntry(ownerIP, ownerPort));
		return tableCopy;
	}
}
