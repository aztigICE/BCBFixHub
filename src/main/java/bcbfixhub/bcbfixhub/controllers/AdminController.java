package bcbfixhub.bcbfixhub.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class AdminController extends BaseController {

    @FXML
    private void onProducts(ActionEvent event) {
        app.switchScene("admin-products");
    }

    @FXML
    private void onOrder(ActionEvent event) {
        app.switchScene("admin-orders");
    }

    @FXML
    private void onUser(ActionEvent event) {
        app.switchScene("admin-users");
    }

    @FXML
    private void onLogOut(ActionEvent event) {
        app.switchScene("login");
    }
}
