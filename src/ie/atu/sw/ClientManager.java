package ie.atu.sw;

import java.net.*;
import java.util.*;
import java.io.*;

public class ClientManager {
	private ArrayList<Connection> clients = new ArrayList<>();

	public void addClient(Connection connection) {
		this.clients.add(connection);
	}

	public void runChat() {
		for (Connection client : clients) {
			String msgFromClient;
			var connection = client.connection();
			var reader = client.reader();
			var writer = client.writer();
			var clientName = client.name();

			try {
				while ((msgFromClient = reader.readLine()) != null) {
					broadcastMsg(msgFromClient, clientName, writer);
				}
			} catch (IOException e) {
				closeResources(connection, reader, writer);
			}

		}
	}

	private void broadcastMsg(String message, String clientName, PrintWriter writer) {
		writer.println(clientName.toUpperCase() + ": " + message);
	}

	// Close all resources for particular connection
	private void closeResources(Socket connection, BufferedReader reader, PrintWriter writer) {
		try {
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		writer.close();
	}
}
