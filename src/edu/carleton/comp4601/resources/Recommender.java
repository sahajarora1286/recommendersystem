// Sahaj Arora 100961220 Jennifer Franklin 100315764
package edu.carleton.comp4601.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import com.mongodb.BasicDBObject;

import edu.carleton.comp4601.crawler.Controller;
import edu.carleton.comp4601.dao.Movies;
import edu.carleton.comp4601.dao.Reviews;
import edu.carleton.comp4601.dao.Users;
import edu.carleton.comp4601.models.Movie;
import edu.carleton.comp4601.models.Review;
import edu.carleton.comp4601.models.User;
import edu.carleton.comp4601.services.DbService;
import edu.carleton.comp4601.utilities.CollabrativeFiltering;
import edu.carleton.comp4601.utilities.Constants;
import edu.carleton.comp4601.utilities.DbCollection;
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
	private final String REST_URL = "http://localhost:8011/COMP4601-RS/rest/rs";
	private final Logger log;

	public Recommender() {
		log = Logger.getLogger("Recommender");
		name = "COMP4601 Recommender System V1.0: Sahaj Arora and Jennifer Franklin";
		//try {
		//Controller.intialize("https://sikaman.dyndns.org/courses/4601/assignments/training");
		//MovieClassification classifyMovies = new MovieClassification();
		//SentimentClassification classifyReviewSentiments = new SentimentClassification();
		//CollabrativeFiltering collaborativeFiltering = new CollabrativeFiltering();
		//} catch (Exception e) {
		// TODO Auto-generated catch block
		//e.printStackTrace();
		//}
		//return;
	}

	@GET
	public String printName() {
		return name;
	}

	@GET
	@Path("reset/{dir}")
	public String resetDatabase(@PathParam("dir") String dir) {
		log.info("made it to reset");
		//NOTE use: http://localhost:8080/COMP4601-RS/rest/rs/reset/training for our default site
		DbService.getInstance().resetDatabase();
		try {
			Controller.intialize(dir);
			return "System initialized";
		} catch(Exception e) {
			e.printStackTrace();
		}

		return "Failed to initialize system";
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("context")
	public String getContextHtml() {
		if(!DbService.getInstance().isClassified()) {
			try {
				MovieClassification classifyMovies = new MovieClassification();
				SentimentClassification classifyReviewSentiments = new SentimentClassification();
				CollabrativeFiltering collaborativeFiltering = new CollabrativeFiltering();
				DbService.getInstance().createCollection(DbCollection.CLASSIFIED);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("Getting user profiles..");
		StringBuffer html = new StringBuffer();
		html.append("<html> " + "<title>" + name + "</title>" + "<body>"); 
		ArrayList<User> users = Users.getInstance().getAllUsers();

		html.append("<table border='1' style='width: 100%'>");
		html.append("<th style='text-align: left'>User</th> <th style='text-align: left'>Movies Reviewed</th>");

		for (User user: users) {
			html.append("<tr>");
			html.append("<td>" + user.getUserId() + "</td>");
			html.append("<td>");
			List<BasicDBObject> movies = user.getReviewedMovies();
			for (int i = 0; i < movies.size(); i++) {
				String movieId = movies.get(i).getString("_id");
				System.out.println("Movie ID: " + movieId);
				String movieHref = "<a href=\"" + REST_URL + "/fetch/" + user.getUserId() + "/" + movieId + "\" target=\"_blank\">" +  movieId + "</a>";
				html.append(movieHref);
				if (i < user.getReviewedMovies().size()-2) 
					html.append(", ");
			}
			html.append("</td> </tr>");
		}
		html.append("</table>");

		html.append("</body>" + "</html>");
		return html.toString();
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("community")
	public String getCommunities() {
		System.out.println("Getting communities...");
		StringBuffer html = new StringBuffer();
		if(!DbService.getInstance().isClassified()) {
			html.append("<html> " + "<title>error context not called yet</title>" + "<body><h3>/context must be called before calling /community</body></html>");
		} else {

			html.append("<html> " + "<title>" + name + "</title>" + "<body>"); 
			ArrayList<User> users = Users.getInstance().getAllUsers();

			ArrayList<String> actionUsers = new ArrayList<>();
			ArrayList<String> adventureUsers = new ArrayList<>();
			ArrayList<String> dramaUsers = new ArrayList<>();
			ArrayList<String> comedyUsers = new ArrayList<>();
			ArrayList<String> horrorUsers = new ArrayList<>();
			ArrayList<String> thrillerUsers = new ArrayList<>();

			for (User user: users) {
				if (user.getCommunity().equals(Constants.ACTION)) actionUsers.add(user.getUserId());
				if (user.getCommunity().equals(Constants.ADVENTURE)) adventureUsers.add(user.getUserId());
				if (user.getCommunity().equals(Constants.DRAMA)) dramaUsers.add(user.getUserId());
				if (user.getCommunity().equals(Constants.COMEDY)) comedyUsers.add(user.getUserId());
				if (user.getCommunity().equals(Constants.HORROR)) horrorUsers.add(user.getUserId());
				if (user.getCommunity().equals(Constants.THRILLER)) thrillerUsers.add(user.getUserId());
			}

			html.append("<table border='1' style='width: 100%'>");
			html.append("<th style='text-align: left'>Community</th> <th style='text-align: left'>Users</th>");

			html.append("<tr> <td>" + Constants.ACTION + "</td> <td>");
			// Add Action Users
			for (int i = 0; i < actionUsers.size(); i++) {
				String userId = actionUsers.get(i);
				System.out.println("User ID: " + userId);
				html.append(userId);
				if (i < actionUsers.size()-2) 
					html.append(", ");
			}
			html.append("</td> </tr>");

			html.append("<tr> <td>" + Constants.ADVENTURE + "</td> <td>");
			// Add Adventure Users
			for (int i = 0; i < adventureUsers.size(); i++) {
				String userId = adventureUsers.get(i);
				System.out.println("User ID: " + userId);
				html.append(userId);
				if (i < adventureUsers.size()-2) 
					html.append(", ");
			}
			html.append("</td> </tr>");

			html.append("<tr> <td>" + Constants.DRAMA + "</td> <td>");
			// Add Drama Users
			for (int i = 0; i < dramaUsers.size(); i++) {
				String userId = dramaUsers.get(i);
				System.out.println("User ID: " + userId);
				html.append(userId);
				if (i < dramaUsers.size()-2) 
					html.append(", ");
			}
			html.append("</td> </tr>");

			html.append("<tr> <td>" + Constants.COMEDY + "</td> <td>");
			// Add Comedy Users
			for (int i = 0; i < comedyUsers.size(); i++) {
				String userId = comedyUsers.get(i);
				System.out.println("User ID: " + userId);
				html.append(userId);
				if (i < comedyUsers.size()-2) 
					html.append(", ");
			}
			html.append("</td> </tr>");

			html.append("<tr> <td>" + Constants.HORROR + "</td> <td>");
			// Add Horror Users
			for (int i = 0; i < horrorUsers.size(); i++) {
				String userId = horrorUsers.get(i);
				System.out.println("User ID: " + userId);
				html.append(userId);
				if (i < horrorUsers.size()-2) 
					html.append(", ");
			}
			html.append("</td> </tr>");

			html.append("<tr> <td>" + Constants.THRILLER + "</td> <td>");
			// Add Thriller Users
			for (int i = 0; i < thrillerUsers.size(); i++) {
				String userId = thrillerUsers.get(i);
				System.out.println("User ID: " + userId);
				html.append(userId);
				if (i < thrillerUsers.size()-2) 
					html.append(", ");
			}
			html.append("</td> </tr>");

			html.append("</table>");

			html.append("</body>" + "</html>");
		}

		return html.toString();
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("fetch/{user}/{page}")
	public String getUserMovieReview(@PathParam("user") String userId, @PathParam("page") String movieId) {
		StringBuffer html = new StringBuffer();
		if(!DbService.getInstance().isClassified()) {
			html.append("<html> " + "<title>error context not called yet</title>" + "<body><h3>context must be called before fetch/{user}/{page} may be generated</body></html>");
		} else {
			Movie movie = Movies.getInstance().getMovie(movieId);
			User user = Users.getInstance().getUser(userId);

			// ** Content **
			html.append("<html> " + "<title>" + name + "</title>" + "<body>"); 

			if (user != null && movie != null) {
				Review review = Reviews.getInstance().getReview(userId, movieId);
				String userCommunity = user.getCommunity();
				String movieGenre = movie.getGenre();

				String movieHref = "<a href=\"" + movie.getUrl() + "\" target=\"_blank\">" + "<h3>Movie: " +  movieId + "</h3></a>";
				html.append(movieHref);
				//			html.append("<h2> Movie: " + movie.getMovieId() + " </h2>");
				//			String userHref = "<a href=\"" + user.getUrl() + "\" target=\"_blank\">" + "User: " +  userId + "</a>";
				//			html.append(userHref);
				html.append("<h3> User: " + user.getUserId() + "</h3>");
				html.append("<h3> Review: </h3>");
				html.append("<p>" + review.getText() + "</p>");

				// Divider
				html.append("<div style=\"width: 100%; height: 2px; background:grey\"> </div>");

				// ** Advertisements **
				html.append("<h2>Advertisements</h2>");

				// User community based
				html.append("<div style=\"padding: 10px; border: 1px solid lightgrey\">");
				html.append("<h3>Advertisement</h3>");
				html.append("<p> This is <b>" + userCommunity + "</b> advertisement 1.");
				html.append("</div>");

				// Movie based
				html.append("<div style=\"margin-top: 15px;padding: 10px; border: 1px solid lightgrey\">");
				html.append("<h3>Advertisement</h3>");
				if (userCommunity.equals(movieGenre)) {
					html.append("<p> This is <b>" + movieGenre + "</b> advertisement 2");
				} else {
					html.append("<p> This is <b>" + movieGenre + "</b> advertisement 1.");
				}
				html.append("</div>");
			} else {
				if (user == null) html.append("<h3> User not found </h3>");
				if (movie == null) html.append("<h3> Movie not found </h3>");
			}

			html.append("</body> </html>");
		}
		return html.toString();
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("advertising/{category}")
	public String getCategory(@PathParam("category") String category) {
		System.out.println("Getting category...");
		StringBuffer html = new StringBuffer();
		html.append("<html> " + "<title>" + name + "</title>" + "<body>"); 

		if (category!=null && (category.equals(Constants.ACTION) || category.equals(Constants.ADVENTURE) || 
				category.equals(Constants.COMEDY) || category.equals(Constants.HORROR) || 
				category.equals(Constants.THRILLER) || category.equals(Constants.DRAMA))) {
			// ** Advertisements **

			// Advertisement 1
			html.append("<div style=\"padding: 10px; border: 1px solid lightgrey\">");
			html.append("<h3>Advertisement 1</h3>");
			html.append("<p> This is " + category + " advertisement 1.");
			html.append("</div>");

			// Advertisement 2
			html.append("<div style=\"padding: 10px; border: 1px solid lightgrey\">");
			html.append("<h3>Advertisement 1</h3>");
			html.append("<p> This is " + category + " advertisement 2.");
			html.append("</div>");

		} else {
			html.append("<h3> There is no such category.</h3>");
		}

		html.append("</body> </html>");
		return html.toString();

	}


}
