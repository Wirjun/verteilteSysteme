package ex3;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

import static ex3.Protocol.reply;

public class ServerHandler implements Runnable{

    private final Socket clientSocket;
    private final ArrayList<String> userList = new ArrayList<>();

    ServerHandler(Socket socket){
        this.clientSocket = socket;
        userList.add("admin");
        userList.add("chef");
        userList.add("boss");
    }

    @Override
    public void run() {
        BufferedReader in;
        String inputLine;
        boolean loggedIn = false;
        String[] parts;
        boolean end = false;

        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            while(!loggedIn){
                inputLine = in.readLine();
                parts = inputLine.split(",");

                //close connection
                if(parts[0].equals("0")){
                    end = true;
                    break;
                }

                //try to login
                else if(parts[0].equals("1")){
                    if(login(userList, parts[1])){
                        reply(clientSocket, "Accepted");
                        loggedIn = true;
                    }
                    else{
                        reply(clientSocket, "Wrong Username");
                    }
                }
            }

            while(!end){
                inputLine = in.readLine();
                parts = inputLine.split(",");

                //close connection
                if(parts[0].equals("0")){
                    break;
                }

                //shutdown server
                if(parts[0].equals("5")){
                    Server.shutdown();
                    break;
                }

                //calculate
                if(parts[0].equals("2")){
                    reply(clientSocket, calc(parts));
                }
            }

            clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Thread End");
    }

    private static boolean login(ArrayList<String> userList, String username){
        for(String u:userList){
            if (username.equals(u)){
                return true;
            }
        }
        return false;
    }

    private static int calc(String[] parts){
        int operation = Integer.parseInt(parts[1]);
        int operator1 = Integer.parseInt(parts[2]);
        int operator2 = Integer.parseInt(parts[3]);

        int result = 0;
        switch(operation){
            case 1: result = operator1 + operator2;
                break;
            case 2: result = operator1 - operator2;
                break;
            case 3: result = operator1 * operator2;
                break;
            case 4: result = fac(operator1);
                break;
        }
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
