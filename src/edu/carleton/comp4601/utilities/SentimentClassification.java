package edu.carleton.comp4601.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import edu.carleton.comp4601.dao.Reviews;
import edu.carleton.comp4601.models.Review;

public class SentimentClassification {
	private static final ArrayList<String> NEGATIVE_PHRASES = new ArrayList<String>(Arrays.asList(
			"it sucked",
			"the movie sucked",
			"it sucks",
			"the movie was terrible",
			"hated it",
			"disliked it",
			"was worse than",
			"boring",
			"not interesting",
			"disappointing movie",
			"it was disappointing",
			"lack originality",
			"look the same",
			"sound the same",
			"too long",
			"missed the mark",
			"was bad",
			"gets old",
			"takes away",
			"bit of a disappointment",
			"lot of frustration",
			"this movie's full of gaping holes",
			"derivative the film is",
			"what a waste",
			"lack of an original thought",
			"downward spiral",
			"gets boring after awhile",
			"don't bother",
			"horrible",
			"bad movie",
			"too bad",
			"movie was so bad",
			"but not as good",
			"definitely not recommended",
			"turn your brain off",
			"spoiled",
			"did nothing for the story",
			"a waste of time",
			"boring movie",
			"difficult to follow",
			"unconvincing finale",
			"over the top",
			"not good at all",
			"a missed opportunity",
			"biggest dud ever",
			"should just not have been made",
			"mess of a film",
			"movie is terribly dull",
			"not bother buying",
			"worst movies i have ever seen",
			"it just doesn't work",
			"convoluted mess",
			"it doesn't gel",
			"a little too slow",
			"plot-holes",
			"disappointed",
			"i didn't like this movie",
			"bad acting",
			"worst",
			"i don't like",
			"the ending sucked",
			"terrible",
			"couldn't get into",
			"turn your mind off",
			"dumb plot",
			"not great",
			"less interesting",
			"nonsense",
			"least funniest",
			"stupid",
			"more than this",
			"2 star",
			"1 star"));
	private static final ArrayList<String> POSITIVE_PHRASES = new ArrayList<String>(Arrays.asList(
			"best movie ever",
			"adored this movie",
			"enjoyed it",
			"loads of laughs",
			"great movie",
			"laughed hilariously",
			"thoroughly enjoyed",
			"liked it",
			"it was fun",
			"sheer entertainment",
			"incredible entertainment experience",
			"lots of action",
			"the best",
			"great-looking",
			"high-energy",
			"they did a good job",
			"good science fiction film",
			"breathtaking action sequences",
			"exciting stunts",
			"astonishing special effects",
			"highly recommended",
			"accomplished film",
			"beautifully choreographed actions scenes",
			"good script",
			"pull off this movie",
			"highly recommend",
			"best film",
			"mind-blowing special effects",
			"smart idea",
			"film is more",
			"entertaining sci-fi film",
			"funny moments",
			"decent action",
			"fair story",
			"definitely a movie you should check out",
			"really good",
			"both excellent",
			"an evening's entertainment",
			"thought-provoking tale",
			"great action/sci-fi movie",
			"emotional story",
			"otherwise, okay",
			"very impressed with this movie",
			"better science fiction movies",
			"excellent big-budget action adventure",
			"is decent",
			"thought-provoking",
			"a great film",
			"a better film",
			"is great",
			"lots of good pieces",
			"seems pretty good",
			"script is ok",
			"this movie has much to say",
			"enjoyed this film",
			"movie was good",
			"good premise",
			"worth every penny",
			"great watch",
			"not a bad movie",
			"pretty entertaining movie",
			"outshines them all",
			"fabulous",
			"great adventure",
			"plenty of action",
			"good chemistry",
			"definately one to see",
			"impressed",
			"happy to watch it again",
			"spectacular",
			"underrated",
			"good film",
			"enjoyable",
			"great premise",
			"fun ride",
			"will satisfy",
			"super performance",
			"first rate adventure movie",
			"a movie i just love",
			"this movie is a top",
			"great action movie",
			"entertaining film",
			"you'll enjoy",
			"high quality entertainment",
			"action sequences are great",
			"fun-filled ride",
			"one of my favourite",
			"suspend disbelief",
			"enjoy the film",
			"incredible flick",
			"you can't stop watching",
			"great job",
			"imaginative effects",
			"have fun",
			"darn good action/adventure flick",
			"type of movie you can watch again and again",
			"something to behold",
			"fast paced action",
			"movie is a good action/comedy thriller",
			"high action movie",
			"it will provide for an evening's entertainment",
			"movie of the year",
			"this movie was not that bad",
			"a good film",
			"one of the most enjoyable",
			"an ok movie",
			"worth the time to watch",
			"submarine of fun",
			"delight to watch",
			"good entertainment",
			"its pretty good itself",
			"solid film",
			"it's worth recommending",
			"well written",
			"well presented",
			"it wasn't half bad",
			"won't disappoint fans",
			"was good",
			"it is just as good",
			"good ending",
			"what more could you want",
			"movie feeling fresh",
			"lives up to the legacy",
			"a kick to watch",
			"get a kick",
			"enjoy",
			"entertaining",
			"good movie",
			"great acting",
			"good plot",
			"good story",
			"terrific movie",
			"excellent",
			"worth buying",
			"masterpiece",
			"best",
			"excellant film",
			"first-rate",
			"solid performance",
			"must see",
			"film is amazing",
			"love this film",
			"timeless story",
			"even better",
			"the movie was just fine",
			"great buy",
			"film for all time",
			"one of the greatest movies",
			"greatest flick",
			"fantastic film",
			"favorite",
			"classic",
			"superb",
			"awesome",
			"very good",
			"incredible job",
			"pleasantly surprised",
			"seen this movie more times than any other",
			"terrific",
			"perfect",
			"loved",
			"big fan",
			"faves",
			"great action",
			"like it",
			"hilarious",
			"funniest",
			"4 star",
			"5 star",
			"was decent",
			"amazing"));
	private static final ArrayList<String> NEUTRAL_PHRASES = new ArrayList<String>(Arrays.asList(
			"torn over",
			"uncertain how i feel about",
			"neither liked nor disliked",
			"mixed feelings",
			"take the middle road",
			"it was okay",
			"often ruined",
			"worth a rental",
			"not so good, not so bad",
			"popcorn filler",
			"sometimes is good",
			"recommending that you rent",
			"a good try",
			"mediocre movie",
			"suspend your disbelief",
			"peculiar movie",
			"popcorn movie",
			"a typical horror flick",
			"predictable film",
			"3 star"));
	private final Logger log;
	
