package bcbfixhub.bcbfixhub.models;

import org.bson.Document;

// User model, first model in the chain. Will try to not repeat comments.
/*
* Create a new model, name it as User, and it must extend the BaseModel class. This class will now
* represent the user in the database.
* */
public class User extends BaseModel {

    // Declare our needed variables, this is based on our ERD.
    private String username;
    private String email;
    private String password;
    private String phone;

    // Default Constructor
    public User() {
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // This is where we use that abstract method from earlier.
    @Override
    public Document toDocument() {
        Document doc = new Document(); // Instantiate
        doc.append("username", this.username); // "append" method add to the document.
        doc.append("email", this.email);
        doc.append("password", this.password);
        doc.append("phone", this.phone);
        return doc; // Return the document created.
    }
}
