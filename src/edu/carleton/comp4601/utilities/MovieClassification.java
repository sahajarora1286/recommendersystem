// Sahaj Arora 100961220 Jennifer Franklin 100315764
package edu.carleton.comp4601.utilities;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

public class MovieClassification {
	public static ArrayList<String> MOVIE_GENRES = new ArrayList<String>(Arrays.asList(
			Constants.ACTION, Constants.ADVENTURE, Constants.COMEDY, Constants.DRAMA, Constants.HORROR, Constants.THRILLER));
	// The memory-resident array of stop words
	public static ArrayList<String> STOP_WORDS = new ArrayList<String>(Arrays.asList("a", "able", "about", "across", "after", "all", "almost", "also", "am", "among", "an", "and", "any", "are", "as", "at", "be", "because", "been", "but", "by", "can", "cannot", "could", "dear", "did", "do", "does", "either", "else", "ever", "every", "for", "from", "get", "got", "had", "has", "have", "he", "her", "hers", "him", "his", "how", "however", "i", "if", "in", "into", "is", "it", "its", "just", "least", "let", "like", "likely", "may", "me", "might", "most", "must", "my", "neither", "no", "nor", "not", "of", "off", "often", "on", "only", "or", "other", "our", "own", "rather", "said", "say", "says", "she", "should", "since", "so", "some", "than", "that", "the", "their", "them", "then", "there", "these", "they", "this", "tis", "to", "too", "twas", "us", "wants", "was", "we", "were", "what", "when", "where", "which", "while", "who", "whom", "why", "will", "with", "would", "yet", "you", "your"));
	// The class containing the list of pre-classified movies used for training
	public static Keywords KEYWORDS;
	// The memory-resident set of all movie keywords
	public static Set<String> MOVIE_KEYWORDS;
	// The memory-resident set of the count of each keyword per genre
	public static HashMap<String, Integer> ADVENTURE_KEYWORD_COUNT;	
	public static HashMap<String, Integer> COMEDY_KEYWORD_COUNT;
	public static HashMap<String, Integer> DRAMA_KEYWORD_COUNT;
	public static HashMap<String, Integer> HORROR_KEYWORD_COUNT;
	public static HashMap<String, Integer> THRILLER_KEYWORD_COUNT;
	// Classifier
	NaiveBayes classifier = new NaiveBayes();
	// A friendly logger which is used to output the progress of the input of the corpus
	private final Logger log;

	public MovieClassification() {
		log = Logger.getLogger("MovieClassification");
		KEYWORDS = new Keywords();
		readMovieWords();
		Instances instances = createTemplate();
		train(instances);
		try {
			classifier.buildClassifier(instances);
		} catch(Exception e) {
			e.printStackTrace();
		}
		classify(instances);
		//log.info("movie review count: "+Reviews.getInstance().getMovieCount());
	}
	
	public static void main(String[] args) {
		MovieClassification classifyMovies = new MovieClassification();
	}
	
