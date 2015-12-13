package ayaseruri.torr.torrfm.global;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp.OkHttpImagePipelineConfigFactory;
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
        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                .newBuilder(this, RetrofitClient.okHttpClient)
                .build();
        Fresco.initialize(this, config);
        LocalDisplay.init(this);
    }
}
