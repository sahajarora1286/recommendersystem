package edu.carleton.comp4601.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import edu.carleton.comp4601.dao.FriendRelations;
import edu.carleton.comp4601.dao.Reviews;
import edu.carleton.comp4601.models.Review;

public class CollabrativeFiltering {
	
	private double[][] originalRatings;
	private double[][] calculatedRatings;//= { {5, 3, 4, 4, -1},{3, 1, 2, 3, 3}, {4, 3, 4, 3, 5}, {3, 3, 1, 5, 4}, {1, 5, 5, 2, 1}};
	private String[] items;
	private String[] users;

	private double pred(int a,int p) {
		double avgA = 0;
		int count = 0;
		for(int i = 0; i < items.length; i++) {
			if(Double.compare(originalRatings[a][i],-1d)!=0) {
				//System.out.println("A rating:"+originalRatings[a][i]);
				avgA += originalRatings[a][i];
				count++;
			}
		}
		if(count!=0)
			avgA = avgA/(double)count;
		else
			avgA = 2; //Assume neutral if no data available
		
		//System.out.println("Average A rating: "+avgA);
		
		double top = 0;
		double bottom = 0;
		for(int b = 0; b < users.length; b++) {			
			if(b!=a && sim(a,b) > 0 && Double.compare(originalRatings[b][p],-1d)!=0) {
				double avgB = 0;
				count = 0;
				for(int j = 0; j < items.length; j++) {
					if(Double.compare(originalRatings[b][j],-1d)!=0) {
						//System.out.println("B rating:"+originalRatings[b][j]);
						avgB += originalRatings[b][j];
						count++;
					}
				}
				if(count!=0)
					avgB = avgB/(double)count;
				else
					avgB = 2; //Assume neutral if no data available
				//System.out.println("Average B rating: "+avgB);
				//System.out.println("rating of b item: "+originalRatings[b][p]);
				//System.out.println("similarity: "+sim(a,b));
				top += sim(a,b) * (originalRatings[b][p] - avgB);
				bottom += sim(a,b);
			}
		}
		if(bottom != 0)
			return avgA + top/bottom;
		else
			return -1d;
	}	
	
	private double sim(int a, int b) {
		double top = 0;
		double bottomA = 0;
		double bottomB = 0;

		for(int p = 0; p < items.length; p++) {
			if(Double.compare(originalRatings[a][p],-1d)!=0 && Double.compare(originalRatings[b][p],-1d)!=0) {
				double avgA = averageUserRating(a);
				double avgB = averageUserRating(b);

				top += (originalRatings[a][p]-avgA)*(originalRatings[b][p]-avgB);
				bottomA += Math.pow((originalRatings[a][p]-avgA),2);
				bottomB += Math.pow((originalRatings[b][p]-avgB),2);
			}
		}
		if(bottomB!=0&&bottomA!=0) {
			return top/(Math.sqrt(bottomA)*Math.sqrt(bottomB));
		} else {
			return 0d;
		}
	}
	
	private double averageUserRating(int a) {
		double avgA = 0;
		int count = 0;
		for(int i = 0; i < items.length; i++) {
			if(Double.compare(originalRatings[a][i],-1d)!=0) {
				avgA += originalRatings[a][i];
				count++;
			}
		}
		if(count!=0)
			return avgA/(double)count;
		else
			return 2; //Assume neutral if no data available					
	}
	
