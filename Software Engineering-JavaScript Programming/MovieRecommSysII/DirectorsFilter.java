import java.util.*;

public class DirectorsFilter implements Filter {
    String directorsList;
    
    public DirectorsFilter (String directors) {
        directorsList = directors;
    }
    
    @Override
    public boolean satisfies(String movId) {
        String[] directorsSplit = directorsList.split(",");
        for (int k=0; k < directorsSplit.length; k++) {
            if (MovieDatabase.getDirector(movId).indexOf(directorsSplit[k]) != -1) {
                return true;
            }
        }
        return false;
    }
}