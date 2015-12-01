package index;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.List;
import java.util.Set;

import org.apache.lucene.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import crawler.Link;
import crawler.Tweet;
import twitter4j.GeoLocation;
import twitter4j.Place;

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
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;	
	}

	protected void addDocuments(Tweet[] tweets, IndexWriter indexWriter) throws Exception {
		for(Tweet tweet : tweets) {
			Document doc = new Document();
			
			doc.add(new StringField("screenName", tweet.getScreenName(), Field.Store.YES));
			doc.add(new StringField("name", tweet.getName(),Field.Store.YES));
			doc.add(new StringField("timestamp",
					DateTools.dateToString(tweet.getTimestamp(), DateTools.Resolution.MINUTE),
					Field.Store.YES));
			//Place
			Place place = tweet.getPlace();
			if(place != null) {
				GeoLocation[][] geo = place.getBoundingBoxCoordinates();
				doc.add(new DoubleField("latitude", geo[0][0].getLatitude(),Field.Store.YES));
				doc.add(new DoubleField("longitude", geo[0][0].getLongitude(),Field.Store.YES));
				doc.add(new StringField("placeName", place.getFullName(),Field.Store.YES));
			}
			doc.add(new TextField("text", tweet.getText(), Field.Store.YES));
			
			for(Link link : tweet.getLinks()) {
				doc.add(new StringField("link", link.getTitle(), Field.Store.YES));
			}
			
			for(String hashtag : tweet.getHashtags()) {
				doc.add(new StringField("hashtag", hashtag, Field.Store.YES));
			}
			
			doc.add(new TextField("lang", tweet.getLang(), Field.Store.YES));
			
			indexWriter.addDocument(doc);
		}
		//indexWriter.commit();
		//indexWriter.close();
		
	}
	
	public Tweet[] parseJSONFile(String filePath) {
		InputStream jsonFile = getClass().getResourceAsStream(filePath);
		Reader readerJSON = new InputStreamReader(jsonFile);
		
		//Parse returns Object, convert Object into Tweet and append to
		// tweets array
		Tweet[] tweets = (Tweet) JSONValue.parse(readerJSON);
		//JSONArray arrayObjects = (JSONArray) fileObjects;
		
		return tweets;
		
	}
	
	public void createIndex(String filePath, String indexPath) {
		
		IndexWriter indexWriter = getIndexWriter(Paths.get(indexPath));
		
	}
	
	public static void search(String indexDir, String query) throws IOException, ParseException{
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDir)));
		IndexSearcher indexSearcher = new IndexSearcher(reader);
		
		QueryParser parser = new QueryParser();
	}
	public static void main(String[] args) {
		System.out.println("Parsing Tweets...");
		JSONArray jsonObjects = parseJSONFile(args[1]);
		System.out.println("Done");
		IndexWriter indexWriter = getIndexWriter(Paths.get(args[0]), Boolean.valueOf(args[2]));
		

	}
}
