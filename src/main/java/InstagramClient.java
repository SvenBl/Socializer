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

public class InstagramClient extends SocialNetworkClient {
    private String accessToken;

    private int followerCount;
    private int followingCount;

    private int postCount;
    private int commentsCount;
    private int likesCount;
    private int likedCount;

    private List<String> followerList;
    private List<String> followingList;

    private JSONArray recentMedia;

    public InstagramClient() throws MalformedURLException {
        super(Network.INSTAGRAM);
        this.accessToken = "5455891555.ac2549d.e49aba4b9d694ae79c5bf5ab474ff5a0";
        //setAccessToken();
        setFollowerList();
        setFollowingList();
        setFollowerCount();
        setFollowingCount();

        setRecentMedia();
        setPostCount();
        setLikedCount();
        setLikesCount();
        setCommentsCount();

        updateSocialDB(this.followerList);
    }

    @Override
    public int getFollowerCount() {
        return followerCount;
    }

    @Override
    public int getFollowingCount() {
        return followingCount;
    }

    @Override
    public int getPostCount() {
        return postCount;
    }

    @Override
    public int getCommentsCount() {
        return commentsCount;
    }

    @Override
    public int getLikesCount() {
        return likesCount;
    }

    @Override
    public int getLikedCount() {
        return likedCount;
    }

    @Override
    public int getMentionCount() {
        return 0;
    }

    @Override
    public int getRetweetCount() {
        return 0;
    }

    @Override
    public List<String> getFollowerList() {
        return followerList;
    }

    @Override
    public List<String> getFollowingList() {
        return followingList;
    }

    @Override
    public void setFollowerList(){

        try {
            this.followerList = new ArrayList<String>();
            JSONArray jsonFollower = getJSONArray(new URL("https://api.instagram.com/v1/users/self/followed-by?access_token="
                    + this.accessToken));
            for (Object aJsonFollower : jsonFollower) {
                JSONObject followerID = (JSONObject) aJsonFollower;
                this.followerList.add(followerID.get("id").toString());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setFollowerCount(){
        this.followerCount = this.followerList.size();
    }

    @Override
    public void setFollowingList(){
        try {
            this.followingList = new ArrayList<String>();
            JSONArray jsonFollowing = getJSONArray(new URL("https://api.instagram.com/v1/users/self/follows?access_token="
                    + accessToken));
            for (Object aJsonFollowing : jsonFollowing) {
                JSONObject followerID = (JSONObject) aJsonFollowing;
                this.followingList.add(followerID.get("id").toString());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setFollowingCount(){
        this.followingCount = followingList.size();
    }

    private void setRecentMedia() throws MalformedURLException {
        this.recentMedia = getJSONArray(new URL("https://api.instagram.com/v1/users/self/media/recent/?access_token="
                + accessToken));
    }

    @Override
    public void setLikedCount(){
        try {
            this.likedCount = getJSONArray(new URL("https://api.instagram.com/v1/users/self/media/liked?access_token="
                    + this.accessToken)).size();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setLikesCount(){
        JSONObject currentObject;
        int likesCount = 0;
        for (Object aRecentMedia : this.recentMedia) {
            currentObject = (JSONObject) aRecentMedia;
            JSONObject likesArray = (JSONObject) currentObject.get("likes");
            likesCount += parseInt(likesArray.get("count").toString());
        }
        this.likesCount = likesCount;
    }

    @Override
    public void setCommentsCount(){
        JSONObject currentObject;
        int commentsCount = 0;
        for (Object aRecentMedia : recentMedia) {
            currentObject = (JSONObject) aRecentMedia;
            JSONObject commentsArray = (JSONObject) currentObject.get("comments");
            commentsCount += parseInt(commentsArray.get("count").toString());
        }
        this.commentsCount = commentsCount;
    }

    @Override
    public void setPostCount(){
        this.postCount = this.recentMedia.size();
    }

    private void setAccessToken() throws MalformedURLException {
        String clientID = "ac2549ddc3b547b4a039c71791702921";
        String redirectURI = "http://localhost:3000";
        URL url = new URL("https://api.instagram.com/oauth/authorize/?client_id=" + clientID + "&redirect_uri=" + redirectURI +
                "&response_type=token&scope=public_content+follower_list+comments+relationships+likes");

        System.out.println(url);
        Scanner sc = new Scanner(System.in);
        System.out.print("Your Access-Token : ");
        this.accessToken = sc.next();
    }

    private JSONArray getJSONArray(URL request){

        String dataJson;
        try {
            dataJson = IOUtils.toString(request);

            JSONObject dataJsonObject = (JSONObject) JSONValue.parseWithException(dataJson);
            return (JSONArray) dataJsonObject.get("data");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setMentionCount() {

    }

    @Override
    public void setRetweetCount() {

    }

    @Override
    public void setToFollowList(String username, int size) {

    }

    @Override
    public void followUsersWithOptions(boolean like, boolean comment) {

    }
}
