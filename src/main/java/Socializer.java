import com.mongodb.*;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

/**
 * Created by disas on 05.06.2017.
 */
public class Socializer {

    public static void main(String args[]) throws Exception {

        //String accessToken = getToken();
        String accessToken = "5455891555.ac2549d.e49aba4b9d694ae79c5bf5ab474ff5a0";


        JSONArray follows = getJSONArray(new URL("https://api.instagram.com/v1/users/self/follows?access_token="
                + accessToken));
        JSONArray follower = getJSONArray(new URL("https://api.instagram.com/v1/users/self/follows?access_token="
                + accessToken));
        JSONArray likes = getJSONArray(new URL("https://api.instagram.com/v1/users/self/media/liked?access_token="
                + accessToken));
        JSONArray recentMedia = getJSONArray(new URL("https://api.instagram.com/v1/users/self/media/recent/?access_token="
                + accessToken));

        JSONObject currentObject = null;

        int likesCount = 0;
        int commentsCount = 0;
            for(int i = 0; i<recentMedia.size()-1;i++) {
                currentObject = (JSONObject) recentMedia.get(i);
                JSONObject likesArray = (JSONObject) currentObject.get("likes");
                JSONObject commentsArray = (JSONObject) currentObject.get("comments");
                likesCount += parseInt(likesArray.get("count").toString());
                commentsCount += parseInt(commentsArray.get("count").toString());
            }

        MongoClient mongoClient = new MongoClient();
        DB db = mongoClient.getDB("mydb");
        DBCollection coll = db.getCollection("testCollection");

        //insert followers
        for(int i = 0; i <= follower.size()-1;i++) {
            JSONObject followerID = (JSONObject) follower.get(i);
            BasicDBObject query = new BasicDBObject("id", followerID.get("id"));

            DBCursor cursor = coll.find(query);

            try {

                if(cursor.hasNext()) {
                    cursor.next().put("follows", "true");
                }else{
                    BasicDBObject doc = new BasicDBObject("id", followerID.get("id"))
                            .append("username", followerID.get("username"))
                            .append("follows", "true");
                    coll.insert(doc);
                }
            } finally {
                cursor.close();
            }
        }


        //get the user who don't follow anymore
        BasicDBObject query = new BasicDBObject("follows", "false");
        DBCursor cursor = coll.find(query);
        int notFollow = 0;
        try {
            while(cursor.hasNext()) {
                notFollow++;
                System.out.println(cursor.next().get("username"));
            }

        } finally {
            cursor.close();
        }


        System.out.println("Follows:" + follows.size());
        System.out.println("Follower: " + follower.size());
        System.out.println("Liked by User: " + likes.size());
        System.out.println("Likes on all Media: " + likesCount);
        System.out.println("Comments on all Media: " + commentsCount);
        System.out.println("Don't follow anymore: " + notFollow);
    }


    public static JSONArray getJSONArray(URL request){

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


    public static String getToken() throws MalformedURLException {
        String clientID = "ac2549ddc3b547b4a039c71791702921";
        String redirectURI = "http://localhost:3000";

        URL url = new URL("https://api.instagram.com/oauth/authorize/?client_id=" + clientID + "&redirect_uri=" + redirectURI +
                "&response_type=token&scope=public_content+follower_list+comments+relationships+likes");


        System.out.println(url);
        Scanner sc = new Scanner(System.in);


        System.out.print("Your Access-Token : ");
        String accessToken = sc.next();
        return accessToken;



    }

}