	public CollabrativeFiltering() {
		ConcurrentHashMap<String, ArrayList<String>> relations = FriendRelations.getInstance().getAllUserFriendsMap();
		for(String userId : relations.keySet()) {
			HashMap<String, Integer> movieMapping = new HashMap<String, Integer>();
			Integer arrayIndex = new Integer(0); //Start at the beginning of the movie item array
			Integer userIndex = new Integer(0); //Start at the beginning of the user array
			//System.out.print("User: "+userId);
			ConcurrentHashMap<String, Review> userReviews = Reviews.getInstance().getUserReviews(userId);
			for(String reviewId : userReviews.keySet()) {
				Review review = userReviews.get(reviewId);
				String movieId = review.getMovie();
				if(!movieMapping.containsKey(movieId)) {
					movieMapping.put(movieId, arrayIndex);
					arrayIndex++; //Go to the next movie index position
				}
			}
			for(String friendUserId : relations.get(userId)) {
				userIndex++;  //Total number of users in matrix
				//System.out.print(" Friend: "+friendUserId);
				ConcurrentHashMap<String, Review> friendsReviews = Reviews.getInstance().getUserReviews(friendUserId);
				for(String friendReviewId : friendsReviews.keySet()) {
					Review friendReview = friendsReviews.get(friendReviewId);
					String friendMovieId = friendReview.getMovie();
					if(!movieMapping.containsKey(friendMovieId)) {
						movieMapping.put(friendMovieId, arrayIndex);
						arrayIndex++; //Go to the next movie index position
					}			
				}
			}
			//System.out.println("Total users:"+(userIndex+1)+" total movies:"+arrayIndex);
			items = new String[arrayIndex];
			users = new String[userIndex+1];
			originalRatings = new double[userIndex+1][arrayIndex];
			calculatedRatings = new double[userIndex+1][arrayIndex];
			for(int i=0; i<userIndex+1; i++) {
				for(int j=0; j<arrayIndex; j++) {
					originalRatings[i][j] = -1d;
					calculatedRatings[i][j] = -1d;
				}
			}
			userIndex = 0;
			for(String reviewId : userReviews.keySet()) {
				Review review = userReviews.get(reviewId);
				String movieId = review.getMovie();
				double reviewValue = 1d; //Assume it is a negative review
				if(review.getSentiment().equals("Positive")) {
					reviewValue = 3d;
				} else if (review.getSentiment().equals("Neutral")) {
					reviewValue = 2d;
				}
				originalRatings[userIndex][movieMapping.get(movieId).intValue()] = reviewValue;
				calculatedRatings[userIndex][movieMapping.get(movieId).intValue()] = reviewValue;
			}
			for(String friendUserId : relations.get(userId)) {
				userIndex++;  //Total number of users in matrix
				//System.out.print(" Friend: "+friendUserId);
				ConcurrentHashMap<String, Review> friendsReviews = Reviews.getInstance().getUserReviews(friendUserId);
				for(String friendReviewId : friendsReviews.keySet()) {
					Review friendReview = friendsReviews.get(friendReviewId);
					String friendMovieId = friendReview.getMovie();
					double friendReviewValue = 1d; //Assume it is a negative review
					if(friendReview.getSentiment().equals("Positive")) {
						friendReviewValue = 3d;
					} else if (friendReview.getSentiment().equals("Neutral")) {
						friendReviewValue = 2d;
					}
					originalRatings[userIndex][movieMapping.get(friendMovieId).intValue()] = friendReviewValue;
					calculatedRatings[userIndex][movieMapping.get(friendMovieId).intValue()] = friendReviewValue;
				}
			}
			/*for(int i=0; i<userIndex+1; i++) {
				for(j=0; j<arrayIndex; j++) {
					
				}
			}*/
			for(int j=0; j<arrayIndex; j++) {
				if (Double.compare(originalRatings[0][j],-1d)==0) {
					calculatedRatings[0][j] = pred(0,j);
					System.out.println("0 "+j+" rating:"+(int)Math.round(calculatedRatings[0][j]));
				}
				//for(int i=0; i<userIndex+1; i++) {
					//System.out.println(i+" "+j+" rating:"+originalRatings[i][j]);
				//}
			}			
		}
		//items = new String[5];
		//users = new String[5];		
		//System.out.println(pred(0,4)); 
	}
	
	public static void main(String[] args) {
		CollabrativeFiltering collabrativeFiltering = new CollabrativeFiltering();
	}
}
