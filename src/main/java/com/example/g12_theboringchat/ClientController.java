package com.example.g12_theboringchat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.Socket;

public class ClientController{
    @FXML
    private Button btn_sendMessage;
    @FXML
    private ScrollPane ScrollPane_chats;
    @FXML
    private TextField txt_message;
    @FXML
    TextArea txt_messageArea;

    private String nicknameBuf; //placeholder for now, client name
    private String msg; //placeholder msg string

    @FXML
    void btn_sendMessage(ActionEvent event){
        try{
            Socket client = new Socket("localhost", 4711);
            txt_messageArea.appendText(nicknameBuf+": "+msg+"\n");
        } catch(IOException e){
            e.printStackTrace();
        }
    }


}