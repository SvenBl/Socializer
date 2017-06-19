
public class Socializer {

    public static void main(String args[]) throws Exception {


        //SocialNetworkClient instagram = new InstagramClient();
        SocialNetworkClient twitter = new TwitterClient();

        /*
        //instagram
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

        //twitter
        System.out.println("Twitter:");
        System.out.println("Follower: " + twitter.getFollowerCount());
        System.out.println("Following: " + twitter.getFollowingCount());
        System.out.println("Posts: " + twitter.getPostCount());
        System.out.println("Likes: " + twitter.getLikesCount());
        System.out.println("Liked: " + twitter.getLikedCount());
        System.out.println("Comments: " + twitter.getCommentsCount());
        System.out.println("Retweets: " + twitter.getRetweetCount());
        System.out.println("Mentions: " + twitter.getMentionCount());
        System.out.println("Don't follow anymore: " + twitter.getNotFollow());
        System.out.println("List: " + twitter.getUserListNotFollow());
    }


}

