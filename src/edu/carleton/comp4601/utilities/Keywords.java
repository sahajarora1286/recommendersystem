package edu.carleton.comp4601.utilities;

import java.util.ArrayList;
import java.util.List;

public class Keywords {
	//sentiments
	private List<String> comedy;
	private List<String> horror;
	private List<String> action;
	private List<String> thriller;	
	private List<String> drama;
	private List<String> adventure;

	public Keywords() {
		comedy = new ArrayList<String>();
		comedy.add("0790742322");
		comedy.add("0790742942");
		comedy.add("0800141709");
		comedy.add("080017948X");
		comedy.add("0792140923");
		comedy.add("0788812262");
		drama = new ArrayList<String>();
		drama.add("0767800117");
		drama.add("0780625633");
		drama.add("0790701251");
		drama.add("0790738139");
		drama.add("079213690X");
		drama.add("0792140923");
		drama.add("0792838289");
		drama.add("630266232X");
		drama.add("630394518X");
		thriller = new ArrayList<String>();
		thriller.add("0780622553");
		thriller.add("630268644X");
		thriller.add("B00005B238");
		thriller.add("B00006FMG0");
		adventure = new ArrayList<String>();
		adventure.add("078062565X");
		adventure.add("0783235208");
		adventure.add("0792158288");
		adventure.add("B00005JNCZ");
		action = new ArrayList<String>();
		action.add("0784010331");
		action.add("0784011923");
		action.add("0783226128");
		action.add("0790729989");
		action.add("0790738147");
		action.add("079074404X");
		action.add("630024203X");
		horror = new ArrayList<String>();
		horror.add("B000VDDWEC");
		horror.add("B0000C24F3");
		horror.add("B00004RJ74");
		horror.add("B004RE29T0");
	}

	public List<String> getComedy() {
		return comedy;
	}

	public List<String> getHorror() {
		return horror;
	}

	public List<String> getAction() {
		return action;
	}

	public List<String> getThriller() {
		return thriller;
	}

	public List<String> getDrama() {
		return drama;
	}

	public List<String> getAdventure() {
		return adventure;
	}

}
