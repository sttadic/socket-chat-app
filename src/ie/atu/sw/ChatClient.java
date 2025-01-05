package ie.atu.sw;

import java.io.*;
import java.net.*;
import java.util.*;

import static java.lang.System.out;

public class ChatClient {
	private int port = 13;
	private String hostname = "localhost";
	private final Scanner scan;

	public ChatClient() {
		this.scan = new Scanner(System.in);
	}

	private String setName() {
		String uName = "";
		while (true) {
			out.print("Please enter your name: ");
			uName = scan.nextLine();
			if (uName.isEmpty()) {
				out.println("Name field cannot be empty!");
				continue;
			}
			break;
		}
		return uName;
	}

	public void startClient() {
		try (Socket socket = new Socket(hostname, port);
			// Setup input and output streams
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

			// Read welcome message from the server
			String receivedMsg = reader.readLine();
			out.println("Server: " + receivedMsg);
			
			// Send the first message to set the name
			writer.println(setName());
			// Send messages to the server
			String sentMsg;
			while (true) {
				out.print("Message: ");
				sentMsg = scan.nextLine();
				if (sentMsg.trim().equals("\\q")) {
					socket.close();
					out.println("Goodbye...");
					return;
				}
				writer.println(sentMsg);

				// Read and print the server's response
				receivedMsg = reader.readLine();
				out.println("Server: " + receivedMsg);

			}

		} catch (Exception e) {
			exceptionHandler(e);
		} finally {
			scan.close();
		}
	}

	private void exceptionHandler(Exception e) {
		if (e instanceof ConnectException) {
			out.println("\r\nNo ChatServer on host '" + hostname + "', port " + port + ". Please try again later.\r\n");
		} else if (e instanceof SocketException) {
			out.println("\r\nChatServer encountered an error. Chat session terminated!\r\n");
		} else {
			out.println("Something went wrong. Chat session terminated.\n\r");
		}
	}

	public static void main(String[] args) {
		new ChatClient().startClient();
	}
}
