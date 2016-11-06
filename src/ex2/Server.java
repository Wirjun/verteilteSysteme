package ex2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import static ex2.Protocol.reply;


public class Server {

    public static void main(String[] args){
        int port = Protocol.portNr;
        String username = "admin";
        boolean on = true;

        try {
            ServerSocket serverSocket = new ServerSocket(port);

            while(on){
                System.out.println("Waiting for a Client...");

                //waits for a client
                Socket clientSocket = serverSocket.accept();

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String inputLine = in.readLine();

                //split recieved message in usable parts (parts[0] = username)
                String[] parts = inputLine.split(",");

                //only calculate if username is correct
                if(parts[0].equals(username)){
                    int operation = Integer.parseInt(parts[1]);
                    int operator1 = Integer.parseInt(parts[2]);
                    int operator2 = Integer.parseInt(parts[3]);

                    System.out.println("Operation: " + operation);
                    System.out.println("Operator 1: " + operator1);
                    System.out.println("Operator 2: " + operator2);

                    int result = 0;
                    switch(operation){
                        case 1: result = add(operator1, operator2);
                            break;
                        case 2: result = sub(operator1, operator2);
                            break;
                        case 3: result = mul(operator1, operator2);
                            break;
                        case 4: result = fac(operator1);
                            break;
                        case 0: on = false;
                            break;
                    }

                    reply(clientSocket, result);
                }

                else{
                    reply(clientSocket, "Wrong Username!");
                }

            }

            serverSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int add(int a, int b){
        int result = a + b;
        return result;
    }

    private static int sub(int a, int b){
        int result = a - b;
        return result;
    }

    private static int mul(int a, int b){
        int result = a * b;
        return result;
    }

    private static int fac(int a){
        int result = 1;
        for(int i = 1; i <= a; i++){
            result *= i;
        }
        return result;
    }
}
