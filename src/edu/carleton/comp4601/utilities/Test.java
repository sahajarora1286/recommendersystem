package edu.carleton.comp4601.utilities;

import edu.carleton.comp4601.services.DbService;

public class Test {

	public static void main(String[] args) {
		if(!DbService.getInstance().isClassified()) {
			System.out.println("not classified");
			try {
				DbService.getInstance().createCollection(DbCollection.CLASSIFIED);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if(!DbService.getInstance().isClassified()) {
			System.out.println("something is wrong");
		}


	}

}
