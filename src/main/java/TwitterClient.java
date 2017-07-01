import twitter4j.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.lang.Long.parseLong;

public class TwitterClient extends SocialNetworkClient{
    private Twitter twitter;
    private User userID;
    private int followerCount;
    private int followingCount;
    private int postCount;
    private int commentsCount;
    private int likesCount;
    private int likedCount;
    private int mentionCount;
    private int retweetCount;

    private List<String> followerList;
    private List<String> followingList;
    private List<String> toFollowList;

    public TwitterClient() {
        super(Network.TWITTER);
        try {
            this.twitter = TwitterFactory.getSingleton();
            this.userID = twitter.showUser(twitter.getId());

            //setFollowerCount();
            //setFollowingCount();
            //setPostCount();
            //setLikedCount();
            //setMentionCount();
            setFollowerList();
            setFollowingList();
            //setLikesCount();
            //setRetweetCount();


            //setToFollowList("Formula1NG");
            //followUsersWithOptions(30,false, false);
            //followUsersWithOptions( 10, true, false);
            //followUsersWithOptions(10, true, true);
            checkFollowers(this.followerList);
            showStatistics();



            //updateSocialDB(this.followerList);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
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

    public List<String> getFollowerByAccount(String name){
        List<String> followerList = new ArrayList<String>();
        int counter = 0;
        try {
            long cursor = -1;
            IDs ids;
            do {
                ids = this.twitter.getFollowersIDs(name, cursor);
                for(long id: ids.getIDs()) {
                    followerList.add(String.valueOf(id));
                }
            } while((cursor = ids.getNextCursor()) != 0 && followerList.size()==150);
        } catch(TwitterException te) {
            te.printStackTrace();
        }
        return followerList;
    }

    public void followUser(String id){
        try {
            this.twitter.createFriendship(parseLong(id));

        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    public void followUsersWithOptions(int amount, boolean like, boolean comment){
        for (int i = 0; i < amount; i++) {
            followUser(this.toFollowList.get(i));
            if(like){
                likeFirstPostByUser(this.toFollowList.get(i));
            }
            if(comment){
                commentFirstPostByUser(this.toFollowList.get(i));
            }
            this.toFollowList.remove(i);
            Random rand = new Random();
            int  n = rand.nextInt(50) + 30;
            addFollowingUserToDB(toFollowList.get(i), like, comment);
            try {
                TimeUnit.SECONDS.sleep(n);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            checkRateLimit();
        }
        setFollowingList();

    }

    public void likeFirstPostByUser(String id){
        try {
            ResponseList<Status> tweets = this.twitter.getUserTimeline(Long.parseLong(id));
            if(tweets.size()!=0) {
                if(!tweets.get(0).isFavorited()) {
                    this.twitter.favorites().createFavorite(tweets.get(0).getId());
                }
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void commentFirstPostByUser(String id){
        try {
            ResponseList<Status> tweets = this.twitter.getUserTimeline(Long.parseLong(id));
            Random rand = new Random();
            int  n = rand.nextInt(10);
            List<String> comments = new ArrayList<String>();
            comments.add("Nice");
            comments.add("Exactly");
            comments.add("You say it!");
            comments.add("Cool");
            comments.add("Wow");
            comments.add("My saying");
            comments.add("Amazing");
            comments.add("Interesting");
            comments.add("Nice !!");
            comments.add(":O");

            if(tweets.size()!=0){
                twitter.updateStatus(new StatusUpdate(comments.get(n)+ " @" + getScreenNameByID(id))
                    .inReplyToStatusId(tweets.get(0).getId()));
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
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

    public void setToFollowList(String username){
        List<String> ressources = getFollowerByAccount(username);
        this.toFollowList = new ArrayList<String>();

        Date currentDate = new Date();
        for (String item : ressources) {
            try {
                ResponseList<Status> tweets = getTweetsByUser(item);
                User user = twitter.showUser(parseLong(item));
                Float ratio = getFollowerFollowingRatio(user);
                if (!this.followingList.contains(item)
                        && this.toFollowList.size() < 10
                        && user.getStatusesCount() >= 10
                        && getDifferenceDays(tweets.get(0).getCreatedAt(), currentDate) <= 3
                        && ratio > 0.8 && ratio < 1.2 ) {
                    System.out.println("test");
                    this.toFollowList.add(item);
                }
                checkRateLimit();
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }
    }

    public static long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public ResponseList<Status> getTweetsByUser(String userid){
        ResponseList<Status> tweets = null;
        try {
            tweets = this.twitter.getUserTimeline(parseLong(userid));
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return tweets;
    }

    public float getFollowerFollowingRatio(User user){
        float follower = user.getFollowersCount();
        float following = user.getFriendsCount();
        return follower/following;
    }



    public String getScreenNameByID(String userid){
        String screenName = "";
        try {
            User user = twitter.showUser(parseLong(userid));
            screenName = user.getScreenName();
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return screenName;
    }

    public void checkRateLimit(){
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
                    TimeUnit.SECONDS.sleep(status.getResetTimeInSeconds());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
