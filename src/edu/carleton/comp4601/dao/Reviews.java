package edu.carleton.comp4601.dao;

import java.util.concurrent.ConcurrentHashMap;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import edu.carleton.comp4601.models.Review;
import edu.carleton.comp4601.services.DbService;

public class Reviews {

	DBCollection collection;
	
	private static Reviews instance;
	
	public static Reviews getInstance() {
		if( instance == null )
			instance = new Reviews();
		return instance;
	}
	
	public Reviews() {
		collection = DbService.getInstance().getCollection("reviews");
		collection.setObjectClass(Review.class);
	}
	
	public Review getReview(String movieId) {
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("movie", movieId);
		DBCursor cursor = collection.find(searchQuery).limit(1);
		try {
			if (cursor.hasNext()) {
				return (Review) cursor.next();			
			}
		} finally {
			cursor.close();
		}
		
		return null;
	}
	
	public ConcurrentHashMap<String, Review> getReviews(String movieId) {
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("movie", movieId);
		DBCursor cursor = collection.find(searchQuery);
		ConcurrentHashMap<String, Review> reviews = new ConcurrentHashMap<String, Review>();
		try {
			while(cursor.hasNext()) {
				Review review = (Review) cursor.next();
				reviews.put(review.getId(), review);
			}
		} finally {
			cursor.close();
		}

		return reviews;		

	}

}
