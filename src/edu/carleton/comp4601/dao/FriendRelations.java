package edu.carleton.comp4601.dao;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import edu.carleton.comp4601.models.FriendRelation;
import edu.carleton.comp4601.models.Movie;
import edu.carleton.comp4601.services.DbService;
import edu.carleton.comp4601.utilities.DbCollection;

public class FriendRelations {

	DBCollection collection;
	
	private static Movies instance;
	private final String ID = "_id";
	
	public static Movies getInstance() {
		if( instance == null )
			instance = new Movies();
		return instance;
	}
	
	public FriendRelations() {
		collection = DbService.getInstance().getCollection(DbCollection.FRIENDS);
		collection.setObjectClass(FriendRelation.class);
	}
	
	public ConcurrentHashMap<String, ArrayList<String>> getAllUserFriendsMap() {
		DBCursor cursor = collection.find();
		ConcurrentHashMap<String, ArrayList<String>> relations = new ConcurrentHashMap<>();
		try {
			while(cursor.hasNext()) {
				FriendRelation relation = (FriendRelation) cursor.next();
				relations.put(relation.getUserId(), relation.getFriendIds() );
			}
		} finally {
			cursor.close();
		}
		return relations;
	}
	
	public ArrayList<String> getFriendsByUserId(String userId) {
		BasicDBObject searchQuery = new BasicDBObject(ID, new BasicDBObject("$eq", userId));
		ArrayList<String> friends = (ArrayList<String>) collection.findOne(searchQuery);
		return friends;
	}


}
