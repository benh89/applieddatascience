import java.util.*;

//An interface is a completely "abstract class" that is used to group related 
//methods with empty bodies.
public interface Rater {
    
    public String getID();
    
    public double getRating(String item);
    
    public boolean hasRating(String item);
    
    public void addRating(String item, double rating);
    
    public int numRatings();

    public ArrayList<String> getItemsRated();
    
    public String toString ();
}