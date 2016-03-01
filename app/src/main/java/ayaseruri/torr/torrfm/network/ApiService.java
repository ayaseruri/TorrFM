package ayaseruri.torr.torrfm.network;

import java.util.List;

import ayaseruri.torr.torrfm.objectholder.ChannelInfo;
import ayaseruri.torr.torrfm.objectholder.OneSentenceInfo;
import ayaseruri.torr.torrfm.objectholder.SongInfo;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by ayaseruri on 15/12/11.
 */
public interface ApiService {
    @GET("/x/?channel")
    Observable<List<ChannelInfo>> getChannelInfo();

    @GET("/x/?rand")
    Observable<List<SongInfo>> getRandSong(@Query("hid") String hid);

    @GET
    Observable<List<SongInfo>> searchMusic(@Url String url);

    @GET
    Observable<ResponseBody> getLrc(@Url String url);

    @GET
    Observable<OneSentenceInfo> getOneSentence(@Url String url);
}
