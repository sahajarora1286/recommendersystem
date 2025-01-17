// Sahaj Arora 100961220 Jennifer Franklin 100315764
package edu.carleton.comp4601.services;

import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import edu.carleton.comp4601.utilities.DbCollection;

public class DbService {
	private static MongoClient mc;
	private static DB database;
	private static final String DB_NAME = "movieSystem";
	private static final String LOCALHOST = "localhost";
	
	public static DbService instance;
	
	public static DbService getInstance() {
		if(instance == null)
			instance = new DbService();
		return instance;
	}
	
	public DbService() {
		establishConnection();
	}
	
	public synchronized boolean isClassified() {
		return database.collectionExists(DbCollection.CLASSIFIED);
	}
	
	public synchronized void dropCollection(String collectionName) {
		DBCollection collection;
		boolean exists = database.collectionExists(collectionName);
		if(exists) {
			collection = database.getCollection(collectionName);
			collection.drop();
		}
	}
	
	public synchronized void createCollection(String collectionName) {
		DBCollection collection;
		boolean exists = database.collectionExists(collectionName);
		if(!exists) {
			collection = database.createCollection(collectionName, null);
		}
		collection = database.getCollection(collectionName);
		BasicDBObject dummy = new BasicDBObject();
		dummy.put("dummy", "value");
		collection.insert(dummy, null);
	}
	
	public synchronized void resetDatabase() {
		database.dropDatabase();
	}
	
	public synchronized DBCollection getCollection(String collectionName) {
		DBCollection collection;
		boolean exists = database.collectionExists(collectionName);
		if(!exists) {
			database.createCollection(collectionName, null);
		}
		
		collection = database.getCollection(collectionName);
		return collection;
	}

	public static void insertOneDocument(BasicDBObject document, String collection) {
		if (mc == null || database == null) establishConnection();
		DBCollection coll = database.getCollection(collection);
		coll.insert(document);
	}
	
	public static void insertManyDocuments(ArrayList<? extends BasicDBObject> documents, String collection) {
		if (mc == null || database == null) establishConnection();
		DBCollection coll = database.getCollection(collection);
		coll.insert(documents);
	}
	
	public static Object getDocumentById(String id, String collection) {
		if (mc == null || database == null) establishConnection();
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("_id", id);
		DBCollection coll = database.getCollection(collection);
		DBCursor cursor = coll.find(searchQuery);
		 
		while (cursor.hasNext()) {
		    return cursor.next();
		}
		
		return null;
	}

	private static void establishConnection() {
		mc = new MongoClient(LOCALHOST, 27017);
		database = mc.getDB(DB_NAME);
	}
	
	public static void closeConnection() {
		if (mc != null) {
			mc.close();
		}
	}
	
	
}
