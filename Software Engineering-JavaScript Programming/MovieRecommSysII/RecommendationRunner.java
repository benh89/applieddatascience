import java.util.*;

public class RecommendationRunner implements Recommender {
    
    public ArrayList<String> getItemsToRate () {
        ArrayList<String> itemsToRate = new ArrayList<String> ();
        ArrayList<String> movies = MovieDatabase.filterBy(new TrueFilter());
       
        for (int k=0; k < 20; k++) {
            Random rand = new Random();
            int random = rand.nextInt(movies.size());
            if (! itemsToRate.contains(movies.get(random))) {
                itemsToRate.add(movies.get(random));
            }
        }
        
        return itemsToRate;
    }
    
    public void printRecommendationsFor (String webRaterID) {
        FourthRatings fourthRatings = new FourthRatings ();
        MovieDatabase.initialize("ratedmoviesfull");
        RaterDatabase.initialize("ratings");
        
        System.out.println("<p>Read data for " + Integer.toString(RaterDatabase.size()) + " raters</p>");
        System.out.println("<p>Read data for " + Integer.toString(MovieDatabase.size()) + " movies</p>");
        
        int numSimilarRaters = 50; 
        int minRatingsNum = 5; 
        ArrayList<Rating1> similarList = fourthRatings.getSimilarRatings(webRaterID, numSimilarRaters, minRatingsNum);
        
        if (similarList.size() == 0) {
            System.out.println("Matching movies NOT found");
        } else {
            String header = ("<table> <tr> <th>Movie Title</th> <th>Rating Value</th>  <th>Genres</th> </tr>");
            String body = "";
            for (Rating1 rating : similarList) {
                body += "<tr> <td>" + MovieDatabase.getTitle(rating.getItem()) + "</td> <td>" 
                + Double.toString(rating.getValue()) + "</td> <td>" + MovieDatabase.getGenres(rating.getItem())
                + "</td> </tr> ";
            }
            System.out.println(header  + body + "</table>");
        }
    }
}