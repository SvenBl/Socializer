import twitter4j.*;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static java.lang.Long.parseLong;

public class TwitterClient extends SocialNetworkClient{
    private Twitter twitter;
    private User userID;

    //statistics
    private int followerCount;
    private int followingCount;
    private int postCount;
    private int commentsCount;
    private int likesCount;
    private int likedCount;
    private int mentionCount;
    private int retweetCount;


    //config variables
    private Float filterMinRatio;
    private Float filterMaxRatio;
    private int filterMinPosts;
    private int filterLastPostInDays;
    private int unfollowAfterDays;

    //id lists
    private List<String> followerList;
    private List<String> followingList;
    private List<String> toFollowList;

    public TwitterClient(Float minRatio, Float maxRatio, int minPosts, int lastPostInDays, int unfollowAfterDays) {
        super(Network.TWITTER);
        try {
            this.twitter = TwitterFactory.getSingleton();
            this.userID = twitter.showUser(twitter.getId());

            //get config variables
            this.filterMinRatio = minRatio;
            this.filterMaxRatio = maxRatio;
            this.filterMinPosts = minPosts;
            this.filterLastPostInDays = lastPostInDays;
            this.unfollowAfterDays = unfollowAfterDays;

            //set instance variables
            setAll();

            //update dbs
            updateDBs();


        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    public void updateDBs(){
        //update old db
        updateSocialDB(this.followerList);

        //update new db and unfollow users who didn't follow back
        checkFollowers(this.followerList);
        List<String> toDeleteList = getToUnfollowUsers(this.unfollowAfterDays);
        unfollowUsers(toDeleteList);

        //unfollow users who unfollowed and delete them from db
        unfollowUsers(getUserListNotFollow());
        deleteNotFollower(getUserListNotFollow());

    }

    public void setAll(){
        setFollowerCount();
        setFollowingCount();
        setPostCount();
        setLikedCount();
        setMentionCount();
        setFollowerList();
        setFollowingList();
        setLikesCount();
        setRetweetCount();
    }

    //getter
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
        return 0;
    }

    @Override
    public int getLikesCount() {
        return this.likesCount;
    }

    @Override
    public int getLikedCount() {
        this.likedCount = this.userID.getFavouritesCount();
        return this.likedCount;
    }

    @Override
    public int getMentionCount(){
        return this.mentionCount;
    }

    @Override
    public int getRetweetCount() {
        return this.retweetCount;
    }

    @Override
    public List<String> getFollowerList() {
        return this.followerList;
    }

    @Override
    public List<String> getFollowingList() {
        return this.followingList;
    }

    private List<String> getFollowerByAccount(String name){
        List<String> followerList = new ArrayList<String>();
        try {
            long cursor = -1;
            IDs ids;
                ids = this.twitter.getFollowersIDs(name, cursor);
                for(long id: ids.getIDs()) {
                    followerList.add(String.valueOf(id));
                }
        } catch(TwitterException te) {
            te.printStackTrace();
        }
        return followerList;
    }

    private ResponseList<Status> getTweetsByUser(String userid){
        ResponseList<Status> tweets = null;
        try {
            tweets = this.twitter.getUserTimeline(parseLong(userid));
            return tweets;
        } catch (TwitterException e) {
            //e.printStackTrace();
            return tweets;
        }
    }

    private float getFollowerFollowingRatio(User user){
        float follower = user.getFollowersCount();
        float following = user.getFriendsCount();
        return follower/following;
    }

    private String getScreenNameByID(String userid){
        String screenName = "";
        try {
            User user = twitter.showUser(parseLong(userid));
            screenName = user.getScreenName();
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return screenName;
    }


    //setter
    public void setFollowerList(){
        this.followerList = new ArrayList<String>();
        try {
            long[] ids = this.twitter.getFollowersIDs(-1).getIDs();
            for (long id : ids) {
                this.followerList.add(String.valueOf(id));
            }
        } catch(TwitterException te) {
            te.printStackTrace();
        }
    }

    public void setFollowingList(){
        this.followingList = new ArrayList<String>();
        try {
            long[] ids = this.twitter.getFriendsIDs(-1).getIDs();
            for (long id : ids) {
                this.followingList.add(String.valueOf(id));
            }
        } catch(TwitterException te) {
            te.printStackTrace();
        }
    }

    public void setFollowerCount() {
        this.followerCount = this.userID.getFollowersCount();
    }

    public void setFollowingCount() {
        this.followingCount = this.userID.getFriendsCount();
    }

    public void setPostCount() {
        this.postCount = userID.getStatusesCount();
    }

    public void setCommentsCount() {
        int page = 1;
        int count = 75;
        this.commentsCount = 0;
        try {
            do {
                ResponseList<Status> statuses = this.twitter.timelines().getUserTimeline(new Paging(page, count));
                for (Status status : statuses) {
                    this.commentsCount += status.getRetweetCount();
                }
                page++;
            } while(this.userID.getStatusesCount() > (page * count));
        } catch(TwitterException te) {
            te.printStackTrace();
        }
    }

    public void setLikesCount() {
        List<Status> statuses;
        try {
            int page = 1;
            int count = 200;
            statuses = this.twitter.getUserTimeline(new Paging(page,count));
            do{
                for (Status statuse : statuses) {
                    this.likesCount += statuse.getFavoriteCount();
                }
                page++;
                statuses = this.twitter.getUserTimeline(new Paging(page,count));
                if(statuses.size()< count){
                    for (Status statuse : statuses) {
                        this.likesCount += statuse.getFavoriteCount();
                    }
                }
            }while(statuses.size()==count);

        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    public void setRetweetCount() {
        int page = 1;
        int count = 75;
        ResponseList<Status> retweets;
        try {
            retweets = twitter.getRetweetsOfMe(new Paging(page, count));
            do{
                this.retweetCount += retweets.size();
                page++;
                retweets = this.twitter.getRetweetsOfMe(new Paging(page, count));
                if(retweets.size()< count){
                    this.retweetCount +=retweets.size();
                }
            }while(retweets.size() == count);

        } catch (TwitterException e) {
            e.printStackTrace();
        }

    }

    public void setLikedCount() {
        this.likedCount = userID.getFavouritesCount();
    }

    public void setMentionCount() {
        try {
            this.mentionCount = twitter.getMentionsTimeline().size();
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setToFollowList(String username, int size){
        System.out.println("Create toFollowList...");
        List<String> ressources = getFollowerByAccount(username);
        this.toFollowList = new ArrayList<String>();
        Date currentDate = new Date();
        int rateLimitCheck = 179;
        for (String item : ressources) {
            if (this.toFollowList.size() >= size){
                break;
            }
            try {
                Date date = new Date();
                ResponseList<Status> tweets = getTweetsByUser(item);
                if(tweets!=null){
                    System.out.print(".");
                    User user = twitter.showUser(parseLong(item));
                    Float ratio = getFollowerFollowingRatio(user);
                    //filters
                    if (!this.followingList.contains(item)
                            && user.getStatusesCount() >= this.filterMinPosts
                            && getDifferenceDays(tweets.get(0).getCreatedAt(), currentDate) <= this.filterLastPostInDays
                            && ratio > this.filterMinRatio && ratio < this.filterMaxRatio) {
                        this.toFollowList.add(item);
                        System.out.println("\nAdded: " + item + " to toFollowList [" + this.toFollowList.size() + "] at "
                        + date);
                    }
                }
                rateLimitCheck--;
                if(rateLimitCheck ==0){
                    System.out.println("Wait 900 seconds until rate limit resets (" + date +")");
                    rateLimitCheck = 179;
                    TimeUnit.SECONDS.sleep(900);
                }
            } catch (TwitterException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //twitter actions
    private void followUser(String id){
        try {
            this.twitter.createFriendship(parseLong(id));

        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void followUsersWithOptions(boolean like, boolean comment){
        System.out.println("Follow users...");
        long wait = 0;
        int userFollowed = 0;
        int counter = 0;
        Iterator<String> i = this.toFollowList.iterator();
        int amount = this.toFollowList.size();
        while (i.hasNext()){
            Date date = new Date();
            String id = i.next();
            followUser(id);
            if(like){
                likeFirstPostByUser(id);
                wait = 300;
            }
            if(comment){
                commentFirstPostByUser(id);
                wait = 900;
            }
            Random rand = new Random();
            int  n = rand.nextInt(60);
            addFollowingUserToDB(id, like, comment);
            userFollowed++;
            System.out.println("You followed: " + id + " " + userFollowed + "/" + amount + " at " + date);
            i.remove();
            if(counter >= amount){
                break;
            }
            try {
                System.out.println("Wait for " + (n+wait) + " seconds before following next user");
                TimeUnit.SECONDS.sleep(n + wait);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        setFollowingList();

    }

    private void likeFirstPostByUser(String id){
        try {
            ResponseList<Status> tweets = this.twitter.getUserTimeline(Long.parseLong(id));
            if(tweets.size()!=0) {
                for(int i = 0; i < tweets.size(); i++){
                    //get
                    if(!tweets.get(i).isRetweet() && !tweets.get(i).isFavorited()){
                        this.twitter.favorites().createFavorite(tweets.get(i).getId());
                        break;
                    }
                    //if only retweets -> use first
                    if(i == (tweets.size() -1) && !tweets.get(0).isFavorited()) {
                        this.twitter.favorites().createFavorite(tweets.get(0).getId());
                    }
                }
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    private void commentFirstPostByUser(String id){
        try {
            ResponseList<Status> tweets = this.twitter.getUserTimeline(Long.parseLong(id));
            Random rand = new Random();
            int  n = rand.nextInt(10);
            List<String> comments = new ArrayList<String>();
            comments.add("Nice");
            comments.add("Exactly");
            comments.add("You said it!");
            comments.add("Cool");
            comments.add("Wow");
            comments.add("My saying");
            comments.add("Amazing");
            comments.add("Interesting");
            comments.add("Nice !!");
            comments.add(":O");

            long tweetid = 0;
            int i = 0;
            while(tweetid==0){
                if(!tweets.get(i).isRetweet()){
                    tweetid = tweets.get(i).getId();
                }
                i++;
                if(i >= (tweets.size() -1)){
                    tweetid = tweets.get(0).getId();
                }
            }
            if(tweets.size()!=0){
                twitter.updateStatus(new StatusUpdate(comments.get(n)+ " @" + getScreenNameByID(id))
                        .inReplyToStatusId(tweetid));
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    private void checkRateLimit(){
        Map<String ,RateLimitStatus> rateLimitStatus = null;
        try {
            rateLimitStatus = twitter.getRateLimitStatus();
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        for (String endpoint : rateLimitStatus.keySet()) {
            RateLimitStatus status = rateLimitStatus.get(endpoint);
            //System.out.println("Endpoint: " + endpoint);
            //System.out.println(" Limit: " + status.getLimit());
            //System.out.println(" Remaining: " + status.getRemaining());
            //System.out.println(" ResetTimeInSeconds: " + status.getResetTimeInSeconds());
            //System.out.println(" SecondsUntilReset: " + status.getSecondsUntilReset());
            if(status.getRemaining()==0){
                try {
                    System.out.println("Wait " + status.getResetTimeInSeconds() + " seconds until rate limit resets");
                    TimeUnit.SECONDS.sleep(status.getResetTimeInSeconds());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void unfollowUsers(List<String> ids){
        try {
            for (String id : ids) {
                try {
                    this.twitter.destroyFriendship(parseLong(id));
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Unfollowed " + ids.size() + " users...");
        } catch (Exception e){
            System.out.println("No users to unfollow...");
        }


    }

    @Override
    public void postRandom() {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = null;
            conn = DriverManager.getConnection("jdbc:mysql://localhost/quotes","root", "");
            System.out.println("Connected to MySQL DB!");
            Statement stmt = conn.createStatement() ;
            String query = "SELECT quote,author FROM quotes\n" +
                    "ORDER BY RAND()\n" +
                    "LIMIT 1" ;
            ResultSet rs = stmt.executeQuery(query) ;
            String latestStatus = "";
            while (rs.next()){
                String quote = rs.getString("quote");
                String author = rs.getString("author");
                latestStatus = quote + " - " + author;
            }
            Date currentDate = new Date();
            System.out.println("Post: " + latestStatus + " at " + currentDate);
            twitter.updateStatus(latestStatus);
            conn.close();
        }
        catch(Exception e)
        {
            System.out.print("Do not connect to DB - Error:"+e);
        }
    }

}
