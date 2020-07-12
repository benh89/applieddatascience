import edu.duke.*;
import java.util.*;
import org.apache.commons.csv.*;

public class RaterDatabase {
    private static HashMap<String,Rater> newRaters;
     
    private static void initialize() {
        if (newRaters == null) {
            newRaters = new HashMap<String,Rater>();
        }
    }
    // calling from addRatings;
    public static void initialize(String filename) {
        if (newRaters == null) {
            newRaters= new HashMap<String,Rater>();
            add_Ratings(filename);  //calling the method below;
        }
    }   
    
    public static void addRaterRating(String raterID, String movieID, double rating) {
        initialize(); 
        Rater rater =  null;
        if (newRaters.containsKey(raterID)) {
            rater = newRaters.get(raterID); 
        } 
        else { 
            rater = new EfficientRater(raterID);
            newRaters.put(raterID,rater);
        }
        rater.addRating(movieID,rating);
    } 
    
    public static void add_Ratings(String filename) {
        initialize(); 
        FileResource fr = new FileResource("data/" + filename + ".csv");
        CSVParser csv_psr = fr.getCSVParser();
        for(CSVRecord record : csv_psr) {
                String rater_id = record.get("rater_id");
                String movId = record.get("movie_id");
                String rating = record.get("rating");
                addRaterRating(rater_id,movId,Double.parseDouble(rating));
        } 
    }
             
    public static Rater getRater(String rater_id) {
        initialize();
        return newRaters.get(rater_id);
    }
    
    public static ArrayList<Rater> getRaters() { 
        initialize();
        ArrayList<Rater> list = new ArrayList<Rater>(newRaters.values());
        return list;
    }
 
    public static int size() {
        return newRaters.size();
    }  
}