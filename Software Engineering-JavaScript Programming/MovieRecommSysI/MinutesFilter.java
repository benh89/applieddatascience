public class MinutesFilter implements Filter {
    private int minMin;
    private int maxMin;
    
    public MinutesFilter (int min, int max) {
        minMin = min;
        maxMin = max;
    }
    
    @Override
    public boolean satisfies(String id) {
        return MovieDatabase.getMinutes(id) >= minMin && MovieDatabase.getMinutes(id) <= maxMin;
    }
}