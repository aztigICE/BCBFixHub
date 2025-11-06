package bcbfixhub.bcbfixhub.models;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import bcbfixhub.bcbfixhub.utils.MongoDBConnectionManager;
import bcbfixhub.bcbfixhub.utils.PasswordUtils;

import static com.mongodb.client.model.Filters.eq;

public class UserDAO {
    private final MongoCollection<Document> collection;

    /**
     * DAO constructor that dynamically chooses the database.
     * You can pass a different database if needed.
     */
    public UserDAO() {
        MongoDatabase database = MongoDBConnectionManager.getDatabase("User-Details");
        collection = database.getCollection("users");
    }

    // --- LOGIN ---
    public boolean authenticate(String email, String password) {
        Document userDoc = collection.find(eq("email", email)).first();
        if (userDoc != null) {
            String storedHashedPassword = userDoc.getString("password");
            return PasswordUtils.verifyPassword(password, storedHashedPassword);
        }
        return false;
    }

    // --- REGISTER ---
    public boolean createUser(String username, String email, String password, String phone) {
        Document existing = collection.find(eq("email", email)).first();
        if (existing != null) {
            return false; // Email already exists
        }

        // Hash the password before saving
        String hashedPassword = PasswordUtils.hashPassword(password);

        Document newUser = new Document("username", username)
                .append("email", email)
                .append("password", hashedPassword)
                .append("phone", phone);

        collection.insertOne(newUser);
        return true;
    }
}
