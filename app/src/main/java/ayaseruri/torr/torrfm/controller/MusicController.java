package ayaseruri.torr.torrfm.controller;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;

import java.io.IOException;

import ayaseruri.torr.torrfm.model.MusicPlayModel;

/**
 * Created by ayaseruri on 15/12/12.
 */
public class MusicController implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    private Context mContext;
    private MediaPlayer mediaPlayer;
    private MusicPlayModel musicPlayModel;

    public MusicController(Context context, MusicPlayModel musicPlayModel) {
        this.musicPlayModel = musicPlayModel;
        this.mContext = context;

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }

    public void play(){
        if(null != musicPlayModel.getMusicInfoCurrent().getUrl()){
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(musicPlayModel.getMusicInfoCurrent().getSrc());
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
                SuperToast.create(mContext, "歌曲信息出错", SuperToast.Duration.LONG, Style.getStyle(Style.RED, SuperToast.Animations.FADE)).show();
            }
        }else {
            SuperToast.create(mContext, "歌曲信息出错", SuperToast.Duration.LONG, Style.getStyle(Style.RED, SuperToast.Animations.FADE)).show();
        }
    }

    public void pause(){
        musicPlayModel.setIsMusicPlaying(false);
        mediaPlayer.pause();
    }

    public void pre(){
        musicPlayModel.setMusicIndexCurrent(musicPlayModel.getMusicIndexCurrent() - 1);
        if(musicPlayModel.isMusicPlaying()){
            play();
        }else {
            pause();
        }
    }

    public void next(){
        musicPlayModel.setMusicIndexCurrent(musicPlayModel.getMusicIndexCurrent() + 1);
        if(musicPlayModel.isMusicPlaying()){
            play();
        }else {
            pause();
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if(!mp.isPlaying()){
            mp.start();
            musicPlayModel.setIsMusicPlaying(true);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }
}
