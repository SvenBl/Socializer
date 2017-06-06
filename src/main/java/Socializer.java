import twitter4j.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by disas on 05.06.2017.
 */
public class Socializer {

    public static void main(String args[]) throws Exception {

        /*
        SocialDB socialDB = new SocialDB("mydb");
        Instagram instagram = new Instagram(socialDB);
        System.out.println("Instagram data:");
        System.out.println(instagram.printInstagramData());

*/
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
        System.out.println(twitter.getMentionsTimeline().size());
        System.out.println(twitter.getRetweetsOfMe().size());
    }

}

