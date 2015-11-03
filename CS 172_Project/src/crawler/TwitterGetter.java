package crawler;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;


public class TwitterGetter {
	
	final static LinkedBlockingQueue<Status> statusQueue= new LinkedBlockingQueue<Status>(300);

	public static Writer[] createDirectoryAndFiles(int numFiles) {
		File dir = new File("./Tweets");
		dir.mkdir();
		String fileName = new String("tweet");
		String fileType = new String(".json");
		String fullName;
		
		Writer tweetWriters[] = new Writer[numFiles];
		for(int i = 0; i < numFiles; i++){
			fullName = fileName + Integer.toString(i) + fileType;
			File tweetFile = new File(dir,fullName);
			try {
				@SuppressWarnings("resource")
				Writer out = new BufferedWriter(
						new OutputStreamWriter(
								new FileOutputStream(tweetFile), "UTF8"));
				tweetWriters[i] = out;
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return tweetWriters;
	}
	
	public static void closeWriters(Writer[] tweetWriters) {
		for(int i = 0; i < tweetWriters.length; i++) {
			try {
				tweetWriters[i].close();
			} catch (IOException e) {
				System.out.println("Failed to close FileOutputStream");
			}
		}
	}
		
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
			//boolean test = true;
	        public void onStatus(Status status) {
	        		try {
	        			statusQueue.add(status);
	        		} catch (IllegalStateException e) {}
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
		
		int filesToCreate = (args.length > 0) ? Integer.parseInt(args[0]) : 1;
		//INIT steps
		Writer tweetWriters[] = createDirectoryAndFiles(filesToCreate);
		ConfigurationBuilder cb = setAuth();
		StatusListener listener = createTweetListener();
	    TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
	    twitterStream.addListener(listener);
	    
	    twitterStream.sample();
	    
	    final ExecutorService tweetConsumers = Executors.newFixedThreadPool(10);
	    for(int i = 0; i < tweetWriters.length; i++) {
	    	tweetConsumers.submit(new ThreadWriter(tweetWriters[i],statusQueue, 10000000));
	    }
	    tweetConsumers.shutdown();
	    try {
			tweetConsumers.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			closeWriters(tweetWriters);
			e1.printStackTrace();
		}
	    
	    System.out.println("Shutting Down");
	    twitterStream.cleanUp();
	    twitterStream.shutdown();
	    closeWriters(tweetWriters);
	    
	    System.out.println("Completed");
	}
}