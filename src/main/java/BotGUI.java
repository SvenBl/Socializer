import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Integer.parseInt;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Created by disas on 02.08.2017.
 */
public class BotGUI {
    private JPanel panelMain;
    private JLabel twitterlabel;
    private JTextField consumerKeyField;
    private JTextField consumerSecretField;
    private JTextField accessTokenField;
    private JTextField accessTokenSecretField;
    private JButton changePropertiesButton;
    private JTextField minRatioField;
    private JTextField maxRatioField;
    private JTextField lastPostField;
    private JTextField followPerDayField;
    private JTextField followListField;
    private JButton changeConfigButton;
    private JTextField minPostsField;
    private JTextField unfollowField;
    private JCheckBox commentCheckBox;
    private JCheckBox likeCheckBox;
    private JButton startBotButton;
    private JSpinner spinner1;

    private int minPosts;
    private int lastPost;
    private int unfollow;
    private Float minRatio;
    private Float maxRatio;
    private int followPerDay;
    private String toFollowListString;
    private boolean like;
    private boolean comment;

    private SocialNetworkClient twitter;
    
    private String configPath;
    private String propPath;


    public BotGUI() {
        spinner1 =new JSpinner( new SpinnerDateModel() );
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(spinner1, "HH:mm:ss");
        spinner1.setEditor(timeEditor);
        spinner1.setValue(new Date());

        Properties config = new Properties();
        Properties prop = new Properties();
        InputStream inputConfig = null;
        InputStream inputProp = null;

        configPath = new File("").getAbsolutePath() + "\\config\\config.txt";
        propPath = new File("").getAbsolutePath() + "\\config\\twitter4j.properties";


        try {

            inputConfig = new FileInputStream(configPath);
            inputProp = new FileInputStream(propPath);
            config.load(inputConfig);
            prop.load(inputProp);

            //get the property value and save it of config
            minPostsField.setText(config.getProperty("MinPosts"));
            lastPostField.setText(config.getProperty("LastPost"));
            unfollowField.setText(config.getProperty("Unfollow"));

            minRatioField.setText(config.getProperty("MinRatio"));
            maxRatioField.setText(config.getProperty("MaxRatio"));
            followPerDayField.setText(config.getProperty("FollowPerDay"));
            followListField.setText(config.getProperty("ToFollow"));
            boolean like = Boolean.valueOf(config.getProperty("Like"));
            likeCheckBox.setSelected(like);
            boolean comment = Boolean.valueOf(config.getProperty("Comment"));
            commentCheckBox.setSelected(comment);

            //get the property value and save it of twitter4j
            consumerKeyField.setText(prop.getProperty("oauth.consumerKey"));
            System.out.println();
            consumerSecretField.setText(prop.getProperty("oauth.consumerSecret"));
            accessTokenField.setText(prop.getProperty("oauth.accessToken"));
            accessTokenSecretField.setText(prop.getProperty("oauth.accessTokenSecret"));
        } catch (IOException ex){
            ex.printStackTrace();
        }
        changePropertiesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                BufferedWriter bwr = null;
                String sb = "";
                try {
                    bwr = new BufferedWriter(new FileWriter(new File(propPath)));
                    sb += ("oauth.consumerKey="+ consumerKeyField.getText() +"\n");
                    sb += ("oauth.consumerSecret="+ consumerSecretField.getText() +"\n");
                    sb += ("oauth.accessToken="+accessTokenField.getText() +"\n");
                    sb += ("oauth.accessTokenSecret="+accessTokenSecretField.getText());

                    bwr.write(sb);
                    bwr.flush();
                    bwr.close();

                    JOptionPane.showMessageDialog(null,
                            "The twitter4j.properties file has been changed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                            "An error occured while writing twitter4j.properties", "Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        changeConfigButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                BufferedWriter bwr = null;
                String sb = "";
                try {
                    bwr = new BufferedWriter(new FileWriter(new File(configPath)));
                    System.out.println(minRatioField.getText());
                    sb += ("MinRatio="+ minRatioField.getText() +"\n");
                    sb += ("MaxRatio="+ maxRatioField.getText() +"\n");
                    sb += ("MinPosts="+minPostsField.getText() +"\n");
                    sb += ("LastPost="+lastPostField.getText() +"\n");
                    sb += ("Unfollow="+unfollowField.getText() +"\n");
                    sb += ("FollowPerDay="+followPerDayField.getText() +"\n");
                    sb += ("Like="+ likeCheckBox.isSelected() +"\n");
                    sb += ("Comment="+ commentCheckBox.isSelected() + "\n");
                    sb += ("ToFollow=" + followListField.getText());

                    bwr.write(sb);
                    bwr.flush();
                    bwr.close();
                    JOptionPane.showMessageDialog(null,
                            "The config.txt file has been changed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                            "An error occured while writing config.txt", "Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        // TODO: 02.08.2017 add calendar for times and craate bot object 
        startBotButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {


                //get bot input
                InputStream inputConfig = null;
                InputStream inputProp = null;
                Properties config = new Properties();
                Properties prop = new Properties();

                try {
                    inputConfig = new FileInputStream(configPath);
                    inputProp = new FileInputStream(propPath);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                    return;
                }
                try {
                    config.load(inputConfig);
                    prop.load(inputProp);
                } catch (IOException e1) {
                    e1.printStackTrace();
                    return;
                }

                //config file
                int minPosts = parseInt(config.getProperty("MinPosts"));
                int lastPost = parseInt(config.getProperty("LastPost"));
                int unfollow = parseInt(config.getProperty("Unfollow"));
                Float minRatio = Float.parseFloat(config.getProperty("MinRatio"));
                Float maxRatio = Float.parseFloat(config.getProperty("MaxRatio"));
                final int followPerDay = parseInt(config.getProperty("FollowPerDay"));
                String toFollowListString = config.getProperty("ToFollow");
                toFollowListString.replaceAll("\\s", "");
                final List<String> toFollowList = Arrays.asList(toFollowListString.split(","));
                final boolean like = Boolean.valueOf(config.getProperty("Like"));
                final boolean comment = Boolean.valueOf(config.getProperty("Comment"));

                //prop file
                String consumerKey = prop.getProperty("oauth.consumerKey");
                String consumerSecret = prop.getProperty("oauth.consumerSecret");
                String accessToken = prop.getProperty("oauth.accessToken");
                String accessTokenSecret = prop.getProperty("oauth.accessTokenSecret");


                //create bot
                twitter = new TwitterClient(minRatio,maxRatio,minPosts,lastPost,unfollow, consumerKey, consumerSecret,
                        accessToken, accessTokenSecret);

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
                        //printAllTwitter(twitter);

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

            }
        });
    }

    public JPanel getPanelMain() {
        return panelMain;
    }

    public static void main(String[]args){


        JFrame frame = new JFrame("Bot");
        frame.setContentPane(new BotGUI().getPanelMain());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        String filepath = new File("").getAbsolutePath();
        System.out.println(filepath);

    }
}

