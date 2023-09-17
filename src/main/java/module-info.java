module com.fugary.javafxdemo {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.fugary.javafxdemo to javafx.fxml;
    exports com.fugary.javafxdemo;
}
