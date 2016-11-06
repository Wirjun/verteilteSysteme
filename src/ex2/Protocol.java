package ex2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Protocol {

    public static int portNr = 5555;

    public static String request(String username, Socket socketClient, int operation, int operator1, int operator2) throws IOException {
        //create message which is sent to the server
        StringBuilder protocol = new StringBuilder();
        protocol.append(username).append(',').append(operation).append(',').append(operator1). append(',').append(operator2);

        //write message in the outputstream
        PrintWriter out = new PrintWriter(socketClient.getOutputStream(), true);
        out.println(protocol);

        //wait for the answer of the server
        BufferedReader in = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
        String result = in.readLine();

        //close streams
        out.close();
        in.close();

        return result;
    }

    public static void reply(Socket clientSocket, int result) throws IOException {
        //send result to the client
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        out.println(result);
        out.close();
    }

    public static void reply(Socket clientSocket, String error) throws IOException {
        //send error message to the client
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        out.println(error);
        out.close();
    }
}
