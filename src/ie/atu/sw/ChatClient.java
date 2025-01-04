package ie.atu.sw;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

import static java.lang.System.out;

public class ChatClient {
	private int port;
	private String hostname = "localhost";
	
	public void startClient() {
		
		try (Socket socket = new Socket(hostname, port)) {
			out.println("Connected to server on host: " + hostname);
			
			socket.setSoTimeout(5000);
			
			InputStream input = socket.getInputStream();
			byte[] inputBytes = input.readAllBytes();
			String message = new String(inputBytes, StandardCharsets.US_ASCII);
			out.println(message);
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
