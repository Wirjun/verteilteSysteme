package ex4;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;


public class RequestHandler implements Runnable{

	private NodeTable table = null;
	private LinkedList<TableEntry> entries = null;
	private boolean answerWithTable;
	private boolean tableRequest = false;
	private HashMap<String, LinkedList<Integer>> broadcastIds = null;
	private BroadcastMessage broadcastMessage = null;
	private boolean broadcastRequest = false;
	private String broadcastRecievedFromIP;
	private int broadcastRecievedFromPort;
	
	private SearchMessage searchMessage = null;
	private String nameOfNode;
	private boolean searchRequest = false;
	
	public RequestHandler(NodeTable _table, LinkedList<TableEntry> _entries, boolean _answerWithTable){
		table = _table;
		entries = _entries;
		answerWithTable = _answerWithTable;
		tableRequest = true;
	}
	
	public RequestHandler(NodeTable _table, HashMap<String, LinkedList<Integer>>  _broadcastIds, BroadcastMessage _message, Socket client){
		table = _table;
		broadcastIds = _broadcastIds;
		broadcastMessage = _message;
		broadcastRecievedFromIP = client.getLocalAddress().toString().substring(1);
		broadcastRecievedFromPort = client.getLocalPort();
		broadcastRequest = true;
	}
	
	public RequestHandler(NodeTable _table, String _name, HashMap<String, LinkedList<Integer>> _broadcastIds, SearchMessage _message, Socket client){
		table = _table;
		nameOfNode = _name;
		broadcastIds = _broadcastIds;
		searchMessage = _message;
		broadcastRecievedFromIP = client.getLocalAddress().toString().substring(1);
		broadcastRecievedFromPort = client.getLocalPort();
		searchRequest = true;
	}
	
	private synchronized void  fillTableWithNewInformation(){
		String owner[] = table.getOwner();
		boolean contain = false;
		
		if(answerWithTable){
			String otherNodeIp = entries.getLast().getIp();
			int otherNodePort = entries.getLast().getPort();
			try{						
				Socket socket = new Socket(otherNodeIp, otherNodePort);
				try {
					ObjectOutputStream serverOos = new ObjectOutputStream(socket.getOutputStream());
					serverOos.writeObject(table.getTableWithOwner());
				} catch (IOException e) {
					System.out.println("Error on sending request");
				}
			}catch(UnknownHostException uhe){
				System.out.println("IP could not be determined: " + uhe.getMessage());
				System.out.println("Delete " + otherNodeIp + ":" + otherNodePort + " from table.");
				table.removeNode(otherNodeIp, otherNodePort);
			}catch(IOException ioe){
				System.out.println("Error on creating the socket: " + ioe.getMessage());
				System.out.println("Delete " + otherNodeIp + ":" + otherNodePort + " from table.");
				table.removeNode(otherNodeIp, otherNodePort);
			}
		}
		
		for(TableEntry newEntry : entries){
			//if NodeTable is not full
			if(!table.isFull()){
				contain = false;
				String ip = newEntry.getIp();
				int port = newEntry.getPort();
				
				//check if new entry is the owner itself
				if(ip.equals(owner[0])){
					if(port == Integer.parseInt(owner[1])){
						contain = true;
					}
				}
				
				//check if the entry is already in the NodeTable
				if(!contain){					
					for(TableEntry oldEntry : table.getOtherNodes()){
						if(oldEntry.getIp().equals(ip)){
							if(oldEntry.getPort() == port){
								contain=true;
								break;
							}
						}
					}
				}
				
				//if not in NodeTable, add
				if(!contain){
					table.addNode(ip, port);
					table.printTableInformation();

				}
			}
		}
	}
	
