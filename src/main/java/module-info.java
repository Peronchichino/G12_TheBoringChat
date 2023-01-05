module com.example.g12_theboringchat {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.g12_theboringchat to javafx.fxml;
    exports com.example.g12_theboringchat;
}