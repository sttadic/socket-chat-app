# Java Socket Programming - Chat Application
Network Technologies module - Higher Diploma in Software Development <br>
Author: Stjepan Tadic

<br>

## Description
This is a console-based chat application written in Java. It demonstrates networking concepts, multithreading, and thread-safe operations. The application uses sockets to establish communication between clients and the server, and Java’s virtual threads for efficient and concurrent handling of multiple clients. Thread safety is ensured using concurrent collection CopyOnWriteArrayList and atomic boolean flag.

<br>

## Usage
#### Prerequisites: Java 19 or later to support virtual threads.
- Clone the repository:
```bash
git clone https://github.com/sttadic/socket-chat-app.git
```

- Run the application (compiled):
```bash
java ie.atu.sw.ChatServer                   // The server starts listening on default port 13 
```

```bash
java ie.atu.sw.ChatClient <host> <port>     // Starts the client(s). With no command-line arguments defaults to 'localhost', port 13
                                            // <host> The optional hostname of the server
                                            // <port> The optional port number of the server
```

<br>

## Features
> **Chat Room Functionality** <br>
- Multiple clients can join or leave the chat room.
- Messages are broadcasted and clearly displayed to all connected clients except to the sender, containing a name of a sender in uppercase and content of a message.

> **Connection Configuration** <br>
- The client can use command-line arguments to specify hostname and port.
- Defaults to *localhost:13* if no arguments are provided.

> **Graceful Disconnection** <br>
- Clients can leave the chat by typing **\q**.
- The server cleans up resources and notifies other clients when a member leaves.

> **Concurrent Client Handling** <br> 
- Virtual threads are used for efficient and lightweight management of client connections.

> **Comprehensive Error Handling** <br>
- Provides extensive error handling and displays error messages in a user-friendly fashion for scenarios such as failure to connect to the server, binding issues with the server, client disconnecting abruptly either before or after providing a username, issues with unresponsive server, and more.

> **Resource Cleanup:** <br> 
- Each part of application efficiently manages resources such as input/output streams, executor service and socket connections by ensuring they are closed when no longer needed.