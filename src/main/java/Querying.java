import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

public class Querying {
	private DB db;
	private MongoClient mongoClient;
	private DBCollection users;
	
	public static final String USERLINE = "userline";
	public static final String TIMELINE = "timeline";

	@SuppressWarnings("deprecation")
	public Querying() {
		mongoClient = new MongoClient(Arrays.asList(new ServerAddress("167.205.35.19", 27017),
                new ServerAddress("167.205.35.20", 27018),
                new ServerAddress("167.205.35.21", 27019),
                new ServerAddress("167.205.35.22", 27019)));

		db = mongoClient.getDB("ichayyu");
		users = db.getCollection("users");
	}
	
	public void register (String username, String password) {
		BasicDBObject user = new BasicDBObject();
		user.put("username", username);
		user.put("password", password);
		users.insert(user);
	}
	
	public boolean login (String username, String password) {
		BasicDBObject andQuery = new BasicDBObject();
		List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
		obj.add(new BasicDBObject("username", username));
		obj.add(new BasicDBObject("password", password));
		andQuery.put("$and", obj);

		DBCursor cursor = users.find(andQuery);
		return cursor.hasNext();
	}
	
	public void follow (String username, String friend) {}
	
	public void tweet (String username, String body) {}
	
	public void showTweets (String username, String type) {}
	
	public void close() {
		mongoClient.close();
	}
	
	public boolean isUserExist (String username) {
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("username", username);
		DBCursor cursor = users.find(whereQuery);
		return cursor.hasNext();
	}
	
	public boolean isUserFollows (String user1, String user2) {return true;}
}
