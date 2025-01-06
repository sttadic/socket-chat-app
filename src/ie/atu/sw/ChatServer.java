package ie.atu.sw;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

import static java.lang.System.out;

public class ChatServer {
	private final static int PORT = 13;
	private final ClientManager clientManager;
	private final ExecutorService executor;

	public ChatServer() {
		this.clientManager = new ClientManager();
		this.executor = Executors.newVirtualThreadPerTaskExecutor();
	}

	public void startServer() {
		try (ServerSocket server = new ServerSocket(PORT)) {
			out.println("ChatServer started on port: " + PORT);

			while (true) {
				Socket clientConnection = server.accept();
				out.println("Client connected from host " + clientConnection.getInetAddress() + ", port: "
						+ clientConnection.getPort());

				// Handle client connection in a separate thread
				executor.execute(() -> handleClient(clientConnection));
			}

		} catch (BindException e) {
			out.println("Error! Port " + PORT + " is already in use.");
		} catch (IOException e) {
			out.println("Error starting the server: " + e.getMessage());
		} finally {
			shutdownExecutor();
		}
	}

	private void handleClient(Socket clientConnection) {
		try (var reader = new BufferedReader(new InputStreamReader(clientConnection.getInputStream()));
				var writer = new PrintWriter(clientConnection.getOutputStream(), true)) {

			// Read the client's name
			String clientName = reader.readLine();

			// Add client to the client manager
			var connection = new Connection(clientConnection, reader, writer, clientName);
			clientManager.addClient(connection);

			// Notify clients about new user
			clientManager.broadcastMsg("SERVER: " + clientName + " has joined the chat", null);

			// Listen for client messages
			String message;
			while ((message = reader.readLine()) != null) {
				// Remove client and bail out if message contains \q
				if (message.equals("\\q")) {
					clientManager.removeClient(connection);
					return;
				}
				clientManager.broadcastMsg(message, connection);
			}

		} catch (IOException e) {
			if (e instanceof SocketException) {
				System.out.println("Connection to client lost --> Hostname: '" + clientConnection.getInetAddress()
						+ "', Port: " + clientConnection.getPort());
			} else {
				System.out.println("Somethig went wrong: " + e.getMessage());
			}
		}
	}

	private void shutdownExecutor() {
		executor.shutdown();
		try {
			if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
				executor.shutdownNow();
			}
		} catch (InterruptedException e) {
			executor.shutdownNow();
		}
	}

	public static void main(String[] args) {
		new ChatServer().startServer();
	}

}
