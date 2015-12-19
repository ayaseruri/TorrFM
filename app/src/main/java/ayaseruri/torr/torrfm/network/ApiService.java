package ayaseruri.torr.torrfm.network;

import java.util.List;

import ayaseruri.torr.torrfm.objectholder.ChannelInfo;
import ayaseruri.torr.torrfm.objectholder.OneSentenceInfo;
import ayaseruri.torr.torrfm.objectholder.SongInfo;
import retrofit.http.GET;
import retrofit.http.Query;
import retrofit.http.Url;
import rx.Observable;

/**
 * Created by ayaseruri on 15/12/11.
 */
public interface ApiService {
    @GET("/x/?channel")
    Observable<List<ChannelInfo>> getChannelInfo();

    @GET("/x/?rand")
    Observable<List<SongInfo>> getRandSong(@Query("hid") String hid);

    @GET("/x/?search")
    Observable<List<SongInfo>> searchMusic(@Query("key") String key);

    @GET
    Observable<OneSentenceInfo> getOneSentence(@Url String url);
}
