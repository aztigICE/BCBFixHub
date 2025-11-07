package bcbfixhub.bcbfixhub.utils;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

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

    /**
     * Decrease the stock of a product in a collection by a specified amount.
     *
     * @param dbName    The database name
     * @param brand     The product brand
     * @param model     The product model
     * @param quantity  The amount to decrease
     */
    public static void decreaseStock(String dbName, String brand, String model, int quantity) {
        try {
            MongoDatabase db = getDatabase(dbName);

            // Loop through collections to find the product
            for (String collectionName : db.listCollectionNames()) {
                MongoCollection<Document> collection = db.getCollection(collectionName);

                Document product = collection.find(and(eq("brand", brand), eq("model", model))).first();
                if (product != null) {
                    int currentStock = product.getInteger("stock", 0);
                    int newStock = Math.max(0, currentStock - quantity); // prevent negative stock

                    collection.updateOne(
                            and(eq("brand", brand), eq("model", model)),
                            new Document("$set", new Document("stock", newStock))
                    );

                    System.out.println("Stock updated for " + brand + " " + model + ": " + currentStock + " -> " + newStock);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
