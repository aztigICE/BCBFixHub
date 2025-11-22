package bcbfixhub.bcbfixhub.repositories;

import bcbfixhub.bcbfixhub.models.BaseModel;
import bcbfixhub.bcbfixhub.utils.DBConnectionHelper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;

/**
 * The type Base repository. When building a software that relies on a database system for storage of data, the best practice
 * is to have single access or repository for each entity. We can use a repository pattern by creating a base
 * repository class, and it is typically an abstract class that contains CRUD abstract methods where
 * subclasses must implement on their end.
 *
 * @param <T> the type parameter
 * You can read on what T does, it's generics that works in java.
 */
public abstract class BaseRepository<T extends BaseModel> {

    // child classes must set this field
    // protected String collectionName = ""; <- this is from the lesson, but instead we do the one below.
    /**
     * The Collection. Declare the collection as a variable.
     */
    protected MongoCollection<Document> collection;

    /**
     * Instantiates a new Base repository.
     */
    public BaseRepository(){
        // DO NOT initialize collection here, instead do it for each repository.
    }

    /**
     * Init collection. Get the collection from the database and set the current collection.
     *
     * @param collectionName the collection name eg. "users" from the database.
     */
    protected void initCollection(String collectionName){
        MongoDatabase database = DBConnectionHelper.getInstance().getDatabase(); // Connect to the Database.
        this.collection = database.getCollection(collectionName); // Get the collection
    }

    /**
     * Base insert method. Insert document contents into the database.
     *
     * @param entity the entity
     */
    public void insert(T entity){
        Document doc = entity.toDocument(); // Instantiate
        this.collection.insertOne(doc); //Insert one document

        // Set the generated id back to the entity
        entity.setId(doc.getObjectId("_id"));
    }

    /**
     * Find by id t.
     *
     * @param id the id
     * @return the t
     */
    public T findById(ObjectId id){
        // Find document by id.
        Document doc = this.collection.find(eq("_id",id)).first();

        if (doc == null){
            return null;
        }

        return this.convert(doc);
    }

    /**
     * Update.
     *
     * @param id     the id
     * @param entity the entity
     */
// The method to update a document by their id.
    public void update(ObjectId id, T entity){
        Document doc = entity.toDocument();
        this.collection.replaceOne(eq("_id",id),doc); //Replace a document content.
    }

    /**
     * Delete.
     *
     * @param id the id
     */
// The method to delete a document by their id.
    public void delete(ObjectId id){
        this.collection.deleteOne(eq("_id",id)); // Delete a document.
    }

    /**
     * Convert t.
     *
     * @param doc the doc
     * @return the t
     */
// Abstract method to convert a document
    protected abstract T convert(Document doc);
}

/*
* The repository class contains all the implementation logic for the most basic CRUD operations
* since almost all entities will share similar logic. It is implemented with generics which is a feature by
* almost all languages that support OOP. It is a feature that enables the creation of classes, interfaces, and
* methods that can operate on different data types without requiring the exact type to be specified in
* advance.
*/