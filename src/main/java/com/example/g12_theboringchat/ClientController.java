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
    private InputStream in;
    private OutputStream out;
    private boolean done;

    @Override
    public void run() {
        try{
            client = new Socket("127.0.0.1", 4711);
            System.out.println("Client: connected to "+client.getInetAddress());

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
            //cant do anything about it
        }
    }


    public void btnSendMsg(ActionEvent event){
        try{
            String msg = txt_message.getText();
            out = client.getOutputStream();
            byte b[] = new byte[msg.length()];
            out.write(b);

            in = client.getInputStream();
            byte b2[] = new byte[msg.length()];
            int bytes = in.read();
            System.out.println("Client: recieved "+bytes+" bytes from server");
            String serverMsg = new String(b2);
            System.out.println("Client: message from server: "+serverMsg);

            txt_messageArea.appendText("Client: "+serverMsg+"\n");
        } catch(IOException e){
            e.printStackTrace();
        }
    }


}