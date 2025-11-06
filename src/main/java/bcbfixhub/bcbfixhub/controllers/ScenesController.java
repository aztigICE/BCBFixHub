package bcbfixhub.bcbfixhub.controllers;

import bcbfixhub.bcbfixhub.ScenesApplication;
import javafx.fxml.FXML;

public class ScenesController {
    protected ScenesApplication app;

    public void setApplication(ScenesApplication app) {
        this.app = app;
    }

    @FXML
    protected void switchToLogin() {
        app.switchTo("login");
    }

    @FXML
    protected void switchToRegister() {
        app.switchTo("register");
    }


    // Optional helper for dynamic buttons:
    protected void switchTo(String name) {
        app.switchTo(name);
    }
}
