package index;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.lang.Float;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.index.IndexWriterConfig.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;


import crawler.Link;
import crawler.Tweet;
import twitter4j.GeoLocation;

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

		
		if(tweet.getGeolocation() != null) {
			GeoLocation[][] geo = tweet.getGeolocation();
			doc.add(new DoubleField("latitude", geo[0][0].getLatitude(),Field.Store.YES));
			doc.add(new DoubleField("longitude", geo[0][0].getLongitude(),Field.Store.YES));
			doc.add(new StringField("placeName", tweet.getPlaceFullName(),Field.Store.YES));
		}
		doc.add(new TextField("text", tweet.getText(), Field.Store.YES));
		
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
		
		doc.add(new TextField("lang", tweet.getLang(), Field.Store.YES));
		
		indexWriter.addDocument(doc);
		
	}

	 public static TopDocs search(IndexSearcher indexSearcher, String queryString, int topk)
			 throws IOException, ParseException{
		  //QueryParser queryparser = new QueryParser("text", new StandardAnalyzer());
		  
		  String fields[] = {"text", "screenName", "link", "hashtag"};
		  HashMap<String, Float> boosts = new HashMap<String, Float>();
		  boosts.put("text", (float) 1.5);
		  boosts.put("screenName", (float) 2.5);
		  boosts.put("link", (float) 2.0);
		  boosts.put("hashtag", (float) 3.0);
		  
		  MultiFieldQueryParser mfQueryParser =
				  new MultiFieldQueryParser(
						  fields,
						  new StandardAnalyzer(),
						  boosts);

		  try {
			  StringTokenizer strtok = new StringTokenizer
					  (queryString, " ~`!$%^&*()-+={[}]|:;'<>,./?\"\'\\/\n\t\b\f\r");
			  String querytoparse = "";
			  while(strtok.hasMoreElements()) {
				  String token = strtok.nextToken();
				  querytoparse += token;
			  }

			  Query query = mfQueryParser.parse(querytoparse);
			  //System.out.println(query.toString());
			  TopDocs results = indexSearcher.search(query, topk);
			  return results;   
			  
		  } 
		  catch (Exception e) {
			  e.printStackTrace();
		  }
		  return null;
	 }
	 
	public static IndexSearcher getIndexSearcher(String indexDir) {
		try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDir)));
			IndexSearcher indexSearcher = new IndexSearcher(reader);
			return indexSearcher;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
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
			e.printStackTrace();
		}
	}
	
	public static ScoreDoc[] queryIndex(String indexPath, String query) {
		IndexSearcher indexSearcher = getIndexSearcher(indexPath);
		try {
			TopDocs results = search(indexSearcher, query, 10);
			System.out.println(results.scoreDocs.length); 
			return results.scoreDocs;
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void updateIndex(String indexPath, boolean overwrite) {
		IndexWriter indexWriter = getIndexWriter(Paths.get(indexPath), overwrite);
		parseJSONIntoIndex(indexWriter,"./Tweets/tweet0.json");
		System.out.println("JSON Loaded into index");
		try {
			indexWriter.commit();
			indexWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		updateIndex("./Index", true);
		
		ScoreDoc[] hits = queryIndex("./Index", "shouts");
		IndexSearcher indexSearcher = getIndexSearcher("./Index");
		if(hits.length > 0) {
			try {
				for(ScoreDoc hit : hits) {
					System.out.println(
							indexSearcher.doc(hit.doc).getField("timestamp").stringValue());
					System.out.println(
							indexSearcher.doc(hit.doc).getField("screenName").stringValue());
					System.out.println(
							indexSearcher.doc(hit.doc).getField("text").stringValue());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
}
