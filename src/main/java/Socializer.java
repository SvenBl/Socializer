import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static java.lang.Integer.parseInt;

public class Socializer {

    public static void main(String args[]) throws Exception {

        File config = new File(args[0]);

        //load properties
        Properties prop = new Properties();
        InputStream input = null;
        Float minRatio = null;
        Float maxRatio = null;
        int minPosts;
        int lastPost;
        int unfollow;
        String toFollow = "";

        try {

            input = new FileInputStream(config);
            prop.load(input);

            //get the property value and save it
            minPosts = parseInt(prop.getProperty("MinPosts"));
            lastPost = parseInt(prop.getProperty("LastPost"));
            unfollow = parseInt(prop.getProperty("Unfollow"));
            minRatio = Float.parseFloat(prop.getProperty("MinRatio"));
            maxRatio = Float.parseFloat(prop.getProperty("MaxRatio"));
            toFollow = prop.getProperty("ToFollow");


            SocialNetworkClient twitter = new TwitterClient(minRatio,maxRatio,minPosts,lastPost,unfollow);

            //instagram
            /*
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
            System.out.println();
            */

            //twitter output
            System.out.println("Twitter:");
            System.out.println("Follower: " + twitter.getFollowerCount());
            System.out.println("Following: " + twitter.getFollowingCount());
            System.out.println("Posts: " + twitter.getPostCount());
            System.out.println("Likes: " + twitter.getLikesCount());
            System.out.println("Liked: " + twitter.getLikedCount());
            System.out.println("Retweets: " + twitter.getRetweetCount());
            System.out.println("Mentions: " + twitter.getMentionCount());
            System.out.println("Don't follow anymore: " + twitter.getNotFollow());
            System.out.println("List: " + twitter.getUserListNotFollow());
            System.out.println();
            System.out.println(twitter.showStatistics());
            twitter.setToFollowList(toFollow, parseInt(args[1]));
            twitter.followUsersWithOptions(Boolean.valueOf(args[2]), Boolean.valueOf(args[3]));


        } catch (IOException ex) {
            ex.printStackTrace();
        } finally{
            if(input!=null){
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }



    }


}

