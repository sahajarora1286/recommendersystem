// Sahaj Arora 100961220 Jennifer Franklin 100315764
package edu.carleton.comp4601.dao;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.cxf.common.i18n.Exception;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import edu.carleton.comp4601.models.Movie;
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
	
	public Review getReview(String userId, String movieId) {
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("movie", movieId);
		searchQuery.put("user", userId);
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

	public Review getReview(String reviewId) {
		BasicDBObject searchQuery = new BasicDBObject("_id", new ObjectId(reviewId));
		return (Review) collection.findOne(searchQuery);
	}
	
	public ConcurrentHashMap<String, Review> getMovieReviews(String movieId) {
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
	
	public ConcurrentHashMap<String, Review> getUserReviews(String userId) {
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("user", userId);
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
	
	public ConcurrentHashMap<String, Review> getAllReviews() {
		DBCursor cursor = collection.find();
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
	
	public int getMovieCount() {
		DBCursor cursor = collection.find();
		HashSet<String> movieIds = new HashSet<String>();
		try {
			while(cursor.hasNext()) {
				Review review = (Review) cursor.next();
				movieIds.add(review.getMovie());
			}
		} finally {
			cursor.close();
		}
		return movieIds.size();
	}
	
	public void updateReviewSentiment(String reviewId, String sentiment) {
		Review review = getReview(reviewId);
		Review updatedReview = getReview(reviewId);
		updatedReview.setSentiment(sentiment);
		collection.update(review, updatedReview);
	}

}
