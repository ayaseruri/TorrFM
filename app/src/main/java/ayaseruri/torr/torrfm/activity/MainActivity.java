package ayaseruri.torr.torrfm.activity;

import android.app.Dialog;
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.commit451.nativestackblur.NativeStackBlur;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.j256.ormlite.dao.Dao;
import com.nineoldandroids.animation.Animator;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ayaseruri.torr.torrfm.R;
import ayaseruri.torr.torrfm.adaptar.MusicContentAdaptar;
import ayaseruri.torr.torrfm.adaptar.MusicListAdaptar;
import ayaseruri.torr.torrfm.adaptar.NavigationAdapar;
import ayaseruri.torr.torrfm.controller.MusicController;
import ayaseruri.torr.torrfm.db.DBHelper;
import ayaseruri.torr.torrfm.model.LrcModel;
import ayaseruri.torr.torrfm.model.MusicPlayModel;
import ayaseruri.torr.torrfm.network.RetrofitClient;
import ayaseruri.torr.torrfm.objectholder.ChannelInfo;
import ayaseruri.torr.torrfm.objectholder.SongInfo;
import ayaseruri.torr.torrfm.utils.LocalDisplay;
import ayaseruri.torr.torrfm.utils.Util;
import ayaseruri.torr.torrfm.view.MSeekBar;
import cn.pedant.SweetAlert.SweetAlertDialog;
import me.relex.circleindicator.CircleIndicator;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity implements MusicPlayModel.IMusicPlay, LrcModel.ILrc {
    private static final int DOWNLOAD_NOTIFICATION_ID = 0;

    private MusicPlayModel musicPlayModel;
    private MusicController musicController;
    private LrcModel mLrcModel;
    private SimpleDraweeView musicCover;
    private NotificationManager mNotifyMgr;
    private DBHelper dbHelper;
    private String musicCoverPre = "";

    @ViewById
    Toolbar toolbar;
    @ViewById(R.id.navigation_recycler)
    RecyclerView navigationRecycler;
    @ViewById(R.id.main_drawer)
    DrawerLayout mainDrawer;
    @ViewById(R.id.bg)
    ImageView bg;
    @ViewById(R.id.music_play)
    CheckBox musicPlayBtn;
    @ViewById(R.id.music_current_time)
    TextView musicCurrentTime;
    @ViewById(R.id.music_total_time)
    TextView musicTotalTime;
    @ViewById(R.id.music_progress)
    MSeekBar musicPorgress;
    @ViewById(R.id.music_content_view_pager)
    ViewPager musicContentViewPager;
    @ViewById(R.id.pager_indicator)
    CircleIndicator pagerIndicator;
    @ViewById(R.id.title)
    TextView title;
    @ViewById(R.id.subTile)
    TextView subTitle;
    @ViewById(R.id.music_like)
    CheckBox musicLike;

    @AfterViews
    void init(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        dbHelper = DBHelper.getInstance(this);
        initDrawer();
        mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        musicPlayModel = new MusicPlayModel();
        musicPlayModel.addIMusicPlays(this);
        musicController = new MusicController(this, musicPlayModel);

        mLrcModel = new LrcModel();
        mLrcModel.addILrc(this);

        musicPorgress.setMax(100);
        musicPorgress.setProgress(0);
        musicPorgress.setSecondaryProgress(0);

        musicPlayBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    musicController.play();
                } else {
                    musicController.pause();
                }
            }
        });

        musicPorgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    musicController.setMusicTimeCurrent(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                musicController.seekTo();
            }
        });

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View musicContentCover = layoutInflater.inflate(R.layout.music_content_cover, null);
        musicCover = (SimpleDraweeView)musicContentCover.findViewById(R.id.music_cover);
        View musicContentLrc = layoutInflater.inflate(R.layout.music_content_lrc, null);
        List<View> musicContent = new ArrayList<>();
        musicContent.add(musicContentCover);
        musicContent.add(musicContentLrc);
        MusicContentAdaptar musicContentAdaptar = new MusicContentAdaptar(musicContent);
        musicContentViewPager.setAdapter(musicContentAdaptar);
        pagerIndicator.setViewPager(musicContentViewPager);

        musicLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    YoYo.with(Techniques.Hinge).playOn(musicLike);
                }else {
                    YoYo.with(Techniques.Swing).playOn(musicLike);
                }
            }
        });

        getRandSongList("1");
    }

    @Click(R.id.music_pre)
    void onMusicPre(){
        musicController.pre();
    }

    @Click(R.id.music_next)
    void onMusicNext(){
        musicController.next();
    }
    @Click(R.id.music_list)
    void onMusicList(){
        if(null == musicPlayModel.getSongInfos() || musicPlayModel.getSongInfos().size() == 0){
            mainDrawer.openDrawer(Gravity.LEFT);
            SuperToast.create(this, "请选择类别以初始化歌单", SuperToast.Duration.LONG
            , Style.getStyle(Style.RED, SuperToast.Animations.FADE)).show();
        }else {
            Dialog musicListDialog = new Dialog(this);
            musicListDialog.setTitle("播放列表（" + musicPlayModel.getSongInfos().size() + "）");
            View view = LayoutInflater.from(this).inflate(R.layout.music_list_dialog, null);
            RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.music_list);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            MusicListAdaptar musicListAdaptar = new MusicListAdaptar(this, musicPlayModel.getSongInfos(), new MusicListAdaptar.IItemClick() {
                @Override
                public void onItemClick(int postion, SongInfo songInfo) {

                }
            });
            recyclerView.setAdapter(musicListAdaptar);
            musicListDialog.setContentView(view);
            musicListDialog.show();
            WindowManager.LayoutParams lp = musicListDialog.getWindow().getAttributes();
            lp.width = LocalDisplay.SCREEN_WIDTH_PIXELS;
            lp.height = LocalDisplay.SCREEN_HEIGHT_PIXELS/2;
            lp.gravity = Gravity.BOTTOM;
            musicListDialog.getWindow().setAttributes(lp);
        }
    }

    @Click(R.id.music_download)
    void onMusicDownLoadClick(){
        String sdStatus = Environment.getExternalStorageState();
        if (sdStatus.equals(Environment.MEDIA_MOUNTED) || sdStatus.equals(Environment.MEDIA_SHARED)) {
            String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TorrFM/music/";
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            downloadMusic(musicPlayModel.getMusicInfoCurrent().getSrc()
                    , musicPlayModel.getMusicInfoCurrent().getTitle()
                    , savePath);
        }else {
            SuperToast.create(this, "手机存储似乎暂时无法使用", SuperToast.Duration.LONG
                    , Style.getStyle(Style.RED, SuperToast.Animations.FADE)).show();
        }
    }

    void initDrawer(){
        RetrofitClient.apiService.getChannelInfo()
                .subscribeOn(Schedulers.from(RetrofitClient.netExecutor))
                .observeOn(Schedulers.from(RetrofitClient.netExecutor))
                .map(new Func1<List<ChannelInfo>, List<ChannelInfo>>() {
                    @Override
                    public List<ChannelInfo> call(List<ChannelInfo> channelInfos) {
                        Dao channelDao = null;
                        try {
                            channelDao = dbHelper.getDBDao(ChannelInfo.class);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        if (null != channelDao) {
                            for (ChannelInfo channelInfo : channelInfos) {
                                try {
                                    channelDao.createOrUpdate(channelInfo);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        return channelInfos;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<ChannelInfo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        SuperToast.create(MainActivity.this, "音乐分类初始化失败"
                                , SuperToast.Duration.LONG, Style.getStyle(Style.RED, SuperToast.Animations.FADE)).show();
                    }

                    @Override
                    public void onNext(List<ChannelInfo> channelInfos) {
                        initNavigationRecycler(channelInfos);
                    }

                    @Override
                    public void onStart() {
                        Dao channelDao = null;
                        try {
                            channelDao = dbHelper.getDBDao(ChannelInfo.class);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        if(null != channelDao){
                            try {
                                List<ChannelInfo> channelInfos = channelDao.queryForAll();
                                initNavigationRecycler(channelInfos);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    void initNavigationRecycler(List<ChannelInfo> channelInfos){
        NavigationAdapar navigationAdapar = new NavigationAdapar(this, channelInfos, new NavigationAdapar.IItemClick() {
            @Override
            public void onItemClick(int postion, ChannelInfo channelInfo) {
               onNavigationItemClick(postion, channelInfo);
            }
        });
        navigationRecycler.setLayoutManager(new LinearLayoutManager(this));
        navigationRecycler.setAdapter(navigationAdapar);
    }

    void onNavigationItemClick(int postion, ChannelInfo channelInfo){
        if (mainDrawer.isDrawerOpen(Gravity.LEFT)) {
            mainDrawer.closeDrawers();
        }
        getRandSongList(channelInfo.getHid());
    }

    void getRandSongList(String hid){
        final SweetAlertDialog progressDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.setCancelable(false);
        progressDialog.setTitleText("正在拉取歌单…");
        RetrofitClient.apiService.getRandSong(hid).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.from(RetrofitClient.netExecutor))
                .subscribe(new Subscriber<List<SongInfo>>() {
                    @Override
                    public void onStart() {
                        progressDialog.show();
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<SongInfo> songInfos) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        musicPlayModel.setSongInfos(songInfos);
                        musicController.preparePlay();
                    }
                });
    }


    @Override
    @UiThread
    public void onMusicPlayStateChange(final MusicPlayModel musicPlayModel) {
        musicPorgress.setSecondaryProgress(musicPlayModel.getMusicBufferPercent());
        if(0 != musicPlayModel.getMusicTimeTotal()){
            musicPorgress.setProgress(musicPlayModel.getMusicTimeCurrent() * 100 / musicPlayModel.getMusicTimeTotal());
        }
        musicPlayBtn.setChecked(musicPlayModel.isMusicPlaying());
        musicCurrentTime.setText(Util.FormatMusicTime(musicPlayModel.getMusicTimeCurrent()));
        musicTotalTime.setText(Util.FormatMusicTime(musicPlayModel.getMusicTimeTotal()));
        mLrcModel.setTimeCurrent(musicPlayModel.getMusicTimeCurrent());

        if(null != musicPlayModel.getMusicInfoCurrent().getImg() && !musicCoverPre.equals(musicPlayModel.getMusicInfoCurrent().getImg())){
            final Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.music_bg_fade_in);
            final Animation fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.music_bg_fade_out);
            musicCoverPre = musicPlayModel.getMusicInfoCurrent().getImg();
            YoYo.with(Techniques.SlideOutUp).duration(200).withListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
                        @Override
                        public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable anim) {
                            if (imageInfo == null) {
                                return;
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    YoYo.with(Techniques.DropOut).playOn(musicCover);
                                }
                            });
                        }

                        @Override
                        public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {

                        }

                        @Override
                        public void onFailure(String id, Throwable throwable) {

                        }
                    };

                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setControllerListener(controllerListener)
                            .setUri(Uri.parse(musicPlayModel.getMusicInfoCurrent().getImg())).build();
                    musicCover.setController(controller);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).playOn(musicCover);

            ImagePipeline imagePipeline = Fresco.getImagePipeline();
            ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(musicPlayModel.getMusicInfoCurrent().getImg()));
            ImageRequest request = builder.build();
            DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(request, this);
            dataSource.subscribe(new BaseBitmapDataSubscriber() {
                @Override
                protected void onNewResultImpl(final Bitmap bitmap) {
                    fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Bitmap bm = NativeStackBlur.process(bitmap, 25);
                                    bg.startAnimation(fadeInAnimation);
                                    bg.setImageBitmap(bm);
                                }
                            });
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bg.startAnimation(fadeOutAnimation);
                        }
                    });
                }

                @Override
                protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

                }
            }, RetrofitClient.netExecutor);

            YoYo.with(Techniques.RotateOutUpLeft).duration(200).withListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if(subTitle.getVisibility() == View.GONE){
                        subTitle.setVisibility(View.VISIBLE);
                    }
                    YoYo.with(Techniques.RotateOutUpLeft).duration(200).withListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            subTitle.setText(musicPlayModel.getMusicInfoCurrent().getArtist_name());
                            YoYo.with(Techniques.RotateInDownLeft).playOn(subTitle);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }).playOn(subTitle);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    title.setText(musicPlayModel.getMusicInfoCurrent().getTitle());
                    YoYo.with(Techniques.RotateInDownLeft).playOn(title);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).playOn(title);
        }
    }

    @Override
    public void onMusicTimeCurrentChange(LrcModel lrcModel) {

    }

    public void downloadMusic(final String url, final String fileName, final String savePath){
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MainActivity.this);

        Observable<Integer> observable = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                Request request = new Request.Builder().url(url).build();
                try {
                    Response response = RetrofitClient.okHttpClient.newCall(request).execute();
                    if(response.isSuccessful()){
                        String mimeType = MimeTypeMap.getFileExtensionFromUrl(url);
                        File musicFile = new File(savePath + fileName + "." + mimeType);
                        if(!musicFile.exists()){
                            if(!musicFile.createNewFile()){
                                subscriber.onError(new IOException());
                                return;
                            }
                        }
                        BufferedSink output = Okio.buffer(Okio.sink(musicFile));

                        BufferedSource input = Okio.buffer(Okio.source(response.body().byteStream()));
                        long totalByteLength = response.body().contentLength();
                        byte data[] = new byte[1024];

                        subscriber.onNext(0);
                        long total = 0;
                        int count;
                        while ((count = input.read(data)) != -1) {
                            total += count;
                            output.write(data, 0, count);
                            subscriber.onNext((int) (total * 100 / totalByteLength));
                        }
                        output.flush();
                        output.close();
                        input.close();
                        subscriber.onCompleted();
                    }else {
                        subscriber.onError(new IOException());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(new IOException());
                }
            }
        }).onBackpressureDrop();

        Subscriber<Integer> subscriber = new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                notificationBuilder.setContentTitle(fileName + "下载完毕");
                mNotifyMgr.notify(DOWNLOAD_NOTIFICATION_ID, notificationBuilder.build());
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                notificationBuilder.setContentTitle(fileName + "下载失败");
                mNotifyMgr.notify(DOWNLOAD_NOTIFICATION_ID, notificationBuilder.build());
                SuperToast.create(MainActivity.this, fileName + "下载失败", SuperToast.Duration.LONG
                        , Style.getStyle(Style.RED, SuperToast.Animations.FADE)).show();
            }

            @Override
            public void onNext(Integer precentage) {
                notificationBuilder.setProgress(100, precentage, false);
                mNotifyMgr.notify(DOWNLOAD_NOTIFICATION_ID, notificationBuilder.build());
            }

            @Override
            public void onStart() {
                notificationBuilder.setSmallIcon(R.drawable.ic_launcher);
                notificationBuilder.setContentTitle("正在下载" + fileName);
                mNotifyMgr.notify(DOWNLOAD_NOTIFICATION_ID, notificationBuilder.build());
            }
        };

        observable.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.from(RetrofitClient.netExecutor))
                .subscribe(subscriber);
    }
}
