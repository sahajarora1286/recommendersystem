package edu.carleton.comp4601.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import org.bson.BSONObject;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoDatabase;

@XmlRootElement
public class User extends BasicDBObject implements DBObject {
	private final String ID = "_id";
	private String userId;
	private ArrayList<Movie> reviewedMovies;
	private String url;
	private ArrayList<User> friends;
	private String community;
	
	public User() {
		
	}
	
	public void setCommunity(String community) {
		this.community = community;
		this.append("community", community);
	}
	
	public String getCommunity() {
		return get("community").toString();
	}
	
	public ArrayList<User> getFriends() {
		return friends;
	}

	public void setFriends(ArrayList<User> friends) {
		this.friends = friends;
	}

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
		return get(ID).toString();
	}

	public void setUserId(String userId) {
		this.userId = userId;
//		this.append("_id", userId);
	}

	public List<BasicDBObject> getReviewedMovies() {
		return (List<BasicDBObject>) get("reviewedMovies");
	}

	public void setReviewedMovies(ArrayList<Movie> reviewedMovies) {
		this.reviewedMovies = reviewedMovies;
		this.append("reviewedMovies", reviewedMovies);
//		this.append("reviewedMovies", reviewedMovies);
	}
	
//	@Override
//	public String toString() {
//		String output  = "UserID: " + getUserId() + "\n";
//		output += "Community: " + getCommunity() + "\n";
//		output += "Movies reviewed: \n";
//		for (Movie movie: reviewedMovies) {
//			output += "MovieID: " + movie.getMovieId() + "\n";
//		}
//		return output;
//	}

	
}
