import java.util.*;

public class MovieRunnerWithFilters {
    public void printAverageRatings () {
        ThirdRatings thirdRatings = new ThirdRatings ("ratings_short_tester");
        //MovieDatabase.initialize("ratedmoviesfull");
        MovieDatabase.initialize("ratedmovies_short_tester");
        
        System.out.println("-----------------------------");
        System.out.println("Read data for " + thirdRatings.getRaterSize() + " raters");
        System.out.println("Read data for " + MovieDatabase.size() + " movies");
        
        int minRatingsNum = 1; 
        ArrayList<Rating1> averageRatings = thirdRatings.getAverageRatings(minRatingsNum);
        System.out.println("There are " + averageRatings.size() + " movies with " +
        minRatingsNum + " or more rating(s) :");
        
        Collections.sort(averageRatings);
        for (Rating1 rating : averageRatings) {
            System.out.println(rating.getValue() + " " + MovieDatabase.getTitle(rating.getItem()));
        }
    }
    
    public void printAverageRatingsByYear () {
        ThirdRatings thirdRatings = new ThirdRatings ("ratings_short_tester");
        //MovieDatabase.initialize("ratedmoviesfull");
        MovieDatabase.initialize("ratedmovies_short_tester");
        
        System.out.println("---------------+--------------");
        System.out.println("Read data for " + thirdRatings.getRaterSize() + " raters");
        System.out.println("Read data for " + MovieDatabase.size() + " movies");
        
        int year = 2000; 
        YearAfterFilter yaf = new YearAfterFilter (year);
        
        int minRatingsNum = 1; 
        ArrayList<Rating1> averageRatings = thirdRatings.getAverageRatingsByFilter(minRatingsNum, yaf);
        System.out.println("There are " + averageRatings.size() + " movies released after "
        + year + " with " + minRatingsNum + " or more rating(s) : ");
        
        Collections.sort(averageRatings);
        for (Rating1 rating : averageRatings) {
            System.out.println(rating.getValue() + " " + MovieDatabase.getYear(rating.getItem())
            + " " + MovieDatabase.getTitle(rating.getItem()));
        }
    }
    
    public void printAverageRatingsByGenre () {
        ThirdRatings thirdRatings = new ThirdRatings ("ratings_short_tester");
        //MovieDatabase.initialize("ratedmoviesfull");
        MovieDatabase.initialize("ratedmovies_short_tester");
        
        System.out.println("---------------+--------------");
        System.out.println("Read data for " + thirdRatings.getRaterSize() + " raters");
        System.out.println("Read data for " + MovieDatabase.size() + " movies");
        
        String genre = "Crime"; 
        GenreFilter gf = new GenreFilter (genre);
        
        int minRatingsNum = 1; 
        ArrayList<Rating1> averageRatings = thirdRatings.getAverageRatingsByFilter(minRatingsNum, gf);
        System.out.println("There are " + averageRatings.size() + " movies  in genre of \""
        + genre + "\" with " + minRatingsNum + " or more rating(s) :");
        
        Collections.sort(averageRatings);
        for (Rating1 rating : averageRatings) {
            System.out.println(rating.getValue() + " " + MovieDatabase.getTitle(rating.getItem()));
            System.out.println("Genre(s) : " + MovieDatabase.getGenres(rating.getItem()));
        }
    }
    
    public void printAverageRatingsByMinutes () {
        ThirdRatings thirdRatings = new ThirdRatings ("ratings_short_tester");
        //MovieDatabase.initialize("ratedmoviesfull");
        MovieDatabase.initialize("ratedmovies_short_tester");
        
        System.out.println("---------------+--------------");
        System.out.println("Read data for " + thirdRatings.getRaterSize() + " raters");
        System.out.println("Read data for " + MovieDatabase.size() + " movies");
        
        int minMins = 110; 
        int maxMins = 170; 
        MinutesFilter mf = new MinutesFilter (minMins, maxMins);
        
        int minRatingsNum = 1; 
        ArrayList<Rating1> averageRatings = thirdRatings.getAverageRatingsByFilter(minRatingsNum, mf);
        System.out.println("There are " + averageRatings.size() + " movies that are between " 
        + minMins + " and " + maxMins + " length with " + minRatingsNum + " or more rating(s) :");
        
        Collections.sort(averageRatings);
        for (Rating1 rating : averageRatings) {
            System.out.println(rating.getValue() + " Time: " + MovieDatabase.getMinutes(rating.getItem())
            + " " + MovieDatabase.getTitle(rating.getItem()));
        }
    }
    
