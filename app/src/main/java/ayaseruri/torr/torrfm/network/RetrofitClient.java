package ayaseruri.torr.torrfm.network;

import com.squareup.okhttp.OkHttpClient;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

/**
 * Created by ayaseruri on 15/12/11.
 */
public class RetrofitClient {
    public static ApiService apiService = new Retrofit.Builder()
            .baseUrl("http://danmu.fm/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build()
            .create(ApiService.class);
    public static OkHttpClient okHttpClient = new OkHttpClient();
}
