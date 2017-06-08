import twitter4j.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by disas on 05.06.2017.
 */
public class Socializer {

    public static void main(String args[]) throws Exception {

        SocialNetworkClient instagram = new InstagramClient();
        System.out.println("Instagram:");
        System.out.println("Follower: " + instagram.getFollowerCount());
        System.out.println("Following: " + instagram.getFollowingCount());
        System.out.println("Posts: " + instagram.getPostCount());
        System.out.println("Likes: " + instagram.getLikesCount());
        System.out.println("Liked: " + instagram.getLikedCount());
        System.out.println("Comments: " + instagram.getCommentsCount());
        System.out.println("Don't follow anymore: " + instagram.getNotFollow());
        System.out.println("List: " + instagram.getUserListNotFollow());

        /*

        Twitter twitter = TwitterFactory.getSingleton();
        User user = twitter.showUser(twitter.getId());



        String twitterScreenName = twitter.getScreenName();
        PagableResponseList<User> statuse = twitter.getFollowersList(twitterScreenName, -1, 100);
        for (User follower : statuse) {
            //System.out.println(follower.getScreenName());
        }



        List<String> followerIDs = new ArrayList<String>();
        try {
            long[] ids = twitter.getFollowersIDs(-1).getIDs();
            for(int i=0; i<ids.length; i++) {
                followerIDs.add(String.valueOf(ids[i]));
            }
        } catch(TwitterException te) {
            te.printStackTrace();
        }
        //System.out.println(followerIDs);
        System.out.println("Followers: " + user.getFollowersCount());
        System.out.println("Friends: " + user.getFriendsCount());
        System.out.println("Postcount: " + user.getStatusesCount());
        System.out.println("Likes: " + user.getFavouritesCount());
        System.out.println("Moments: " + user.getListedCount());
        Paging paging = new Paging(1, 75);
        System.out.println(twitter.getMentionsTimeline().size());
        System.out.println(twitter.getRetweetsOfMe(paging).size());
        ResponseList<Status>  test = twitter.getRetweetsOfMe(paging);
        ResponseList<Status> test2 = twitter.getRetweetsOfMe(new Paging(2,75));

        System.out.printf("size" + test.size());
        System.out.println(test.get(0));
        System.out.println(test2.size());

        List<Status> statuses = twitter.getUserTimeline(new Paging(1,100));
        List<Status> statuses2 = twitter.getUserTimeline(new Paging(2,100));
        System.out.println("Showing home timeline.");
            System.out.println(statuses.get(0).getUser().getName() + ":" +
                    statuses.get(0).getText());
        System.out.println(statuses2.get(0).getUser().getName() + ":" +
                statuses2.get(0).getText());
        System.out.println(statuses.size());
        System.out.println(user.getStatusesCount());
        */
    }


}

