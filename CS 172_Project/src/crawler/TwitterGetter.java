package crawler;

import java.io.*;


import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
//import twitter4j.*;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;


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
	        public void onStatus(Status status) {
	            System.out.println(status.getUser().getName() + " : " + status.getText());
	        }
	        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
	        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
	        public void onException(Exception ex) {
	            ex.printStackTrace();
	        }
			@Override
			public void onScrubGeo(long arg0, long arg1) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onStallWarning(StallWarning arg0) {
				// TODO Auto-generated method stub
				
			}	
		};
		return tweetListener;
	}
	
	public static void main(String[] args) throws TwitterException, IOException{
			 
		ConfigurationBuilder cb = setAuth();
	
	    StatusListener listener = createTweetListener();
	    TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
	    twitterStream.addListener(listener);
	    // sample() method internally creates a thread which manipulates TwitterStream and calls these adequate listener methods continuously.
	    twitterStream.sample();
	}
}