	public SentimentClassification() {
		log = Logger.getLogger("SentimentClassification");
		log.info("beginning to classify review's sentiments");
		ConcurrentHashMap<String, Review> reviews = Reviews.getInstance().getAllReviews();
		for(String reviewId : reviews.keySet()) {
			Review review = Reviews.getInstance().getReview(reviewId);
			int positivePhraseCount = 0;
			int negativePhraseCount = 0;
			int neutralPhraseCount = 0;
			String text = review.getText().toLowerCase();
			for(String phrase : POSITIVE_PHRASES) {
				if(text.contains(phrase))
					positivePhraseCount++;
			}
			for(String phrase : NEGATIVE_PHRASES) {
				if(text.contains(phrase))
					negativePhraseCount++;
			}
			for(String phrase : NEUTRAL_PHRASES) {
				if(text.contains(phrase))
					neutralPhraseCount++;
			}
			if(positivePhraseCount>0 || negativePhraseCount>0 || neutralPhraseCount>0) {
				if(neutralPhraseCount > positivePhraseCount && neutralPhraseCount > negativePhraseCount) {
					Reviews.getInstance().updateReviewSentiment(reviewId, "Neutral");
				} else if(positivePhraseCount >= neutralPhraseCount && positivePhraseCount > negativePhraseCount) {
					Reviews.getInstance().updateReviewSentiment(reviewId, "Positive");
				} else if(negativePhraseCount >= neutralPhraseCount && negativePhraseCount > positivePhraseCount) {
					Reviews.getInstance().updateReviewSentiment(reviewId, "Negative");
				} else if(negativePhraseCount == positivePhraseCount) {
					Reviews.getInstance().updateReviewSentiment(reviewId, "Neutral");
				} else {
					Reviews.getInstance().updateReviewSentiment(reviewId, "Neutral");
				}				
			} else {
				Reviews.getInstance().updateReviewSentiment(reviewId, "Neutral");
			}
		}
		log.info("finished classifying review's sentiments");
	}
	
	public static void main(String[] args) {
		SentimentClassification classifyReviewSentiments = new SentimentClassification();
	}

}
