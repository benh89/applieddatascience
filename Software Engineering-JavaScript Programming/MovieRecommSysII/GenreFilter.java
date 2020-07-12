public class GenreFilter implements Filter {
    private String myGenre;
    
    public GenreFilter (String genre) {
        myGenre = genre;
    }
    
    @Override
    public boolean satisfies(String movId) {
        return MovieDatabase.getGenres(movId).contains(myGenre);
    }
}