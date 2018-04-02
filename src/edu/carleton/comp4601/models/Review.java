// Sahaj Arora 100961220 Jennifer Franklin 100315764
package edu.carleton.comp4601.models;

import javax.xml.bind.annotation.XmlRootElement;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@XmlRootElement
public class Review extends BasicDBObject implements DBObject {
	
	private String _id;
	private Movie movie;
	private User user;
	private String text;
	private String sentiment;
	
	public Review() {
		
	}
	
	public Review (Movie movie, User user) {
		this.movie = movie;
		this.put("movie", movie.getMovieId());
		this.user = user;
		this.put("user", user.getUserId());
	}
	
	public String getId() {
		return get("_id").toString();
	}
	
	public String getSentiment() {
		return get("sentiment").toString();
	}
	
	public void setSentiment(String sentiment) {
		this.append("sentiment", sentiment);
	}
	
	public String getText() {
		return get("text").toString();
	}

	public void setText(String text) {
		this.text = text;
		this.append("text", text);
	}


	public String getMovie() {
		return get("movie").toString();
	}

	public void setMovie(Movie movie) {
		this.movie = movie;
		this.append("movie", movie.getMovieId());
	}

	public String getUser() {
		return get("user").toString();
	}

	public void setUser(User user) {
		this.user = user;
		this.append("user", user.getUserId());
	}

	
}
