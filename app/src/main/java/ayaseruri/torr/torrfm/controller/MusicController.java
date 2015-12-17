package ayaseruri.torr.torrfm.controller;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.j256.ormlite.dao.Dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ayaseruri.torr.torrfm.db.DBHelper;
import ayaseruri.torr.torrfm.global.Constant;
import ayaseruri.torr.torrfm.model.MusicPlayModel;
import ayaseruri.torr.torrfm.objectholder.SongInfo;

/**
 * Created by ayaseruri on 15/12/12.
 */
public class MusicController implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    private Context mContext;
    private MediaPlayer mediaPlayer;
    private MusicPlayModel musicPlayModel;
    private Timer mTimer;
    private DBHelper dbHelper;

    public MusicController(final Context context, final MusicPlayModel musicPlayModel) {
        this.musicPlayModel = musicPlayModel;
        this.mContext = context;

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);

        dbHelper = DBHelper.getInstance(context);

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mediaPlayer.isPlaying()) {
                    musicPlayModel.setMusicTimeCurrent(mediaPlayer.getCurrentPosition());
                }
            }
        }, 0, 1000);
    }

    public void play() {
        musicPlayModel.setIsMusicPlaying(true);
        for(int i=0; i < musicPlayModel.getSongInfos().size(); i++){
            if(i == musicPlayModel.getMusicIndexCurrent()){
                musicPlayModel.getSongInfos().get(i).setIsPlaying(true);
            }else {
                musicPlayModel.getSongInfos().get(i).setIsPlaying(false);
            }
        }
        mediaPlayer.start();
    }

    public void preparePlay() {
        checkIsLike();
        if (null != musicPlayModel.getMusicInfoCurrent().getSrc()) {
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(musicPlayModel.getMusicInfoCurrent().getSrc());
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
                SuperToast.create(mContext, "歌曲信息出错", SuperToast.Duration.LONG, Style.getStyle(Style.RED, SuperToast.Animations.FADE)).show();
            }
        } else {
            SuperToast.create(mContext, "歌曲信息出错", SuperToast.Duration.LONG, Style.getStyle(Style.RED, SuperToast.Animations.FADE)).show();
        }
    }

    public void pause() {
        musicPlayModel.setIsMusicPlaying(false);
        mediaPlayer.pause();
    }

    public void pre() {
        musicPlayModel.setMusicIndexCurrent(musicPlayModel.getMusicIndexCurrent() - 1);
        preparePlay();
        if (musicPlayModel.isMusicPlaying()) {
            play();
        } else {
            pause();
        }
    }

    public void next() {
        musicPlayModel.setMusicIndexCurrent(musicPlayModel.getMusicIndexCurrent() + 1);
        preparePlay();
        if (musicPlayModel.isMusicPlaying()) {
            play();
        } else {
            pause();
        }
    }

    public void setMusicTimeCurrent(int percentage) {
        musicPlayModel.setMusicTimeCurrent((int)(percentage / 100.0f * musicPlayModel.getMusicTimeTotal()));
    }

    public void seekTo() {
        mediaPlayer.seekTo(musicPlayModel.getMusicTimeCurrent());
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        musicPlayModel.setMusicBufferPercent(percent);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        musicPlayModel.setMusicTimeTotal(mp.getDuration());
        if (musicPlayModel.isMusicPlaying()) {
            play();
        }
        //为什么这里直接调用pause方法会直接触发media.onCompletion
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    public void like() {
        musicPlayModel.setIsLike(true);
    }

    public void dislike() {
        musicPlayModel.setIsLike(false);
    }

    private void checkIsLike() {
        Constant.executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Dao songInfoDao = dbHelper.getDBDao(SongInfo.class);
                    List<SongInfo> songInfos = songInfoDao.queryBuilder().where().eq("title", musicPlayModel.getMusicInfoCurrent().getTitle()).query();
                    if (null != songInfos && songInfos.size() > 0) {
                        musicPlayModel.setIsLike(true);
                    } else {
                        musicPlayModel.setIsLike(false);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
