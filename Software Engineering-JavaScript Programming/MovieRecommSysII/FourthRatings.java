import java.util.*;

public class FourthRatings {
    public FourthRatings () {
        // default constructor
        this("ratings.csv");
    }
    
    public FourthRatings (String ratingsfile) {
        RaterDatabase.initialize(ratingsfile);
    }
     
    private double getAverageByID (String movId, int minimalRaters) {
        double sum = 0.0;
        int count = 0;
       
        for (Rater rater : RaterDatabase.getRaters()) {
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
     
    public ArrayList<Rating1> getAverageRatings1 (int minimalRaters) {
        ArrayList<Rating1> averageRatings = new ArrayList<Rating1> ();
        ArrayList<String> movies = MovieDatabase.filterBy(new TrueFilter());
        
        for (String movId : movies) {
            double average = Math.round(getAverageByID(movId, minimalRaters) * 100.0) / 100.0;
            if (average != 0.0) {
                Rating1 newdat = new Rating1 (movId, average);
                averageRatings.add(newdat);
            }
        }
        
        return averageRatings;
    }
     
    public ArrayList<Rating1> getAverageRatingsByFilter (int minimalRaters, Filter newFilter) {
        ArrayList<Rating1> averageRatings = new ArrayList<Rating1> ();
        ArrayList<String> newMovies = MovieDatabase.filterBy(newFilter);
         
        for (String movId : newMovies) {
            double average = Math.round(getAverageByID(movId, minimalRaters) * 100.0) / 100.0;
            if (average != 0.0) {
                Rating1 newdat = new Rating1 (movId, average);
                averageRatings.add(newdat);
            }
        }
        return averageRatings;
    }
    
    //The method translate a rating from the scale 0 to 10 to the scale ­5 to 5 and return
    //the dot product of the ratings of movies that they both rated
    private double dotProduct (Rater rater1, Rater rater2) {
        double dotProduct = 0.0;
        ArrayList<String> newRatedDat = rater1.getItemsRated();
        
        for (String movId : newRatedDat) {
            if (rater2.getItemsRated().contains(movId)) {
                double currRatingR2 = rater2.getRating(movId);
                double currRatingR1 = rater1.getRating(movId);
                
                dotProduct += (currRatingR2 - 5.0) * (currRatingR1 - 5.0);
            }
        }
        return dotProduct;
    }
    
    private ArrayList<Rating1> getSimilarities (String rater_id) {
        ArrayList<Rating1> similarities = new ArrayList<Rating1> ();
        Rater me = RaterDatabase.getRater(rater_id);
        
        for (Rater currRater : RaterDatabase.getRaters()) {
            if (! currRater.getID().equals(rater_id)) {
               double dotProduct = dotProduct(me, currRater);
               if (dotProduct >= 0) {
                   similarities.add(new Rating1(currRater.getID(), dotProduct));
               }
            }
        }
        Collections.sort(similarities, Collections.reverseOrder());
        return similarities;
    }
    
    public ArrayList<Rating1> getSimilarRatings (String rater_id, int numSimilarRaters, int minimalRaters) {
        ArrayList<Rating1> newRatings = new ArrayList<Rating1> ();
        ArrayList<Rating1> similarList = getSimilarities(rater_id);
        ArrayList<String> movies = MovieDatabase.filterBy(new TrueFilter());
        
        HashMap<String,Double> accumulatedRating = new HashMap<String,Double> ();
        HashMap<String,Integer> accumulatedCount = new HashMap<String,Integer> ();
        
        for (String movId : movies) {
            double currRating = 0.0;
            int currCount = 0;
            
            for (int k=0; k < numSimilarRaters; k++) {
                Rating1 rating = similarList.get(k);
                String raterID = rating.getItem();
                double weight = rating.getValue();
                
                Rater rater = RaterDatabase.getRater(raterID);
                
                if (rater.hasRating(movId)) {
                    double _rating = rater.getRating(movId) * weight; //formula
                    currRating += _rating;
                    currCount += 1;
                }
            }
            
            if (currCount >= minimalRaters) {
                accumulatedRating.put(movId, currRating);
                accumulatedCount.put(movId, currCount);
            }
        }
        
        for (String movId : accumulatedRating.keySet()) {
            double new_Rating = Math.round((accumulatedRating.get(movId) / accumulatedCount.get(movId)) * 100.0) / 100.0;
            Rating1 rating = new Rating1(movId, new_Rating);
            newRatings.add(rating);
        }
        
        Collections.sort(newRatings, Collections.reverseOrder());
        return newRatings;
    }
    
    //bring some filter in, for example:
    //String genre = "Action"; GenreFilter gf = new GenreFilter(genre);
    //String rater_id = "65"; int numSimilarRaters = 20; int minimalRaters = 5;
    public ArrayList<Rating1> getSimilarRatingsByFilter 
    (String rater_id, int numSimilarRaters, int minimalRaters, Filter newFilter) {
        ArrayList<Rating1> newRatings = new ArrayList<Rating1> ();
        ArrayList<Rating1> similarList = getSimilarities(rater_id);
        ArrayList<String> filteredMovies = MovieDatabase.filterBy(newFilter);
        
        HashMap<String,Double> accumulatedRating = new HashMap<String,Double> ();
        HashMap<String,Integer> accumulatedCount = new HashMap<String,Integer> ();
        
        for (String movId : filteredMovies) {
            double currRating = 0.0;
            int currCount = 0;
            
            for (int k=0; k < numSimilarRaters; k++) {
                Rating1 rating = similarList.get(k);
                String raterID = rating.getItem();
                double weight = rating.getValue();
                
                Rater rater = RaterDatabase.getRater(raterID);
                
                if (rater.hasRating(movId)) {
                    //refer to a formula;
                    double _rating = rater.getRating(movId) * weight;
                    currRating += _rating;
                    currCount += 1;
                }
            }
            
            if (currCount >= minimalRaters) {
                accumulatedRating.put(movId, currRating);
                accumulatedCount.put(movId, currCount);
            }
        }
        
        for (String movieID : accumulatedRating.keySet()) {
            double new_Rating = Math.round((accumulatedRating.get(movieID) / accumulatedCount.get(movieID)) * 100.0) / 100.0;
            Rating1 rating = new Rating1(movieID, new_Rating);
            newRatings.add(rating);
        }
        //sorting
        Collections.sort(newRatings, Collections.reverseOrder());
        return newRatings;
    }
    
    //tester;
}