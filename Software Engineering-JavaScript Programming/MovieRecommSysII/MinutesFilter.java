public class MinutesFilter implements Filter {
    private int minMin;
    private int maxMin;
    
    public MinutesFilter (int min, int max) {
        minMin = min;
        maxMin = max;
    }
    
    @Override
    public boolean satisfies(String movId) {
        return MovieDatabase.getMinutes(movId) >= minMin && MovieDatabase.getMinutes(movId) <= maxMin;
    }
}