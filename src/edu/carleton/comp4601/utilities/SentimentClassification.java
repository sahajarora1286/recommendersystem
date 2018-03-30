package edu.carleton.comp4601.utilities;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import edu.carleton.comp4601.dao.Movies;
import edu.carleton.comp4601.dao.Reviews;
import edu.carleton.comp4601.models.Movie;
import edu.carleton.comp4601.models.Review;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.DenseInstance;
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
		Instances instances = createTemplate();
		train(instances);
		try {
			classifier.buildClassifier(instances);
		} catch(Exception e) {
			e.printStackTrace();
		}
		classify(instances);		
	}
	
	public static void main(String[] args) {
		SentimentClassification classifyReviewSentiments = new SentimentClassification();
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
	
	private Instances createTemplate() {
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		for(String keyword : SENTIMENT_KEYWORDS) {
			attributes.add(new Attribute(keyword));
		}
		attributes.add(new Attribute("COMP4601Sentiment", SENTIMENT_CATEGORIES));		
		Instances instances = new Instances("Rel", attributes, 10);
		instances.setClass(instances.attribute("COMP4601Sentiment"));
		return instances;
	}
	
	private void train(Instances instances) {
		Map<String, String> positiveReviews = SENTIMENTS.getPositive();
		for(String userId : positiveReviews.keySet()) {
			DenseInstance di = new DenseInstance(SENTIMENT_KEYWORDS.size()+1);
			int noReviewWords = 0;
			int distinctReviewWords = 0;
			HashMap<String, Integer> positiveReviewKeywordCount = new HashMap<String, Integer>();
			List<String> keywords = getKeyWordsFromFilmReview(userId, positiveReviews.get(userId));
			for(String keyword : keywords) {
				noReviewWords++;
				if(positiveReviewKeywordCount.containsKey(keyword)) {
					positiveReviewKeywordCount.put(keyword, positiveReviewKeywordCount.get(keyword) + 1);
				} else {
					distinctReviewWords++;
					positiveReviewKeywordCount.put(keyword, 1);
				}	
			}
			// Remove all of the words that we don't care about
			for (String stopWord : STOP_WORDS) {
				positiveReviewKeywordCount.remove(stopWord);
			}
			for(String keyword : positiveReviewKeywordCount.keySet()) {
				di.setValue(instances.attribute(keyword), positiveReviewKeywordCount.get(keyword));
			}			
			di.setValue(instances.attribute("COMP4601Sentiment"), "Positive");
			instances.add(di);
			log.info("no of postive review words: "+noReviewWords+" no of distinct positive review words: "+distinctReviewWords);
		}
		Map<String, String> negativeReviews = SENTIMENTS.getNegative();
		for(String userId : negativeReviews.keySet()) {
			DenseInstance di = new DenseInstance(SENTIMENT_KEYWORDS.size()+1);
			int noReviewWords = 0;
			int distinctReviewWords = 0;
			HashMap<String, Integer> negativeReviewKeywordCount = new HashMap<String, Integer>();
			List<String> keywords = getKeyWordsFromFilmReview(userId, negativeReviews.get(userId));
			for(String keyword : keywords) {
				noReviewWords++;
				if(negativeReviewKeywordCount.containsKey(keyword)) {
					negativeReviewKeywordCount.put(keyword, negativeReviewKeywordCount.get(keyword) + 1);
				} else {
					distinctReviewWords++;
					negativeReviewKeywordCount.put(keyword, 1);
				}	
			}
			// Remove all of the words that we don't care about
			for (String stopWord : STOP_WORDS) {
				negativeReviewKeywordCount.remove(stopWord);
			}
			for(String keyword : negativeReviewKeywordCount.keySet()) {
				di.setValue(instances.attribute(keyword), negativeReviewKeywordCount.get(keyword));
			}			
			di.setValue(instances.attribute("COMP4601Sentiment"), "Negative");
			instances.add(di);
			log.info("no of postive review words: "+noReviewWords+" no of distinct positive review words: "+distinctReviewWords);
		}
		Map<String, String> neutralReviews = SENTIMENTS.getNegative();
		for(String userId : neutralReviews.keySet()) {
			DenseInstance di = new DenseInstance(SENTIMENT_KEYWORDS.size()+1);
			int noReviewWords = 0;
			int distinctReviewWords = 0;
			HashMap<String, Integer> neutralReviewKeywordCount = new HashMap<String, Integer>();
			List<String> keywords = getKeyWordsFromFilmReview(userId, neutralReviews.get(userId));
			for(String keyword : keywords) {
				noReviewWords++;
				if(neutralReviewKeywordCount.containsKey(keyword)) {
					neutralReviewKeywordCount.put(keyword, neutralReviewKeywordCount.get(keyword) + 1);
				} else {
					distinctReviewWords++;
					neutralReviewKeywordCount.put(keyword, 1);
				}	
			}
			// Remove all of the words that we don't care about
			for (String stopWord : STOP_WORDS) {
				neutralReviewKeywordCount.remove(stopWord);
			}
			for(String keyword : neutralReviewKeywordCount.keySet()) {
				di.setValue(instances.attribute(keyword), neutralReviewKeywordCount.get(keyword));
			}			
			di.setValue(instances.attribute("COMP4601Sentiment"), "Neutral");
			instances.add(di);
			log.info("no of postive review words: "+noReviewWords+" no of distinct positive review words: "+distinctReviewWords);
		}
	}
	
	private void classify(Instances instances) {
		ConcurrentHashMap<String, Review> reviews = Reviews.getInstance().getAllReviews();
		for(String reviewId: reviews.keySet()) {
			DenseInstance di = new DenseInstance(SENTIMENT_KEYWORDS.size()+1);
			di.setDataset(instances);
			List<String> reviewWords = getKeyWordsFromFilmReview(reviewId);
			HashMap<String, Integer> reviewKeywordCount = new HashMap<String, Integer>();
			if(!reviewWords.isEmpty()) { //Remove once the crawl is fixed
				for (String word : reviewWords) {
					if(SENTIMENT_KEYWORDS.contains(word)) {
						if (reviewKeywordCount.containsKey(word)) {
							reviewKeywordCount.put(word, reviewKeywordCount.get(word) + 1);
						} else {
							reviewKeywordCount.put(word, 1);
						}
					}
				}
				if(!reviewKeywordCount.keySet().isEmpty()) {
					for (String keyword : reviewKeywordCount.keySet()) {
						di.setValue(instances.attribute(keyword), reviewKeywordCount.get(keyword));
					}
					try {
						double sentiment = classifier.classifyInstance(di);
						
						log.info("review: " + reviewId + " is classified as " + SENTIMENT_CATEGORIES.get((int) Math.round(sentiment)));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		log.info("finished classifying the user's movie sentiments");
	}

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
	
	private List<String> getKeyWordsFromFilmReview(String reviewId) {
		Review review = Reviews.getInstance().getReview(reviewId);
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
