package ayaseruri.torr.torrfm.view;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import ayaseruri.torr.torrfm.R;
import ayaseruri.torr.torrfm.adaptar.MusicListAdaptar;
import ayaseruri.torr.torrfm.db.DBHelper;
import ayaseruri.torr.torrfm.global.Constant;
import ayaseruri.torr.torrfm.model.FavouriteSongsModel;
import ayaseruri.torr.torrfm.objectholder.SongInfo;
import ayaseruri.torr.torrfm.utils.LocalDisplay;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by ayaseruri on 15/12/16.
 */
public class FavouriteSongsDialog extends Dialog implements FavouriteSongsModel.IFavouriteSongsChanged {
    private MusicListAdaptar musicListAdaptar;
    private Context mContext;
    private FavouriteSongsModel favouriteSongsModel;
    private IFavouriteSongsItemAction iFavouriteSongsItemAction;

    public FavouriteSongsDialog(Context context, IFavouriteSongsItemAction iFavouriteSongsItemAction) {
        super(context);
        this.mContext = context;
        this.iFavouriteSongsItemAction = iFavouriteSongsItemAction;
        init();
    }

    private void init() {
        this.setTitle("我的喜欢(0)");
        favouriteSongsModel = new FavouriteSongsModel();
        favouriteSongsModel.addIFavouriteSongsChanged(this);

        Observable.create(new Observable.OnSubscribe<List<SongInfo>>() {
            @Override
            public void call(Subscriber<? super List<SongInfo>> subscriber) {
                try {
                    Dao dao = DBHelper.getInstance(mContext).getDao(SongInfo.class);
                    List<SongInfo> songInfos = (List<SongInfo>) dao.queryForAll();
                    subscriber.onNext(songInfos);
                    subscriber.onCompleted();
                } catch (SQLException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.from(Constant.executor))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<SongInfo>>() {
                    @Override
                    public void call(List<SongInfo> songInfos) {
                        favouriteSongsModel.setFavouriteSongs(songInfos);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        SuperToast.create(mContext
                                , "读取喜欢音乐列表数据库失败"
                                , SuperToast.Duration.LONG
                                , Style.getStyle(Style.RED, SuperToast.Animations.FADE)).show();
                    }
                });
    }

    @Override
    public void onFavouriteSongsChanged(List<SongInfo> songInfos) {
        this.setTitle("我的喜欢(" + songInfos.size() + ")");
        if(null == musicListAdaptar){
            View view = LayoutInflater.from(mContext).inflate(R.layout.music_list_dialog, null);
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.music_list);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            musicListAdaptar = new MusicListAdaptar(mContext, favouriteSongsModel.getFavouriteSongs(), new MusicListAdaptar.IItemAction() {
                @Override
                public void onItemClick(int postion, SongInfo songInfo) {
                    iFavouriteSongsItemAction.onFavouriteSongsItemClick(favouriteSongsModel.getFavouriteSongs(), postion);
                }

                @Override
                public void onItemDelete(int postion, SongInfo songInfo) {
                    iFavouriteSongsItemAction.onFavouriteSongsItemDelete(favouriteSongsModel.getFavouriteSongs(), postion);
                    favouriteSongsModel.deleteSong(postion);
                }
            });
            this.setContentView(view);
            recyclerView.setAdapter(musicListAdaptar);
        }
        musicListAdaptar.notifyDataSetChanged();
    }

    @Override
    public void show() {
        super.show();
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.width = LocalDisplay.SCREEN_WIDTH_PIXELS;
        lp.height = LocalDisplay.SCREEN_HEIGHT_PIXELS / 2;
        lp.gravity = Gravity.BOTTOM;
        this.getWindow().setAttributes(lp);
    }

    public interface IFavouriteSongsItemAction {
        void onFavouriteSongsItemClick(List<SongInfo> songInfos, int postion);

        void onFavouriteSongsItemDelete(List<SongInfo> songInfos, int postion);
    }
}
