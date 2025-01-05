package ie.atu.sw;

import java.net.*;
import java.util.*;
import java.io.*;

public class ClientManager {
	private ArrayList<Connection> clients = new ArrayList<>();
	private BufferedReader reader;
	private PrintWriter writer;
	
	
	public void addClient(Connection connection) {
		this.clients.add(connection);
		
		System.out.println("I am hererererererer");
		for (Connection c : clients) {
			System.out.println(c.name());
		}
	}
	
	
	
	
}
