package bcbfixhub.bcbfixhub.utils;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.util.HashMap;
import java.util.Map;

public class MongoDBConnectionManager {

    private static final String CONNECTION_STRING = "mongodb+srv://njbermoy_db_user:vhjSWDXGMS6dJ8dy@bcbfixhub-cluster.ezoyndh.mongodb.net/?appName=BCBFixhub-Cluster";
    private static MongoClient mongoClient = null;
    private static final Map<String, MongoDatabase> databases = new HashMap<>();

    private static MongoClient getClient() {
        if (mongoClient == null) {
            mongoClient = MongoClients.create(CONNECTION_STRING);
        }
        return mongoClient;
    }

    public static MongoDatabase getDatabase(String dbName) {
        if (!databases.containsKey(dbName)) {
            databases.put(dbName, getClient().getDatabase(dbName));
        }
        return databases.get(dbName);
    }
}
