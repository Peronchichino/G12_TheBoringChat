package com.example.g12_theboringchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.example.g12_theboringchat.ClientController;

public class Server implements Runnable{
    private ArrayList<ConnectionHandler> connections;
    private ServerSocket server;
    private boolean done = false;
    private ExecutorService threadpool;

    public Server(){
        connections = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(4711);
            threadpool = Executors.newCachedThreadPool();
            System.out.println("Server started, waiting for clients.");
            while(!done){
                Socket client = server.accept();
                ConnectionHandler handler = new ConnectionHandler(client);
                connections.add(handler);
                threadpool.execute(handler); //threadpool instead of individual threads to make it easier cause of frequent connections
            }

        } catch (Exception e) { //should shutdown no matter the exception
            shutdown();
            e.printStackTrace();
        }
    }

    //broadcast msg from server to all clients
    public void broadcast(String msg){
        for(ConnectionHandler ch : connections){ //for each loop
            if(ch != null){
                ch.sendMsg(msg);
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
            for(ConnectionHandler ch : connections){
                ch.shutdownClient();
            }
            System.exit(0);
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
        private String nickname;

        public ConnectionHandler(Socket client){
            this.client = client;
        }
        @Override
        public void run() {
            try{
                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                //out.println("Hello client"); to send messages to client from server
                //in.readLine(); get message from the client to server
                out.println("Please enter nickname: ");
                nickname = ClientController.txt_message.getText();
                System.out.println(nickname + " connected"); //msg to see client is connected
                broadcast(nickname + " joined the chat");

                String msg;
                while((msg = in.readLine()) != null){
                    if(msg.startsWith("/nick ")){
                        String[] msgSplit = msg.split(" ", 2);
                        if(msgSplit.length == 2){
                            broadcast(nickname+" renamed themselves to "+msgSplit[1]);
                            //System.out.println(nickname+" renamed themselves to "+msgSplit[1]);
                            ClientController.txt_messageArea.appendText("SERVER: "+nickname+" renamed themselves to "+msgSplit[1]);
                            nickname = msgSplit[1];
                            out.println("Successfully changed nickname to: "+nickname);
                        } else{
                            out.println("No new nickname was provided.");
                        }
                    } else if(msg.startsWith("/quit")){
                        broadcast(nickname+" has left the chat");
                        shutdownClient();
                    } else {
                        broadcast(nickname + ": "+msg); //format for the actual messages being sent
                    }
                }
            } catch(IOException e){
                shutdownClient();
            }
        }

        public void sendMsg(String message){
            ClientController.txt_messageArea.appendText(message);
        }

        public void shutdownClient(){
            try{
                in.close();
                out.close();

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
