// Sahaj Arora 100961220 Jennifer Franklin 100315764
package edu.carleton.comp4601.crawler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.carleton.comp4601.models.Movie;
import edu.carleton.comp4601.models.Review;
import edu.carleton.comp4601.models.User;
import edu.carleton.comp4601.services.DbService;
import edu.carleton.comp4601.utilities.DbCollection;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import org.apache.http.Header;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import com.mongodb.BasicDBObject;

import java.io.IOException;
import java.net.*;

public class MyCrawler extends WebCrawler {
	private final String TAG = "tag";
	private static final Pattern IMAGE_EXTENSIONS = Pattern.compile(".*\\.(bmp|gif|jpg|png)$");
	public static CrawlGraph graph = new CrawlGraph("NewGraph");
	public static ArrayList<String> links = new ArrayList<>();
	public static ArrayList<String> images = new ArrayList<>();
	public static HashMap<String, ArrayList> textElements = new HashMap<String, ArrayList>();
	public static ArrayList<String> paragraphsTextList = new ArrayList<>();
	public static ArrayList<String> headings1TextList = new ArrayList<>();
	public static ArrayList<String> headings2TextList = new ArrayList<>();
	public static ArrayList<String> headings3TextList = new ArrayList<>();
	public static ArrayList<String> headings4TextList = new ArrayList<>();
	public static ArrayList<Long> times = new ArrayList<>();
	public static int counter = -1;
	public static Long shouldVisitTime = 0L;

	public static final String MOVIE_URL_PREFIX = "https://sikaman.dyndns.org/courses/4601/assignments/training/pages";
	public static final String USER_URL_PREFIX = "https://sikaman.dyndns.org/courses/4601/assignments/training/users";

	public boolean isMoviePage = false, isUserPage = false;

	/**
	 * You should implement this function to specify whether the given url
	 * should be crawled or not (based on your crawling logic).
	 */
	@Override
	public synchronized boolean shouldVisit(Page referringPage, WebURL url) {
		// Check if this page has already been visited or not.
		if (url.toString().contains(MOVIE_URL_PREFIX)) {
			// Get movie id from url
			String movieId = getIdFromUrl(url.toString());
			//System.out.println("Checking if already parsed movie page: " + movieId);
			if (movieId.length() > 0) {
				if (DbService.getDocumentById(movieId, DbCollection.MOVIES) == null) return true;
			}
		} else if (url.toString().contains(USER_URL_PREFIX)) {
			// Get user id from url
			String userId = getIdFromUrl(url.toString());
			//System.out.println("Checking if already parsed user page: " + userId);
			if (userId.length() > 0) {
				if (DbService.getDocumentById(userId, DbCollection.USERS) == null) return true;
			}
		}

		return false;
	}

	private String getIdFromUrl(String url) {
		String[] urlParts = url.split("/");
		String id = urlParts[urlParts.length - 1];
		if (id.contains(".html")) {
			id = id.split(".html")[0];
		}

		return id;
	}

