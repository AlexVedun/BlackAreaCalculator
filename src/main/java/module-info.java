module com.alexeyefr.blackareacalculator {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.alexeyefr.blackareacalculator to javafx.fxml;
    exports com.alexeyefr.blackareacalculator;
}