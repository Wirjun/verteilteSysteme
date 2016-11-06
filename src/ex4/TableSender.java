package ex4;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class TableSender extends Thread{
	
	private Node node;
	private NodeTable table;
	private volatile boolean stop = false;
	
	
	public TableSender(Node _node, NodeTable _table){
		node = _node;
		table = _table;
		this.start();
	}
	
	private void sendTableToNode(){
		String randomNode[] = table.getRandomNode();
		String randomIp = randomNode[0];
		int randomPort = Integer.parseInt(randomNode[1]);
		try{						
			Socket socket = new Socket(randomIp, randomPort);
			try {
				ObjectOutputStream serverOos = new ObjectOutputStream(socket.getOutputStream());
				serverOos.writeObject(table.getTableWithOwner());
				node.setExpectTableAnswerFromIP(randomIp);
				node.setExpectTableAnswerFromPort(randomPort);
			} catch (IOException e) {
				System.out.println("Error on sending request");
			}
		}catch(UnknownHostException uhe){
			System.out.println("Socket could not be connected!\n" +  "Delete " + randomIp + ":" + randomPort + " from table.");
			table.removeNode(randomIp, randomPort);
		}catch(IOException ioe){
			System.out.println("Socket could not be connected!\n" +  "Delete " + randomIp + ":" + randomPort + " from table.");
			table.removeNode(randomIp, randomPort);
			table.printTableInformation();
		}
	}
	
	@Override
	public void run() {
		while(!stop){
			try{
				sleep(5000);
				if(!stop && !table.isEmpty()){
					sendTableToNode();
				}
			}catch(InterruptedException ie){
				System.out.println("Sender was interrupted: " + ie.getMessage());
			}
		}
		
	}
	
	public void stopSender(){
		stop = true;
	}

}
