import java.util.*;

public class MovieRunnerSimilarRatings {
    public void printAverageRatings () {
        FourthRatings fourthRatings = new FourthRatings ("ratings");
        MovieDatabase.initialize("ratedmoviesfull");
        
        //ArrayList<Rater> new_list = RaterDatabase.getRaters();
        //System.out.println("------------+------------+----------");
        //System.out.println("To see the list: " + new_list);
        
        System.out.println("Read data for " + RaterDatabase.size() + " raters");
        System.out.println("Read data for " + MovieDatabase.size() + " movies");
        
        int minRatingsNum = 35; 
        ArrayList<Rating1> averageRatings = fourthRatings.getAverageRatings1(minRatingsNum);
        System.out.println("The number of " + averageRatings.size() + " movies with " +
        minRatingsNum + " even more rating(s)");
        
        Collections.sort(averageRatings);
        for (Rating1 rating : averageRatings) {
            System.out.println(rating.getValue() + " " + MovieDatabase.getTitle(rating.getItem()));
        }
    }
    
    public void printAverageRatingsByYearAfterAndGenre () {
        FourthRatings fourthRatings = new FourthRatings ("ratings");
        MovieDatabase.initialize("ratedmoviesfull");
        
        System.out.println("----------------------------------");
        System.out.println("Read data for " + RaterDatabase.size() + " raters");
        System.out.println("Read data for " + MovieDatabase.size() + " movies");
        
        String genre = "Drama"; 
        GenreFilter gf = new GenreFilter (genre);
        int year = 1990; 
        YearAfterFilter yearAfer = new YearAfterFilter (year);
        
        AllFilters af = new AllFilters();
        af.addFilter(yearAfer);
        af.addFilter(gf);
        
        int minRatingsNum = 8; 
        ArrayList<Rating1> avgRatings = fourthRatings.getAverageRatingsByFilter(minRatingsNum, af);
        System.out.println("The number of " + avgRatings.size() + " movie(s)  in genre of \""
        + genre + "\" that was(were) directed after " + year + " with " + minRatingsNum 
        + "even more rating(s)");
        
        Collections.sort(avgRatings);
        for (Rating1 rating : avgRatings) {
            System.out.println(rating.getValue() + " " + MovieDatabase.getYear(rating.getItem())
            + " " + MovieDatabase.getTitle(rating.getItem()));
            System.out.println("Genre: " + MovieDatabase.getGenres(rating.getItem()));
        }
    }
    
    public void printSimilarRatings () {
        FourthRatings fourthRatings = new FourthRatings ("ratings");
        MovieDatabase.initialize("ratedmoviesfull");
       
        System.out.println("----------------------------------");
        System.out.println("Read data for " + RaterDatabase.size() + " raters");
        System.out.println("Read data for " + MovieDatabase.size() + " movies");
        
        String rater_id = "65"; 
        int numSimilarRaters = 20; 
        int minimalRaters = 5; 
        ArrayList<Rating1> similarRatings = fourthRatings.getSimilarRatings(rater_id, numSimilarRaters, minimalRaters);
        System.out.println("The number of " + similarRatings.size() + " movie(s) that is(are) " 
        + "recommended for the rater(ID:) " + rater_id + " with " + minimalRaters
        + "even more rating(s). " + numSimilarRaters + " closest raters were considered.");
        
        for (Rating1 rating : similarRatings) {
            System.out.println(rating.getValue() + " " + MovieDatabase.getTitle(rating.getItem()));
        }
    }
    
    public void printSimilarRatingsByGenre () {
        FourthRatings fourthRatings = new FourthRatings ("ratings");
        MovieDatabase.initialize("ratedmoviesfull");
        
        System.out.println("----------------------------------");
        System.out.println("Read data for " + RaterDatabase.size() + " raters");
        System.out.println("Read data for " + MovieDatabase.size() + " movies");
        
        String genre = "Action"; 
        GenreFilter gf = new GenreFilter(genre);
         
        String rater_id = "65"; 
        int numSimilarRaters = 20; 
        int minimalRaters = 5; 
        ArrayList<Rating1> similarRatings = fourthRatings.getSimilarRatingsByFilter
        (rater_id, numSimilarRaters, minimalRaters, gf);
        System.out.println("The number of " + similarRatings.size() + " movie(s) that is(are) " 
        + "recommended for the rater(ID:) " + rater_id + " and with " + minimalRaters
        + "even more rating(s), in \"" + genre + "\" genre. " + numSimilarRaters + " closest raters were considered.");
        
        for (Rating1 rating : similarRatings) {
            System.out.println(rating.getValue() + " " + MovieDatabase.getTitle(rating.getItem()));
            System.out.println("Genre: " + MovieDatabase.getGenres(rating.getItem()));
        }
    }
    
