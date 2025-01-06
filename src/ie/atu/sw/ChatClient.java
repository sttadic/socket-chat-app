package ie.atu.sw;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.System.out;

public class ChatClient {
	private final Scanner scan;
	private final Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	private final AtomicBoolean running;

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

	public void sendMessage() {
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

	public void receiveMessage() {
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

	
	public static void main(String[] args) throws UnknownHostException, IOException {
		Scanner scan = new Scanner(System.in);
		// Default configuration
		String host = "localhost";
		int port = ChatServer.PORT;
		
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
			out.println("\r\nCould not connect to host: " + host + "\r\n");
		} catch (NoSuchElementException e) {
			out.println("\r\nChat session terminated!\r\n");
		}
	}
}
