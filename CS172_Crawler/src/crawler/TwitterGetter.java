package crawler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.Gson;
public class Id{
	private String identifier;
	private String url;
	private String place_type;
	private String name;
	private String country;
	
	@Override
	public String toString(){
		return name + " " + country;
	}
}

public class Places{
	private Id ID;
}

public class TwitterGetter {

	public static void main(String[] args) throws MalformedURLException, IOException {
		String consumer_key = "&oauth_consumer_key=k2Ykj3xZkolzy0NYFGRAZqbTg";
		String access_token = "&oauth_token=1499500044-FvdxAP0owsCyAmVGZ4dbrSHUOQN5te8l0tX3Uld";
		String signature_method = "&oauth_signature_method=HMAC-SHA1&oauth_timestamp=1446024385&"
				+ "oauth_nonce=3Vp41N&oauth_version=1.0&oauth_signature=4VkOspk9JboXDJy4DgP9xGjhnlQ%3D";
		
		String query = "Toronto";
		
		String api_base = "https://api.twitter.com/1.1/geo/search.json?";
		
		String url = api_base+"query="+query+consumer_key+access_token+signature_method;
		
		InputStream input = new URL(url).openStream();
		Reader reader = new InputStreamReader(input, "UTF-8");
		Places data = new Gson().fromJson(reader, Places.class);
		System.out.println(data);
		
	}

}
