package bcbfixhub.bcbfixhub.utils;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

 /*
 * Implement a database helper which is able to connect and get the mongodb database instance.
 * The database helper is implemented as a singleton pattern to ensure that only one instance of the
 * database helper and database connection exist.
 */

public class DBConnectionHelper {

    // Important strings for database connection and cluster connection in MongoDB.
    private static final String CONNECTION_STRING = "mongodb+srv://njbermoy_db_user:vhjSWDXGMS6dJ8dy@bcbfixhub-cluster.ezoyndh.mongodb.net/?appName=BCBFixhub-Cluster";
    private static final String DATABASE_STRING = "fixhub-db"; //Only one database instead of multiple ones in MongoDB.

    // Below are words from Sir Ariel's own version of this.
    // Ensure only one instance of DatabaseHelper exists (singleton pattern).
    private static final DBConnectionHelper instance = new DBConnectionHelper();

    // Declaration
    private MongoDatabase database;
    private MongoClient mongoClient;

    private DBConnectionHelper() {
        // Sir A said that this will prevent instantiation from the outside.
    }

    //Getter method of an instance for this class
    public static DBConnectionHelper getInstance() {return instance;}

    // Mongo Client Getter
    public MongoClient getMongoClient() {
        try {
            if (this.mongoClient == null) {
                this.mongoClient = MongoClients.create(CONNECTION_STRING); // This creates a MongoDB client basically.
                System.out.println("Mongo Client Created Successfully!"); // Debug message.
            }
        } catch (Exception e) {
            System.out.println("Mongo Client Creation Failed!"); // Failure (Debug msg).
        }

        return this.mongoClient; // Return MongoClient instance
    }

    // Mongo Database Getter, basically builds on the client getter
    public MongoDatabase getDatabase() {
        try {
            if (this.database == null) {
                this.database = this.getMongoClient().getDatabase(DATABASE_STRING);
                System.out.println("Database Connected Successfully!");
            }
        } catch (Exception e) {
            System.out.println("Database Connected Failed!");
        }

        return this.database;
    }

}
