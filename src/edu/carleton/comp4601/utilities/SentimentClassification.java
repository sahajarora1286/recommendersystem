package edu.carleton.comp4601.utilities;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import edu.carleton.comp4601.dao.Reviews;
import edu.carleton.comp4601.models.Review;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.Instances;

public class SentimentClassification {
	public static ArrayList<String> SENTIMENT_CATEGORIES = new ArrayList<String>(Arrays.asList("Positive", "Neutral", "Negative"));
	// The memory-resident array of stop words
	public static ArrayList<String> STOP_WORDS = new ArrayList<String>(Arrays.asList("a", "able", "about", "across", "after", "all", "almost", "also", "am", "among", "an", "and", "any", "are", "as", "at", "be", "because", "been", "but", "by", "can", "cannot", "could", "dear", "did", "do", "does", "either", "else", "ever", "every", "for", "from", "get", "got", "had", "has", "have", "he", "her", "hers", "him", "his", "how", "however", "i", "if", "in", "into", "is", "it", "its", "just", "least", "let", "like", "likely", "may", "me", "might", "most", "must", "my", "neither", "no", "nor", "not", "of", "off", "often", "on", "only", "or", "other", "our", "own", "rather", "said", "say", "says", "she", "should", "since", "so", "some", "than", "that", "the", "their", "them", "then", "there", "these", "they", "this", "tis", "to", "too", "twas", "us", "wants", "was", "we", "were", "what", "when", "where", "which", "while", "who", "whom", "why", "will", "with", "would", "yet", "you", "your"));
	// The class containing the list of pre-classified movies used for training
	public static Sentiments SENTIMENTS;
	// The memory-resident set of all movie keywords
	public static Set<String> SENTIMENT_KEYWORDS;
	// Classifier
	NaiveBayes classifier = new NaiveBayes();
	// A friendly logger which is used to output the progress of the input of the corpus
	private final Logger log;
	
	public SentimentClassification() {
		log = Logger.getLogger("SentimentClassification");
		SENTIMENTS = new Sentiments();
		readAllKeyWords();
		//Instances instances = createTemplate();
		//train(instances);
		//try {
		//	classifier.buildClassifier(instances);
		//} catch(Exception e) {
		//	e.printStackTrace();
		//}
		//classify(instances);		
	}
	
	private void readAllKeyWords() {
		SENTIMENT_KEYWORDS = new HashSet<String>();
		Map<String, String> positive = SENTIMENTS.getPositive();
		for(String userId : positive.keySet()) {
			SENTIMENT_KEYWORDS.addAll(getKeyWordsFromFilmReview(userId, positive.get(userId)));
		}
		Map<String, String> neutral = SENTIMENTS.getNeutral();
		for(String userId : neutral.keySet()) {
			SENTIMENT_KEYWORDS.addAll(getKeyWordsFromFilmReview(userId, positive.get(userId)));
		}
		Map<String, String> negative = SENTIMENTS.getNegative();
		for(String userId : negative.keySet()) {
			SENTIMENT_KEYWORDS.addAll(getKeyWordsFromFilmReview(userId, positive.get(userId)));
		}
	}
	
	/*
	private Instances createTemplate() {
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		for(String keyword : MOVIE_KEYWORDS) {
			attributes.add(new Attribute(keyword));
		}
		attributes.add(new Attribute("COMP4601Genre", MOVIE_GENRES));		
		Instances instances = new Instances("Rel", attributes, 10);
		instances.setClass(instances.attribute("COMP4601Genre"));
		return instances;
	}
	*/
	
	private List<String> getKeyWordsFromFilmReview(String userId, String filmId) {
		Review review = Reviews.getInstance().getReview(userId, filmId);
		String page = "";
 		if(review!=null) {			
 			page += review.getText();
 		}
		try {
			EnglishAnalyzer analyzer = new EnglishAnalyzer();
			ArrayList<String> words = new ArrayList<String>();
			TokenStream stream  = analyzer.tokenStream(null, new StringReader(page));
			stream.reset();
			while (stream.incrementToken()) {
				words.add(stream.getAttribute(CharTermAttribute.class).toString());
			}
			stream.close();
 			return words;
		} catch(Exception e) {
			e.printStackTrace();
		}
 		return new ArrayList<String>();
	}

}
