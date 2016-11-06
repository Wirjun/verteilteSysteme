package ex3;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {

    private static boolean run = true;
    private static ServerSocket serverSocket;
    private static ExecutorService pool;

    public static void main(String[] args) throws InterruptedException {
        int port = Protocol.portNr;

        int poolSize = 100;

        try {
            serverSocket = new ServerSocket(port);
            pool = Executors.newFixedThreadPool(poolSize);

            while(run) {
                System.out.println("Waiting for a Client...");
                pool.execute(new ServerHandler(serverSocket.accept()));
            }

        } catch (SocketException e){
        } catch (IOException e) {
            e.printStackTrace();
        }

        //wait for all Threads to end
        pool.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);

        System.out.println("Shut down!");
    }

    public static void shutdown(){
        System.out.println("Shutting down...");
        run = false;
        //no more new Threads possible
        pool.shutdown();

        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
