package crawler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.*;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class WebGetter {

	public static void main(String[] args) {
		//THIS CODE WORKS FOR DOWNLOADING WEBPAGES
		String html = "http://www.ucr.edu/";
		Document doc = null;
		try {
			doc = Jsoup.connect(html).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(doc.title());
		System.out.println(doc.head());
		System.out.println(doc.body());
	}

}
