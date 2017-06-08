import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

/**
 * Created by disas on 06.06.2017.
 */
public class SocialDB {

    private DBCollection instagramCollection;
    private DBCollection twitterCollection;
    private DB db;

    public SocialDB(String dbName){
        MongoClient mongoClient = new MongoClient();
        this.db = mongoClient.getDB(dbName);

    }

    public DBCollection getInstagramCollection(){
        instagramCollection = this.db.getCollection("instagramCollection");
        return instagramCollection;
    }

    public DBCollection getTwitterCollection(){
        twitterCollection = this.db.getCollection("twitterCollection");
        return twitterCollection;
    }
}