	/**
	 * This function is called when a page is fetched and ready to be processed
	 * by your program.
	 */
	@Override
	public void visit(Page page) {
		int docid = page.getWebURL().getDocid();
		String url = page.getWebURL().getURL();
		String domain = page.getWebURL().getDomain();
		String path = page.getWebURL().getPath();
		String subDomain = page.getWebURL().getSubDomain();
		String parentUrl = page.getWebURL().getParentUrl();
		String anchor = page.getWebURL().getAnchor();

		Long timeTaken = System.currentTimeMillis() - MyCrawler.shouldVisitTime;
		//System.out.println("Time taken for visit: " + timeTaken);
		MyCrawler.times.add(timeTaken);
		MyCrawler.counter++;

		//logger.debug("Docid: {}", docid);
		//logger.info("URL: {}", url);
		//logger.debug("Domain: '{}'", domain);
		//logger.debug("Sub-domain: '{}'", subDomain);
		//logger.debug("Path: '{}'", path);
		//logger.debug("Parent page: {}", parentUrl);
		//logger.debug("Anchor text: {}", anchor);

		if (page.getWebURL().getURL().contains(MOVIE_URL_PREFIX)) {
			isMoviePage = true;
		} else if (page.getWebURL().getURL().contains(USER_URL_PREFIX)) {
			isUserPage = true;
		}

		if (page.getParseData() instanceof HtmlParseData) {

			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String text = htmlParseData.getText();
			String html = htmlParseData.getHtml();
			Set<WebURL> links = htmlParseData.getOutgoingUrls();

			//logger.debug("Text length: {}", text.length());
			//logger.debug("Html length: {}", html.length());
			//logger.debug("Number of outgoing links: {}", links.size());

			//			updateHrefGraph(page);

			Document doc;

			ArrayList<Movie> movies = new ArrayList<>();
			ArrayList<User> users = new ArrayList<>();
			ArrayList<Review> reviews = new ArrayList<>();
			Movie movie = null;
			User user = null;

			try {
				doc = Jsoup.connect(url).get();
				String title = doc.title();
				String content = doc.html();

				//System.out.println("JSOUP PARSING: ");
				//System.out.println("Document Title: " + title);

				boolean shouldParsePage = true;

				if (isUserPage) {
					user = new User(title, url);
					//System.out.println("This is a user page");
//					if (DbService.getDocumentById(title, DbCollection.USERS) == null) shouldParsePage = true;
				} else if (isMoviePage) {
					movie = new Movie(title, url);
					//System.out.println("This is a movie page");
//					if (DbService.getDocumentById(title, DbCollection.MOVIES) == null) shouldParsePage = true;
				}

				if (shouldParsePage) {
					// Get all links
					if (isUserPage) {
						Elements pageLinks = doc.select("a[href]");	
						for	(Element link : pageLinks)	{	
							// Get the value from href attribute
							String movieId = link.text();
							if (isId(movieId)) {
								String movieUrl = link.attr("abs:href");
								movies.add(new Movie(movieId, movieUrl));
							}
							MyCrawler.links.add(link.attr("href"));
						}	
					}

					else if (isMoviePage) {
						Matcher m = Pattern.compile("(<a href=.*</a>)\\s*<br\\s*/>\\s*<p>(.*?)</p>\\s*<br\\s*/>").matcher(content);
						while (m.find()) {
							Matcher m2 = Pattern.compile("<a href=.*>(.*)</a>").matcher(m.group(1));
							if (m2.find()) {
								// Add User
								String userId = m2.group(1);
								//System.out.println("User id: " + m2.group(1));
								User u = new User(userId, "");
								users.add(u);

								// Add Review
								Review review = new Review(movie, u);
								review.setText(m.group(2));
								reviews.add(review);
							}
							//						

						}
					}

					// Save User/Movie/Reviews to the database
					if (isUserPage) {
						// Set user's reviewed movies
						user.setReviewedMovies(movies);
						// Insert the user into database
						DbService.insertOneDocument(user, DbCollection.USERS);
					} else if (isMoviePage) {
						//System.out.println("Users size: " + users.size());
						//System.out.println("Reviews size: " + reviews.size());

						// Insert the movie into database
						DbService.insertOneDocument(movie, DbCollection.MOVIES);

						// Insert All Reviews into database
						if (reviews.size() > 0) {
							DbService.insertManyDocuments(reviews, "reviews");
						}

					}
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	

		}

		Header[] responseHeaders = page.getFetchResponseHeaders();
		if (responseHeaders != null) {
			//logger.debug("Response headers:");
			for (Header header : responseHeaders) {
				//logger.debug("\t{}: {}", header.getName(), header.getValue());
			}
		}
		//logger.debug("=============");
	}

	private boolean isId(String linkText) {
		final String MOVIE_ID_PREFIX = "B", USER_ID_PREFIX = "A";
		if (linkText.startsWith(MOVIE_ID_PREFIX) || linkText.startsWith(USER_ID_PREFIX)) {
			if (linkText.split(" ").length == 1) {
				return true;
			}
		}
		return false;
	}

	private List<String> generateTags(String text) {
		return Arrays.asList(text.split(" "));
	}

	//	private void saveSdaDocument(edu.carleton.comp4601.dao.Document doc) {
	//		Map<String, Object> map = new HashMap<String, Object>();
	//		map.put("id", doc.getId());
	//		map.put("name", doc.getName());
	//		map.put("score", doc.getScore());
	//		map.put("text", doc.getText());
	//		map.put("url", doc.getUrl());
	//		map.put("tags", doc.getTags());
	//		map.put("links", doc.getLinks());
	//
	//		Documents.getInstance().create(new edu.carleton.comp4601.dao.Document(map));
	//	}

	private void updateHrefGraph(Page page)
	{
		try {
			String parentUrl = page.getWebURL().getParentUrl();
			URL newUrl = new URL(page.getWebURL().getURL());
			URL newParentUrl = new URL(parentUrl);

			Vertex v = new Vertex(new Long(page.getWebURL().getDocid()), "vertex");
			// add the vertices
			graph.addVertex(v);

			if (parentUrl != null) {
				Long parentDocId = new Long(page.getWebURL().getParentDocid());
				Vertex parentV;

				// Check if this parent already exists in the graph
				Vertex p = graph.find(parentDocId);
				if (p != null) {
					// Parent vertex already exists
					parentV = p;
				} else {
					parentV = new Vertex(parentDocId, "parent");
				}

				// add edges to create linking structure
				graph.addEdge(v, parentV);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		//System.out.println("<------ UPDATED GRAPH ----->");
		//System.out.println(graph.toString());
	}

	//	private boolean parseNonHTMLContent(Page page) {
	//		Controller.logger.log(Level.INFO, "visited[" + page.getWebURL().getDocid() + ",o]: " + 
	//				page.getWebURL().getURL());
	//		java.io.InputStream input = new ByteArrayInputStream()
	//	}

}
