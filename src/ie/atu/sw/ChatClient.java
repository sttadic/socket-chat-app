package ie.atu.sw;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.*;

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
		// Flag shared with all threads
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

		while (running.get()) {
			out.print("Message: ");
			String outMessage = scan.nextLine();

			// Close chat
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
		try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
			executor.execute(() -> {
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
			});
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
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (writer != null) writer.close();
		if (scan != null) scan.close();
	}

	public static void main(String[] args) throws UnknownHostException, IOException {
		try {
		Socket socket = new Socket("localhos", 13);
		var chatClient = new ChatClient(socket);
		chatClient.sendMessage();
		chatClient.receiveMessage();
		} catch (ConnectException e) {
			out.println("Could not connect to the host at specified port!");
		} catch (UnknownHostException e) {
			out.println("Could not connect to specified host");
		}
	}
}
