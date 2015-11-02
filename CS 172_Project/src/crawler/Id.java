package crawler;

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