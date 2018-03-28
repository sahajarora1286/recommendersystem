package edu.carleton.comp4601.models;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@XmlRootElement
public class Movie extends BasicDBObject implements DBObject {
	private String movieId;
//	private ArrayList<Review> reviews;
	private String reviewsText;
	private String url;
	private HashMap<String, String> reviews;
	
	public Movie(String movieId, String url) {
		this.movieId = movieId;
		this.append("_id", movieId);
		this.url = url;
		this.append("url", url);
	}
	
	public Movie(String movieId, String url, HashMap<String, String> reviews, String reviewsText) {
		this.movieId = movieId;
		this.append("_id", movieId);
		this.reviews = reviews;
		this.append("reviews", reviews);
		if (reviewsText == null) reviewsText = "";
		this.reviewsText = reviewsText;
		this.append("reviewsText", reviewsText);
		this.url = url;
		this.append("url", url);
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void addReview(String user, String review) {
		this.reviews.put(user, review);
//		this.append("reviews", review);
	}

	public String getMovieId() {
		return movieId;
	}

	public void setMovieId(String movieId) {
		this.movieId = movieId;
//		this.append("_id", movieId);
	}

	public HashMap<String, String> getReviews() {
		return reviews;
	}

	public void setReviews(HashMap<String, String> reviews) {
		this.reviews = reviews;
		this.append("reviews", reviews);
//		this.append("reviews", reviews);
	}

	public String getReviewsText() {
		return reviewsText;
	}

	public void setReviewsText(String reviewsText) {
		this.reviewsText = reviewsText;
//		this.append("reviewsText", reviewsText);
	}
	
}