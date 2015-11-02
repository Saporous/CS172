package crawler;

import org.jsoup.*;
import org.jsoup.nodes.Document;

public class Link {
	private String address;
	private String title;

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
	public Link(String addr) {
		this.address = addr;
		this.title = getTitleFromURL(this.address);
	}
	
	public String getAddress() {
		return this.address;
	}
	
	public String getTitle() {
		return this.title;
	}

}
