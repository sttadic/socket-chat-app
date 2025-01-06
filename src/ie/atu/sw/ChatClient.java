package ie.atu.sw;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.System.out;

/**
 * The {@code ChatClient} class represents a client for a chat application.
 * Handles connecting to a chat server, sending messages, and receiving messages 
 * from the server.
 */
public class ChatClient {
	private final Scanner scan;
	private final Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	private final AtomicBoolean running;

	/**
	 * Constructs a ChatClient and sets up input and output streams.
	 * 
	 * @param socket the socket connected to the chat server
	 */
	public ChatClient(Socket socket) {
		this.scan = new Scanner(System.in);
		this.socket = socket;
		// Atomic flag shared with all threads
		this.running = new AtomicBoolean(true);
		try {
			this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.writer = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			exceptionHandler(e);
		}
	}

	/**
	 * Sends messages to the chat server. The first message sets the username, 
	 * and subsequent messages are broadcast to the chat room.
	 * Typing '\q' exits the chat.
	 */
	private void sendMessage() {
		// First message assigns username
		writer.println(setName());
		// Chat messages
		while (running.get()) {
			String outMessage = scan.nextLine();
			// Exit chat
			if (outMessage.trim().equals("\\q")) {
				writer.println(outMessage);
				running.set(false);
				closeResources();
				out.println("\n\rGoodbye...\n\r");
				return;
			}
			writer.println(outMessage);
		}
	}

	/**
	 * Receives messages from the chat server and displays them in the console.
	 * Continues running until the client exits or the server disconnects.
	 */
	private void receiveMessage() {
		try {
			while (running.get()) {
				String inMessage = reader.readLine();
				if (inMessage == null) {
					running.set(false); // Connection is closed
					break;
				}
				out.println(inMessage);
			}
		} catch (Exception e) {
			if (running.get()) exceptionHandler(e);
		}
	}

	private String setName() {
		String userName;
		while (true) {
			out.print("Please enter your name: ");
			userName = scan.nextLine();
			if (userName.isEmpty()) {
				out.println("Name field cannot be empty!");
				continue;
			}
			break;
		}
		return userName;
	}

	private void exceptionHandler(Exception e) {
		System.out.println(e.getClass());
		closeResources();
		if (e instanceof ConnectException) {
			out.println("\r\nNo ChatServer is found. Please try again later.\r\n");
		} else if (e instanceof SocketException) {
			out.println("\r\nChat session terminated due to an error!\r\n");
		} else {
			out.println("Something went wrong. Chat session terminated.\n\r");
		}
	}

	// Synchronized access to close resources method
	private synchronized void closeResources() {
		try {
			if (reader != null) reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			if (socket != null) socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (writer != null) writer.close();
		if (scan != null) scan.close();
	}

	/**
	 * The main method for starting the {@code ChatClient}. Reads the hostname
	 * and port from command-line arguments, or uses default values.
	 * 
	 * @param args optional arguments for hostname and port
	 * @throws ConnectException if specified port is unavailable
	 * @throws UnknownHostException if the specified host is unknown
	 * @throws NoSuchElementException if session is abrubted forcefully (ctrl+c)
	 */
	public static void main(String[] args) throws UnknownHostException, IOException {
		// Default configuration
		String host = "localhost";
		int port = ChatServer.PORT;
		
		// Show usage info for too many arguments
		if (args.length > 2) {
			out.println("\r\nUsage: java ChatClient [host] [port]");
			out.println("With no arguments -> host: " + host + ", port: " + port + "\r\n");
			return;
		}
		// Read command line arguments and assign to appropriate variables
		if (args.length > 0) host = args[0];
		if (args.length > 1) port = Integer.parseInt(args[1]);

		try {
			Socket socket = new Socket(host, port);
			var chatClient = new ChatClient(socket);

			var executor = Executors.newVirtualThreadPerTaskExecutor();

			// Run receiveMessage() in separate virtual thread for each client
			executor.execute(() -> chatClient.receiveMessage());
			chatClient.sendMessage();
			
			executor.shutdown();
		} catch (ConnectException e) {
			out.println("\r\nCould not connect to '" + host + "' at port " + port + "\r\n");
		} catch (UnknownHostException e) {
			out.println("\r\nCould not connect to hostname: " + host + "\r\n");
		} catch (NoSuchElementException e) {
			out.println("\r\nChat session terminated!\r\n");
		}
	}
}
