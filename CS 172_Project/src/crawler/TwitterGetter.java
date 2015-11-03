package crawler;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;


public class TwitterGetter {
	
	//Queues used for threading
	final static LinkedBlockingQueue<Status> statusQueue= new LinkedBlockingQueue<Status>(200);
	final static LinkedBlockingQueue<String> jsonQueue = new LinkedBlockingQueue<String>(150);
	
	//Function creates storage folder,files and opens Writers to those files
	public static Writer[] createDirectoryAndFiles(int numFiles) {
		//Creating Directory
		File dir = new File("./Tweets");
		dir.mkdir();
		
		String fileName = new String("tweet");
		String fileType = new String(".json");
		String fullName;
		
		Writer tweetWriters[] = new Writer[numFiles];
		
		for(int i = 0; i < numFiles; i++){
			//Concatenates strings together to get proper file names
			fullName = fileName + Integer.toString(i) + fileType;
			File tweetFile = new File(dir,fullName);
			//Creates the file and opens a source writer in UTF-8 encoding to them
			try {
				//Suppresses unclosed writer warning
				@SuppressWarnings("resource")
				Writer out = new BufferedWriter(
						new OutputStreamWriter(
								new FileOutputStream(tweetFile), "UTF8"));
				tweetWriters[i] = out;
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return tweetWriters;
	}
	
	//Closes the writers
	public static void closeWriters(Writer[] tweetWriters) {
		for(int i = 0; i < tweetWriters.length; i++) {
			try {
				tweetWriters[i].close();
			} catch (IOException e) {
				System.out.println("Failed to close FileOutputStream");
			}
		}
	}
	
	//Sets up the ConfigurationBuider with the authentication keys
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
	
	//Function creates the stream listener used by TwitterStream
	public static StatusListener createTweetListener(){
		StatusListener tweetListener = new StatusListener() {
	        public void onStatus(Status status) {
	        		try {
	        			//listens for status, then pushes onto queue
	        			statusQueue.add(status);
	        		} catch (IllegalStateException e) {
	        			//System.out.println("statusQ full");
	        		}
	        }
	        //To be implemented later if necessary
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
		
		//Determine number of files and file size
		//Defaults to 1 10MB file
		int filesToCreate = (args.length > 0) ? Integer.parseInt(args[0]) : 1;
		int bytesPerFile = (args.length > 1) ? Integer.parseInt(args[1]) : 10000000;
		
		if(args.length > 2) {
			System.out.println("Invalid arguments entered.");
			System.out.println("Please run as program <number of files> <size of each file in Bytes");
			System.exit(1);
		}
		System.out.printf("Creating %d, %d Byte files.\n", filesToCreate, bytesPerFile);

		//INIT steps, calls various init functions
		Writer tweetWriters[] = createDirectoryAndFiles(filesToCreate);
		ConfigurationBuilder cb = setAuth();
		StatusListener listener = createTweetListener();
	    TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
	    twitterStream.addListener(listener);
	    
	    
	    
	    //Threading uses a multilevel producer-consumer system
	    //The listener thread (1), pushes statuses onto a queue
	    //The statusConsumers (many) convert status -> Tweet -> JSON format strings
	    //and pushes them onto a different queue
	    //The jsonConsumers (1 per File) writes the JSON Strings to their files
	    twitterStream.sample();
	    
	    final ExecutorService statusConsumers = Executors.newFixedThreadPool(100);
	    final ExecutorService jsonConsumers = Executors.newFixedThreadPool(filesToCreate);
	    
	    int minRuns = ((filesToCreate * 20) < 100) ? (filesToCreate * 20) : 100;
	    for(int i = 0; i < minRuns; i++) {
	    	statusConsumers.submit(new StatusToJSON(statusQueue, jsonQueue));
	    }
	    
	    for(int i = 0; i < tweetWriters.length; i++) {
	    	jsonConsumers.submit(new ThreadWriter(tweetWriters[i],
	    			jsonQueue,
	    			bytesPerFile));
	    }
	    
	    //Blocks additional runables
	    statusConsumers.shutdown();
	    jsonConsumers.shutdown();
	    
	    //Wait for threads to finish
	    //Ends in this order
	    //jsonConsumers -> listener -> statusConsumer
	    try {
			jsonConsumers.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			System.out.println("Tweets Acquired, Terminating");
		    twitterStream.cleanUp();
		    twitterStream.shutdown();
			statusConsumers.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e1) {
			closeWriters(tweetWriters);
			e1.printStackTrace();
		}
	    
	    //Close Writers
	    closeWriters(tweetWriters);
	    
	    System.out.println("Completed");
	}
}