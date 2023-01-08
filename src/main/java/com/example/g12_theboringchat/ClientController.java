package com.example.g12_theboringchat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.Socket;

public class ClientController implements Runnable{
    @FXML
    private Button btn_sendMessage;
    @FXML
    public static TextField txt_message;
    @FXML
    static
    TextArea txt_messageArea;

    private Socket client;
    private PrintWriter out;
    private BufferedReader in;

    @Override
    public void run() {
        try {
            client = new Socket("localhost", 4711);
            System.out.println("Client: connected to " + client.getInetAddress());
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            String inMsg;
            while ((inMsg = in.readLine()) != null) {
                txt_messageArea.appendText(inMsg);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error creating client...");
            shutdown();
        }
    }

    @FXML
    public void btnSendMsg(ActionEvent event) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(client.isConnected()){
                    try{
                        String msg = txt_message.getText();
                        if(msg.equals("/quit")){
                            out.println();
                            shutdown();
                        } else{
                            txt_messageArea.appendText("Client: "+msg+"\n");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        shutdown();
                    }
                    txt_message.setText("");
                }
            }
        }).start();
    }
    public void shutdown(){
        try{
            in.close();
            out.close();
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