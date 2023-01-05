package com.example.g12_theboringchat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientController{
    @FXML
    private Button btn_sendMessage;
    @FXML
    private TextField txt_message;
    @FXML
    TextArea txt_messageArea;

    private String msg = "fuck";

    public void btnSendMsg(ActionEvent event){
        String message = txt_message.getText();
        txt_message.setText("");
        txt_messageArea.appendText(message+"\n");
    }
}