package crawler;

import org.jsoup.*;
import org.jsoup.nodes.Document;

//Link class contains a links address as well as the title
//of the page the link goes to
public class Link {
	private String address;
	private String title;

	//helper function gets page title from url using Jsoup
	public String getTitleFromURL(String url){
		Document doc;
		try {
			doc = Jsoup.connect(url).get();
			return doc.title();
		}
		catch(Exception e){
			return "Unable to fetch title";
		}
	}
	
	//Constructor
	public Link(String addr) {
		this.address = addr;
		this.title = getTitleFromURL(this.address);
	}
	
	//Get functions for json
	public String getAddress() {
		return this.address;
	}
	
	public String getTitle() {
		return this.title;
	}

}