    public void printAverageRatingsByDirectors () {
        ThirdRatings thirdRatings = new ThirdRatings ("ratings_short_tester");
        //MovieDatabase.initialize("ratedmoviesfull");
        MovieDatabase.initialize("ratedmovies_short_tester");
        
        System.out.println("---------------+--------------");
        System.out.println("Read data for " + thirdRatings.getRaterSize() + " raters");
        System.out.println("Read data for " + MovieDatabase.size() + " movies");
        
        String directorsList ="Charles Chaplin,Michael Mann,Spike Jonze";
        //String directorsList = "Clint Eastwood,Joel Coen,Martin Scorsese,Roman Polanski,Nora Ephron,Ridley Scott,Sydney Pollack"; // variable
        DirectorsFilter df = new DirectorsFilter (directorsList);
        
        int minRatingsNum = 1; 
        ArrayList<Rating1> averageRatings = thirdRatings.getAverageRatingsByFilter(minRatingsNum, df);
        System.out.println("There are " + averageRatings.size() + " movies that were directed " 
        + "by either of those directors : " +  directorsList + ", with " + minRatingsNum 
        + " or more rating(s) :");
        
        Collections.sort(averageRatings);
        for (Rating1 rating : averageRatings) {
            System.out.println(rating.getValue() + " " + MovieDatabase.getTitle(rating.getItem()));
            System.out.println("Directed by : " + MovieDatabase.getDirector(rating.getItem()));
        }
    }
    
    public void printAverageRatingsByYearAfterAndGenre () {
        ThirdRatings thirdRatings = new ThirdRatings ("ratings_short_tester");
        //MovieDatabase.initialize("ratedmoviesfull");
        MovieDatabase.initialize("ratedmovies_short_tester");
        
        System.out.println("---------------+--------------");
        System.out.println("Read data for " + thirdRatings.getRaterSize() + " raters");
        System.out.println("Read data for " + MovieDatabase.size() + " movies");
        
        int year = 1980; 
        YearAfterFilter yaf = new YearAfterFilter (year);
        
        String genre = "Romance"; 
        GenreFilter gf = new GenreFilter (genre);
        
        AllFilters af = new AllFilters();
        af.addFilter(yaf);
        af.addFilter(gf);
        
        int minRatingsNum = 1; 
        ArrayList<Rating1> avgRatings = thirdRatings.getAverageRatingsByFilter(minRatingsNum, af);
        System.out.println("There is(are) " + avgRatings.size() + " movie(s)  in genre of \""
        + genre + "\" that was(were) directed after " + year + " with " + minRatingsNum 
        + " or more rating(s) :");
        
        Collections.sort(avgRatings);
        for (Rating1 rating : avgRatings) {
            System.out.println(rating.getValue() + " " + MovieDatabase.getYear(rating.getItem())
            + " " + MovieDatabase.getTitle(rating.getItem()));
            System.out.println("Genre : " + MovieDatabase.getGenres(rating.getItem()));
        }
    }
    
    public void printAverageRatingsByDirectorsAndMinutes () {
        ThirdRatings thirdRatings = new ThirdRatings ("ratings_short_tester");
        //MovieDatabase.initialize("ratedmoviesfull");
        MovieDatabase.initialize("ratedmovies_short_tester");
        
        System.out.println("---------------+--------------");
        System.out.println("Read data for " + thirdRatings.getRaterSize() + " raters");
        System.out.println("Read data for " + MovieDatabase.size() + " movies");
        
        String directorsList = "Spike Jonze,Michael Mann,Charles Chaplin,Francis Ford Coppola";
        //String directorsList = "Clint Eastwood,Joel Coen,Tim Burton,Ron Howard,Nora Ephron,Sydney Pollack"; // variable
        DirectorsFilter df = new DirectorsFilter (directorsList);
        
        int minMins = 30; 
        int maxMins = 170; 
        MinutesFilter mf = new MinutesFilter (minMins, maxMins);
        
        AllFilters af = new AllFilters();
        af.addFilter(df);
        af.addFilter(mf);
        
        int minRatingsNum = 1; 
        ArrayList<Rating1> avgRatings = thirdRatings.getAverageRatingsByFilter(minRatingsNum, af);
        System.out.println("There is(are) " + avgRatings.size() + " movie(s) that were filmed by"
        + " either one of these directors : " + directorsList + "; and between " 
        + minMins + " and " + maxMins + " in length, with " + minRatingsNum
        + " or more rating(s) :"); 
        
        Collections.sort(avgRatings);
        for (Rating1 rating : avgRatings) {
            System.out.println(rating.getValue() + " Time: " + MovieDatabase.getMinutes(rating.getItem())
            + " " + MovieDatabase.getTitle(rating.getItem()));
            System.out.println("Directed by : " + MovieDatabase.getDirector(rating.getItem()));
        }
    }
}