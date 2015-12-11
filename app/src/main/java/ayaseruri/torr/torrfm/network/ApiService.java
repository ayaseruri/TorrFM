package ayaseruri.torr.torrfm.network;

import java.util.List;

import ayaseruri.torr.torrfm.objectholder.ChannelInfo;
import ayaseruri.torr.torrfm.objectholder.SongInfo;
import retrofit.http.GET;
import rx.Observable;

/**
 * Created by ayaseruri on 15/12/11.
 */
public interface ApiService {
    @GET("/x/?channel")
    Observable<List<ChannelInfo>> getChannelInfo();
    @GET("/x/?rand")
    Observable<List<SongInfo>> getRandSong();
}
