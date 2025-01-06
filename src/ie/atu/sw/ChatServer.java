package ie.atu.sw;

import java.io.*;
import java.net.*;
import java.util.concurrent.Executors;

import static java.lang.System.out;

public class ChatServer {
	private final static int PORT = 13;
	private final ClientManager clientManager;

	public ChatServer() {
		this.clientManager = new ClientManager();
	}

	public void startServer() {
		try (ServerSocket server = new ServerSocket(PORT)) {
			out.println("ChatServer started on port: " + PORT);

			try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
				while (true) {
					Socket clientConnection = server.accept();
					out.println("Client connected from host " + clientConnection.getInetAddress() + ", port: "
							+ clientConnection.getPort());

					// Handle client connection in a new thread
					executor.execute(() -> handleClient(clientConnection));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handleClient(Socket clientConnection) {
		try (var reader = new BufferedReader(new InputStreamReader(clientConnection.getInputStream()));
				var writer = new PrintWriter(clientConnection.getOutputStream(), true)) {
			String clientName = reader.readLine();
			
			// Add client to the client manager
			var connection = new Connection(clientConnection, reader, writer, clientName);
			clientManager.addClient(connection);
			
			// Notify clients about new user
			clientManager.broadcastMsg("SERVER: " + clientName + " has joined the chat", null);
			
			// Listen for client messages
			String message;
			while ((message = reader.readLine()) != null) {
				if (message.equals("\\q")) {
					clientManager.removeClient(connection);
					break;
				}
				clientManager.broadcastMsg(message, connection);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		new ChatServer().startServer();
	}

}
