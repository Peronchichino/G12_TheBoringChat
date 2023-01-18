package com.example.g12_theboringchat;

import java.io.*;
import java.net.CookieHandler;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.*;

public class Server implements Runnable{
    private LinkedBlockingQueue<ConnectionHandler> connections;
    private ServerSocket server;
    private boolean done;
    private ExecutorService threadpool;

    public Server(){
        connections = new LinkedBlockingQueue<>();
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
                connections = new LinkedBlockingQueue<>();
                threadpool.execute(handler); //threadpool instead of individual threads to make it easier cause of frequent connections
            }
        } catch (Exception e) { //should shutdown no matter the exception
            shutdown();
            e.printStackTrace();
        }
    }

    //broadcast msg from server to all clients
    public void broadcast(String msg){
        Iterator<ConnectionHandler> it = connections.iterator();
        while(it.hasNext()){
            ConnectionHandler ch = it.next();
            if(ch != null){
                ch.out.println(msg+"\n");
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
        private String name;

        public ConnectionHandler(Socket client) throws IOException {
            this.client = client;
        }
        @Override
        public void run() {
            try{
                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out.println("Please enter a nickname:");
                name = in.readLine();
                System.out.println("SERVER: "+name+" has joined the chat!");
                broadcast("SERVER: "+name+" has joined the chat!");

                String msg;
                while((msg = in.readLine()) != null){
                    if(msg.startsWith("/quit")){
                        shutdownClient();
                    } else if(msg.startsWith("/nick ")){
                        String[] messageSplit = msg.split(" ",2);
                        if(messageSplit.length == 2){
                            System.out.println(name+" renamed themselves to "+messageSplit[1]);
                            name = messageSplit[1];
                            out.println("Successfully changed nickname to "+name);
                        } else{
                            out.println("No nickname provided.");
                        }
                    }else {
                        broadcast(msg+"broadcast");
                    }
                }
            } catch(IOException e){
                shutdownClient();
            }
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