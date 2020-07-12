// An immutable passive data object (PDO) to represent the rating data
//The class Rating is also a POJO class for storing the data about one rating of an item
public class Rating1 implements Comparable<Rating1> {
    private String movId;   
    private double value;
    //A constructor with two parameters to initialize the private variables.
    public Rating1 (String anItem, double aValue) {
        movId = anItem;
        value = aValue;
    }

    // Returns item being rated
    public String getItem () {
        return movId;
    }

    // Returns the value of this rating (as a number so it can be used in calculations)
    public double getValue () {
        return value;
    }

    // Returns a string of all the rating information
    //A toString method to represent rating information as a String.
    public String toString () {
        return "[" + getItem() + ", " + getValue() + "]";
    }
    //A compareTo method to compare this rating with another rating
    public int compareTo(Rating1 other) {
        if (value < other.value) {
            return -1;
        }
        if (value > other.value) {
            return 1;
        }
        return 0;
    }
}