import java.util.*;

public class EfficientRater implements Rater {
    private String newID;
    // use HashMap - the efficient way;
    private HashMap<String,Rating1> newRatings;

    public EfficientRater(String movId) {
        newID = movId;
        newRatings = new HashMap<String,Rating1> ();
    }

    public void addRating(String movieId, double rating) {
       newRatings.put(movieId, new Rating1(movieId,rating));
    }

    public boolean hasRating(String movieId) {
        if (newRatings.containsKey(movieId)) {
            return true;
        }
       
        return false;
    }

    public String getID() {
        return newID;
    }

    public double getRating(String movieId) {
        for (String movieID : newRatings.keySet()) {
            if (movieID.equals(movieId)) {
                return newRatings.get(movieID).getValue();
            }
        }
       
        return -1;
    }

    public int numRatings() {
        return newRatings.size();
    }

    public ArrayList<String> getItemsRated() {
        ArrayList<String> list = new ArrayList<String> ();
        for (String movieID : newRatings.keySet()) {
            list.add(newRatings.get(movieID).getItem());
        }
        return list;
    }
    //for testing purpose;
    public String toString () {
        String result = "Rater [raterID=" + newID + "]";
        return result;
    }
}