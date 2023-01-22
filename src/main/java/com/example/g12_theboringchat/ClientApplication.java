package com.example.g12_theboringchat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * When run, starts the client GUI.
 * <p>
 *     This class is essentially the main() of the client. It calls the ClientController and uses it as its back-end essentially.
 *     One cannot function without the other.
 * </p>
 *
 * @author Lukas Buchmayer, Bober Kamil, Christof Pichler
 */
public class ClientApplication extends Application {
    /**
     * Function that starts the Client.
     * <p>
     *     Calls ClientController.java and creates the chat GUI for the client.
     *     The way the stage is called is different here because we implemented threading in ClientController.java.
     *     We call the stage with Parent instead of the FXML loader because FXML loader caused some thread initialization errors.
     * </p>
     *
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