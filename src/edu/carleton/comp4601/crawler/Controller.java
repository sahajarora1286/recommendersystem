package edu.carleton.comp4601.crawler;

import java.net.UnknownHostException;

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
		
	public static void intialize() throws Exception {
        String crawlStorageFolder = System.getProperty("user.home")+"/.rs/";
        int numberOfCrawlers = 1;
        // public static CrawlGraph graph
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setMaxDepthOfCrawling(2);
        config.setMaxPagesToFetch(10000);
        config.setMaxDownloadSize(15000000);
        config.setIncludeBinaryContentInCrawling(true);

        /*
         * Instantiate the controller for this crawl.
         */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        /*
         * For each crawl, you need to add some seed urls. These are the first
         * URLs that are fetched and then the crawler starts following links
         * which are found in these pages
         */
        controller.addSeed("https://sikaman.dyndns.org/courses/4601/assignments/training/pages/");
//        controller.addSeed("https://sikaman.dyndns.org/courses/4601/assignments/training/users/");

        /*
         * Start the crawl. This is a blocking operation, meaning that your code
         * will reach the line after this only when crawling is finished.
         */
        controller.start(MyCrawler.class, numberOfCrawlers);
        
        // Save Graph. Currently the graph is not updated in the crawler.
        byte[] bytesGraph = Marshaller.serializeObject(MyCrawler.graph);
        
        System.out.println("Graph: ");
        System.out.println(bytesGraph);
        
        BasicDBObject graphDoc = new BasicDBObject();
        graphDoc.put("graph", bytesGraph);
        DbService.insertOneDocument(graphDoc, "graph");
        
        DbService.closeConnection();
        
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

