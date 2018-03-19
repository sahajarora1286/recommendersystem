package edu.carleton.comp4601.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

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
	}
	
	@GET
	public String printName() {
		return name;
	}

}
