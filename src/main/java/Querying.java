import java.util.Arrays;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

public class Querying {
	private DB db;
	private MongoClient mongoClient;
	
	public static final String USERLINE = "userline";
	public static final String TIMELINE = "timeline";

	@SuppressWarnings("deprecation")
	public Querying() {
		mongoClient = new MongoClient(Arrays.asList(new ServerAddress("167.205.35.19", 27017),
                new ServerAddress("167.205.35.20", 27018),
                new ServerAddress("167.205.35.21", 27019),
                new ServerAddress("167.205.35.22", 27019)));

		db = mongoClient.getDB("ichayyu");
	}
	
	public void register (String username, String password) {}
	
	public boolean login (String username, String password) {return true;}
	
	public void follow (String username, String friend) {}
	
	public void tweet (String username, String body) {}
	
	public void showTweets (String username, String type) {}
	
	public void close() {
		mongoClient.close();
	}
	
	public boolean isUserExist (String username) {return true;}
	
	public boolean isUserFollows (String user1, String user2) {return true;}
}
