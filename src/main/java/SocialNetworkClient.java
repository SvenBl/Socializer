import com.mongodb.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by disas on 08.06.2017.
 */
public abstract class SocialNetworkClient {
    private Network network;
    private DB db;
    private MongoClient mongoClient;
    private DBCollection coll;
    private List<String> userListNotFollow;
    private int notFollow;

    public enum Network {
        INSTAGRAM,
        TWITTER
    }
    public SocialNetworkClient(Network network){
        this.network = network;
        try {
            this.mongoClient = new MongoClient();
            this.db = mongoClient.getDB("socialdb");
            this.coll = this.db.getCollection("follower_" + this.network.toString().toLowerCase());
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public abstract int getFollowerCount();
    public abstract int getFollowingCount();
    public abstract int getPostCount();
    public abstract int getCommentsCount();
    public abstract int getLikesCount();
    public abstract int getLikedCount();

    private void setFollowingFalse(){
        DBCursor cursor = coll.find();
        try {

            while (cursor.hasNext()) {
                coll.update(new BasicDBObject("id", cursor.next().get("id")),
                        new BasicDBObject("$set", new BasicDBObject("follows", "false")));
            }
        }finally{
            cursor.close();
        }
    }

    private void insertFollowers(List<String> followerList){
        DBCursor cursor;
        for(int i = 0; i <= followerList.size()-1;i++) {
            String followerID = followerList.get(i);
            BasicDBObject query = new BasicDBObject("id", followerID);
            cursor = coll.find(query);

            try {

                if(cursor.hasNext()) {
                    coll.update(new BasicDBObject("id", cursor.next().get("id")),
                            new BasicDBObject("$set", new BasicDBObject("follows", "true")));
                }else{
                    BasicDBObject doc = new BasicDBObject("id", followerID)
                            .append("follows", "true");
                    coll.insert(doc);
                }
            } finally {
                cursor.close();
            }
        }
    }

    private void setNotFollower(){
        BasicDBObject query = new BasicDBObject("follows", "false");
        DBCursor cursor = coll.find(query);
        this.notFollow = 0;
        this.userListNotFollow = new ArrayList<String>();
        try {
            while(cursor.hasNext()) {
                this.notFollow++;
                this.userListNotFollow.add(cursor.next().get("id").toString());
            }

        } finally {
            cursor.close();
        }
    }

    protected void updateSocialDB(List<String> followerList){
        setFollowingFalse();
        insertFollowers(followerList);
        setNotFollower();
    }

    public int getNotFollow() {
        return notFollow;
    }

    public List<String> getUserListNotFollow(){
        return userListNotFollow;
    }
}
