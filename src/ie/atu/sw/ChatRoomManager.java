package ie.atu.sw;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.io.*;

/**
 * The {@code ChatRoomManager} class manages chat room members and facilitates 
 * message broadcasting.
 */
public class ChatRoomManager {
	private final List<ChatRoomMember> chatRoomMembers = new CopyOnWriteArrayList<>();

	/**
	 * Adds a new member to the chat room.
	 * 
	 * @param chatRoomMember the member representing new client
	 */
	public void joinRoom(ChatRoomMember chatRoomMember) {
		chatRoomMembers.add(chatRoomMember);
	}

	/**
	 * Removes member from the chat room, notifies other members and cleans up
	 * resources.
	 * 
	 * @param chatRoomMember the member to remove
	 */
	public void leaveRoom(ChatRoomMember chatRoomMember) {
		broadcastToRoom("SERVER: " + chatRoomMember.name() + " has left the chat!", null);
		closeResources(chatRoomMember);
		chatRoomMembers.remove(chatRoomMember);
	}

	/**
	 * Broadcasts a message to all member in the chat room.
	 * 
	 * @param message the message to broadcast
	 * @param sender the sender of the message (null for server)
	 */
	public void broadcastToRoom(String message, ChatRoomMember sender) {
		for (ChatRoomMember member : chatRoomMembers) {
			// Don't broadcast to sender
			if (sender != null && member.name().equals(sender.name())) continue;

			try {
				member.writer().println(sender == null ? message : sender.name().toUpperCase() + ": " + message);
			} catch (Exception e) {
				System.out.println("Something went wrong: " + e.getMessage());
			}
		}

	}

	// Close all resources for particular client's connection
	private void closeResources(ChatRoomMember chatRoomMember) {
		try {
			if (chatRoomMember.reader() != null) chatRoomMember.reader().close();
		} catch (IOException e) {
			System.out.println("Error closing BufferedReader: " + e.getMessage());
			e.printStackTrace();
		}
		
		if (chatRoomMember.writer() != null) chatRoomMember.writer().close();

		try {
			if (chatRoomMember.connection() != null) chatRoomMember.connection().close();
		} catch (IOException e) {
			System.out.println("Error closing Socket: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
