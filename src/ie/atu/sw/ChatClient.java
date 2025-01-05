package ie.atu.sw;

import java.io.*;
import java.net.*;
import java.util.*;

import static java.lang.System.out;

public class ChatClient {
	private final String userName;
	private int port = 13;
	private String hostname = "localhost";
	private final Scanner scan;
	
	public ChatClient() {
		this.scan = new Scanner(System.in);
		this.userName = setName();
	}
	
	private String setName() {
		String uName = "";
		while (true) {
			out.println("Please enter your name: ");
			uName = scan.nextLine();
			if (uName.isEmpty()) {
				out.println("Name cannot be empty! Please try again");
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
			
			out.println("Connected to server on host: " + hostname);
			// Read welcome message from the server
			String receivedMsg = reader.readLine();
			out.println("Server: " + receivedMsg);
			
			// Send messages to the server
			String sentMsg;
			while (true) {
				out.print("Message: ");
				sentMsg = scan.nextLine();
				writer.println(sentMsg);
			
			// Read and print the server's response
			receivedMsg = reader.readLine();
			out.println("Server: " + receivedMsg);
			
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			scan.close();
		}
	}
	
	
	public static void main(String[] args) {
		new ChatClient();
	}
}
