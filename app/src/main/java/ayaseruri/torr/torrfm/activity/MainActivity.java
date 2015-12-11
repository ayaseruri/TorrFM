package ayaseruri.torr.torrfm.activity;

import android.app.Dialog;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import ayaseruri.torr.torrfm.R;
import ayaseruri.torr.torrfm.adaptar.MusicListAdaptar;
import ayaseruri.torr.torrfm.adaptar.NavigationAdapar;
import ayaseruri.torr.torrfm.network.RetrofitClient;
import ayaseruri.torr.torrfm.objectholder.ChannelInfo;
import ayaseruri.torr.torrfm.objectholder.SongInfo;
import cn.pedant.SweetAlert.SweetAlertDialog;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
    private List<SongInfo> mSongInfos;

    @ViewById
    Toolbar toolbar;
    @ViewById(R.id.navigation_recycler)
    RecyclerView navigationRecycler;
    @ViewById(R.id.main_drawer)
    DrawerLayout mainDrawer;

    @AfterViews
    void init(){
        setSupportActionBar(toolbar);
        initDrawer();
    }

    @Click(R.id.music_play)
    void onMusicPlay(){

    }

    @Click(R.id.music_list)
    void onMusicList(){
        if(null == mSongInfos || mSongInfos.size() == 0){
            mainDrawer.openDrawer(Gravity.LEFT);
            SuperToast.create(this, "请选择类别以初始化歌单", SuperToast.Duration.LONG
            , Style.getStyle(Style.RED, SuperToast.Animations.FADE)).show();
        }else {
            Dialog musicListDialog = new Dialog(this);
            musicListDialog.setTitle("播放列表（" + mSongInfos.size() + "）");
            View view = LayoutInflater.from(this).inflate(R.layout.music_list_dialog, null);
            RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.music_list);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            MusicListAdaptar musicListAdaptar = new MusicListAdaptar(this, mSongInfos, new MusicListAdaptar.IItemClick() {
                @Override
                public void onItemClick(int postion, SongInfo songInfo) {

                }
            });
            recyclerView.setAdapter(musicListAdaptar);
            musicListDialog.setContentView(view);
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
                        mSongInfos = songInfos;

                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });
    }
}
