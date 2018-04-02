# Assignment 2
Sahaj Arora 100961220
Jennifer Franklin 100315764


## Steps to configure the project:
1. Start your Mongo database.
2. IF ON WINDOWS: in Controller.java line ?? under edu.carleton.comp4601.crawler change forward slashes (/) to back slashes (\)
3. This is a maven project that is also a dynamic web module project configure Eclipse accordingly
4. Right click your project then choose Maven -> Update Project before running the project

## Testing instructions 
All the REST web-services requested in the requirement doc have been implemented.

Once server is running:
- go to http://localhost:8011/COMP4601-RS/rest/rs to confirm the service is running

To start the crawler or reset it:
- go to http://localhost:8011/COMP4601-RS/rest/rs/reset/{dir}

To run movie and user classification and get an HTML representation of user profiles:
- go to http://localhost:8011/COMP4601-RS/rest/rs/context. Clicking on any "movie" link would call the web service /fetch/{user}/{page} that would display
the movie review, and 2 advertisements based on that user's and movie's classification.

To get an HTML representation of communities in our system:
- go to http://localhost:8011/COMP4601-RS/rest/rs/community. This will list all communities and the users belonging to each of those communities.

To get an HTML representation of a category:
- go to http://localhost:8011/COMP4601-RS/rest/rs/advertising/{category}, where category is one of:
	- Action
	- Adventure
	- Comedy
	- Horror
	- Drama
	- Thriller
	

