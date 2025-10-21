package bcbfixhub.bcbfixhub.models;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection {
    private static final String CONNECTION_STRING = "mongodb+srv://njbermoy_db_user:4ZhdzC0TJ8du6seM@testdb.z8dbuui.mongodb.net/";
    private static final String DATABASE_NAME = "userDetails"; // or whatever your db name is

    private static MongoClient mongoClient = null;

    public static MongoDatabase getDatabase() {
        if (mongoClient == null) {
            mongoClient = MongoClients.create(CONNECTION_STRING);
        }
        return mongoClient.getDatabase(DATABASE_NAME);
    }
}