    public void printSimilarRatingsByDirector () {
        FourthRatings fourthRatings = new FourthRatings ("ratings");
        MovieDatabase.initialize("ratedmoviesfull");
        
        System.out.println("------------------+----------------");
        System.out.println("Read data for " + RaterDatabase.size() + " raters");
        System.out.println("Read data for " + MovieDatabase.size() + " movies");
        
        String directors = "Clint Eastwood,Sydney Pollack,David Cronenberg,Oliver Stone";
        //String directors = "Clint Eastwood,J.J. Abrams,Alfred Hitchcock,Sydney Pollack,David Cronenberg,Oliver Stone,Mike Leigh"; // variable
        DirectorsFilter df = new DirectorsFilter(directors);
         
        String rater_id = "1034"; 
        int numSimilarRaters = 10; 
        int minimalRaters = 3; 
        ArrayList<Rating1> similarRatings = fourthRatings.getSimilarRatingsByFilter
        (rater_id, numSimilarRaters, minimalRaters, df);
        System.out.println("The number of " + similarRatings.size() + " movie(s) that is(are) " 
        + "recommended for the rater(ID:) " + rater_id + " and with " + minimalRaters
        + "even more rating(s), that was(were) directed by either of the following directors: "
        + directors + ". " + numSimilarRaters + " closest raters were considered.");
        
        for (Rating1 rating : similarRatings) {
            System.out.println(rating.getValue() + " " + MovieDatabase.getTitle(rating.getItem()));
            System.out.println("Directed by : " + MovieDatabase.getDirector(rating.getItem()));
        }
    }
    
    public void printSimilarRatingsByGenreAndMinutes () {
        FourthRatings fourthRatings = new FourthRatings ("ratings");
        MovieDatabase.initialize("ratedmoviesfull");
        
        System.out.println("------------------+----------------");
        System.out.println("Read data for " + RaterDatabase.size() + " raters");
        System.out.println("Read data for " + MovieDatabase.size() + " movies");
        
        String genre = "Adventure"; 
        GenreFilter gf = new GenreFilter (genre);
        int minMin = 100; 
        int maxMin = 200; 
        MinutesFilter mf = new MinutesFilter (minMin, maxMin);
        
        AllFilters af = new AllFilters();
        af.addFilter(gf);
        af.addFilter(mf);
        
        String rater_id = "65"; 
        int numSimilarRaters = 10; 
        int minimalRaters = 5; 
        ArrayList<Rating1> similarRatings = fourthRatings.getSimilarRatingsByFilter
        (rater_id, numSimilarRaters, minimalRaters, af);
        System.out.println("The number of " + similarRatings.size() + " movie(s) that is(are) " 
        + "recommended for the rater(ID:) " + rater_id + " and with " + minimalRaters
        + "even more rating(s), in \"" + genre + "\" genre, that is(are) between " + minMin
        + " and " + maxMin + " minutes in length. " + numSimilarRaters + " closest raters were considered.");
        
        for (Rating1 rating : similarRatings) {
            System.out.println(rating.getValue() + " " + MovieDatabase.getTitle(rating.getItem())
            + " Time: " + MovieDatabase.getMinutes(rating.getItem()));
            System.out.println("Genre: " + MovieDatabase.getGenres(rating.getItem()));
        }
    }
    
    public void printSimilarRatingsByYearAfterAndMinutes () {
        FourthRatings fourthRatings = new FourthRatings ("ratings");
        MovieDatabase.initialize("ratedmoviesfull");
        
        System.out.println("------------------+----------------");
        System.out.println("Read data for " + RaterDatabase.size() + " raters");
        System.out.println("Read data for " + MovieDatabase.size() + " movies");
        
        int year = 2000; 
        YearAfterFilter yearAfter = new YearAfterFilter (year);
        int minMin = 80; 
        int maxMin = 100; 
        MinutesFilter mf = new MinutesFilter (minMin, maxMin);
        
        AllFilters af = new AllFilters();
        af.addFilter(yearAfter);
        af.addFilter(mf);
        
        String rater_id = "65"; 
        int numSimilarRaters = 10; 
        int minimalRaters = 5; 
        ArrayList<Rating1> similarRatings = fourthRatings.getSimilarRatingsByFilter
        (rater_id, numSimilarRaters, minimalRaters, af);
        System.out.println("The number of " + similarRatings.size() + " movie(s) that is(are) " 
        + "recommended for the rater(ID:) " + rater_id + " and with " + minimalRaters
        + "even more rating(s), that is(are) between " + minMin + " and " + maxMin 
        + " minutes in length and released after year " + year + ". " + numSimilarRaters 
        + " closest raters were considered.");
        
        for (Rating1 rating : similarRatings) {
            //based on formula: rating.getValue();
            System.out.println(rating.getValue() + " " + MovieDatabase.getTitle(rating.getItem())
            + " Year: " + MovieDatabase.getYear(rating.getItem()) + " Time: " 
            + MovieDatabase.getMinutes(rating.getItem()));
        }
    }
}