package edu.carleton.comp4601.models;

import javax.xml.bind.annotation.XmlRootElement;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@XmlRootElement
public class Review extends BasicDBObject implements DBObject {
	
	private Movie movie;
	private User user;
	private String text;
	
	public Review (Movie movie, User user) {
		this.movie = movie;
		this.put("movie", movie.getMovieId());
		this.user = user;
		this.put("user", user.getUserId());
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		this.append("text", text);
	}


	public Movie getMovie() {
		return movie;
	}

	public void setMovie(Movie movie) {
		this.movie = movie;
		this.append("movie", movie.getMovieId());
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
		this.append("user", user.getUserId());
	}

	
}
