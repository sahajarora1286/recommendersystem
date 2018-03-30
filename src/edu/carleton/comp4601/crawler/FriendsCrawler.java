package edu.carleton.comp4601.crawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.carleton.comp4601.models.FriendRelation;
import edu.carleton.comp4601.models.Movie;
import edu.carleton.comp4601.models.Review;
import edu.carleton.comp4601.models.User;
import edu.carleton.comp4601.services.DbService;
import edu.carleton.comp4601.utilities.DbCollection;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class FriendsCrawler extends WebCrawler {
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

	public static final String GRAPH_URL_PREFIX = "https://sikaman.dyndns.org/courses/4601/assignments/training/graph";

	/**
	 * You should implement this function to specify whether the given url
	 * should be crawled or not (based on your crawling logic).
	 */
	@Override
	public synchronized boolean shouldVisit(Page referringPage, WebURL url) {
		// Check if this page has already been visited or not.
		if (url.toString().contains(GRAPH_URL_PREFIX)) {
			// Get user id from url
			String userId = getIdFromUrl(url.toString());
			System.out.println("Checking if already parsed user page: " + userId);
			if (userId.length() > 0) {
				if (DbService.getDocumentById(userId, DbCollection.FRIENDS) == null) return true;
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
		System.out.println("Time taken for visit: " + timeTaken);
		MyCrawler.times.add(timeTaken);
		MyCrawler.counter++;

		logger.debug("Docid: {}", docid);
		logger.info("URL: {}", url);
		logger.debug("Domain: '{}'", domain);
		logger.debug("Sub-domain: '{}'", subDomain);
		logger.debug("Path: '{}'", path);
		logger.debug("Parent page: {}", parentUrl);
		logger.debug("Anchor text: {}", anchor);

		if (page.getParseData() instanceof HtmlParseData) {

			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String text = htmlParseData.getText();
			String html = htmlParseData.getHtml();
			Set<WebURL> links = htmlParseData.getOutgoingUrls();

			logger.debug("Text length: {}", text.length());
			logger.debug("Html length: {}", html.length());
			logger.debug("Number of outgoing links: {}", links.size());

			//			updateHrefGraph(page);

			Document doc;

			User user = null;
			ArrayList<String> friendIds = new ArrayList<>();

			try {
				doc = Jsoup.connect(url).get();
				String title = doc.title();
				String content = doc.html();

				System.out.println("JSOUP PARSING: ");
				System.out.println("Document Title: " + title);

				user = new User(title, url);

				// Get all links
				Elements pageLinks = doc.select("a[href]");	
				for	(Element link : pageLinks)	{	
					// Get the value from href attribute

					String userId = link.text();
					if (isId(userId)) {
						System.out.println("Adding friend id: " + userId);
						friendIds.add(userId);
					}

					MyCrawler.links.add(link.attr("href"));
				}	

				// Save User-friends relation to the database
				if (friendIds.size() > 0) {
					// Set user-friend relation
					FriendRelation relation = new FriendRelation(user.getUserId(), friendIds);
					// Insert the user into database
					DbService.insertOneDocument(relation, DbCollection.FRIENDS);
				} 

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	

		}

		Header[] responseHeaders = page.getFetchResponseHeaders();
		if (responseHeaders != null) {
			logger.debug("Response headers:");
			for (Header header : responseHeaders) {
				logger.debug("\t{}: {}", header.getName(), header.getValue());
			}
		}
		logger.debug("=============");
	}

	private boolean isId(String linkText) {
		final String MOVIE_ID_PREFIX = "B", USER_ID_PREFIX = "A";
		if (linkText.startsWith(MOVIE_ID_PREFIX) || linkText.startsWith(USER_ID_PREFIX)) {
			System.out.println("starts with correct prefix");
			if (linkText.split(" ").length == 1) {
				System.out.println(linkText.split(" "));
				System.out.println("does not contain spaces");
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

		System.out.println("<------ UPDATED GRAPH ----->");
		System.out.println(graph.toString());
	}
}
