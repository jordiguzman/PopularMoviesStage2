package appkite.jordiguzman.com.polularmoviesstage2.fonts;

import android.app.Application;

import appkite.jordiguzman.com.polularmoviesstage2.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;



public class Font extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/uberlin.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

}
