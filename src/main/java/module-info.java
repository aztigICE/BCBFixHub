module bcbfixhub.bcbfixhub {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires org.mongodb.driver.core;
    requires org.mongodb.bson;
    requires org.mongodb.driver.sync.client;
    requires javafx.graphics;
    requires jbcrypt;

    opens bcbfixhub.bcbfixhub to javafx.fxml;
    opens bcbfixhub.bcbfixhub.controllers to javafx.fxml;
    exports bcbfixhub.bcbfixhub;
}
