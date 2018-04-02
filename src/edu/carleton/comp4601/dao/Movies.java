// Sahaj Arora 100961220 Jennifer Franklin 100315764
package edu.carleton.comp4601.dao;

import java.util.concurrent.ConcurrentHashMap;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import edu.carleton.comp4601.models.Movie;
import edu.carleton.comp4601.services.DbService;

public class Movies {

	DBCollection collection;
	
	private static Movies instance;
	
	public static Movies getInstance() {
		if( instance == null )
			instance = new Movies();
		return instance;
	}
	
	public Movies() {
		collection = DbService.getInstance().getCollection("movies");
		collection.setObjectClass(Movie.class);
	}
	
	public ConcurrentHashMap<String, Movie> getMovies() {
		DBCursor cursor = collection.find();
		ConcurrentHashMap<String, Movie> movies = new ConcurrentHashMap<String, Movie>();
		try {
			while(cursor.hasNext()) {
				Movie movie = (Movie) cursor.next();
				movies.put(movie.getMovieId(), movie);
			}
		} finally {
			cursor.close();
		}
		return movies;
	}
	
	public Movie getMovie(String movieId) {
		BasicDBObject searchQuery = new BasicDBObject("_id", new BasicDBObject("$eq", movieId));
		Movie movie = (Movie) collection.findOne(searchQuery);
		return movie;		
	}
	
	public void updateMovie(String movieId, String genre) {
		Movie movie = getMovie(movieId);
		Movie updatedMovie = getMovie(movieId);
		updatedMovie.setGenre(genre);
		collection.update(movie, updatedMovie);
	}

}
