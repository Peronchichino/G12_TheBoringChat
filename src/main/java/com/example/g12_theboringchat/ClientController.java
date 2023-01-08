package com.example.g12_theboringchat;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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
        txt_messageArea.setEditable(false);
        workerThread = Executors.newSingleThreadExecutor();
    }

    @Override
    public void run() {
        initialized = true;
        done = false;
        try {
            InetAddress host = InetAddress.getLocalHost();
            System.out.println(host.getHostAddress());
            System.out.println(host.getHostName());

            client = new Socket("127.0.0.1", 9999);
            System.out.println("Client: connected to " + client.getInetAddress());

            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));

            receiveMessages();
        } catch (IOException e) {
            e.getMessage();
            e.printStackTrace();
            System.out.println("Error creating client...");
            shutdown();
        }
    }

    @FXML
    public void btnSendMsg(ActionEvent event) {
        if(!initialized){ //makes sure that run() gets initialized first
            run();
        }
        String msg = txt_message.getText();
        workerThread.execute(() -> {
            out.println(msg);
        });
    }

    private void receiveMessages() {
        Executor receiveExecutor = Executors.newSingleThreadExecutor();
        receiveExecutor.execute(() -> {
            try {
                while (!done) {
                    String message = in.readLine();
                    if (message == null) {
                        break;
                    }
                    System.out.println("Client: received messagefrom server: " + message);
                    Platform.runLater(() -> {
                        txt_messageArea.appendText("Client: " + message + "\n");
                    });
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
            in.close();
            out.close();
            if (!client.isClosed()) {
                client.close();
            }
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
            // can't do anything about it
        }
    }
}