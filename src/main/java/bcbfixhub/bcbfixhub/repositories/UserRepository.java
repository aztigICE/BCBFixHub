package bcbfixhub.bcbfixhub.repositories;

import bcbfixhub.bcbfixhub.models.User;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

/**
 * The type User repository.
 * We can now create the repository class for the users. Create a class named UserRepository
 * and extend the BaseRepository. Since the BaseRepository class requires the subclass to
* specify what type of class that it will be handling, you need to provide the User class.
*/
public class UserRepository extends BaseRepository<User> {

    /**
     * Instantiates a new User repository.
     * Here we initialize what we didn't in base. To call out the collection from the Database
     */
    public UserRepository() {
        super();
        initCollection("users"); // Method from BaseRepository.
    }

    // Here we make use of the abstract method from earlier.
    @Override
    protected User convert(Document doc) {
        User user = new User();
        user.setId(doc.getObjectId("_id"));
        user.setUsername(doc.getString("username"));
        user.setEmail(doc.getString("email"));
        user.setPhone(doc.getString("phone"));
        user.setPassword(doc.getString("password"));
        return user;
    }

    /**
     * Find by email user.
     *
     * @param email the email
     * @return the user
     */
    public User findByEmail(String email) {
        Document doc = this.collection.find(eq("email", email)).first();
        if (doc == null) {
            return null;
        }
        return this.convert(doc);
    }
}
