package com.example.g12_theboringchat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("client-view.fxml"));
        Scene scene = new Scene(root, 750, 600);
        scene.getStylesheets().add(getClass().getResource("client-stylesheet.css").toExternalForm());
        stage.setTitle("The Boring Chat ( ._.)");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        ClientController cont = new ClientController();
        cont.run();
        launch();
    }
}