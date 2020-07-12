public class YearAfterFilter implements Filter {
    private int newYear;
    
    public YearAfterFilter(int year) {
        newYear = year;
    }
    
    @Override
    public boolean satisfies(String movId) {
        return MovieDatabase.getYear(movId) >= newYear;
    }
}