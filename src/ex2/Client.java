package ex2;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class Client {

	public static void main(String[] args) throws IOException {
		int port = Protocol.portNr;

		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
		int operation;
		int operator1;
		int operator2;
		int decision;
		String username;

		System.out.println("Enter your Username:");
		username = in.nextLine();

		String serverAddress = "localhost";

		System.out.println("What do you want to do?");
		System.out.println("1: Addition");
		System.out.println("2: Subtraction");
		System.out.println("3: Multiplication");
		System.out.println("4: Factorial");
		System.out.println("0: Close Server");

		operation = in.nextInt();

		// Check if input is valid
		while (operation > 4 || operation < 0) {
			System.out.println("Invalid Operation");
			System.out.println("Choose again:");
			operation = in.nextInt();
		}

		System.out.println("Enter first Operator:");
		operator1 = in.nextInt();

		if (operation != 4) {
			System.out.println("Enter second Operator:");
			operator2 = in.nextInt();
		} else {
			operator2 = 0;
		}

		String result = connect(username, serverAddress, port, operation,
				operator1, operator2);
		System.out.println(result);

		System.out.println("Terminate? Press '0' ");
		decision = in.nextInt();

	}

	private static String connect(String username, String ServerAddress,
			int port, int operation, int operator1, int operator2)
			throws IOException {
		Socket clientSocket = null;

		try {
			clientSocket = new Socket(ServerAddress, port);
		} catch (ConnectException e) {
			System.out.println("No Server Found");
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Connection established");

		String result = Protocol.request(username, clientSocket, operation,
				operator1, operator2);

		clientSocket.close();
		return result;
	}
}
