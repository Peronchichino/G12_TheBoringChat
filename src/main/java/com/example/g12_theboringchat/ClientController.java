package com.example.g12_theboringchat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ClientController implements Runnable{
    @FXML
    private Button btn_sendMessage;
    @FXML
    public TextField txt_message;
    @FXML
    TextArea txt_messageArea;
    Socket client;
    private boolean done;

    @Override
    public void run() {
        done = false;
        try {
            InetAddress host = InetAddress.getLocalHost();
            System.out.println(host.getHostAddress());
            System.out.println(host.getHostName());

            client = new Socket(host.getHostAddress(), 9999);
            System.out.println("Client: connected to " + client.getInetAddress());
        } catch (IOException e) {
            e.getMessage();
            e.printStackTrace();
            System.out.println("Error creating client...");
            shutdown();
        } finally{
            try{
                if(client != null){
                    System.out.println("Error creating/connecting client");
                    client.close();
                    System.exit(0);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    public void btnSendMsg(ActionEvent event) {
        String msg = txt_message.getText();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    while(!done){
                        OutputStream out = client.getOutputStream();
                        byte bout[] = msg.getBytes();
                        out.write(bout);

                        InputStream in = client.getInputStream();
                        byte bin[] = new byte[msg.length()];
                        int bytes = in.read(bin);
                        System.out.println("Client: received "+bytes+" Bytes from server");
                        String message = new String(bin);
                        System.out.println("Client: message from server"+message);

                        txt_messageArea.appendText("Client: "+message+"\n");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    shutdown();
                }
            }
        }).start();
    }
    public void shutdown(){
        done = true;
        try{
//            in.close();
//            out.close();
            if(!client.isClosed()){
                client.close();
            }
            System.exit(0);
        } catch(IOException e){
            e.printStackTrace();
            //cant do anything about it
        }
    }


}