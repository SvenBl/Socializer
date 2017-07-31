import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Integer.parseInt;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

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

        try {

            input = new FileInputStream(config);
            prop.load(input);

            //get the property value and save it
            minPosts = parseInt(prop.getProperty("MinPosts"));
            lastPost = parseInt(prop.getProperty("LastPost"));
            unfollow = parseInt(prop.getProperty("Unfollow"));
            minRatio = Float.parseFloat(prop.getProperty("MinRatio"));
            maxRatio = Float.parseFloat(prop.getProperty("MaxRatio"));
            final int followPerDay = parseInt(prop.getProperty("FollowPerDay"));
            final String toFollowListString = prop.getProperty("ToFollow");
            final List<String> toFollowList = Arrays.asList(toFollowListString.split(","));
            final boolean like = Boolean.valueOf(prop.getProperty("Like"));
            final boolean comment = Boolean.valueOf(prop.getProperty("Comment"));


            final SocialNetworkClient twitter = new TwitterClient(minRatio,maxRatio,minPosts,lastPost,unfollow);


            //twitter output
            printAllTwitter(twitter);

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

            // Create a calendar instance
            Calendar calendar1 = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();
            Calendar calendar3 = Calendar.getInstance();
            Calendar calendar4 = Calendar.getInstance();

            // Set times of execution.
            //1-3 for posts
            calendar1.set(Calendar.HOUR, 7);
            calendar1.set(Calendar.MINUTE, 30);
            calendar1.set(Calendar.SECOND, 0);
            calendar1.set(Calendar.AM_PM, Calendar.AM);

            calendar2.set(Calendar.HOUR, 5);
            calendar2.set(Calendar.MINUTE, 0);
            calendar2.set(Calendar.SECOND, 0);
            calendar2.set(Calendar.AM_PM, Calendar.PM);

            calendar3.set(Calendar.HOUR, 8);
            calendar3.set(Calendar.MINUTE, 0);
            calendar3.set(Calendar.SECOND, 0);
            calendar3.set(Calendar.AM_PM, Calendar.PM);

            //time for follow/unfollow
            calendar4.set(Calendar.HOUR, 1);
            calendar4.set(Calendar.MINUTE, 45);
            calendar4.set(Calendar.SECOND, 0);
            calendar4.set(Calendar.AM_PM, Calendar.AM);

            Long currentTime = new Date().getTime();

            // Check if current time is greater than our calendar's time. If So,
            // then change date to one day plus. As the time already pass for
            // execution.
            if (calendar1.getTime().getTime() < currentTime) {
                calendar1.add(Calendar.DATE, 1);
            }
            if (calendar2.getTime().getTime() < currentTime) {
                calendar2.add(Calendar.DATE, 1);
            }
            if (calendar3.getTime().getTime() < currentTime) {
                calendar3.add(Calendar.DATE, 1);
            }
            if (calendar4.getTime().getTime() < currentTime) {
                calendar4.add(Calendar.DATE, 1);
            }

            // Calendar is scheduled for future; so, it's time is higher than
            // current time.
            long startScheduler1 = calendar1.getTime().getTime() - currentTime;
            long startScheduler2 = calendar2.getTime().getTime() - currentTime;
            long startScheduler3 = calendar3.getTime().getTime() - currentTime;
            long startScheduler4 = calendar4.getTime().getTime() - currentTime;

            // Executor is Runnable. The code which you want to run periodically.
            Runnable postTask = new Runnable() {
                public void run() {
                    twitter.postRandom();
                }
            };

            Runnable followTask = new Runnable() {
                public void run() {
                    twitter.setAll();
                    twitter.updateDBs();

                    //twitter output
                    printAllTwitter(twitter);

                    Random rand = new Random();
                    int  n = rand.nextInt(toFollowList.size());
                    String toFollow = toFollowList.get(n);
                    twitter.setToFollowList(toFollow, followPerDay);
                    twitter.followUsersWithOptions(like, comment);
                }
            };


            // Get an instance of scheduler
            final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
            // execute scheduler at fixed time.

            long dayMilli = TimeUnit.DAYS.toMillis(1);

            scheduler.scheduleAtFixedRate(postTask, startScheduler1, dayMilli, MILLISECONDS);
            scheduler.scheduleAtFixedRate(postTask, startScheduler2, dayMilli, MILLISECONDS);
            scheduler.scheduleAtFixedRate(postTask, startScheduler3, dayMilli, MILLISECONDS);
            scheduler.scheduleAtFixedRate(followTask, startScheduler4, dayMilli, MILLISECONDS);


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

    public static void printAllTwitter(SocialNetworkClient twitter){
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
    }


}

