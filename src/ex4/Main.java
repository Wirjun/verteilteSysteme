package ex4;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;


public class Main {
	public static void main(String args[]){
		String ip = "";
		int port = 8001;
		Random generator = new Random();
		LinkedList<Node> nodes = new LinkedList<Node>();
		int randomMessageTimer = generator.nextInt(10000) + 10000;
		long nextMessageSend = new Date().getTime() + randomMessageTimer;
		
		int randomRemoveCreateNodeTimer = generator.nextInt(20000) + 10000;
		long nextRemoveCreate = new Date().getTime() + randomRemoveCreateNodeTimer;
		
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		//create and start nodes
		nodes.add(new Node(ip, ++port, "", 0));
		for(int i = 0; i<9; i++){
			nodes.add(new Node(ip, ++port, ip, port-1));
		}
		for(int i = 0; i< nodes.size(); i++){
			nodes.get(i).start();
		}

		while(true){
			if(nextMessageSend < new Date().getTime()){
				int nextBroadcastMessageSender = generator.nextInt(nodes.size());
				int nextSearchMessageSender = generator.nextInt(nodes.size());
				int lookForNode = port - generator.nextInt(nodes.size());
				nodes.get(nextBroadcastMessageSender).sendNewBroadcastMessage("" + new Date().getTime());
				nodes.get(nextSearchMessageSender).lookForName("node" + lookForNode);
				randomMessageTimer = generator.nextInt(10000) + 10000;
				nextMessageSend = new Date().getTime() + randomMessageTimer;

			}
			if(nextRemoveCreate < new Date().getTime()){
				int nextRemoved = generator.nextInt(nodes.size());
				Node removedNode = nodes.get(nextRemoved);
				nodes.remove(nextRemoved);
				removedNode.shutdown();
				int connectWithPort = generator.nextInt(nodes.size());
				nodes.add(new Node(ip, ++port, ip, nodes.get(connectWithPort).getPort()));
				System.out.println("New node created on " + ip + ":" + port);
				randomRemoveCreateNodeTimer = generator.nextInt(10000) + 10000;
				nextRemoveCreate = new Date().getTime() + randomRemoveCreateNodeTimer;

			}
		}
	}
}
