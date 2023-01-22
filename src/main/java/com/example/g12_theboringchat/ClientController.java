package com.example.g12_theboringchat;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * The JavaFX controller and the client socket.
 *
 * <p>
 *     This is the JavaF controller called by ClientApplication.java and connects to the Server.java socket running locally.
 *     The class uses many different threads and most actions are triggered by the press of a button.
 * </p>
 *
 * This class gets automatically called when running ClientApplication.java.
 *
 * @author Lukas Buchmayer, Bober Kamil, Christof Pichler
 */

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

    /**
     * ClientController constructor that initializes the workerThread.
     */
    public ClientController() {
        workerThread = new ThreadPoolExecutor(
                1,
                1,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>()
        );
    }

    /**
     * Method used to initialize the ClientController correctly.
     * <p>
     *     Makes sure that the run function is called properly.
     * </p>
     */
    @FXML
    public void initialize() {
        this.run();
        initialized = true;
        txt_messageArea.setEditable(false);
        workerThread = Executors.newSingleThreadExecutor();
    }

    /**
     * The run method used to connect the client socket to the server socket.
     */
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

    /**
     * Method that sends a message when the send button is clicked.
     *
     * @param event On-click.
     */
    @FXML
    public void btnSendMsg(ActionEvent event) {
        String message = txt_message.getText();
        System.out.println(message);
        if(message != null){
            workerThread.execute(() -> out.println(message));
            out.flush();

            txt_messageArea.appendText("Me: "+message+"\n");
            if(message.startsWith("/quit")){
                shutdown();
            }

            txt_message.clear();
        }
    }

    /**
     * Function that allows the client socket to receive messages from the server by running the messages in a thread.
     */
    public void receiveMessages() {
        Executor receiveExecutor = Executors.newSingleThreadExecutor();
        receiveExecutor.execute(() -> {
            try {
                while (!done) {
                    String message;
                    while((message = in.readLine()) != null){
                        System.out.println("Server: " + message);
                        txt_messageArea.appendText(message+"\n");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                shutdown();
            }
        });
    }

    /**
     * Shutdown method for the client socket.
     * <p>
     *     Gets called whenever an exception is thrown, an error occurs, or the client sends the message "/quit".
     * </p>
     */
    public void shutdown() {
        done = true;
        try {
            if (!client.isClosed()) {
                client.close();
            }
            System.exit(0);
        } catch (IOException e) {
            e.getMessage();
        }
    }

    /**
     * Function that allows a client to download the current chat to an external .txt file.
     *
     * @param actionEvent On-click.
     */
    @FXML
    public void btnDownload(ActionEvent actionEvent) {
        ObservableList<CharSequence> paragraph = txt_messageArea.getParagraphs();
        Iterator<CharSequence> iter = paragraph.iterator();
        File archive = new File("TheBoringChatArchive.txt");
        try{
            BufferedWriter fw = new BufferedWriter(new FileWriter(archive));
            while(iter.hasNext())
            {
                CharSequence seq = iter.next();
                fw.append(seq);
                fw.newLine();
            }
            fw.flush();
            fw.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}