	private synchronized void sendBroadcast(){
		int broadcastMessageID = broadcastMessage.getId();
		String startIp = broadcastMessage.getStartIp();
		int startPort = broadcastMessage.getStartPort();
		
		boolean resendMessage = false;
		//check if message was here already
		if(broadcastIds.containsKey(startIp + ":" + startPort)){
			if(!broadcastIds.get(startIp + ":" + startPort).contains(broadcastMessageID)){
				broadcastIds.get(startIp + ":" + startPort).add(broadcastMessageID);
				resendMessage = true;
			}
		}
		else{
			broadcastIds.put(startIp + ":" + startPort, new LinkedList<Integer>());
			broadcastIds.get(startIp + ":" + startPort).add(broadcastMessageID);
			resendMessage = true;
		}
		
		//if it is a new message, resend the message to the nodes in the node table
		if(resendMessage){		
			for(TableEntry entry : table.getOtherNodes()){
				//don't send to the node from which i got the message
				if(!(entry.getIp().equals(broadcastRecievedFromIP) && entry.getPort() == broadcastRecievedFromPort)){
					//don't send the message to the node, which created the message
					if(!(entry.getIp().equals(startIp) && entry.getPort() == startPort)){
						try{						
							Socket socket = new Socket(entry.getIp(), entry.getPort());
							try {
								ObjectOutputStream serverOos = new ObjectOutputStream(socket.getOutputStream());
								serverOos.writeObject(broadcastMessage);
							} catch (IOException e) {
								System.out.println("Error on sending request");
							}
						}catch(UnknownHostException uhe){
							System.out.println("IP could not be determined: " + uhe.getMessage());
							System.out.println("Delete " + entry.getIp() + ":" + entry.getPort() + " from table.");
							table.removeNode(entry.getIp(), entry.getPort());
						}catch(IOException ioe){
							System.out.println("Socket could not be connected!\n" +  "Delete " + entry.getIp() + ":" + entry.getPort() + " from table.");
							table.removeNode(entry.getIp(), entry.getPort());
						}
					}
				}
			}
		}
	}
	
	private synchronized void sendSearch(){
		int broadcastMessageID = searchMessage.getId();
		String startIp = searchMessage.getStartIp();
		int startPort = searchMessage.getStartPort();
		
		boolean resendMessage = false;
		if(broadcastIds.containsKey(startIp + ":" + startPort)){
			if(!broadcastIds.get(startIp + ":" + startPort).contains(broadcastMessageID)){
				broadcastIds.get(startIp + ":" + startPort).add(broadcastMessageID);
				resendMessage = true;
			}
		}
		else{
			broadcastIds.put(startIp + ":" + startPort, new LinkedList<Integer>());
			broadcastIds.get(startIp + ":" + startPort).add(broadcastMessageID);
			resendMessage = true;
		}
		if(resendMessage){
			//if this node has been searched -> answer with ip and port
			if(nameOfNode.equals(searchMessage.getName())){
				//answer
				System.out.println("answer");
				try{						
					Socket socket = new Socket(startIp, startPort);
					try {
						ObjectOutputStream serverOos = new ObjectOutputStream(socket.getOutputStream());
						String[] ownerInformation = table.getOwner();
						String ownerIp = ownerInformation[0];
						int ownerPort = Integer.parseInt(ownerInformation[1]);
						serverOos.writeObject(new AnswerMessage(nameOfNode, ownerIp, ownerPort));
					} catch (IOException e) {
						System.out.println("Error on sending request");
					}
				}catch(UnknownHostException uhe){
					System.out.println("IP could not be determined: " + uhe.getMessage());
				}catch(IOException ioe){
					System.out.println("Socket could not be connected!");
				}
			}
			
			//else resend the message to the neighbors
			else{
				for(TableEntry entry : table.getOtherNodes()){
					//don't send to the node from which i got the message
					if(!(entry.getIp().equals(broadcastRecievedFromIP) && entry.getPort() == broadcastRecievedFromPort)){
						//don't send the message to the node, which created the message
						if(!(entry.getIp().equals(startIp) && entry.getPort() == startPort)){
							try{						
								Socket socket = new Socket(entry.getIp(), entry.getPort());
								try {
									ObjectOutputStream serverOos = new ObjectOutputStream(socket.getOutputStream());
									serverOos.writeObject(searchMessage);
								} catch (IOException e) {
									System.out.println("Error on sending request");
								}
							}catch(UnknownHostException uhe){
								System.out.println("Delete " + entry.getIp() + ":" + entry.getPort() + " from table.");
								table.removeNode(entry.getIp(), entry.getPort());
							}catch(IOException ioe){
								System.out.println("Socket could not be connected!\n" +  "Delete " + entry.getIp() + ":" + entry.getPort() + " from table.");
								table.removeNode(entry.getIp(), entry.getPort());
								table.printTableInformation();
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	public void run() {
		if(tableRequest){
			fillTableWithNewInformation();
		}
		else if(broadcastRequest){
			sendBroadcast();
		}
		else if(searchRequest){
			sendSearch();
		}
	}
	
}
