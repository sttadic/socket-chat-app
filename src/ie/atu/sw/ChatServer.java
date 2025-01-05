package ie.atu.sw;

import java.io.*;
import java.net.*;

import static java.lang.System.out;

public class ChatServer {
	private final static int PORT = 13;
	
	public void startServer() {
		try (ServerSocket server = new ServerSocket(PORT)) {
			 
			while (true) {
				out.println("ChatServer listening on port " + PORT);
				
				try (Socket connection = server.accept();
					// Setup input and output stream
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					PrintWriter writer = new PrintWriter(connection.getOutputStream(), true)) { // autoFlush 
					
					out.println("Client connected from host " + connection.getInetAddress() + ", port: " + connection.getPort());
					// Send welcome message to the client
					writer.println("Hello from the ChatServer!");
					
					// Continously read messages from the client
					String receivedMsg;
					while ((receivedMsg = reader.readLine()) != null) {
						out.println("Received: " + receivedMsg);
						// Optionally send response back
						writer.println("Message received: " + receivedMsg);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new ChatServer().startServer();
	}
}
