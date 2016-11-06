package ex3;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException {
        int port = Protocol.portNr;
        Socket clientSocket = new Socket("localhost", port);

        Scanner in = new Scanner(System.in);
        String username;
        String result;
        boolean loggedIn = false;
        boolean end = false;

        while(!loggedIn){
            System.out.println("Enter your Username: (0 to close Connection)");
            username = in.nextLine();

            //close Connection
            if(username.equals("0")){
                String close = createMessage("0");
                Protocol.request(clientSocket, close);
                end = true;
                break;
            }

            //Try to login
            String login = createMessage("1", username);
            result = Protocol.request(clientSocket, login);

            if(result.equals("Accepted")){
                loggedIn = true;
                System.out.println(result);
            }
            else{
                System.out.println(result);
            }
        }


        while(!end){
            String message = enterValues();

            //close Connection or shutdown Server
            if(message.equals("0") || message.equals("5")){
                Protocol.request(clientSocket, message);
                break;
            }

            result = Protocol.request(clientSocket, message);
            System.out.println("Result: " + result);

            if(end = repeat()){
                Protocol.request(clientSocket, "0");
            }
        }

        System.out.println("End");
        clientSocket.close();
    }


    private static String enterValues(){
        Scanner in = new Scanner(System.in);
        int operation;
        int operator1;
        int operator2;
        String message;

        System.out.println("What do you want to do?");
        System.out.println("1: Addition");
        System.out.println("2: Subtraction");
        System.out.println("3: Multiplication");
        System.out.println("4: Factorial");
        System.out.println("5: Shutdown Server");
        System.out.println("0: Close Connection");


        operation = in.nextInt();

        //Check if input is valid
        while(operation > 5 || operation < 0){
            System.out.println("Invalid Operation");
            System.out.println("Choose again:");
            operation = in.nextInt();
        }

        if(operation == 0){
            return "0";
        }

        if(operation == 5){
            return "5";
        }

        System.out.println("Enter first Operator:");
        operator1 = in.nextInt();

        if(operation != 4){
            System.out.println("Enter second Operator:");
            operator2 = in.nextInt();
        }
        else{
            operator2 = 0;
        }

        message = createMessage("2", String.valueOf(operation), String.valueOf(operator1), String.valueOf(operator2));
        return message;
    }

    private static boolean repeat(){
        boolean noLoop = false;
        Scanner in = new Scanner(System.in);

        System.out.println("Again? 1-yes, 2-no");
        if(in.nextInt() != 1)
            noLoop = true;

        return noLoop;
    }

    private static String createMessage(Object... parameters){
        //create message which is sent to the server
        StringBuilder message = new StringBuilder();
        for(Object s: parameters){
            message.append(s).append(',');
        }
        return message.toString();
    }
}
