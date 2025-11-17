package bcbfixhub.bcbfixhub.controllers;

import bcbfixhub.bcbfixhub.BcbfixhubApplication;
import bcbfixhub.bcbfixhub.models.User;
import javafx.scene.control.Alert;

public abstract class BaseController  {
    protected BcbfixhubApplication app;
    protected User currentUser;

    public void setApp(BcbfixhubApplication app) {
        this.app = app;
    }

    public BcbfixhubApplication getApp() {
        return app;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    protected void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void onSceneShown() {}
}
