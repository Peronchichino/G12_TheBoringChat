package com.example.g12_theboringchat;

import java.io.*;
import java.net.CookieHandler;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.*;

/**
 * This class is the Server socket which handles the client sockets and their output streams.
 *
 * <p>
 *     The server class has a nested class called ConnectionHandler which is called as a thread and handles a specified connected client.
 * </p>
 *
 * @author Lukas Buchmayer, Bober Kamil, Christof Pichler
 */
public class Server implements Runnable{
    private LinkedBlockingQueue<ConnectionHandler> connections;
    private ServerSocket server;
    private boolean done;
    private ExecutorService threadpool;
    private ArrayList<String> members;

    /**
     * Server constructor that initializes Client list.
     */
    public Server(){
        connections = new LinkedBlockingQueue<>();
        done = false;
    }

    /**
     * The run method that starts the server socket and accepts connecting clients.
     */
    @Override
    public void run() {
        try {
            server = new ServerSocket(9999);
            threadpool = Executors.newCachedThreadPool();
            System.out.println("Server started, waiting for clients.");
            members = new ArrayList<>();
            while(!done){
                Socket client = server.accept();
                System.out.println(client.getClass().getSimpleName());
                ConnectionHandler handler = new ConnectionHandler(client);
                threadpool.execute(handler);
            }
        } catch (Exception e) {
            shutdown();
            e.printStackTrace();
        }
    }

    /**
     * Method that sends a message to all connected clients/sockets.
     *
     * @param msg Message to be sent.
     * @param sender Client socket that sends the message.
     *               @see ConnectionHandler
     */
    public void broadcast(String msg, ConnectionHandler sender){
        for(ConnectionHandler ch : connections){
            if(ch != null  && ch != sender){
                ch.out.println(msg);
            }
        }
    }

    /**
     * The shutdown function for the server if any exceptions or errors occur.
     */
    public void shutdown() {
        try{
            done = true;
            threadpool.shutdown();
            if(!server.isClosed()){
                server.close();
            }
        } catch(IOException e){
            e.printStackTrace();
            System.out.println("Error with server shutdown function...");
        }
    }

    /**
     * Class used to handle all connected clients.
     * <p>
     *     A nested class of Server.java which is created as an object for each connected client socket.
     * </p>
     */
    public class ConnectionHandler implements Runnable{
        private Socket client;
        private BufferedReader in;
        private PrintWriter out;
        private String name;

        /**
         * Constructor for ConnectionHandler.java which is used for calling it as an object for each connected client socket.
         * @param client The corresponding socket in the LinkedBlockingQueue/Connections.
         */
        public ConnectionHandler(Socket client) throws IOException {
            this.client = client;
        }

        /**
         * Run method for threading.
         */
        @Override
        public void run() {
            try{
                connections.offer(this);
                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out.println("Welcome to the chat!");
                out.println("Please enter a nickname:");
                name = in.readLine();
                broadcast(name+" has joined the chat!", this);
                members.add(name);

                String msg;
                while((msg = in.readLine()) != null){
                    if(msg.startsWith("/quit")){
                        broadcast(name+"left the chat", this);
                        int i = members.indexOf(name);
                        members.remove(i);
                        shutdownClient();
                    } else if(msg.startsWith("/nick ")) {
                        String[] messageSplit = msg.split(" ", 2);
                        if (messageSplit.length == 2) {
                            System.out.println(name + " renamed themselves to " + messageSplit[1]);
                            int i = members.indexOf(name);
                            members.remove(i);
                            name = messageSplit[1];
                            members.add(name);
                            out.println("Successfully changed nickname to " + name);
                        } else {
                            out.println("No nickname provided.");
                        }
                    }else if(msg.startsWith("/showon")){
                        StringBuffer sb = new StringBuffer();
                        for(String s : members){
                            sb.append(s);
                            sb.append(", ");
                        }
                        String allMembers = sb.toString();
                        out.println("SERVER: all online members {"+allMembers+"}");
                    }else {
                        System.out.println(name+": "+msg);
                        broadcast(name+": "+msg, this);
                    }
                }
            } catch(IOException e){
                shutdownClient();
            }
        }

        /**
         * Method to force shutdown a connected socket is an exception is thrown, errors occurs or client wants to leave the chat.
         */
        public void shutdownClient(){
            connections.remove(this);
            try{
                if(!client.isClosed()){
                    client.close();
                }
            } catch(IOException e){
                e.printStackTrace();
                System.out.println("Error with client shutdown function");
            }
        }
    }

    /**
     * Main function that starts the server socket.
     */
    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}