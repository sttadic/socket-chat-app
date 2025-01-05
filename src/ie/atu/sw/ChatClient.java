package ie.atu.sw;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Executors;

import static java.lang.System.out;

public class ChatClient {
	private final Scanner scan;
	private final Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;

	public ChatClient(Socket socket) {
		this.scan = new Scanner(System.in);
		this.socket = socket;
		try {
			this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.writer = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			exceptionHandler(e);
		}
	}

	public void sendMessage() {
		// First message will assign username
		writer.println(setName());

		while (true) {
			out.print("Message: ");
			String outMessage = scan.nextLine();
			if (outMessage.trim().equals("\\q")) {
				try {
					socket.close();
				} catch (IOException e) {
					exceptionHandler(e);
				}
				out.println("Goodbye...");
				return;
			}
			writer.println(outMessage);
		}
	}

	public void receiveMessage() {
		try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
			executor.execute(() -> {
				String inMessage;
				try {
					inMessage = reader.readLine();
					out.println(inMessage);
				} catch (IOException e) {
					exceptionHandler(e);
				}
			});
		}
	}

	private String setName() {
		String userName = "";
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
		closeResources();
		if (e instanceof ConnectException) {
			out.println("\r\nNo ChatServer is found. Please try again later.\r\n");
		} else if (e instanceof SocketException) {
			out.println("\r\nChatServer encountered an error. Chat session terminated!\r\n");
		} else {
			out.println("Something went wrong. Chat session terminated.\n\r");
		}
	}

	private void closeResources() {
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		writer.close();
		scan.close();
	}

	public static void main(String[] args) throws UnknownHostException, IOException {
		Socket socket = new Socket("localhost", 13);
		var chatClient = new ChatClient(socket);
		chatClient.sendMessage();
		chatClient.receiveMessage();
	}
}
