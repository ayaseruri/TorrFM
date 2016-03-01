package ayaseruri.torr.torrfm.global;

import android.app.Application;
import android.content.res.Resources;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

import org.androidannotations.annotations.EApplication;

import ayaseruri.torr.torrfm.network.RetrofitClient;
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

    public int getStatusBarHeight() {
        return getBarHeight("status_bar_height");
    }

    public int getNavigationBarHeight(){
        return getBarHeight("navigation_bar_height");
    }

    private int getBarHeight(String resourcesName){
        Resources resources = this.getResources();
        int resourceId = resources.getIdentifier(resourcesName, "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}
