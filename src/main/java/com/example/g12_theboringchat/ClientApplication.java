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
        Scene scene = new Scene(root, 560, 518);
        scene.getStylesheets().add(getClass().getResource("client-stylesheet.css").toExternalForm());
        stage.setTitle("G12 ODE Projekt: Chat Room");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}