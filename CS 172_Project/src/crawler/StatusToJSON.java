package crawler;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import twitter4j.Status;

//Class used for multithreading
//Converts status objects into tweet objects
//also does crawling for links found in tweets
//then converts tweets to JSON format and pushes
//them onto the next queue

public class StatusToJSON implements Runnable{

	LinkedBlockingQueue<Status> statusQueue;
	LinkedBlockingQueue<String> jsonQueue;
	
	//Constructor
	public StatusToJSON(LinkedBlockingQueue<Status> sQueue,
			LinkedBlockingQueue<String> jQueue) {
		this.statusQueue = sQueue;
		this.jsonQueue = jQueue;

	}
	
	//Runnable execution
	@Override
	public void run() {
		
		while(true) {
			try {
				//Function loops until its producer (the twitterStream) stops producing
				Status status = this.statusQueue.poll(5L, TimeUnit.SECONDS);
				if(status == null) {
					return;
				}
				Tweet tweet = new Tweet(status);
    			String tweetJson = tweet.toJSON();
    			jsonQueue.add(tweetJson);

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				System.out.println("jsonQ full");
				
			}
		}
	}

}
