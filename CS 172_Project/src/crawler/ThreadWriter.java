package crawler;

import java.io.*;
import java.util.concurrent.LinkedBlockingQueue;

//Class used for multiThreading
//Gets json format strings from queue
//Writes to file
public class ThreadWriter implements Runnable {

	Writer tweetFileWriter;
	LinkedBlockingQueue<String> jsonQueue;
	int maxFileSize = 10000000;
	int bytesWritten = 0;
	
	//Constructor
	public ThreadWriter(Writer tfw, LinkedBlockingQueue<String> sq, int maxSize) {
		this.tweetFileWriter = tfw;
		this.jsonQueue = sq;
		this.maxFileSize = maxSize;
	}
	
	//Runnable execution
	public void run() {
		//Writes until file reaches required size
		while(this.bytesWritten < this.maxFileSize) {
			try {
    			String tweetJson = this.jsonQueue.take();
    			this.tweetFileWriter.write(tweetJson + '\n');
    			this.bytesWritten += tweetJson.length();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return;
	}

}
