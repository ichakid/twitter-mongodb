package mongoJavaClient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class Querying {
	private DB db;
	private MongoClient mongoClient;
	private DBCollection users;
	private DBCollection tweets;
	
	public static final String USERLINE = "userline";
	public static final String TIMELINE = "timeline";

	@SuppressWarnings("deprecation")
	public Querying() {
		mongoClient = new MongoClient("167.205.35.19", 27017);
		db = mongoClient.getDB("ichayyu");
		users = db.getCollection("users");
		tweets = db.getCollection("tweets");
	}
	
	public void close() {
		mongoClient.close();
	}
	
	public void register (String username, String password) {
		if (!isUserExist(username)) {
			BasicDBObject user = new BasicDBObject();
			user.put("username", username);
			user.put("password", password);
			users.insert(user);
		} else {
			System.out.println("User already exists.");
		}
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
	
	public void follow (String username, String friend) {
		if (isUserExist(username) && isUserExist(friend)) {
			BasicDBObject searchQuery = new BasicDBObject("username", friend);
			Date now = new Date();
			
			BasicDBObject follower = new BasicDBObject();
			follower.put("follower", username);
			follower.put("since", now);
			users.update(searchQuery, new BasicDBObject(
					"$push", new BasicDBObject("followers", follower)));
			
			BasicDBObject friendObj = new BasicDBObject();
			friendObj.put("friend", username);
			friendObj.put("since", now);
			users.update(searchQuery, new BasicDBObject(
					"$push", new BasicDBObject("followers", follower)));
		} else {
			System.out.println("Cannot follow " + friend);
		}
	}
	
	public void tweet (String username, String body) {
		if (isUserExist(username)) {
			UUID tweet_id = UUID.randomUUID();
			BasicDBObject tweet = new BasicDBObject();
			tweet.put("tweet_id", tweet_id);
			tweet.put("username", username);
			tweet.put("body", body);
			tweet.put("time", new Date());
			tweets.insert(tweet);
			
			BasicDBObject tweet2 = new BasicDBObject("tweet_id", tweet_id);
			insertTweet(new BasicDBObject("userline", tweet2), username);
			insertTweet(new BasicDBObject("timeline", tweet2), username);
			
			DBCursor cursor = users.find(new BasicDBObject("username", username));
			if (cursor.hasNext()) {
				BasicDBList followers = (BasicDBList) cursor.next().get("followers");
				if (followers != null) {
					for (Object follower: followers) {
						insertTweet(new BasicDBObject("timeline", tweet2),
								(String) ((DBObject) follower).get("follower"));
					}
				}
			}
		}
	}
	
	public void insertTweet (BasicDBObject tweet, String username) {
		BasicDBObject searchQuery = new BasicDBObject("username", username);
		users.update(searchQuery, new BasicDBObject(
				"$push", new BasicDBObject(tweet)));
	}
	
	public void showTweets (String username, String type) {
		if (isUserExist(username)) {
			BasicDBObject user = new BasicDBObject("username", username);
			DBCursor cursor = users.find(user);
			if(cursor.hasNext()) {
				BasicDBList lines = (BasicDBList) cursor.next().get(type);
				if (lines != null) {
					for (Object line: lines) {
						printTweet ((BasicDBObject) line);
					}
				} else {
					System.out.println(type + " is empty.");
				}
			}
		} else {
			System.out.println("Username " + username + "doesn't exist.");
		}
	}
	
	public void printTweet (BasicDBObject idTweet) {
		DBCursor cursor = tweets.find(idTweet);
		if(cursor.hasNext()) {
			DBObject tmp = cursor.next();
			String username = (String) tmp.get("username");
			String body = (String) tmp.get("body");
			System.out.format("[%s] %s\n", 
        			username,
        			body);
		}
	}
		
	public boolean isUserExist (String username) {
		BasicDBObject whereQuery = new BasicDBObject("username", username);
		DBCursor cursor = users.find(whereQuery);
		return cursor.hasNext();
	}
	
	public boolean isUserFollows (String user1, String user2) {
		List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
		obj.add(new BasicDBObject("username", user2));
		obj.add(new BasicDBObject("followers.follower", user1));
		BasicDBObject andQuery = new BasicDBObject("$and", obj);

		DBCursor cursor = users.find(andQuery);
		return cursor.hasNext();
	}
}
