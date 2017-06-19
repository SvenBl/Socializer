import com.mongodb.*;

import java.util.ArrayList;
import java.util.List;

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
    public abstract int getMentionCount();
    public abstract int getRetweetCount();
    public abstract List<String> getFollowerList();
    public abstract List<String> getFollowingList();

    public abstract void setFollowerCount();
    public abstract void setFollowingCount();
    public abstract void setPostCount();
    public abstract void setCommentsCount();
    public abstract void setLikesCount();
    public abstract void setLikedCount();
    public abstract void setMentionCount();
    public abstract void setRetweetCount();
    public abstract void setFollowerList();
    public abstract void setFollowingList();

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

    public void updateSocialDB(List<String> followerList){
        this.coll = this.db.getCollection("follower_" + this.network.toString().toLowerCase());
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

    public void addFollowingUsersToDB(List<String> ids, int amount, boolean like, boolean comment){
        this.coll = this.db.getCollection("following_" + this.network.toString().toLowerCase());
        this.coll.drop();
        DBCursor cursor;
        for(int i = 0; i < amount;i++) {
            String followerID = ids.get(i);
            BasicDBObject query = new BasicDBObject("id", followerID);
            cursor = coll.find(query);

            try {
                if(!cursor.hasNext()) {
                    BasicDBObject doc = new BasicDBObject("id", followerID)
                            .append("follows", false).append("like", like)
                            .append("comment", comment);
                    coll.insert(doc);

                }
            } finally {
                cursor.close();
            }
        }
    }

    public void showStatistics(){
        this.coll = this.db.getCollection("following_" + this.network.toString().toLowerCase());
        DBCursor cursor = coll.find();
        System.out.println("Database: ");
        try {
            while(cursor.hasNext()) {
                System.out.println(cursor.next());
            }
        } finally {
            cursor.close();
        }
    }

    public void checkFollowers(List<String> followerIds){
        this.coll = this.db.getCollection("following_" + this.network.toString().toLowerCase());
        DBCursor cursor = coll.find();
        String id;
        try {
            while(cursor.hasNext()) {
                id = (String) cursor.next().get("id");
                if(followerIds.contains(id)){
                    coll.update(new BasicDBObject("id", id),
                            new BasicDBObject("$set", new BasicDBObject("follows", true)));
                }else{
                   coll.update(new BasicDBObject("id", id),
                            new BasicDBObject("$set", new BasicDBObject("follows", false)));
                }
            }
        } finally {
            cursor.close();
        }
    }

}
