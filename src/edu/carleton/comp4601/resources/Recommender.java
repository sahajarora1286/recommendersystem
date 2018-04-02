package edu.carleton.comp4601.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import edu.carleton.comp4601.crawler.Controller;
import edu.carleton.comp4601.utilities.MovieClassification;
import edu.carleton.comp4601.utilities.SentimentClassification;

@Path("/")
public class Recommender {
	// Allows to insert contextual objects into the class,
	// e.g. ServletContext, Request, Response, UriInfo
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	
	private String name;

	public Recommender() {
		name = "COMP4601 Recommender System V1.0: Sahaj Arora and Jennifer Franklin";
		try {
			Controller.intialize();
			MovieClassification classifyMovies = new MovieClassification();
			SentimentClassification classifyReviewSentiments = new SentimentClassification();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}
	
	@GET
	public String printName() {
		return name;
	}

}
