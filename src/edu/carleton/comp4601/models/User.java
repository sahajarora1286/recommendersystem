package edu.carleton.comp4601.models;

import java.util.ArrayList;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import org.bson.BSONObject;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoDatabase;

@XmlRootElement
public class User extends BasicDBObject implements DBObject {
	private String userId;
	private ArrayList<Movie> reviewedMovies;
	private String url;
	
	public User(String userId, String url) {
		this.userId = userId;
		this.url = url;
		this.append("_id", userId);
		this.append("url", url);
	}
	
	public User(String userId, String url, ArrayList<Movie> reviewedMovies) {
		this.userId = userId;
		this.append("_id", userId);
		this.reviewedMovies = reviewedMovies;
		this.append("reviewedMovies", reviewedMovies);
		this.url = url;
		this.append("url", url);
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void addReviewedMovie(Movie movie) {
		this.reviewedMovies.add(movie);
//		this.append("reviewedMovies", reviewedMovies);
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
//		this.append("_id", userId);
	}

	public ArrayList<Movie> getReviewedMovies() {
		return reviewedMovies;
	}

	public void setReviewedMovies(ArrayList<Movie> reviewedMovies) {
		this.reviewedMovies = reviewedMovies;
		this.append("reviewedMovies", reviewedMovies);
//		this.append("reviewedMovies", reviewedMovies);
	}

	
}
