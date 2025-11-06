package bcbfixhub.bcbfixhub.models;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class ProductDBConnection {
    private static final String CONNECTION_STRING = "mongodb+srv://dbuling438_db_user:L6G8GYlF94K2QxTR@bcbfixhub-cluster.ezoyndh.mongodb.net/?appName=BCBFixhub-Cluster";
    private static final String DATABASE_NAME = "Product-Details"; // new db NAME

    private static MongoClient mongoClient = null;

    public static MongoDatabase getDatabase() {
        if (mongoClient == null) {
            mongoClient = MongoClients.create(CONNECTION_STRING);
        }
        return mongoClient.getDatabase(DATABASE_NAME);
    }
}
