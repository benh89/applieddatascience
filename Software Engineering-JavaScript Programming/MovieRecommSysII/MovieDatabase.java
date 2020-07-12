import java.util.*;
import org.apache.commons.csv.*;
import edu.duke.FileResource;

public class MovieDatabase {
    private static HashMap<String, Movie> newMovies;

    public static void initialize(String moviefile) {
        if (newMovies == null) {
            newMovies = new HashMap<String,Movie>();
            loadMovies(moviefile);
        }
    }
    //The private initialize method with no parameters that will load the movie file
    //ratedmoviesfull.csv-if no file has been loaded
    private static void initialize() {
        if (newMovies == null) {
            newMovies = new HashMap<String,Movie>();
            loadMovies("ratedmoviesfull");
        }
    }	

    private static void loadMovies(String filename) {
        FirstRatings fr = new FirstRatings();
        ArrayList<Movie> list = fr.loadMovies(filename);
        for (Movie movie : list) {
            newMovies.put(movie.getID(), movie);
        }
    }

    public static boolean containsID(String movId) {
        initialize();
        return newMovies.containsKey(movId);
    }

    public static int getYear(String movId) {
        initialize();
        return newMovies.get(movId).getYear();
    }

    public static String getGenres(String movId) {
        initialize();
        return newMovies.get(movId).getGenres();
    }

    public static String getTitle(String movId) {
        initialize();
        return newMovies.get(movId).getTitle();
    }

    public static Movie getMovie(String movId) {
        initialize();
        return newMovies.get(movId);
    }

    public static String getPoster(String movId) {
        initialize();
        return newMovies.get(movId).getPoster();
    }

    public static int getMinutes(String movId) {
        initialize();
        return newMovies.get(movId).getMinutes();
    }

    public static String getCountry(String movId) {
        initialize();
        return newMovies.get(movId).getCountry();
    }

    public static String getDirector(String movId) {
        initialize();
        return newMovies.get(movId).getDirector();
    }

    public static int size() {
        return newMovies.size();
    }

    public static ArrayList<String> filterBy(Filter f) {
        initialize();
        ArrayList<String> list = new ArrayList<String>();
        for(String movId : newMovies.keySet()) {
            if (f.satisfies(movId)) {
                list.add(movId);
            }
        } 
        return list;
    }

}