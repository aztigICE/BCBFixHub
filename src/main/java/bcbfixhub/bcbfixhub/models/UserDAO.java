package bcbfixhub.bcbfixhub.models;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

public class UserDAO {
    private final MongoCollection<Document> collection;

    public UserDAO() {
        MongoDatabase database = MongoDBConnection.getDatabase();
        collection = database.getCollection("users");
    }

    public boolean authenticate(String email, String password) {
        Document userDoc = collection.find(eq("email", email)).first();
        if (userDoc != null) {
            String storedPassword = userDoc.getString("password");
            return storedPassword.equals(password);
        }
        return false;
    }

    public boolean createUser(String email, String password) {
        // Check if user already exists
        Document existing = collection.find(eq("email", email)).first();
        if (existing != null) {
            return false; // email already taken
        }

        Document newUser = new Document("email", email)
                .append("password", password);
        collection.insertOne(newUser);
        return true;
    }

}
