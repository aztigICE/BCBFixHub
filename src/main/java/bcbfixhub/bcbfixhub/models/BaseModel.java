package bcbfixhub.bcbfixhub.models;

// Imports for BSON
import org.bson.Document;
import org.bson.types.ObjectId;

/*
* Even though our code will get longer, Ice and Tan. I've added these necessary comments as to why we have these codes.
* -------------------------------------------------------------------------------------------------------------------
* MongoDB doesn’t have native Modeling feature, but you can implement your own modeling.
* Let’s start by defining an abstract base model class where a model class can convert itself to a MongoDB
* document. This class is not meant to be instantiated but rather would serve as a template to all models,
* the child models have to implement its own toDocument method. - From Week 13 pdf lesson Mongo Crud, and Models
*/
public abstract class BaseModel {
    //MongoDB document id, the ID in the database we have.
    protected ObjectId id;

    // Getter and Setters
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    // Abstract method to convert class variables into Document format --- see meaning in google, document format vs json.
    public abstract Document toDocument();
}
