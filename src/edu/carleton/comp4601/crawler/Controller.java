// Sahaj Arora 100961220 Jennifer Franklin 100315764
package edu.carleton.comp4601.crawler;

import java.net.UnknownHostException;
import java.util.logging.Logger;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

import edu.carleton.comp4601.services.DbService;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller {
	
	public static DB database;
	public static DBCollection collection;
	public static final String LOCALHOST = "localhost";
	public static final String SDA = "sda";
	public static final String GRAPH = "graph";
	private static Logger log;
		
	public static void intialize(String dir) throws Exception {
		String URL = "https://sikaman.dyndns.org/courses/4601/assignments/";
		log = Logger.getLogger("Controller");
        String crawlStorageFolder = System.getProperty("user.home")+"/.rs/";
        String crawlStorageFolder2 = System.getProperty("user.home")+"/.rs-friends";
        int numberOfCrawlers = 1;
        // public static CrawlGraph graph
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setMaxDepthOfCrawling(2);
        config.setMaxPagesToFetch(10000);
        config.setMaxDownloadSize(15000000);
        config.setIncludeBinaryContentInCrawling(true);
        
        // public static CrawlGraph graph
        CrawlConfig config2 = new CrawlConfig();
        config2.setCrawlStorageFolder(crawlStorageFolder2);
        config2.setMaxDepthOfCrawling(2);
        config2.setMaxPagesToFetch(10000);
        config2.setMaxDownloadSize(15000000);
        config2.setIncludeBinaryContentInCrawling(true);       

        /*
         * Instantiate the controller for this crawl.
         */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
        PageFetcher pageFetcher2 = new PageFetcher(config2);
        CrawlController controller2 = new CrawlController(config2, pageFetcher2, robotstxtServer);

        /*
         * For each crawl, you need to add some seed urls. These are the first
         * URLs that are fetched and then the crawler starts following links
         * which are found in these pages
         */
        controller.addSeed(URL+dir+"/pages/");

        log.info("About to crawl: "+URL+dir+"/pages/");
        /*
         * Start the crawl. This is a blocking operation, meaning that your code
         * will reach the line after this only when crawling is finished.
         */
        controller.start(MyCrawler.class, numberOfCrawlers);
        
        log.info("About to crawl: "+URL+dir+"/graph/");
        // Crawl User Friends Network
        controller2.addSeed(URL+dir+"/graph/");
        controller2.start(FriendsCrawler.class, numberOfCrawlers);
        
        // Save Graph. Currently the graph is not updated in the crawler.
        byte[] bytesGraph = Marshaller.serializeObject(MyCrawler.graph);
        
        System.out.println("Graph: ");
        System.out.println(bytesGraph);
        
        BasicDBObject graphDoc = new BasicDBObject();
        graphDoc.put("graph", bytesGraph);
        DbService.insertOneDocument(graphDoc, "graph");
        
        //DbService.closeConnection();
        
//        // Initialize MongoDb database
//        MongoClient mc;
//		try {
//			mc = new	MongoClient(LOCALHOST, 27017);
//			database = mc.getDB(SDA);
//			database.createCollection(GRAPH, null);
//			collection = database.getCollection(GRAPH);
////			collection.setObjectClass(CrawlGraph.class);
//			BasicDBObject document = new BasicDBObject();
//			document.put("bytes", bytesGraph);
//			document.put("links", MyCrawler.links);
//			document.put("images", MyCrawler.images);
//			document.put("text", MyCrawler.textElements);
//			collection.insert(document);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        
    }
}

