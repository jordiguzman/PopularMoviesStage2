package appkite.jordiguzman.com.polularmoviesstage2.interfaces;

import appkite.jordiguzman.com.polularmoviesstage2.model.Movie;

/**
 * Created by Jordi Guzmán on 08/02/2018.
 */

public interface AsynTaskCompleteListeningMovie {
    void onTaskComplete(Movie[] result);


}
