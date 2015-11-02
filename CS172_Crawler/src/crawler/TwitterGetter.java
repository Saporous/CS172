package crawler;

import java.io.*;

import twitter4j.FilterQuery;
import twitter4j.GeoLocation;
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
	
	
	public static void main(String[] args) throws TwitterException, IOException{
		String[] authKeys = auth.authKeys();
		 
		ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true)
			.setOAuthConsumerKey(authKeys[0])
			.setOAuthConsumerSecret(authKeys[1])
			.setOAuthAccessToken(authKeys[2])
			.setOAuthAccessTokenSecret(authKeys[3]);	

			
	    StatusListener listener = new StatusListener(){
			int count = 0;
			File file = null;
			FileWriter outFile = null;
			
	        public void onStatus(Status status) {
	        	try{
	        		file = new File(Integer.toString(count) + ".txt");
	        		outFile = new FileWriter(file, true);
	        		GeoLocation loc = status.getGeoLocation();
	        		String[] links = new String[10];
	        		String link = status.getText();
	        		int i = 0;
	        		while(link.indexOf("https://t.co") != -1){
	        			links[i] = link.substring(link.indexOf("https://"), link.substring(link.indexOf("https://")).indexOf(" "));
	        			link = link.substring(link.indexOf("https://")+23);
	        			i++;
	        		}
	        		//if(loc != null){
	        			outFile.write(status.getUser().getName() + " : " + status.getGeoLocation() + " : " + 
	        					status.getText().replaceAll("\\n","").replaceAll("\\r","") + "\n");
	        		//}
        			outFile.close();
	        		if(file.length() > 10000000){
	        			count++;
	        		}   
	        	} 
	        	catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

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
	    TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
	    twitterStream.addListener(listener);
	    // sample() method internally creates a thread which manipulates TwitterStream and calls these adequate listener methods continuously.
	    twitterStream.sample();
	}
}