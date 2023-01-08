package com.example.g12_theboringchat;

import java.io.*;
import java.net.Socket;


public class Client implements Runnable{
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done;

    @Override
    public void run() {
        try{
            Socket client = new Socket("127.0.0.1", 4711);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            InputHandler inHandler = new InputHandler();
            Thread t = new Thread(inHandler);
            t.start();

            String inMsg;
            while((inMsg = in.readLine()) != null){
                System.out.println(inMsg);
            }
        } catch(IOException e){
            e.printStackTrace();
            shutdown();
        }
    }

    public void shutdown(){
        done = true;
        try{
            in.close();
            out.close();
            if(!client.isClosed()){
                client.close();
            }
            System.exit(0);
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    class InputHandler implements Runnable{

        @Override
        public void run() {
            try {
                BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
                while (!done){
                    String msg = inReader.readLine();
                    if(msg.equals("/quit")){
                        out.println(msg);
                        inReader.close();
                        shutdown();
                    } else {
                        out.println(msg);
                    }
                }
            } catch(IOException e){
                shutdown();
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }

}
