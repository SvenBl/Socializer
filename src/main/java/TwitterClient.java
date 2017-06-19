import twitter4j.*;

import java.util.ArrayList;
import java.util.List;

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
            //setFollowingList();
            //setLikesCount();
            //setRetweetCount();
            List<String> userRescources = getFollowerByAccount("muxermann");

            followUsersWithOptions(userRescources, 1,false, false);

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
        try {
            long cursor = -1L;
            IDs ids;
            do {
                ids = this.twitter.getFollowersIDs(name, cursor);
                for(long id: ids.getIDs()) {
                    followerList.add(String.valueOf(id));
                }
            } while((cursor = ids.getNextCursor()) != 0);
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

    public void followUsersWithOptions(List<String> idList, int amount, boolean like, boolean comment){
        for (int i = 0; i < amount; i++) {
            followUser(idList.get(i));
            if(like){
                likeFirstPostByUser(idList.get(i));
            }
            if(comment){
                commentFirstPostByUser(idList.get(i));
            }
        }
        addFollowingUsersToDB(idList, amount, like, comment);
    }

    public void likeFirstPostByUser(String id){
        try {
            ResponseList<Status> tweets = this.twitter.getUserTimeline(Long.parseLong(id));
            if(!tweets.get(0).isFavorited()) {
                this.twitter.favorites().createFavorite(tweets.get(0).getId());
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    public void commentFirstPostByUser(String id){
        try {
            ResponseList<Status> tweets = this.twitter.getUserTimeline(Long.parseLong(id));
            int randomComment = (int)Math.random()*10+1;
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

            twitter.updateStatus(new StatusUpdate(comments.get(randomComment)+ "@" + twitter.getScreenName()).inReplyToStatusId(tweets.get(0).getId()));
        } catch (TwitterException e) {
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

}
