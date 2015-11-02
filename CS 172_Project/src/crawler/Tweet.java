package crawler;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.*;

import com.google.gson.Gson;

import twitter4j.Place;
import twitter4j.Status;


public class Tweet {
	//private GeoLocation geoLocation;
	private String screenName;
	private String name;
	
	private Date timestamp;
	private Place place;
	private String text;
	
	private Link links[];
	
	//Other useful info
	private String hashtags[];
	private String lang;
	private boolean isRetweet;
	
	public static Link[] parseTextForLinks(String text) {
		if(text.contains("http") == false) {
			return null;
		}
		Set<Link> linkList = new HashSet<Link>();
		
		String[] textParts = text.split("\\s+");
		for (String part : textParts) {
			if(part.startsWith("http") == true) {
				try{
					URL url = new URL(part);
					if(url.getHost() != null) {
						linkList.add(new Link(part));
					}
				}
				catch(MalformedURLException e) {
					continue;
				}
			}
		}
		if(linkList.isEmpty() != true) {
			Link links[] = linkList.toArray(new Link[linkList.size()]);
			return links;
		}
		return null;
	}
	
	public static String[] parseTextForHashtags(String text) {
		if(text.contains("#") == false){
			return null;
		}
		Set<String> hashtagList = new HashSet<String>();
		Matcher matcher = Pattern.compile("#\\s*(\\w+)").matcher(text);
		while(matcher.find()){
			hashtagList.add("#".concat(matcher.group(1)) );
		}
		if(hashtagList.isEmpty() != true) {
			return hashtagList.toArray(new String[hashtagList.size()]);
		}
		return null;
	}
	
	public Tweet(Status status) {
    	//this.geoLocation = status.getGeoLocation();
    	this.place = status.getPlace();
    	this.timestamp = status.getCreatedAt();
    	try{
    		String textTemp = status.getText();
    		byte[] array = textTemp.getBytes("UTF-8");
    		this.text = new String(array, Charset.forName("UTF-8"));
    	}
    	catch(UnsupportedEncodingException e) {
    		System.out.println("encoding failed");
    		this.text = status.getText();
    	}
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
	
	public Link[] getLinks() {
		return this.links;
	}
	
	public String getLang() {
		return this.lang;
	}
	
	public boolean getIsRetweet() {
		return this.isRetweet;
	}
	
	public String[] getHashtags() {
		return this.hashtags;
	}
	
	public String toJSON() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
