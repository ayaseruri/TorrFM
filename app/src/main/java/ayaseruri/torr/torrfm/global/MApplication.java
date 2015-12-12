package ayaseruri.torr.torrfm.global;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

import org.androidannotations.annotations.EApplication;

import ayaseruri.torr.torrfm.utils.LocalDisplay;

/**
 * Created by ayaseruri on 15/12/11.
 */
@EApplication
public class MApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        LocalDisplay.init(this);
    }
}
