import java.util.*;

public class ThirdRatings {
    private ArrayList<Rater> newRaters;
    
    public ThirdRatings () {
        // default constructor
        this("ratings.csv");
    }
    
    public ThirdRatings (String ratingsfile) {
        FirstRatings firstRatings = new FirstRatings ();
        newRaters = firstRatings.loadRaters(ratingsfile);
    }
    
    public int getRaterSize () {
        return newRaters.size();
    }
    
    private double getAverageByID (String movId, int minimalRaters) {
        double sum = 0.0;
        int count = 0;
       
        for (Rater rater : newRaters) {
            if (rater.hasRating(movId)) {
                sum += rater.getRating(movId);
                count += 1;
            }
        }
        
        if (count >= minimalRaters) {
            return sum / count;
        } 
        else {
            return 0.0;
        }
    }
    
    public ArrayList<Rating1> getAverageRatings (int minimalRaters) {
        ArrayList<String> movies = MovieDatabase.filterBy(new TrueFilter());
        ArrayList<Rating1> averageRatings = new ArrayList<Rating1> ();
        
        for (String movId : movies) {
            double average = Math.round(getAverageByID(movId, minimalRaters) * 100.0) / 100.0;
            if (average != 0.0) {
                Rating1 rating = new Rating1 (movId, average);
                averageRatings.add(rating);
            }
        }
        
        return averageRatings;
    }
    
    public ArrayList<Rating1> getAverageRatingsByFilter (int minimalRaters, Filter filterCriteria) {
        ArrayList<Rating1> averageRatings = new ArrayList<Rating1> ();
        ArrayList<String> filteredMovies = MovieDatabase.filterBy(filterCriteria);
        
        for (String movId : filteredMovies) {
            double average = Math.round(getAverageByID(movId, minimalRaters) * 100.0) / 100.0;
            if (average != 0.0) {
                Rating1 rating = new Rating1 (movId, average);
                averageRatings.add(rating);
            }
        }
        return averageRatings;
    }
}