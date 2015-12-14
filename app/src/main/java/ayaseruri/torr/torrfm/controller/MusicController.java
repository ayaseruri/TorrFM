package ayaseruri.torr.torrfm.controller;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import ayaseruri.torr.torrfm.model.MusicPlayModel;

/**
 * Created by ayaseruri on 15/12/12.
 */
public class MusicController implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    private Context mContext;
    private MediaPlayer mediaPlayer;
    private MusicPlayModel musicPlayModel;
    private Timer mTimer;

    public MusicController(Context context, final MusicPlayModel musicPlayModel) {
        this.musicPlayModel = musicPlayModel;
        this.mContext = context;

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(mediaPlayer.isPlaying()){
                    musicPlayModel.setMusicTimeCurrent(mediaPlayer.getCurrentPosition());
                }
            }
        }, 0, 1000);
    }

    public void play(){
        musicPlayModel.setIsMusicPlaying(true);
        mediaPlayer.start();
    }

    public void preparePlay(){
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
        preparePlay();

        if(musicPlayModel.isMusicPlaying()){
            play();
        }else {
            pause();
        }
    }

    public void next(){
        musicPlayModel.setMusicIndexCurrent(musicPlayModel.getMusicIndexCurrent() + 1);
        preparePlay();

        if(musicPlayModel.isMusicPlaying()){
            play();
        }else {
            pause();
        }
    }

    public void setMusicTimeCurrent(int percentage){
        musicPlayModel.setMusicTimeCurrent(percentage/100 * musicPlayModel.getMusicTimeTotal());
    }

    public void seekTo(){
        mediaPlayer.seekTo(musicPlayModel.getMusicTimeCurrent());
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        musicPlayModel.setMusicBufferPercent(percent);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        musicPlayModel.setMusicTimeTotal(mp.getDuration());
        if(musicPlayModel.isMusicPlaying()){
            play();
        }
        //为什么这里直接调用pause方法会直接触发media.onCompletion
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }
}
