/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.net.*;
import java.io.*;
import java.io.IOException;
import java.util.ArrayList;
// implementing  Chat server class
public class ChatServer implements Runnable {
	public ArrayList<String> newUserName = new ArrayList<>();
	public ServerSocket ChatServerSocket = null;
	public ArrayList<Socket> connectedChatClient = new ArrayList<>();
	//Chat server main class
	public static void main(String[] args) {

		   try{
		   ChatServer server = new ChatServer(5000);
			   server.runServer();
	   }catch(IOException ex){
       	System.out.print("port number not found");

		}
	}

	public ChatServer(int portNumber) throws IOException {
		ChatServerSocket = new ServerSocket(portNumber);
		// create a socket and bind to a port
		System.out.println("Server Started and listening in port: " + portNumber);
	}
	public void runServer() throws IOException {
		while (true) {
			try {
				// Wait to accept connections from clients
				Socket s = ChatServerSocket.accept();
				// add the accepted socket to the list
				connectedChatClient.add(s);
				Thread thread = new Thread(this);
				thread.start();

			} catch (IOException ex) {
				ChatServerSocket.close();
				System.out.println("client connection not found");
			}
		}
	}
	public void close() throws IOException {
		ChatServerSocket.close();
	}
	@Override
	public void run() {
		String input = null;
		BufferedReader inStreamReadFromSocket = null;
		Socket currentConnectedSocket = connectedChatClient.get(connectedChatClient.size() - 1);

		try {
			//Create a Buffered Reader using current connect socket to read from the socket 
			inStreamReadFromSocket = new BufferedReader(new InputStreamReader(currentConnectedSocket.getInputStream()));
		} catch (IOException ex) {
			System.out.println("Noting to read from Buffer");
			try {
				close();
			} catch (IOException ex1){
				System.out.println("Unable to write to client socket");
			}
			System.exit(1);
		}

		try {
			// Reading from the client socket 

			while ((input = inStreamReadFromSocket.readLine()) != null){
				System.out.println("Message from client " + " : " + input);
				for (Socket socket : connectedChatClient) {
					if (socket.getPort() != currentConnectedSocket.getPort()) {
						//Forward received message to the other clients in the same chat room  
						BufferedWriter outStreamWriteToSocket = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
						sendMessage(input, outStreamWriteToSocket);
					}
				}
			}
		} catch (IOException ex) {
			try {
				close();
				inStreamReadFromSocket.close();
			} catch (IOException ex1) {
				System.out.println("unable to forward message");
			}
		}
	}
	public void sendMessage(String message, BufferedWriter streamOut) throws IOException {
		streamOut.write(message);
		streamOut.newLine();
		streamOut.flush();
	}

}
