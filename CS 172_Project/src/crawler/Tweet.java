package crawler;

import java.util.Date;

import twitter4j.GeoLocation;
import twitter4j.Place;
import twitter4j.Status;
import twitter4j.User;

public class Tweet {
	//private GeoLocation geoLocation;
	private Place place;
	private Date timestamp;
	private String text;
	//private User usr;
	private String name;
	private String screenName;
	private String links[];
	
	//Other useful info
	private String lang;
	private boolean isRetweet;
	private String hashtags[];
	
	public static String[] parseTextForLinks(String text) {
		String links[] = new String[0];
		return null;
	}
	public static String[] parseTextForHashtags(String text) {
		String hashtags[] = new String[0];
		return null;
	}
	
	public Tweet(Status status) {
    	//this.geoLocation = status.getGeoLocation();
    	this.place = status.getPlace();
    	this.timestamp = status.getCreatedAt();
    	this.text = status.getText();
    	this.name = status.getUser().getName();
    	this.screenName = status.getUser().getScreenName();
    	this.links = parseTextForLinks(this.text);
    	
    	
    	//Other useful info
    	this.lang = status.getLang();
    	this.isRetweet = status.isRetweet();
    	this.hashtags = parseTextForHashtags(this.text);
	}
	/*
	public GeoLocation getGeoLocation() {
		return this.geoLocation;
	}*/
	
	public Place getPlace() {
		return this.place;
	}
	
	public Date getTimestamp() {
		return this.timestamp;
	}
	
	public String getText() {
		return this.text;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getScreenName() {
		return this.screenName;
	}
	
	public String[] getLinks() {
		return this.links;
	}
	
	public boolean getIsRetweet() {
		return this.isRetweet;
	}
	
	public String[] getHashtags() {
		return this.hashtags;
	}
}
