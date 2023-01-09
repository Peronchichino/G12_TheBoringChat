module com.example.g12_theboringchat {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens com.example.g12_theboringchat to javafx.fxml;
    exports com.example.g12_theboringchat;
}