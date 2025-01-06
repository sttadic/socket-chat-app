package ie.atu.sw;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.io.*;

public class ChatRoomManager {
	private final List<ChatRoomMember> chatRoomMembers = new CopyOnWriteArrayList<>();

	public void joinRoom(ChatRoomMember connection) {
		chatRoomMembers.add(connection);
	}

	// Remove member from a list of members, broadcast to all and clean resources
	public void leaveRoom(ChatRoomMember chatRoomMember) {
		broadcastToRoom("SERVER: " + chatRoomMember.name() + " has left the chat!", null);
		closeResources(chatRoomMember);
		chatRoomMembers.remove(chatRoomMember);
	}

	public void broadcastToRoom(String message, ChatRoomMember sender) {
		for (ChatRoomMember member : chatRoomMembers) {
			// Don't broadcast to sender
			if (sender != null && member.name().equals(sender.name())) continue;

			try {
				member.writer().println(sender == null ? message : sender.name().toUpperCase() + ": " + message);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	// Close all resources for particular client's connection
	private void closeResources(ChatRoomMember chatRoomMember) {
		try {
			if (chatRoomMember.reader() != null) chatRoomMember.reader().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (chatRoomMember.writer() != null) chatRoomMember.writer().close();

		try {
			if (chatRoomMember.connection() != null) chatRoomMember.connection().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
