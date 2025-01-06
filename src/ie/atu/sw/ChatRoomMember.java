package ie.atu.sw;

import java.io.*;
import java.net.Socket;

public record ChatRoomMember(Socket connection, BufferedReader reader, PrintWriter writer, String name) {

}
