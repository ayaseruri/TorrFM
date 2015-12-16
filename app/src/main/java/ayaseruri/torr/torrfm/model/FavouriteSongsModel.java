package ayaseruri.torr.torrfm.model;

import java.util.ArrayList;
import java.util.List;

import ayaseruri.torr.torrfm.objectholder.SongInfo;

/**
 * Created by ayaseruri on 15/12/16.
 */
public class FavouriteSongsModel {
    private List<SongInfo> favouriteSongs;
    private List<IFavouriteSongsChanged> iFavouriteSongsChangeds;

    public List<SongInfo> getFavouriteSongs() {
        if(null == favouriteSongs){
            favouriteSongs = new ArrayList<>();
        }
        return favouriteSongs;
    }

    public void setFavouriteSongs(List<SongInfo> favouriteSongs) {
        this.favouriteSongs = favouriteSongs;
        update();
    }

    public void deleteSong(int postion){
        if(postion >= 0 && postion < favouriteSongs.size()){
            favouriteSongs.remove(postion);
            update();
        }
    }

    public void addIFavouriteSongsChanged(IFavouriteSongsChanged iFavouriteSongsChanged) {
        if (null == iFavouriteSongsChangeds) {
            iFavouriteSongsChangeds = new ArrayList<>();
        }
        iFavouriteSongsChangeds.add(iFavouriteSongsChanged);
    }

    private void update() {
        for (IFavouriteSongsChanged iFavouriteSongsChanged : iFavouriteSongsChangeds) {
            iFavouriteSongsChanged.onFavouriteSongsChanged(favouriteSongs);
        }
    }

    public interface IFavouriteSongsChanged {
        void onFavouriteSongsChanged(List<SongInfo> songInfos);
    }
}
