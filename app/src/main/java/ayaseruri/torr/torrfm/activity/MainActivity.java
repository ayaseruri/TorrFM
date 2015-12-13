package ayaseruri.torr.torrfm.activity;

import android.app.Dialog;
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
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
import android.webkit.MimeTypeMap;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.commit451.nativestackblur.NativeStackBlur;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ayaseruri.torr.torrfm.R;
import ayaseruri.torr.torrfm.adaptar.MusicContentAdaptar;
import ayaseruri.torr.torrfm.adaptar.MusicListAdaptar;
import ayaseruri.torr.torrfm.adaptar.NavigationAdapar;
import ayaseruri.torr.torrfm.controller.MusicController;
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
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity implements MusicPlayModel.IMusicPlay, LrcModel.ILrc {
    private static final int DOWNLOAD_NOTIFICATION_ID = 0;

    private MusicPlayModel musicPlayModel;
    private MusicController musicController;
    private LrcModel mLrcModel;
    private int musicCoverPanelHeight = 0;
    private SimpleDraweeView musicCover;
    private NotificationManager mNotifyMgr;

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

    @AfterViews
    void init(){
        setSupportActionBar(toolbar);
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
                    musicController.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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
        RetrofitClient.apiService.getChannelInfo().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.from(RetrofitClient.netExecutor))
                .subscribe(new Observer<List<ChannelInfo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<ChannelInfo> channelInfos) {
                        initNavigationRecycler(channelInfos);
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
        final SweetAlertDialog progressDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.setCancelable(false);
        progressDialog.setTitleText("正在拉取歌单…");

        RetrofitClient.apiService.getRandSong().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.from(RetrofitClient.netExecutor))
                .subscribe(new Subscriber<List<SongInfo>>() {
                    @Override
                    public void onStart() {
                        mainDrawer.closeDrawers();
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
    public void onMusicPlayStateChange(MusicPlayModel musicPlayModel) {
        if(null != musicPlayModel.getMusicInfoCurrent().getImg()){
            musicCover.setImageURI(Uri.parse(musicPlayModel.getMusicInfoCurrent().getImg()));

            ImagePipeline imagePipeline = Fresco.getImagePipeline();
            ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(musicPlayModel.getMusicInfoCurrent().getImg()));
            ImageRequest request = builder.build();
            DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(request, this);
            dataSource.subscribe(new BaseBitmapDataSubscriber() {
                @Override
                protected void onNewResultImpl(Bitmap bitmap) {
                    final Bitmap bm = NativeStackBlur.process(bitmap, 25);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bg.setImageBitmap(bm);
                        }
                    });
                }

                @Override
                protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

                }
            }, RetrofitClient.netExecutor);
        }

        musicPorgress.setSecondaryProgress(musicPlayModel.getMusicBufferPercent());
        if(0 != musicPlayModel.getMusicTimeTotal()){
            musicPorgress.setProgress(musicPlayModel.getMusicTimeCurrent() * 100 / musicPlayModel.getMusicTimeTotal());
        }
        musicPlayBtn.setChecked(musicPlayModel.isMusicPlaying());
        musicCurrentTime.setText(Util.FormatMusicTime(musicPlayModel.getMusicTimeCurrent()));
        musicTotalTime.setText(Util.FormatMusicTime(musicPlayModel.getMusicTimeTotal()));

        toolbar.setTitle(musicPlayModel.getMusicInfoCurrent().getTitle());
        toolbar.setSubtitle(musicPlayModel.getMusicInfoCurrent().getArtist_name());

        mLrcModel.setTimeCurrent(musicPlayModel.getMusicTimeCurrent());

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
                            musicFile.createNewFile();
                        }
                        BufferedSink output = Okio.buffer(Okio.sink(musicFile));

                        BufferedSource input = Okio.buffer(Okio.source(response.body().byteStream()));
                        long totalByteLength = response.body().contentLength();
                        byte data[] = new byte[20240];

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

        observable.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread())
                .subscribe(subscriber);
    }
}
