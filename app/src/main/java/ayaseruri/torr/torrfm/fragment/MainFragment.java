package ayaseruri.torr.torrfm.fragment;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.AbstractDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
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
import com.j256.ormlite.stmt.DeleteBuilder;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ayaseruri.torr.torrfm.R;
import ayaseruri.torr.torrfm.activity.MainActivity;
import ayaseruri.torr.torrfm.adaptar.MusicContentAdaptar;
import ayaseruri.torr.torrfm.adaptar.NavigationAdapar;
import ayaseruri.torr.torrfm.controller.LrcController;
import ayaseruri.torr.torrfm.controller.MusicController;
import ayaseruri.torr.torrfm.db.DBHelper;
import ayaseruri.torr.torrfm.db.SettingPrefs_;
import ayaseruri.torr.torrfm.global.Constant;
import ayaseruri.torr.torrfm.global.MApplication;
import ayaseruri.torr.torrfm.model.LrcModel;
import ayaseruri.torr.torrfm.model.MusicPlayModel;
import ayaseruri.torr.torrfm.network.RetrofitClient;
import ayaseruri.torr.torrfm.objectholder.ChannelInfo;
import ayaseruri.torr.torrfm.objectholder.SongInfo;
import ayaseruri.torr.torrfm.utils.Util;
import ayaseruri.torr.torrfm.view.FavouriteSongsDialog;
import ayaseruri.torr.torrfm.view.LrcView;
import ayaseruri.torr.torrfm.view.MCheckBox;
import ayaseruri.torr.torrfm.view.MSeekBar;
import cn.pedant.SweetAlert.SweetAlertDialog;
import me.relex.circleindicator.CircleIndicator;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by ayaseruri on 15/12/17.
 */
