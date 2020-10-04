/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.net.*;
import java.io.*;
import java.io.IOException;
import java.net.Socket;
import java.util.*;
//Chat Client class 
public class ChatClient {
    private String chatUserName;
    private BufferedWriter outStreamWriteToSocket = null;
    private BufferedReader inStreamReadFromSocket = null;
    private Socket socket = null;
    private Integer port = null;
    private String Ip = null;
    /*
    * main method to start client chat connection
    * @it accept IP address and port number as an argument
    * otherwise return IOexception message that requires IP and port not found
     */
    public static void main(String[] args) {
        if(args.length != 2) {
            System.out.println("Please Enter Ip address and port number");
            System.exit(1);
        }

        ChatClient chat = null;
        try{
            chat = new ChatClient(args[0], args[1]);
            chat.runChatClient();
        } catch(Exception e) {
            try {
                chat.close();
            } catch (IOException ex) {
                System.out.println("port number or Ip address not found");
            }
        }
    }

    public ChatClient(String IpAddress, String PortNumber) {
        port = Integer.parseInt(PortNumber);
        Ip = IpAddress;
    }

    public void runChatClient() throws IOException {
        System.out.println("Chat Client is starting >>>");
        Scanner username = new Scanner(System.in);
        socket = new Socket(Ip, port);
        outStreamWriteToSocket = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        inStreamReadFromSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        /*Start a new thread to read from the socket to receive messages from other clients
        * create a thread that reads messages from server and prints to the screen
        * reading from the server socket
        */
        Thread toRead = new Thread(new Runnable(){
            @Override
            public void run() {

                String readFromSocket;
                try {
                    // always read from the the server socket and print to the screen
                    while ((readFromSocket = inStreamReadFromSocket.readLine()) != null){
                        System.out.println(readFromSocket);
                    }
                } catch (IOException ex) {
                    System.out.println("reading from the server not found");
                }
            }
        });
        toRead.start();
        //start a new thread to write to socket to send message to other clients
        // writing to the server socket
        Thread toWrite = new Thread(new Runnable(){ 
            @Override
            public void run() {
                String userInput;
                BufferedReader stdIn = new BufferedReader( new InputStreamReader(System.in));
                System.out.println("Message to send: ");
                try {
                    // always read from the standard output and write to the server socket 
                   
                    while((userInput = stdIn.readLine()) !=null){
                        outStreamWriteToSocket.write(userInput);
                        outStreamWriteToSocket.newLine();
                        outStreamWriteToSocket.flush();
                        if(userInput.equalsIgnoreCase("exit")){
                            close();
                        }
                    }
                } catch (IOException ex) {
                    System.out.println("user Input not found");
                }
            }
        });
        toWrite.start();
    }
    public void close() throws IOException {
        outStreamWriteToSocket.close();
        inStreamReadFromSocket.close();
        socket.close();
    }
}
