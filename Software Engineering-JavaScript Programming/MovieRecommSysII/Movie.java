import java.util.ArrayList;
import java.util.Arrays;

// An immutable passive data object (PDO) to represent item data
public class Movie {
    private String movId;
    private String title;
    private int year;
    private String country;
    private String genres;
    private String director;
    private int minutes;
    private String poster;
    //A constructor with eight parameters to initialize the private variables
    public Movie (String anID, String aTitle, String aYear, String aCountry, String aGenres, String aDirector,
    int theMinutes, String aPoster) {
        // just in case data file contains extra whitespace
        movId = anID.trim();
        title = aTitle.trim();
        year = Integer.parseInt(aYear.trim());
        country = aCountry;
        genres = aGenres;
        director = aDirector;
        minutes = theMinutes;
        poster = aPoster;
    }

    // Returns ID associated with this item
    public String getID () {
        return movId;
    }

    // Returns title of this item
    public String getTitle () {
        return title;
    }

    // Returns year in which this item was published
    public int getYear () {
        return year;
    }
    
    public String getCountry(){
        return country;
    }

    // Returns genres associated with this item
    public String getGenres () {
        return genres;
    }

    public String getDirector(){
        return director;
    }
    
    public int getMinutes(){
        return minutes;
    }

    public String getPoster(){
        return poster;
    }

    // Returns a string of the item's information
    //A toString method for representing movie information as a String so it can easily be printed
    public String toString () {
        String result = "Movie [movId=" + movId + ", title=" + title + ", year=" + year + ", country=" + country;;
        result += ", genres= " + genres + "]";
        return result;
    }
}
