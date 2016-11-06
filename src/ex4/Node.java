package ex4;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class Node extends Thread{
	private String ip;
	private int port;
	private String name;
	private volatile NodeTable table;
	private HashMap<String, LinkedList<Integer>> broadcastIDs;
	private TableSender tableSender;
	private volatile int idCounter = 0;
	private volatile boolean stop = false;
	private ServerSocket server;
	private Socket client;
	private Socket socket;
	private ObjectInputStream ois;
	
	//ip and port of the node, which got the table from this node and will answer
	private volatile String expectTableAnswerIp = "";
	private volatile int expectTableAnswerPort = 0;
	
	private ExecutorService  threadPoolExecutor;
	private LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
	
	public Node(String _ip, int _port, String otherIp, int otherPort){
		ip = _ip;
		port = _port;
		name = "node" + port;
		broadcastIDs = new HashMap<String, LinkedList<Integer>>();
		table = new NodeTable(ip, port, 3);
		if(!otherIp.equals("") && otherPort != 0){
			table.addNode(otherIp, otherPort);
		}
		table.printTableInformation();
	}	
	
	public void sendNewBroadcastMessage(String message){
		//if table is empty don't send message anyone
		if(!table.isEmpty()){
			BroadcastMessage broadcastMessage = new BroadcastMessage(++idCounter, message, ip, port);
			
			//add the message ID to the list
			if(broadcastIDs.containsKey(ip + ":" + port)){
				broadcastIDs.get(ip + ":" + port).add(idCounter);
			}
			else{				
				broadcastIDs.put(ip + ":" + port, new LinkedList<Integer>());
				broadcastIDs.get(ip + ":" + port).add(idCounter);
			}
			
			System.out.println("Node " + name + " sends now a broadcast message");
			
			//try to send the message to every node in the node table
			for(TableEntry entry : table.getOtherNodes()){
				try{						
					socket = new Socket(entry.getIp(), entry.getPort());
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
					System.out.println("Error on connecting: " + ioe.getMessage());
					System.out.println("Delete " + entry.getIp() + ":" + entry.getPort() + " from table.");
					table.removeNode(entry.getIp(), entry.getPort());
				}
			}
		}
	}
	
	public void lookForName(String searchName){
		//if table is empty don't send message anyone
		if(!table.isEmpty()){
			SearchMessage searchMessage = new SearchMessage(++idCounter, name, ip, port);
			
			//add the message ID to the list
			if(broadcastIDs.containsKey(ip + ":" + port)){
				broadcastIDs.get(ip + ":" + port).add(idCounter);
			}
			else{				
				broadcastIDs.put(ip + ":" + port, new LinkedList<Integer>());
				broadcastIDs.get(ip + ":" + port).add(idCounter);
			}
			System.out.println("Node " + name + " is now searching for node " + searchName);
			//try to send the message to every node in the node table
			for(TableEntry entry : table.getOtherNodes()){
				try{						
					socket = new Socket(entry.getIp(), entry.getPort());
					try {
						ObjectOutputStream serverOos = new ObjectOutputStream(socket.getOutputStream());
						serverOos.writeObject(searchMessage);
					} catch (IOException e) {
						System.out.println("Error on sending request");
					}
				}catch(UnknownHostException uhe){
					System.out.println("IP could not be determined: " + uhe.getMessage());
					System.out.println("Delete " + entry.getIp() + ":" + entry.getPort() + " from table.");
					table.removeNode(entry.getIp(), entry.getPort());
				}catch(IOException ioe){
					System.out.println("Error on creating the socket: " + ioe.getMessage());
					System.out.println("Delete " + entry.getIp() + ":" + entry.getPort() + " from table.");
					table.removeNode(entry.getIp(), entry.getPort());
				}
			}
		}
	}
	
	public void shutdown(){
		System.out.println("Node " + name + " with ip " + ip + ":" + port + " will now shut down");
		try{
			threadPoolExecutor.shutdown();
			while (!threadPoolExecutor.awaitTermination(3, TimeUnit.SECONDS)) {
				  System.out.println("Waiting for shutdown!");
			}
		}catch(InterruptedException ie){
			System.out.println("Shutdown was interrupted");
		}
		tableSender.stopSender();
		try {
			if(ois != null){
				ois.close();
			}
			if(socket != null){				
				socket.close();
			}
			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IO Error on closing server socket: " + e.getMessage());
		}
		stop = true;
	}
	
	public HashMap<String, LinkedList<Integer>> getBroadcastIDs(){
		return broadcastIDs;
	}
	
	public void setExpectTableAnswerFromIP(String _ip){
		expectTableAnswerIp = _ip;
	}
	
	public void setExpectTableAnswerFromPort(int _port){
		expectTableAnswerPort = _port;
	}
	
	public int getPort(){
		return port;
	}
	
	public void run(){
		threadPoolExecutor = Executors.newFixedThreadPool(5);
		try {
			//create a table sender, who sends the node table every 5 seconds to a random node in the node table
			tableSender = new TableSender(this, table);
			server = new ServerSocket(port);
						
		} catch (IOException e) {
			System.out.println("IO Error: " + e.getMessage());
		} catch (Exception ex){
			System.out.println("Error: " + ex.getMessage());
			ex.printStackTrace();
		}
		
		while(!stop){
			try{
				client = server.accept();
				ois = new ObjectInputStream(client.getInputStream());
				Object stream = ois.readObject();

				//check, which type of message was sent and handle it individually
				if(stream.getClass().equals(LinkedList.class)){
					LinkedList<TableEntry> tableFromOtherNode = (LinkedList<TableEntry>) stream;
					if((expectTableAnswerIp.equals(tableFromOtherNode.getLast().getIp())) && (expectTableAnswerPort == tableFromOtherNode.getLast().getPort())){
						threadPoolExecutor.execute(new RequestHandler(table, tableFromOtherNode, false));
					}
					else{
						threadPoolExecutor.execute(new RequestHandler(table, tableFromOtherNode, true));
					}
				}
				else if(stream.getClass().equals(BroadcastMessage.class)){
					BroadcastMessage message = (BroadcastMessage) stream;
					threadPoolExecutor.execute(new RequestHandler(table, broadcastIDs, message, client));
				}
				else if(stream.getClass().equals(SearchMessage.class)){
					SearchMessage message = (SearchMessage) stream;
					threadPoolExecutor.execute(new RequestHandler(table, name, broadcastIDs, message, client));
				}
				else if(stream.getClass().equals(AnswerMessage.class)){
					AnswerMessage message = (AnswerMessage) stream;
					System.out.println("The ip and port of the searched node " + message.getName() + " are: " + message.getIp() + ":" + message.getPort());
				}
			}catch(IOException ioe){}
			catch(ClassNotFoundException cnfe){
				System.out.println("Class Error: " + cnfe.getMessage());
			}
		}
	}

}
