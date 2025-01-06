package ie.atu.sw;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.io.*;

public class ClientManager {
	private final List<Connection> clients = new CopyOnWriteArrayList<>();

	public void addClient(Connection connection) {
		clients.add(connection);
	}
	
	// Remove client from list of clients and broadcast to all
	public void removeClient(Connection client) {
		// Notify all clients about disconnected user
		broadcastMsg("SERVER: " + client.name() + " has left the chat!", null);
		// Close client's resources and remove from list
		closeResources(client);
		clients.remove(client);
	}

	public void broadcastMsg(String message, Connection sender) {
		for (Connection client : clients) {
			// Don't broadcast to sender
			if (sender != null && client.equals(sender)) continue;
			
			try {
				client.writer().println(sender == null ? message : sender.name().toUpperCase() + ": " + message);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
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
