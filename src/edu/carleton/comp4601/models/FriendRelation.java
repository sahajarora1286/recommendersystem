// Sahaj Arora 100961220 Jennifer Franklin 100315764
package edu.carleton.comp4601.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@XmlRootElement
public class FriendRelation extends BasicDBObject implements DBObject {
	private String userId;
	private ArrayList<String> friendIds;
	private final String ID = "_id";
	private final String FRIEND_IDS = "friendIds";
	
	public FriendRelation() {
		
	}
	
	public FriendRelation (String userId, ArrayList<String> friendIds) {
		this.userId = userId;
		this.put(ID, userId);
		this.friendIds = friendIds;
		this.put(FRIEND_IDS, friendIds);
	}

	public String getUserId() {
		return get(ID).toString();
	}

	public void setUserId(String userId) {
		this.userId = userId;
		this.put(ID, userId);
	}

	public ArrayList<String> getFriendIds() {
		return (ArrayList<String>) get(FRIEND_IDS);
	}

	public void setFriendIds(ArrayList<String> friendIds) {
		this.friendIds = friendIds;
		this.put(FRIEND_IDS, friendIds);
	}

	
}
