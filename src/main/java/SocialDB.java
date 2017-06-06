import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

/**
 * Created by disas on 06.06.2017.
 */
public class SocialDB {

    private DBCollection instagramCollection;
    private DB db;

    public SocialDB(String dbName){
        //initiate mongodb
        MongoClient mongoClient = new MongoClient();
        db = mongoClient.getDB(dbName);

    }

    public DBCollection getInstagramCollection(){
        instagramCollection = db.getCollection("instagramCollection");
        return instagramCollection;
    }
}
