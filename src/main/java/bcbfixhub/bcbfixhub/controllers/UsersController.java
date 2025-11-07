package bcbfixhub.bcbfixhub.controllers;

import bcbfixhub.bcbfixhub.utils.MongoDBConnectionManager;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class UsersController extends ScenesController {

    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> phoneColumn;
    @FXML private TableColumn<User, Void> actionColumn;

    private static final String DATABASE_NAME = "User-Details";
    private static final String COLLECTION_NAME = "users";

    @FXML
    public void initialize() {
        // Setup columns
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        // Add action buttons
        addActionButtons();

        // Load users
        loadUsers();
    }

    private void loadUsers() {
        List<User> users = fetchUsers();
        usersTable.setItems(FXCollections.observableArrayList(users));
    }

    private List<User> fetchUsers() {
        List<User> usersList = new ArrayList<>();
        MongoDatabase db = MongoDBConnectionManager.getDatabase(DATABASE_NAME);
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);

        for (Document doc : collection.find()) {
            String username = doc.getString("username");
            String email = doc.getString("email");
            String phone = doc.getString("phone");

            usersList.add(new User(username, email, phone));
        }
        return usersList;
    }

    // Add Edit/Delete buttons to each row
    private void addActionButtons() {
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox container = new HBox(10, editButton, deleteButton);

            {
                editButton.setOnAction(e -> handleEdit(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(e -> handleDelete(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }
        });
    }

    // Edit user
    private void handleEdit(User user) {
        TextInputDialog dialog = new TextInputDialog(user.getEmail());
        dialog.setTitle("Edit User");
        dialog.setHeaderText("Editing user: " + user.getUsername());
        dialog.setContentText("Email:");

        dialog.showAndWait().ifPresent(newEmail -> {
            user.setEmail(newEmail);
            // Update MongoDB
            MongoCollection<Document> collection = MongoDBConnectionManager
                    .getDatabase(DATABASE_NAME)
                    .getCollection(COLLECTION_NAME);
            collection.updateOne(eq("username", user.getUsername()), new Document("$set", new Document("email", newEmail)));
            usersTable.refresh();
        });
    }

    // Delete user
    private void handleDelete(User user) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText(null);
        confirm.setContentText("Delete user \"" + user.getUsername() + "\"?");
        confirm.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                MongoCollection<Document> collection = MongoDBConnectionManager
                        .getDatabase(DATABASE_NAME)
                        .getCollection(COLLECTION_NAME);
                collection.deleteOne(eq("username", user.getUsername()));
                usersTable.getItems().remove(user);
            }
        });
    }

    public void backAdmin(ActionEvent event) {
        app.switchTo("admin");
    }

    // Inner User class
    public static class User {
        private String username;
        private String email;
        private String phone;

        public User(String username, String email, String phone) {
            this.username = username;
            this.email = email;
            this.phone = phone;
        }

        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }

        public void setUsername(String username) { this.username = username; }
        public void setEmail(String email) { this.email = email; }
        public void setPhone(String phone) { this.phone = phone; }
    }
}
