package ie.atu.sw;

import java.util.*;
import java.io.*;

public class ClientManager {
	private ArrayList<Connection> clients = new ArrayList<>();
	private Connection server;

	public void addClient(Connection connection) {
		this.clients.add(connection);
		this.server = new Connection(null, null, null, "Server");
	}

	public void runChat() {
		for (Connection client : clients) {
			String msgFromClient;
			var reader = client.reader();
			
			try {
				while ((msgFromClient = reader.readLine()) != null) {
					// Client disconnects / leaves the chat
					if (msgFromClient.equals("\\q")) removeClient(client);
					// Broadcast message
					broadcastMsg(msgFromClient, client);
				}
			} catch (IOException e) {
				closeResources(client);
				break;
			}

		}
	}

	private void broadcastMsg(String message, Connection client) {
		client.writer().println(client.name().toUpperCase() + ": " + message);
	}
	
	// Remove client from list of clients and broadcast to all
	private void removeClient(Connection client) {
		broadcastMsg("SERVER: Client " + client.name() + " has left the chat!", server);
		clients.remove(client);
	}

	// Close all resources for particular client's connection
	private void closeResources(Connection client) {
		try {
			client.connection().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			client.reader().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		client.writer().close();
	}
}
