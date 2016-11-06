package ex3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Protocol {

    public static int portNr = 5555;

    public static String request(Socket socketClient, String message) throws IOException {
        //write message in the outputstream
        PrintWriter out = new PrintWriter(socketClient.getOutputStream(), true);
        out.println(message);

        //wait for the answer of the server
        BufferedReader in = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
        String result = in.readLine();

        return result;
    }

    public static void reply(Socket clientSocket, int result) throws IOException {
        //send result to the client
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        out.println(result);
    }

    public static void reply(Socket clientSocket, String error) throws IOException {
        //send error message to the client
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        out.println(error);
    }

    public static void shutdown(){

    }
}