	private void classify(Instances instances) {
		ConcurrentHashMap<String, Movie> movies = Movies.getInstance().getMovies();
		for(String movieId: movies.keySet()) {
			DenseInstance di = new DenseInstance(MOVIE_KEYWORDS.size()+1);
			di.setDataset(instances);
			List<String> reviewWords = getKeyWordsFromFilmReviews(movieId);
			HashMap<String, Integer> movieKeywordCount = new HashMap<String, Integer>();
			if(!reviewWords.isEmpty()) { //Remove once the crawl is fixed
				for (String word : reviewWords) {
					if(MOVIE_KEYWORDS.contains(word)) {
						if (movieKeywordCount.containsKey(word)) {
							movieKeywordCount.put(word, movieKeywordCount.get(word) + 1);
						} else {
							movieKeywordCount.put(word, 1);
						}
					}
				}
				if(!movieKeywordCount.keySet().isEmpty()) {
					for (String keyword : movieKeywordCount.keySet()) {
						di.setValue(instances.attribute(keyword), movieKeywordCount.get(keyword));
					}
					try {
						double classification = classifier.classifyInstance(di);
						Movies.getInstance().updateMovie(movieId, MOVIE_GENRES.get((int) Math.round(classification)));
						
						//log.info("movie: " + movieId + " is classified as " + MOVIE_GENRES.get((int) Math.round(classification)));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		log.info("finished classifying the movies");
	}
	
	private void train(Instances instances) {
		List<String> actions = KEYWORDS.getAction();
		for(String actionFilm : actions) {
			DenseInstance di = new DenseInstance(MOVIE_KEYWORDS.size()+1);
			int noActionWords = 0;
			int distinctActionWords = 0;
			HashMap<String, Integer> ACTION_KEYWORD_COUNT = new HashMap<String, Integer>();
			List<String> keywords = getKeyWordsFromFilmReviews(actionFilm);
			for(String keyword : keywords) {
				noActionWords++;
				if(ACTION_KEYWORD_COUNT.containsKey(keyword)) {
					ACTION_KEYWORD_COUNT.put(keyword, ACTION_KEYWORD_COUNT.get(keyword) + 1);
				} else {
					distinctActionWords++;
					ACTION_KEYWORD_COUNT.put(keyword, 1);
				}	
			}
			// Remove all of the words that we don't care about
			for (String stopWord : STOP_WORDS) {
				ACTION_KEYWORD_COUNT.remove(stopWord);
			}
			for(String keyword : ACTION_KEYWORD_COUNT.keySet()) {
				di.setValue(instances.attribute(keyword), ACTION_KEYWORD_COUNT.get(keyword));
			}			
			di.setValue(instances.attribute("COMP4601Genre"), Constants.ACTION);
			instances.add(di);
			log.info("no of action words: "+noActionWords+" no of distinct action words: "+distinctActionWords);
		}
		List<String> adventures = KEYWORDS.getAdventure();
		for(String adventureFilm : adventures) {
			DenseInstance di = new DenseInstance(MOVIE_KEYWORDS.size()+1);
			int noAdventureWords = 0;
			int distinctAdventureWords = 0;
			ADVENTURE_KEYWORD_COUNT = new HashMap<String, Integer>();
			List<String> keywords = getKeyWordsFromFilmReviews(adventureFilm);
			for(String keyword : keywords) {
				noAdventureWords++;
				if(ADVENTURE_KEYWORD_COUNT.containsKey(keyword)) {
					ADVENTURE_KEYWORD_COUNT.put(keyword, ADVENTURE_KEYWORD_COUNT.get(keyword) + 1);
				} else {
					distinctAdventureWords++;
					ADVENTURE_KEYWORD_COUNT.put(keyword, 1);
				}
			}
			// Remove all of the words that we don't care about
			for (String stopWord : STOP_WORDS) {
				ADVENTURE_KEYWORD_COUNT.remove(stopWord);
			}
			for(String keyword : ADVENTURE_KEYWORD_COUNT.keySet()) {
				if(MOVIE_KEYWORDS.contains(keyword))
					di.setValue(instances.attribute(keyword), ADVENTURE_KEYWORD_COUNT.get(keyword));
			}			
			di.setValue(instances.attribute("COMP4601Genre"), Constants.ADVENTURE);
			instances.add(di);
			log.info("no of adventure words: "+noAdventureWords+" no of distinct adventure words: "+distinctAdventureWords);
		}
		List<String> comedys = KEYWORDS.getComedy();
		for(String comedyFilm : comedys) {
			DenseInstance di = new DenseInstance(MOVIE_KEYWORDS.size()+1);
			int noComedyWords = 0;
			int distinctComedyWords = 0;
			COMEDY_KEYWORD_COUNT = new HashMap<String, Integer>();
			List<String> keywords = getKeyWordsFromFilmReviews(comedyFilm);
			for(String keyword : keywords) {
				noComedyWords++;
				if(COMEDY_KEYWORD_COUNT.containsKey(keyword)) {
					COMEDY_KEYWORD_COUNT.put(keyword, COMEDY_KEYWORD_COUNT.get(keyword) + 1);
				} else {
					distinctComedyWords++;
					COMEDY_KEYWORD_COUNT.put(keyword, 1);
				}
			}
			// Remove all of the words that we don't care about
			for (String stopWord : STOP_WORDS) {
				COMEDY_KEYWORD_COUNT.remove(stopWord);
			}
			for(String keyword : COMEDY_KEYWORD_COUNT.keySet()) {
				if(MOVIE_KEYWORDS.contains(keyword))
					di.setValue(instances.attribute(keyword), COMEDY_KEYWORD_COUNT.get(keyword));
			}			
			di.setValue(instances.attribute("COMP4601Genre"), Constants.COMEDY);
			instances.add(di);
			log.info("no of comedy words: "+noComedyWords+" no of distinct comedy words: "+distinctComedyWords);
		}
		
		List<String> dramas = KEYWORDS.getDrama();
		for(String dramaFilm : dramas) {
			DenseInstance di = new DenseInstance(MOVIE_KEYWORDS.size()+1);
			int noDramaWords = 0;
			int distinctDramaWords = 0;
			DRAMA_KEYWORD_COUNT = new HashMap<String, Integer>();
			List<String> keywords = getKeyWordsFromFilmReviews(dramaFilm);
			for(String keyword : keywords) {
				noDramaWords++;
				if(DRAMA_KEYWORD_COUNT.containsKey(keyword)) {
					DRAMA_KEYWORD_COUNT.put(keyword, DRAMA_KEYWORD_COUNT.get(keyword) + 1);
				} else {
					distinctDramaWords++;
					DRAMA_KEYWORD_COUNT.put(keyword, 1);
				}
			}
			// Remove all of the words that we don't care about
			for (String stopWord : STOP_WORDS) {
				DRAMA_KEYWORD_COUNT.remove(stopWord);
			}
			for(String keyword : DRAMA_KEYWORD_COUNT.keySet()) {
				if(MOVIE_KEYWORDS.contains(keyword))
					di.setValue(instances.attribute(keyword), DRAMA_KEYWORD_COUNT.get(keyword));
			}			
			di.setValue(instances.attribute("COMP4601Genre"), Constants.DRAMA);
			instances.add(di);
			log.info("no of drama words: "+noDramaWords+" no of distinct drama words: "+distinctDramaWords);
		}
		
		List<String> horrors = KEYWORDS.getHorror();
		for(String horrorFilm : horrors) {
			DenseInstance di = new DenseInstance(MOVIE_KEYWORDS.size()+1);
			int noHorrorWords = 0;
			int distinctHorrorWords = 0;
			HORROR_KEYWORD_COUNT = new HashMap<String, Integer>();
			List<String> keywords = getKeyWordsFromFilmReviews(horrorFilm);
			for(String keyword : keywords) {
				noHorrorWords++;
				if(HORROR_KEYWORD_COUNT.containsKey(keyword)) {
					HORROR_KEYWORD_COUNT.put(keyword, HORROR_KEYWORD_COUNT.get(keyword) + 1);
				} else {
					distinctHorrorWords++;
					HORROR_KEYWORD_COUNT.put(keyword, 1);
				}
			}
			// Remove all of the words that we don't care about
			for (String stopWord : STOP_WORDS) {
				HORROR_KEYWORD_COUNT.remove(stopWord);
			}
			for(String keyword : HORROR_KEYWORD_COUNT.keySet()) {
				if(MOVIE_KEYWORDS.contains(keyword))
					di.setValue(instances.attribute(keyword), HORROR_KEYWORD_COUNT.get(keyword));
			}			
			di.setValue(instances.attribute("COMP4601Genre"), Constants.HORROR);
			instances.add(di);
			log.info("no of horror words: "+noHorrorWords+" no of distinct horror words: "+distinctHorrorWords);
		}
		
		List<String> thrillers = KEYWORDS.getThriller();
		for(String thrillerFilm : thrillers) {
			DenseInstance di = new DenseInstance(MOVIE_KEYWORDS.size()+1);
			int noThrillerWords = 0;
			int distinctThrillerWords = 0;
			THRILLER_KEYWORD_COUNT = new HashMap<String, Integer>();
			List<String> keywords = getKeyWordsFromFilmReviews(thrillerFilm);
			for(String keyword : keywords) {
				noThrillerWords++;
				if(THRILLER_KEYWORD_COUNT.containsKey(keyword)) {
					THRILLER_KEYWORD_COUNT.put(keyword, THRILLER_KEYWORD_COUNT.get(keyword) + 1);
				} else {
					distinctThrillerWords++;
					THRILLER_KEYWORD_COUNT.put(keyword, 1);
				}
			}
			// Remove all of the words that we don't care about
			for (String stopWord : STOP_WORDS) {
				THRILLER_KEYWORD_COUNT.remove(stopWord);
			}			
			for(String keyword : THRILLER_KEYWORD_COUNT.keySet()) {
				if(MOVIE_KEYWORDS.contains(keyword))
					di.setValue(instances.attribute(keyword), THRILLER_KEYWORD_COUNT.get(keyword));
			}			
			di.setValue(instances.attribute("COMP4601Genre"), Constants.THRILLER);
			instances.add(di);
			log.info("no of thriller words: "+noThrillerWords+" no of distinct thriller words: "+distinctThrillerWords);
		}

	}
	
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
	
	private List<String> getKeyWordsFromFilmReviews(String filmId) {
		ConcurrentHashMap<String, Review> reviews = Reviews.getInstance().getMovieReviews(filmId);
		String page = "";
 		if(!reviews.isEmpty()) {			
 			for(String reviewId : reviews.keySet()) {
 				page += reviews.get(reviewId).getText();
 			}
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
		
	private void readMovieWords() {
		MOVIE_KEYWORDS = new HashSet<String>();
		List<String> actions = KEYWORDS.getAction();
		for(String actionFilm : actions) {
			MOVIE_KEYWORDS.addAll(getKeyWordsFromFilmReviews(actionFilm));
		}
		List<String> adventures = KEYWORDS.getAdventure();
		for(String adventureFilm : adventures) {
			MOVIE_KEYWORDS.addAll(getKeyWordsFromFilmReviews(adventureFilm));
		}
		List<String> comedies = KEYWORDS.getComedy();
		for(String comedyFilm : comedies) {
			MOVIE_KEYWORDS.addAll(getKeyWordsFromFilmReviews(comedyFilm));
		}
		List<String> dramas = KEYWORDS.getDrama();
		for(String dramaFilm : dramas) {
			MOVIE_KEYWORDS.addAll(getKeyWordsFromFilmReviews(dramaFilm));
		}
		List<String> horrors = KEYWORDS.getHorror();
		for(String horrorFilm : horrors) {
			MOVIE_KEYWORDS.addAll(getKeyWordsFromFilmReviews(horrorFilm));
		}
		List<String> thrillers = KEYWORDS.getThriller();
		for(String thrillerFilm : thrillers) {
			MOVIE_KEYWORDS.addAll(getKeyWordsFromFilmReviews(thrillerFilm));
		}
		// Remove all of the words that we don't care about
		for (String stopWord : STOP_WORDS) {
			MOVIE_KEYWORDS.remove(stopWord);
		}
	}

}
