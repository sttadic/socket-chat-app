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
	
	public void startClient() {
		
		try (Socket socket = new Socket(hostname, port);
			// Setup input and output streams
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter output = new PrintWriter(socket.getOutputStream(), true)) {
			
			out.println("Connected to server on host: " + hostname);
			// Read welcome message from the server
			String receivedMsg = input.readLine();
			out.println("Server: " + receivedMsg);
			
			// Send messages to the server
			String sentMsg;
			while (true) {
				out.print("Message: ");
				sentMsg = scan.nextLine();
				output.println(sentMsg);
			
			// Read and print the server's response
			receivedMsg = input.readLine();
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
		new ChatClient().startClient();
	}
}
