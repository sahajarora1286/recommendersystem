package edu.carleton.comp4601.dao;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

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

	public void assignCommunity(String userId, String community) {
		User user = getUser(userId);
		User updatedUser = getUser(userId);
		updatedUser.setCommunity(community);
		collection.update(user, updatedUser);
	}

}
