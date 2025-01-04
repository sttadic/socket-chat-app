package ie.atu.sw;

import java.io.*;
import java.net.*;
import static java.lang.System.out;

public class ChatServer {
	private final static int PORT = 13;
	
	public void startServer() {
		try (ServerSocket server = new ServerSocket(PORT)) {
			 
			while (true) {
				out.println("ChatServer listening on port " + PORT);
				
				try (Socket connection = server.accept()) {
					out.println("Client connected from host " + connection.getInetAddress() + ", port: " + connection.getPort());
					
					Writer output = new OutputStreamWriter(connection.getOutputStream());
					output.write("Hello World \r\n");
					output.flush();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
