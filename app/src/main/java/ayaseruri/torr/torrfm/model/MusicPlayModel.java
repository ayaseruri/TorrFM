package ayaseruri.torr.torrfm.model;

import java.util.ArrayList;
import java.util.List;

import ayaseruri.torr.torrfm.objectholder.SongInfo;

/**
 * Created by ayaseruri on 15/12/12.
 */
public class MusicPlayModel {
    private List<SongInfo> songInfos;
    private List<IMusicPlay> iMusicPlays;

    private int musicTimeTotal;
    private int musicTimeCurrent;
    private int musicIndexCurrent;
    private boolean isMusicPlaying;

    public MusicPlayModel() {
        iMusicPlays = new ArrayList<>();
        musicIndexCurrent = 0;
        musicTimeCurrent = 0;
        musicTimeTotal = 0;
    }

    public List<SongInfo> getSongInfos() {
        return songInfos;
    }

    public void setSongInfos(List<SongInfo> songInfos) {
        this.songInfos = songInfos;
        update();
    }

    public void addIMusicPlays(IMusicPlay iMusicPlays) {
        this.iMusicPlays.add(iMusicPlays);
    }

    public int getMusicTimeTotal() {
        return musicTimeTotal;
    }

    public void setMusicTimeTotal(int musicTimeTotal) {
        this.musicTimeTotal = musicTimeTotal;
        update();
    }

    public int getMusicTimeCurrent() {
        return musicTimeCurrent;
    }

    public void setMusicTimeCurrent(int musicTimeCurrent) {
        this.musicTimeCurrent = musicTimeCurrent;
        update();
    }

    public int getMusicIndexCurrent() {
        return musicIndexCurrent;
    }

    public void setMusicIndexCurrent(int musicIndexCurrent) {
        if(musicIndexCurrent < 0){
            musicIndexCurrent = songInfos.size() - 1;
        }else if(musicIndexCurrent > songInfos.size()){
            musicIndexCurrent = 0;
        }else {
            this.musicIndexCurrent = musicIndexCurrent;
        }
        update();
    }

    public boolean isMusicPlaying() {
        return isMusicPlaying;
    }

    public void setIsMusicPlaying(boolean isMusicPlaying) {
        this.isMusicPlaying = isMusicPlaying;
        update();
    }

    public SongInfo getMusicInfoCurrent() {
        return songInfos.get(musicIndexCurrent);
    }

    private void update(){
        for(IMusicPlay iMusicPlay : iMusicPlays){
            iMusicPlay.onMusicPlayStateChange(this);
        }
    }

    public interface IMusicPlay{
        void onMusicPlayStateChange(MusicPlayModel musicPlayModel);
    }
}
