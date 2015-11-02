package crawler;

import java.io.*;
import java.util.Date;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import com.google.gson.Gson;


public class TwitterGetter {
		
	public static BufferedWriter openFile(String filename) {
		FileWriter fWriter = null;
		try {
			fWriter = new FileWriter(filename);

		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
		BufferedWriter bWriter = new BufferedWriter(fWriter);
		return bWriter;
	}
	
	public static int writeTweetToFile(BufferedWriter bWriter, String Tweet) {
		try {
			bWriter.write(Tweet);
		}
		catch(IOException ex) {
            System.out.println("Error writing to file");
            return 0;
		}
		return Tweet.length();
	}
		
	public static ConfigurationBuilder setAuth() {
		String[] authKeys = auth.authKeys();
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
			.setOAuthConsumerKey(authKeys[0])
			.setOAuthConsumerSecret(authKeys[1])
			.setOAuthAccessToken(authKeys[2])
			.setOAuthAccessTokenSecret(authKeys[3]);
		return cb;
	}
	
	public static StatusListener createTweetListener(){
		StatusListener tweetListener = new StatusListener() {
			boolean test = true;
	        public void onStatus(Status status) {
	        		Tweet tweet = new Tweet(status);
	        		if(test == true) {
	        			Gson gson = new Gson();
	        			String tweetJson = gson.toJson(tweet);
	        			System.out.println(tweetJson);
	        			test = false;
	        		}
	        }
	        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
	        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
			public void onScrubGeo(long arg0, long arg1) {}
			public void onStallWarning(StallWarning arg0) {}
	        public void onException(Exception ex) {
	            ex.printStackTrace();
	        }
	
		};
		return tweetListener;
	}
	
	public static void main(String[] args) throws TwitterException, IOException{
			 
		ConfigurationBuilder cb = setAuth();
	
	    StatusListener listener = createTweetListener();
	    TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
	    
	    twitterStream.addListener(listener);
	    //FilterQuery tweetFilterQuery = new FilterQuery();
	    //tweetFilterQuery.language(new String[]{"en"});
	    //twitterStream.filter(tweetFilterQuery);
	    
	    
	    twitterStream.sample();
	    
	    try {
	    	Thread.sleep(5000L);
	    	System.out.println("Shutting Down");
	    	twitterStream.cleanUp();
	    	twitterStream.shutdown();
	    }
	    catch(Exception e) {
	    	twitterStream.cleanUp();
	    	twitterStream.shutdown();
	    }
	}
}