@EFragment(R.layout.fragment_main)
public class MainFragment extends Fragment implements MusicPlayModel.IMusicPlay
        , LrcModel.ILrc
        , FavouriteSongsDialog.IFavouriteSongsItemAction {
    private static final int DOWNLOAD_NOTIFICATION_ID = 0;

    @ViewById(R.id.navigation_recycler)
    RecyclerView navigationRecycler;
    @ViewById(R.id.main_drawer)
    DrawerLayout mainDrawer;
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
    @ViewById(R.id.music_like)
    MCheckBox musicLike;
    @ViewById(R.id.music_content_root)
    FrameLayout musicContentRoot;
    @ViewById(R.id.music_content_view_pager_root)
    LinearLayout viewPagerRoot;
    @App
    MApplication mApplication;

    @Pref
    SettingPrefs_ settingPrefs;

    private MusicPlayModel musicPlayModel;
    private MusicController musicController;
    private LrcModel mLrcModel;
    private LrcController mLrcController;
    private LrcView mLrcView;
    private TextView lrcHint;
    private SimpleDraweeView musicCover;
    private NotificationManager mNotifyMgr;
    private DBHelper dbHelper;
    private String musicCoverPre = "";

    @AfterViews
    void init() {
        int extraPadding = 0;
        extraPadding = ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) ? mApplication.getStatusBarHeight() : 0)
                + viewPagerRoot.getPaddingTop();
        viewPagerRoot.setPadding(0, extraPadding, 0, 0);

        dbHelper = DBHelper.getInstance(getActivity());
        initDrawer();
        mNotifyMgr = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        musicPlayModel = new MusicPlayModel();
        musicPlayModel.addIMusicPlays(this);
        musicController = new MusicController(getActivity(), musicPlayModel);

        musicPorgress.setMax(100);
        musicPorgress.setProgress(0);
        musicPorgress.setSecondaryProgress(0);
        musicPorgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                musicController.setMusicTimeCurrent(seekBar.getProgress());
                musicController.seekTo();
            }
        });

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View musicContentCover = layoutInflater.inflate(R.layout.music_content_cover, null);
        musicCover = (SimpleDraweeView) musicContentCover.findViewById(R.id.music_cover);
        View musicContentLrc = layoutInflater.inflate(R.layout.music_content_lrc, null);
        mLrcView = (LrcView) musicContentLrc.findViewById(R.id.lrc);
        lrcHint = (TextView)musicContentLrc.findViewById(R.id.lrc_hint);
        List<View> musicContent = new ArrayList<>();
        musicContent.add(musicContentCover);
        musicContent.add(musicContentLrc);
        MusicContentAdaptar musicContentAdaptar = new MusicContentAdaptar(musicContent);
        musicContentViewPager.setAdapter(musicContentAdaptar);
        pagerIndicator.setViewPager(musicContentViewPager);

        mLrcModel = new LrcModel();
        mLrcModel.addILrc(this);
        mLrcController = new LrcController(mLrcModel);
        mLrcView.setLrcModel(mLrcModel);

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

        musicLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    musicController.like();
                    YoYo.with(Techniques.Swing).playOn(musicLike);
                    onMusicDownLoadClick();
                    //经过验证这里面也是主线程
                } else {
                    musicController.dislike();
                    deletMusicByName(musicPlayModel.getMusicInfoCurrent().getTitle());
                    YoYo.with(Techniques.Hinge).withListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            YoYo.with(Techniques.FadeIn).playOn(musicLike);
                        }
                    }).playOn(musicLike);
                }
            }
        });

        mainDrawer.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                ((MainActivity)getActivity()).hideToolBar(slideOffset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        getRandSongList("1");
    }

    @Click(R.id.music_pre)
    void onMusicPre() {
        musicController.pre();
    }

    @Click(R.id.music_next)
    void onMusicNext() {
        musicController.next();
    }

    @Click(R.id.music_list)
    void onMusicList() {
        FavouriteSongsDialog favouriteSongsDialog = new FavouriteSongsDialog(getActivity(), musicContentRoot, this);
        favouriteSongsDialog.show();
    }

    @Click(R.id.music_download)
    void onMusicDownLoadClick() {
        String sdStatus = Environment.getExternalStorageState();
        if (sdStatus.equals(Environment.MEDIA_MOUNTED) || sdStatus.equals(Environment.MEDIA_SHARED)) {
            final String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TorrFM/music/";
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            Observable.create(new Observable.OnSubscribe<List<SongInfo>>() {
                @Override
                public void call(Subscriber<? super List<SongInfo>> subscriber) {
                    try {
                        Dao dao = dbHelper.getDao(SongInfo.class);
                        subscriber.onNext((List<SongInfo>) dao.queryBuilder().where().eq("title"
                                , musicPlayModel.getMusicInfoCurrent().getTitle()).query());
                        subscriber.onCompleted();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    }
                }
            }).subscribeOn(Schedulers.from(Constant.executor))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<SongInfo>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            SuperToast.create(getActivity(), "数据库读取失败"
                                    , SuperToast.Duration.LONG
                                    , Style.getStyle(Style.RED, SuperToast.Animations.FADE)).show();
                        }

                        @Override
                        public void onNext(List<SongInfo> songInfos) {
                            if (null == songInfos || songInfos.size() <= 0) {
                                downloadMusic(savePath);
                            } else {
                                SuperToast.create(getActivity(), "歌曲已经在缓存列表中"
                                        , SuperToast.Duration.LONG
                                        , Style.getStyle(Style.RED, SuperToast.Animations.FADE)).show();
                            }
                        }

                        @Override
                        public void onStart() {
                            SuperToast.create(getActivity()
                                    , "开始缓存音乐:" + musicPlayModel.getMusicInfoCurrent().getTitle()
                                    , SuperToast.Duration.LONG
                                    , Style.getStyle(Style.RED, SuperToast.Animations.FADE)).show();
                        }
                    });
        } else {
            SuperToast.create(getActivity(), "手机存储空间似乎不可用"
                    , SuperToast.Duration.LONG
                    , Style.getStyle(Style.RED, SuperToast.Animations.FADE)).show();
        }
    }

    @Override
    @UiThread
    public void onMusicPlayStateChange(final MusicPlayModel musicPlayModel) {
        final SongInfo currentInfo = musicPlayModel.getMusicInfoCurrent();

        musicPorgress.setSecondaryProgress(musicPlayModel.getMusicBufferPercent());
        if (0 != musicPlayModel.getMusicTimeTotal()) {
            musicPorgress.setProgress(musicPlayModel.getMusicTimeCurrent() * 100 / musicPlayModel.getMusicTimeTotal());
        }

        musicCurrentTime.setText(Util.FormatMusicTime(musicPlayModel.getMusicTimeCurrent()));
        musicTotalTime.setText(Util.FormatMusicTime(musicPlayModel.getMusicTimeTotal()));
        musicLike.justChangeCheckState(musicPlayModel.isLike());

        mLrcController.setMusicTimeCurrent(musicPlayModel.getMusicTimeCurrent());

        if (null != currentInfo.getImg() && !musicCoverPre.equals(currentInfo.getImg())) {
            mLrcController.reset();
            musicCoverPre = currentInfo.getImg();
            YoYo.with(Techniques.SlideOutUp).duration(200).withListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
                        @Override
                        public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable anim) {
                            if (imageInfo == null) {
                                return;
                            }
                            getActivity().runOnUiThread(new Runnable() {
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

                    AbstractDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder()
                            .setControllerListener(controllerListener);
                    if(currentInfo.isDownload()){
                        musicCover.setController(builder.setUri(Uri.fromFile(new File(currentInfo.getImg()))).build());
                    }else {
                        musicCover.setController(builder.setUri(Uri.parse(currentInfo.getImg())).build());
                    }
                }
            }).playOn(musicCover);

            ImagePipeline imagePipeline = Fresco.getImagePipeline();
            ImageRequestBuilder builder;
            if(currentInfo.isDownload()){
                builder = ImageRequestBuilder.newBuilderWithSource(Uri.fromFile(new File(currentInfo.getImg())));
            }else {
                builder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(currentInfo.getImg()));
            }
            final ImageRequest request = builder.build();
            DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(request, this);
            dataSource.subscribe(new BaseBitmapDataSubscriber() {
                @Override
                protected void onNewResultImpl(final Bitmap bitmap) {
                    ((MainActivity) getActivity()).setMainBg(bitmap);
                }

                @Override
                protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

                }
            }, Constant.executor);

            ((MainActivity)getActivity()).setTitle(musicPlayModel.getMusicInfoCurrent().getTitle()
                    , musicPlayModel.getMusicInfoCurrent().getArtist_name());

            //以下开始初始化歌词
            RetrofitClient.apiService.getLrc("http://danmu.fm/x/?lrc/" + currentInfo.getSid())
                    .subscribeOn(Schedulers.from(Constant.executor))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ResponseBody>() {
                        @Override
                        public void onCompleted() {
                            lrcHint.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Throwable e) {
                            SuperToast.create(getActivity()
                                    , "歌词初始化失败"
                                    , SuperToast.Duration.LONG
                                    , Style.getStyle(Style.RED, SuperToast.Animations.FADE)).show();
                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            try {
                                mLrcModel.setLrcInfos(Util.decodeLrc(responseBody.string()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                                SuperToast.create(getActivity()
                                        , "歌词格式错误"
                                        , SuperToast.Duration.LONG
                                        , Style.getStyle(Style.RED, SuperToast.Animations.FADE)).show();
                            }
                        }

                        @Override
                        public void onStart() {
                            lrcHint.setVisibility(View.VISIBLE);
                        }
                    });
        }
    }

    @Override
    public void onFavouriteSongsItemClick(List<SongInfo> songInfos, int postion) {
        musicController.pause();
        boolean isAutoPlay = musicPlayModel.isMusicPlaying();
        musicPlayModel.setSongInfos(songInfos);
        musicPlayModel.setMusicIndexCurrent(postion);
        musicController.preparePlay();
        if(isAutoPlay){
            musicController.play();
        }
    }

    @Override
    public void onFavouriteSongsItemDelete(final List<SongInfo> songInfos, final int postion) {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog.setTitleText("提示");
        sweetAlertDialog.setContentText("确定要从列表中删除:" + songInfos.get(postion).getTitle() + "?");
        sweetAlertDialog.setConfirmText("删除");
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                if (musicPlayModel.getMusicInfoCurrent().isDownload() && musicPlayModel.getMusicIndexCurrent() == postion) {
                    boolean isAutoPlay = musicPlayModel.isMusicPlaying();
                    musicController.pause();
                    musicController.next();
                    if (isAutoPlay) {
                        musicController.play();
                    }
                }
                deletMusicByName(songInfos.get(postion).getTitle());
            }
        });
        sweetAlertDialog.setCancelText("取消");
    }

    @Override
    @UiThread
    public void onLrcUpdate(LrcModel lrcModel) {
        mLrcView.invalidate();
    }

    void downloadMusic(final String savePath) {
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getActivity());
        final SongInfo currentMusicInfo = musicPlayModel.getMusicInfoCurrent();
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                Request request = new Request.Builder().url(currentMusicInfo.getSrc()).build();
                BufferedSink output = null;
                BufferedSource input = null;
                try {
                    Dao songInfoDao = songInfoDao = dbHelper.getDBDao(SongInfo.class);
                    Response response = RetrofitClient.okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        File file = new File(savePath + currentMusicInfo.getTitle() + "." + MimeTypeMap.getFileExtensionFromUrl(currentMusicInfo.getSrc()));
                        if (!file.exists()) {
                            if (!file.createNewFile()) {
                                subscriber.onError(new IOException("无法创建歌曲文件"));
                                return;
                            }
                        }
                        output = Okio.buffer(Okio.sink(file));
                        input = Okio.buffer(Okio.source(response.body().byteStream()));
                        long totalByteLength = response.body().contentLength();
                        byte data[] = new byte[2048];

                        subscriber.onNext(0);
                        long total = 0;
                        int count = 0;
                        while ((count = input.read(data)) != -1) {
                            total += count;
                            output.write(data, 0, count);
                            subscriber.onNext((int) (total * 100 / totalByteLength));
                        }
                        output.flush();
                        output.close();
                        input.close();
                    } else {
                        subscriber.onError(new Exception("下载歌曲时网络错误"));
                    }

                    request = new Request.Builder().url(currentMusicInfo.getImg()).build();
                    response = RetrofitClient.okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        File file = new File(savePath + currentMusicInfo.getTitle() + "." + MimeTypeMap.getFileExtensionFromUrl(currentMusicInfo.getImg()));
                        if (!file.exists()) {
                            if (!file.createNewFile()) {
                                subscriber.onError(new IOException("无法创建歌曲封面文件"));
                                return;
                            }
                        }
                        output = Okio.buffer(Okio.sink(file));
                        input = Okio.buffer(Okio.source(response.body().byteStream()));
                        byte data[] = new byte[2048];
                        int count;
                        while ((count = input.read(data)) != -1) {
                            output.write(data, 0, count);
                        }
                        output.flush();
                        output.close();
                        input.close();
                    } else {
                        subscriber.onError(new Exception("网络错误"));
                    }

                    request = new Request.Builder().url(currentMusicInfo.getImg()).build();
                    response = RetrofitClient.okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        File musicCoverFile = new File(savePath + currentMusicInfo.getTitle() + ".lrc");
                        if (!musicCoverFile.exists()) {
                            if (!musicCoverFile.createNewFile()) {
                                subscriber.onError(new IOException("无法创建歌词文件"));
                                return;
                            }
                        }
                        output = Okio.buffer(Okio.sink(musicCoverFile));
                        input = Okio.buffer(Okio.source(response.body().byteStream()));
                        byte musicCoverData[] = new byte[2048];

                        int musicCoverDataCount;
                        while ((musicCoverDataCount = input.read(musicCoverData)) != -1) {
                            output.write(musicCoverData, 0, musicCoverDataCount);
                        }
                        output.flush();
                        output.close();
                        input.close();
                    } else {
                        subscriber.onError(new Exception("网络错误"));
                    }

                    if (musicPlayModel.isLike() && null != songInfoDao) {
                        SongInfo songInfo = new SongInfo();
                        songInfo.setTitle(currentMusicInfo.getTitle());
                        songInfo.setArtist_name(currentMusicInfo.getArtist_name());
                        songInfo.setSrc(savePath + currentMusicInfo.getTitle() + "." + MimeTypeMap.getFileExtensionFromUrl(currentMusicInfo.getSrc()));
                        songInfo.setImg(savePath + currentMusicInfo.getTitle() + "." + MimeTypeMap.getFileExtensionFromUrl(currentMusicInfo.getImg()));
                        songInfo.setLrcPath(savePath + currentMusicInfo.getTitle() + ".lrc");
                        songInfo.setIsDownload(true);
                        songInfo.setTime(System.currentTimeMillis());
                        songInfoDao.createOrUpdate(songInfo);
                    }

                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(new IOException("数据读写错误"));
                } catch (SQLException e) {
                    e.printStackTrace();
                    subscriber.onError(new SQLException("数据库文件读写失败"));
                }
            }
        }).throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.from(Constant.executor))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        notificationBuilder.setProgress(100, 100, false);
                        notificationBuilder.setContentTitle(musicPlayModel.getMusicInfoCurrent().getTitle() + "下载完毕");
                        mNotifyMgr.notify(DOWNLOAD_NOTIFICATION_ID, notificationBuilder.build());
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        notificationBuilder.setContentTitle(musicPlayModel.getMusicInfoCurrent().getTitle() + "下载失败");
                        mNotifyMgr.notify(DOWNLOAD_NOTIFICATION_ID, notificationBuilder.build());
                        SuperToast.create(getActivity(), musicPlayModel.getMusicInfoCurrent().getTitle() + "下载失败:" + e.getMessage(), SuperToast.Duration.LONG
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
                        notificationBuilder.setContentTitle("正在下载" + musicPlayModel.getMusicInfoCurrent().getTitle());
                        mNotifyMgr.notify(DOWNLOAD_NOTIFICATION_ID, notificationBuilder.build());
                    }
                });
    }

    void deletMusicByName(final String songName) {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    Dao dao = dbHelper.getDao(SongInfo.class);
                    DeleteBuilder deleteBuilder = dao.deleteBuilder();
                    deleteBuilder.where().eq("title", songName);
                    deleteBuilder.delete();
                    subscriber.onCompleted();
                } catch (SQLException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        })
                .subscribeOn(Schedulers.from(Constant.executor))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        SuperToast.create(getActivity()
                                , "删除歌曲失败"
                                , SuperToast.Duration.LONG
                                , Style.getStyle(Style.RED, SuperToast.Animations.FADE)).show();
                    }
                });
    }

    void initDrawer() {
        RetrofitClient.apiService.getChannelInfo()
                .subscribeOn(Schedulers.from(Constant.executor))
                .observeOn(Schedulers.from(Constant.executor))
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
                        SuperToast.create(getActivity(), "音乐分类初始化失败"
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

                        if (null != channelDao) {
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

    void initNavigationRecycler(List<ChannelInfo> channelInfos) {
        NavigationAdapar navigationAdapar = new NavigationAdapar(getActivity(), channelInfos, new NavigationAdapar.IItemClick() {
            @Override
            public void onItemClick(int postion, ChannelInfo channelInfo) {
                onNavigationItemClick(postion, channelInfo);
            }
        });
        navigationRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        navigationRecycler.setAdapter(navigationAdapar);
    }

    void onNavigationItemClick(int postion, ChannelInfo channelInfo) {
        if (mainDrawer.isDrawerOpen(Gravity.LEFT)) {
            mainDrawer.closeDrawers();
        }
        getRandSongList(channelInfo.getHid());
    }

    void getRandSongList(String hid) {
        final SweetAlertDialog progressDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.setCancelable(false);
        progressDialog.setTitleText("正在拉取歌单…");
        RetrofitClient.apiService.getRandSong(hid).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.from(Constant.executor))
                .subscribe(new Subscriber<List<SongInfo>>() {
                    @Override
                    public void onStart() {
                        progressDialog.show();
                    }

                    @Override
                    public void onCompleted() {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (progressDialog.isShowing()) {
                            progressDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            progressDialog.setTitleText("生成随机音乐歌单失败");
                            progressDialog.setContentText("请从左边频道中选择分类以重新生成歌单");
                            mainDrawer.openDrawer(Gravity.LEFT);
                        }
                    }

                    @Override
                    public void onNext(List<SongInfo> songInfos) {
                        musicPlayModel.setSongInfos(songInfos);
                        musicController.preparePlay();
                    }
                });
    }

    public void openDrawer(){
        mainDrawer.openDrawer(Gravity.LEFT);
    }

    public void onSearchItemClick(List<SongInfo> songInfos){
        onFavouriteSongsItemClick(songInfos, 0);
    }
}

