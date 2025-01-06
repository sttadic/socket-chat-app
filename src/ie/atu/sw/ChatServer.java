package ie.atu.sw;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

import static java.lang.System.out;

public class ChatServer {
	private final static int PORT = 13;
	private final ChatRoomManager chatRoomManager;
	private final ExecutorService executor;

	public ChatServer() {
		this.chatRoomManager = new ChatRoomManager();
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

	@SuppressWarnings("unused")
	private void handleClient(Socket chatMemberConnecton) {
		// Initialize client (chatRoomMember)
		ChatRoomMember chatRoomMember = null;
		try (var reader = new BufferedReader(new InputStreamReader(chatMemberConnecton.getInputStream()));
			 var writer = new PrintWriter(chatMemberConnecton.getOutputStream(), true)) {

			// Read the client's name
			String memberName = reader.readLine();

			// Create a member and add member to the chat room manager
			chatRoomMember = new ChatRoomMember(chatMemberConnecton, reader, writer, memberName);
			chatRoomManager.joinRoom(chatRoomMember);

			// Notify other members about new user
			chatRoomManager.broadcastToRoom("SERVER: " + memberName + " has joined the chat", null);

			// Listen for members (clients) messages
			String message;
			while ((message = reader.readLine()) != null) {
				// Remove member and bail out if message contains \q
				if (message.equals("\\q")) {
					chatRoomManager.leaveRoom(chatRoomMember);
					return;
				}
				chatRoomManager.broadcastToRoom(message, chatRoomMember);
			}

		} catch (IOException e) {
			// In case connection to the client is lost for other reasons (not client entering \q)
			if (e instanceof SocketException) {
				System.out.println("Connection to client lost --> Hostname: '" + chatMemberConnecton.getInetAddress()
						+ "', Port: " + chatMemberConnecton.getPort());
				// Handle situations depending if connection is lost prior to or after entering the client name
				if (chatRoomMember != null) chatRoomManager.leaveRoom(chatRoomMember);
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
