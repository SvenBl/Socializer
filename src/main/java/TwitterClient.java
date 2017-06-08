/**
 * Created by disas on 08.06.2017.
 */
public class TwitterClient extends SocialNetworkClient{
    public TwitterClient() {
        super(Network.TWITTER);
    }

    @Override
    public int getFollowerCount() {
        return 0;
    }

    @Override
    public int getFollowingCount() {
        return 0;
    }

    @Override
    public int getPostCount() {
        return 0;
    }

    @Override
    public int getCommentsCount() {
        return 0;
    }

    @Override
    public int getLikesCount() {
        return 0;
    }

    @Override
    public int getLikedCount() {
        return 0;
    }
}
