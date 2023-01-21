package com.example.g12_theboringchat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This class calls the ClientController.java class and is essentially the GUI.
 *
 * @author Lukas Buchmayer, Bobar Kamil, Christof Pichler
 */
public class ClientApplication extends Application {
    /**
     * Function that starts the Client.
     * <p>
     *     Calls ClientController.java and creates the chat GUI for the client.
     * </p>
     * @param stage the primary stage for this application, onto which the application scene can be set.
     * Applications may create other stages, if needed, but they will not be primary stages.
     */
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("client-view.fxml"));
        Scene scene = new Scene(root, 560, 518);
        scene.getStylesheets().add(getClass().getResource("client-stylesheet.css").toExternalForm());
        stage.setTitle("G12 ODE Projekt: Chat Room");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Main method that starts the Client and shows GUI.
     */
    public static void main(String[] args) {
        launch();
    }
}