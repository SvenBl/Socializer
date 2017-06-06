/**
 * Created by disas on 05.06.2017.
 */
public class Socializer {

    public static void main(String args[]) throws Exception {

        SocialDB socialDB = new SocialDB("mydb");
        Instagram instagram = new Instagram(socialDB);
        System.out.println(instagram.printInstagramData());



    }

}

