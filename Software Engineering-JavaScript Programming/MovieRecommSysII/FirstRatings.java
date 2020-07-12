import edu.duke.*;
import java.util.*;
import org.apache.commons.csv.*;

public class FirstRatings {
    public ArrayList<Movie> loadMovies (String filename) {
        ArrayList<Movie> movieDat = new ArrayList<Movie> ();
        
        FileResource fr = new FileResource("data/" + filename + ".csv");
        CSVParser parser = fr.getCSVParser();
        
        for (CSVRecord record : parser) {
            String movId = record.get("id");
            String title = record.get("title");
            String year = record.get("year");
            String country = record.get("country");
            String genre = record.get("genre");
            String director = record.get("director");
            int minutes = Integer.parseInt(record.get("minutes"));
            String poster = record.get("poster");
            Movie m = new Movie(movId, title, year, country, genre, director, minutes, poster);
            movieDat.add(m);
        }
        
        return movieDat;
    }
    
    public ArrayList<Rater> loadRaters (String filename) {
        ArrayList<Rater> ratersDat = new ArrayList<Rater> ();
        ArrayList<String> IDs = new ArrayList<String> ();
        //ArrayList<Rating1> myRatings; 
        
        FileResource fr = new FileResource("data/" + filename + ".csv");
        CSVParser records = fr.getCSVParser();
        //HashMap<String, ArrayList<rating>> mapRaters = new HashMap<String, ArrayList<rating>>();
        
        for (CSVRecord record : records) {
            String rater_id = record.get("rater_id");
            String movId = record.get("movie_id");
            double rating = Double.parseDouble(record.get("rating").trim());
            
            if (! IDs.contains(rater_id)) {
                Rater curtRater = new EfficientRater(rater_id);
                ratersDat.add(curtRater);
                curtRater.addRating(movId, rating);
            
            } else {
                for (int k=0; k < ratersDat.size(); k++) {
                    if (ratersDat.get(k).getID().equals(rater_id)) {
                        ratersDat.get(k).addRating(movId, rating);
                    }
                }
            }
            
            IDs.add(rater_id);
        }
        //System.out.println("IDs: " + IDs);
        //System.out.println("ratersData: " + ratersDat);
        return ratersDat;
    }
    
    // -- Tester   
    public void raterTester() {
        ArrayList<Rater> raters = loadRaters("ratings_short_tester");
        
        System.out.println("---------+----------+----------");
        System.out.println(raters); 
        //
        HashMap<String, HashMap<String, Double>> hashmap = new HashMap<String, HashMap<String, Double>> ();
        for (Rater rater : raters) {
            HashMap<String, Double> ratings = new HashMap<String, Double> ();
            ArrayList<String> itemsRated = rater.getItemsRated();
            
            for (int i=0; i < itemsRated.size(); i++) {
                String movieID = itemsRated.get(i);
                double movieRating = rater.getRating(movieID);
                
                ratings.put(movieID, movieRating);
            }
            hashmap.put(rater.getID(), ratings);
            System.out.println("---itemsRated:----" + itemsRated);
            System.out.println("---ratings:----" + ratings);
        }
        System.out.println("-------->" + hashmap);
        System.out.println("---Keys: " + hashmap.keySet());

        //String movieID = "1798709";
        int numOfRatings = 0;
        for (String key : hashmap.keySet()) {
            if(hashmap.get(key).containsKey("1798709")) {
                numOfRatings +=1;
            }
        }
        System.out.println("Number of ratings movie 1798709 has : " + numOfRatings);
        
        ArrayList<String> uniqueMovies = new ArrayList<String> ();
        for (String key : hashmap.keySet()) {
            for (String currMovieID : hashmap.get(key).keySet()) {
                if (! uniqueMovies.contains(currMovieID)) {
                    uniqueMovies.add(currMovieID);
                }
            }
        }
        System.out.println("Total number of movies that were rated : " + uniqueMovies.size());
        System.out.println("The uniqueMovies are : " + uniqueMovies);
}
}
