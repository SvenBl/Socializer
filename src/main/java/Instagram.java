import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

/**
 * Created by disas on 06.06.2017.
 */
public class Instagram {
    private String accessToken;

    private JSONArray follows;
    private JSONArray follower;
    private JSONArray likes;
    private JSONArray recentMedia;

    private int likesCount;
    private int commentsCount;
    private int notFollow;
    private List<String> userListNotFollow;

    private String instagramData;
    private DBCollection instagramColl;

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setFollows() throws MalformedURLException {
        JSONArray follows = getJSONArray(new URL("https://api.instagram.com/v1/users/self/follows?access_token="
                + accessToken));
        this.follows = follows;
    }
    public void setFollower() throws MalformedURLException {
        JSONArray follower = getJSONArray(new URL("https://api.instagram.com/v1/users/self/followed-by?access_token="
                + accessToken));
        this.follower = follower;
    }
    public void setLikes() throws MalformedURLException {
        JSONArray likes = getJSONArray(new URL("https://api.instagram.com/v1/users/self/media/liked?access_token="
                + accessToken));
        this.likes = likes;
    }
    public void setRecentMedia() throws MalformedURLException {
        JSONArray recentMedia = getJSONArray(new URL("https://api.instagram.com/v1/users/self/media/recent/?access_token="
                + accessToken));
        this.recentMedia = recentMedia;
    }
    public void setLikesCount() throws MalformedURLException {
        JSONObject currentObject = null;
        int likesCount = 0;
        for(int i = 0; i<recentMedia.size()-1;i++) {
            currentObject = (JSONObject) recentMedia.get(i);
            JSONObject likesArray = (JSONObject) currentObject.get("likes");
            likesCount += parseInt(likesArray.get("count").toString());
        }
        this.likesCount = likesCount;
    }
    public void setCommentsCount() throws MalformedURLException {
        JSONObject currentObject = null;
        int commentsCount = 0;
        for(int i = 0; i<recentMedia.size()-1;i++) {
            currentObject = (JSONObject) recentMedia.get(i);
            JSONObject commentsArray = (JSONObject) currentObject.get("comments");
            commentsCount += parseInt(commentsArray.get("count").toString());
        }
        this.commentsCount = commentsCount;
    }

    public Instagram(SocialDB socialDB) throws MalformedURLException {
        accessToken = "5455891555.ac2549d.e49aba4b9d694ae79c5bf5ab474ff5a0";

        instagramColl =  socialDB.getInstagramCollection();
        //setAccessToken();
        setFollows();
        setFollower();
        setRecentMedia();
        setLikesCount();
        setLikes();
        setCommentsCount();
        setFollowsFalse();
        insertFollowers();
        setNotFollower();

    }

    public String printInstagramData() throws MalformedURLException {
        instagramData = "Follows: " + follows.size()
                + "\nFollower: " + follower.size()
                + "\nLiked by User: " + likes.size()
                + "\nLikes on all Media: " + likesCount
                + "\nComments on all Media: " + commentsCount
                + "\nDon't follow anymore: " + notFollow
                + "\nList of Users who are not following anymore: " + userListNotFollow.toString()
        ;
        return instagramData;
    }

    private JSONArray getJSONArray(URL request){

        String dataJson = null;
        try {
            dataJson = IOUtils.toString(request);

            JSONObject dataJsonObject = (JSONObject) JSONValue.parseWithException(dataJson);
            JSONArray dataArray = (JSONArray) dataJsonObject.get("data");
            return dataArray;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setAccessToken() throws MalformedURLException {
        String clientID = "ac2549ddc3b547b4a039c71791702921";
        String redirectURI = "http://localhost:3000";

        URL url = new URL("https://api.instagram.com/oauth/authorize/?client_id=" + clientID + "&redirect_uri=" + redirectURI +
                "&response_type=token&scope=public_content+follower_list+comments+relationships+likes");


        System.out.println(url);
        Scanner sc = new Scanner(System.in);


        System.out.print("Your Access-Token : ");
        String accessToken = sc.next();
        this.accessToken = accessToken;
    }

    private void setFollowsFalse(){
        DBCursor cursor = instagramColl.find();
        try {

            while (cursor.hasNext()) {
                instagramColl.update(new BasicDBObject("id", cursor.next().get("id")),
                        new BasicDBObject("$set", new BasicDBObject("follows", "false")));
            }
        }finally{
            cursor.close();
        }
    }

    private void insertFollowers(){
        DBCursor cursor;
        for(int i = 0; i <= follower.size()-1;i++) {
            JSONObject followerID = (JSONObject) follower.get(i);
            BasicDBObject query = new BasicDBObject("id", followerID.get("id"));
            cursor = instagramColl.find(query);

            try {

                if(cursor.hasNext()) {
                    instagramColl.update(new BasicDBObject("id", cursor.next().get("id")),
                            new BasicDBObject("$set", new BasicDBObject("follows", "true")));
                }else{
                    BasicDBObject doc = new BasicDBObject("id", followerID.get("id"))
                            .append("username", followerID.get("username"))
                            .append("follows", "true");
                    instagramColl.insert(doc);
                }
            } finally {
                cursor.close();
            }
        }
    }

    private void setNotFollower(){
        BasicDBObject query = new BasicDBObject("follows", "false");
        DBCursor cursor = instagramColl.find(query);
        int notFollow = 0;
        List<String> userListNotFollow = new ArrayList<String>();
        try {
            while(cursor.hasNext()) {
                notFollow++;
                userListNotFollow.add(cursor.next().get("username").toString());
            }

        } finally {
            cursor.close();
        }
        this.userListNotFollow = userListNotFollow;
        this.notFollow = notFollow;
    }

}
