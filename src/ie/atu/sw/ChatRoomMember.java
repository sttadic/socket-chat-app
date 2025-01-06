package ie.atu.sw;

import java.io.*;
import java.net.Socket;

/**
 * The {@code ChatRoomMember} record represents a member of the chat room,
 * holding the client's connection, input/output streams, and username
 * 
 * @param connection the client's socket connection
 * @param reader     the BufferedReader for reading client messages
 * @param writer     the PrintWriter for sending messages to the client
 * @param name       the username of the client
 */
public record ChatRoomMember(Socket connection, BufferedReader reader, PrintWriter writer, String name) {

}
