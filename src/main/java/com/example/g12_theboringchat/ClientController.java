package com.example.g12_theboringchat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.TableHeaderRow;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Runnable{
    @FXML
    private Button btn_sendMessage;
    @FXML
    private TextField txt_message;
    @FXML
    TextArea txt_messageArea;

    private Socket client;
    private boolean done;
    private PrintWriter out;
    private BufferedReader in;


    @Override
    public void run(){
        try {
            client = new Socket("127.0.0.1", 4711);
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
            //cant do anything about it
        }
    }

    @FXML
    public void btnSendMsg(ActionEvent event) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    while(!done){
                        String msg = txt_message.getText();
                        if(msg.equals("/quit")){
                            out.println();
                            shutdown();
                        } else{
                            txt_messageArea.appendText("Client: "+msg+"\n");
                            txt_message.setText("");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    shutdown();
                }
            }
        }).start();
    }

}