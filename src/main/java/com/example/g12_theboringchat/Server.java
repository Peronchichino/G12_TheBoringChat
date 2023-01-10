package com.example.g12_theboringchat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable{
    private ConcurrentHashMap<String, ConnectionHandler> connections;
    private ServerSocket server;
    private boolean done;
    private ExecutorService threadpool;

    public Server(){
        connections = new ConcurrentHashMap<>();
        done = false;
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(9999);
            threadpool = Executors.newCachedThreadPool();
            System.out.println("Server started, waiting for clients.");
            while(!done){
                Socket client = server.accept();
                System.out.println(client.getClass().getSimpleName());
                ConnectionHandler handler = new ConnectionHandler(client);
                connections = new ConcurrentHashMap<>();
                threadpool.execute(handler); //threadpool instead of individual threads to make it easier cause of frequent connections
            }
        } catch (Exception e) { //should shutdown no matter the exception
            shutdown();
            e.printStackTrace();
        }
    }

    //broadcast msg from server to all clients
    public void broadcast(String msg){
        Iterator<ConnectionHandler> it = connections.values().iterator();
        while(it.hasNext()){
            ConnectionHandler ch = it.next();
            if(ch != null){
                ch.out.println(msg+"\n");
            } else {
                it.remove();
            }
        }
    }

    //shutdown server
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
            //ignore, cant do anything about it
        }
    }

    public class ConnectionHandler implements Runnable{
        private Socket client;
        private BufferedReader in;
        private PrintWriter out;

        public ConnectionHandler(Socket client) throws IOException {
            this.client = client;
            this.out = new PrintWriter(client.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        }
        @Override
        public void run() {
            try{
                out.println("Hello client"); //to send messages to client from server
                String test = in.readLine(); //get message from the client to server
                System.out.println(test);
                broadcast("Client joined the chat");

                String msg;
                while((msg = in.readLine()) != null){
                    broadcast(msg+"broadcast");
                }
            } catch(IOException e){
                shutdownClient();
            }
        }

        public void sendMsg(String message){
            out.println(message+"\n");
        }

        public void shutdownClient(){
            try{
                if(!client.isClosed()){
                    client.close();
                }
            } catch(IOException e){
                e.printStackTrace();
                System.out.println("Error with client shutdown function");
                //ignore
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}