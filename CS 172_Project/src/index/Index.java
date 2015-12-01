package index;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


import org.apache.lucene.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.index.IndexWriterConfig.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;


import crawler.Link;
import crawler.Tweet;
import twitter4j.GeoLocation;
import twitter4j.Place;

import com.google.gson.Gson;

public class Index {
	
	public static final Version luceneVersion = Version.LUCENE_5_3_1;
	
	public static IndexWriter getIndexWriter(Path dir, boolean create) {
		try {
			Directory indexDir = FSDirectory.open(dir);
			Analyzer luceneAnalyzer = new StandardAnalyzer();
			IndexWriterConfig luceneConfig = new IndexWriterConfig(luceneAnalyzer);
			
			if(create) {
				luceneConfig.setOpenMode(OpenMode.CREATE);
			}
			else {
				luceneConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}
			return (new IndexWriter (indexDir, luceneConfig));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;	
	}

	protected static void addDocuments(Tweet tweet, IndexWriter indexWriter) throws Exception {

		Document doc = new Document();
		
		doc.add(new StringField("screenName", tweet.getScreenName(), Field.Store.YES));
		doc.add(new StringField("name", tweet.getName(),Field.Store.YES));
		doc.add(new StringField("timestamp",
				DateTools.dateToString(tweet.getTimestamp(), DateTools.Resolution.MINUTE),
				Field.Store.YES));

		Place place = tweet.getPlace();
		if(place != null) {
			GeoLocation[][] geo = place.getBoundingBoxCoordinates();
			doc.add(new DoubleField("latitude", geo[0][0].getLatitude(),Field.Store.YES));
			doc.add(new DoubleField("longitude", geo[0][0].getLongitude(),Field.Store.YES));
			doc.add(new StringField("placeName", place.getFullName(),Field.Store.YES));
		}
		doc.add(new TextField("text", "", Field.Store.YES));
		
		if(tweet.getLinks() != null) {
			for(Link link : tweet.getLinks()) {
				doc.add(new StringField("link", link.getTitle(), Field.Store.YES));
			}
		}
		
		if(tweet.getHashtags() != null) {
			for(String hashtag : tweet.getHashtags()) {
				doc.add(new StringField("hashtag", hashtag, Field.Store.YES));
			}
		}
		
		doc.add(new TextField("text", tweet.getLang(), Field.Store.YES));
		
		indexWriter.addDocument(doc);
		
	}

	/*
	public static void search(String indexDir, String query) throws IOException, ParseException{
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDir)));
		IndexSearcher indexSearcher = new IndexSearcher(reader);
		
		QueryParser parser = new QueryParser()
	}
	*/
	
	public static void parseJSONIntoIndex(IndexWriter indexWriter, String filePath) {
		
		Gson gson = new Gson();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			
			String json = br.readLine();
			Tweet tweet;
			while(json != null) {
				tweet = gson.fromJson(json, Tweet.class);
			
				addDocuments(tweet, indexWriter);
				json = br.readLine();
			}
			br.close();
		}catch(IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		
		IndexWriter indexWriter = getIndexWriter(Paths.get("/Index"), true);
		parseJSONIntoIndex(indexWriter,"Tweets/tweet1.json");
		System.out.println("JSON Loaded into index");
		
		try {
			indexWriter.commit();
			indexWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
