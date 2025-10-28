package bcbfixhub.bcbfixhub;

import com.mongodb.client.*;
import org.bson.Document;

public class PrintDatabase {
    public static void main(String[] args) {
        // Replace with your actual connection string
        String uri = "mongodb+srv://njbermoy_db_user:4ZhdzC0TJ8du6seM@testdb.z8dbuui.mongodb.net/";
        String dbName = "userDetails"; // FIXED
        String collectionName = "user";

        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase(dbName);
            MongoCollection<Document> collection = database.getCollection(collectionName);

            System.out.println("Database: " + dbName);
            System.out.println("Collection: " + collectionName);

            try (MongoCursor<Document> cursor = collection.find().iterator()) {
                if (!cursor.hasNext()) {
                    System.out.println("(No documents found)");
                } else {
                    while (cursor.hasNext()) {
                        Document doc = cursor.next();
                        System.out.println(doc.toJson());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
