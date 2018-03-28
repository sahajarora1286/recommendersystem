package edu.carleton.comp4601.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

public class MovieClassification {

	public MovieClassification() {
		Set<String> movieKeywords = new HashSet<String>();
		Keywords keywords = new Keywords();
		List<String> actions = keywords.getAction();
		for(String actionFilm : actions) {
			//TODO database lookups here
			//Stub function returning string arraylist of keywords from reviews here
			movieKeywords.addAll(getKeyWordsFromFilmReviews(actionFilm));
		}
		
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		//(key), keywords
		
		//Instances  = new Instances("Name", attributes);
		new DenseInstance(movieKeywords.size()+1);
	}
	
	private List<String> getKeyWordsFromFilmReviews(String filmId) {
		String page = "";
		switch(filmId) {
			case "0767800117":
				page = "AGEIT17HENDIS Over the past several years that this space has existed I have touted the heroic experiences of the American Civil War pro-Union black volunteer regiment, the Massachusetts 54th Infantry many times.";
				break;
			case "0790742322":
				page = "AFV2584U13XP3 If this is what the afterlife is going to be like then I guess it won't be so bad when it's my time though gotta stay away from anyone like Beetlejuice.";
				break;
			default:
				page = "A38921VU3NJDFK Of no relation to or influence from the 1950 or 1990 films of the same title, this stylish, atmospheric, and very creepy sci-fi noir features a great cast, striking, dreamlike visuals, and plays out like a nihilistic nightmare";
				break;
		}
 		
		String[] words = page.split("\\s");
		return Arrays.asList(words);
	}

}
