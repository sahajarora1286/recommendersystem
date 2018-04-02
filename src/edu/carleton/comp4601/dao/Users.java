// Sahaj Arora 100961220 Jennifer Franklin 100315764
package edu.carleton.comp4601.dao;

import java.util.ArrayList;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import edu.carleton.comp4601.models.User;
import edu.carleton.comp4601.services.DbService;

public class Users {

	DBCollection collection;
	
	private static Users instance;
	
	public static Users getInstance() {
		if( instance == null )
			instance = new Users();
		return instance;
	}
	
	public Users() {
		collection = DbService.getInstance().getCollection("users");
		collection.setObjectClass(User.class);
	}

	public User getUser(String userId) {
		BasicDBObject searchQuery = new BasicDBObject("_id", new BasicDBObject("$eq", userId));
		User user = (User) collection.findOne(searchQuery);
		return user;
	}
	
	public ArrayList<User> getAllUsers() {
		ArrayList<User> users = new ArrayList<>();
		DBCursor cursor = collection.find();
		while (cursor.hasNext()) {
			User user = (User) cursor.next();
//			user.put("reviewedMovies", user.get("reviewedMovies"));
			if (user != null) {
				users.add(user);
			}
		}
		return users;
	}
	
//	public ArrayList<User> getMoviesReviewedByUserId(String userId) {
//		ArrayList<User> movies = new ArrayList<>();
//		BasicDBObject searchQuery = new BasicDBObject("_id", new BasicDBObject("$eq", userId));
//		DBCursor cursor = collection.findOne(searchQuery);
//		while (cursor.hasNext()) {
//			User user = (User) cursor.next();
//			if (user != null) {
//				users.add(user);
//			}
//		}
//		return users;
//	}

	public void assignCommunity(String userId, String community) {
		User user = getUser(userId);
		User updatedUser = getUser(userId);
		updatedUser.setCommunity(community);
		collection.update(user, updatedUser);
	}

}
