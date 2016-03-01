package ayaseruri.torr.torrfm.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

/**
 * Created by ayaseruri on 15/12/11.
 */
public class RetrofitClient {
    public static ApiService apiService = new Retrofit.Builder()
            .baseUrl("http://danmu.fm/")
            .addConverterFactory(new FastJsonConverter())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build()
            .create(ApiService.class);
    public static OkHttpClient okHttpClient = new OkHttpClient();
}
