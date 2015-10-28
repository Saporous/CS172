package crawler;

import org.jsoup.*;
import org.jsoup.nodes.Document;

import java.io.IOException;

import org.json.*;

public class WebGetter {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String html = "<html><head><title>First parse</title></head>"
				+ "<body><p>Parsed HTML into a doc.</p></body></html>";
		Document doc2 = Jsoup.parse(html);
		System.out.println(doc2.title());
		
		String html2 = "http://www.ucr.edu/";
		Document doc = null;
		try {
			doc = Jsoup.connect(html2).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(doc.title());
		
	}

}
