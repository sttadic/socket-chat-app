package ie.atu.sw;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

import static java.lang.System.out;

/**
 * The class {@code ChatServer} represents a server that listens for client
 * connections, manages chat rooms, and broadcasts messages to connected clients.
 */
public class ChatServer {
	public final static int PORT = 13;
	private final ChatRoomManager chatRoomManager;
	private final ExecutorService executor;

	/**
	 * Constructs a ChatServer and initializes chat room manager and thread
	 * executor.
	 */
	public ChatServer() {
		this.chatRoomManager = new ChatRoomManager();
		this.executor = Executors.newVirtualThreadPerTaskExecutor();
	}

	/**
	 * Starts the chat server and listens for incoming client connections.
	 * Each client connection is handled in a separate virtual thread.
	 */
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
				// Handle situations based on wheather connection is lost before or after entering the client name
				if (chatRoomMember != null) chatRoomManager.leaveRoom(chatRoomMember);
			} else {
				System.out.println("Somethig went wrong: " + e.getMessage());
			}
		}
	}

	// Shut down the thread executor gracefully
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

	/**
	 * Starts the ChatServer
	 * 
	 * @param args command line arguments (not used)
	 */
	public static void main(String[] args) {
		new ChatServer().startServer();
	}

}
