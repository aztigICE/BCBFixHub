package bcbfixhub.bcbfixhub.controllers;

import bcbfixhub.bcbfixhub.BcbfixhubApplication;
import bcbfixhub.bcbfixhub.models.User;
import bcbfixhub.bcbfixhub.utils.DBConnectionHelper;
import com.mongodb.client.MongoDatabase;
import javafx.scene.control.Alert;

public abstract class BaseController  {

    protected BcbfixhubApplication app;
    protected User currentUser;
    protected MongoDatabase database; // Shared database instance for all controllers

    // Automatically connect controllers to the database helper singleton
    public BaseController() {
        this.database = DBConnectionHelper.getInstance().getDatabase();
    }

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

    // Hook method for scenes that need logic when shown
    public void onSceneShown() {}
}
