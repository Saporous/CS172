package crawler;

import java.io.*;
import java.util.concurrent.LinkedBlockingQueue;

import twitter4j.Status;

public class ThreadWriter implements Runnable {

	Writer tweetFileWriter;
	LinkedBlockingQueue<Status> statusQueue;
	int maxFileSize = 10000000;
	int bytesWritten = 0;
	
	public ThreadWriter(Writer tfw, LinkedBlockingQueue<Status> sq, int maxSize) {
		this.tweetFileWriter = tfw;
		this.statusQueue = sq;
		this.maxFileSize = maxSize;
	}
	@Override
	public void run() {
		while(this.bytesWritten < this.maxFileSize) {
			try {
				Status status = this.statusQueue.take();
				Tweet tweet = new Tweet(status);
    			String tweetJson = tweet.toJSON();
    			this.tweetFileWriter.write(tweetJson + '\n');
    			this.bytesWritten += tweetJson.length() + 1;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}

}
