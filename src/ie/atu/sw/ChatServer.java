package ie.atu.sw;

import java.io.*;
import java.net.*;
import java.util.List;

import static java.lang.System.out;

public class ChatServer {
	private final static int PORT = 13;
	private final ClientManager clientManager;
	
	public ChatServer() {
		this.clientManager = new ClientManager();
	}
	
	public void startServer() {
		try (ServerSocket server = new ServerSocket(PORT)) {
			 
			while (true) {
				out.println("ChatServer listening on port " + PORT);
				
				try (Socket clientConnection = server.accept();
					// Setup input and output stream
					var reader = new BufferedReader(new InputStreamReader(clientConnection.getInputStream()));
					var writer = new PrintWriter(clientConnection.getOutputStream(), true)) {
					
					// Acknowledge client connection
					out.println("Client connected from host " + clientConnection.getInetAddress() + ", port: " + clientConnection.getPort());
					// Send welcome message to the client
					writer.println("Hello from the ChatServer!");
					// Get the name
					String clientName = reader.readLine();
					// Add connection to client manager
					clientManager.addClient(new Connection(clientConnection, reader, writer, clientName));
					
					

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
