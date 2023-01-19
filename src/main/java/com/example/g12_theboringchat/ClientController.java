package com.example.g12_theboringchat;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ClientController implements Runnable {
    @FXML
    private Button btn_sendMessage;
    @FXML
    public TextField txt_message;
    @FXML
    TextArea txt_messageArea;
    public Socket client;
    private boolean done;
    private Executor workerThread;
    private BufferedReader in;
    private PrintWriter out;
    private boolean initialized = false;

    public ClientController() {
        workerThread = new ThreadPoolExecutor(
                1, // core pool size
                1, // maximum pool size
                0L, // keep-alive time
                TimeUnit.MILLISECONDS, // time unit
                new LinkedBlockingQueue<>() // work queue
        );
    }

    @FXML
    public void initialize() {
        this.run();
        initialized = true;
        txt_messageArea.setEditable(false);
        workerThread = Executors.newSingleThreadExecutor();
    }

    @Override
    public void run() {
        initialized = true;
        done = false;
        try {
            InetAddress host = InetAddress.getLocalHost();

            client = new Socket("127.0.0.1", 9999);
            System.out.println("Client: connected to " + host.getHostName());

            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);

            receiveMessages();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error creating client...");
            shutdown();
        }
    }

    @FXML
    public void btnSendMsg(ActionEvent event) {
        String message = txt_message.getText();
        System.out.println(message);
        if(message != null){
            workerThread.execute(() -> out.println(message));
            out.flush();

            txt_messageArea.appendText(message+"\n");
            if(message.startsWith("/quit")){
                shutdown();
            }

            txt_message.clear();
        }
    }

    public void receiveMessages() {
        Executor receiveExecutor = Executors.newSingleThreadExecutor();
        receiveExecutor.execute(() -> {
            try {
                while (!done) {
                    String message;
                    while((message = in.readLine()) != null){
                        System.out.println("Message from server: " + message);

                        txt_messageArea.appendText(message+"\n");
                        //Platform.runLater(() -> txt_messageArea.appendText(message +"\n"));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                shutdown();
            }
        });
    }
    public void shutdown() {
        done = true;
        try {
            if (!client.isClosed()) {
                client.close();
            }
            System.exit(0);
        } catch (IOException e) {
            e.getMessage();
            // can't do anything about it
        }
    }
}