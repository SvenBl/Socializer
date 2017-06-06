import com.mongodb.*;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

/**
 * Created by disas on 05.06.2017.
 */
public class Socializer {

    public static void main(String args[]) throws Exception {

        SocialDB socialDB = new SocialDB("mydb");
        Instagram instagram = new Instagram(socialDB);
        System.out.println(instagram.getInstagramData());


    }

}

