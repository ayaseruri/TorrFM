package ayaseruri.torr.torrfm.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.j256.ormlite.dao.Dao;
import com.mingle.sweetpick.CustomDelegate;
import com.mingle.sweetpick.DimEffect;
import com.mingle.sweetpick.SweetSheet;

import java.sql.SQLException;
import java.util.List;

import ayaseruri.torr.torrfm.R;
import ayaseruri.torr.torrfm.adaptar.MusicListAdaptar;
import ayaseruri.torr.torrfm.db.DBHelper;
import ayaseruri.torr.torrfm.global.Constant;
import ayaseruri.torr.torrfm.model.FavouriteSongsModel;
import ayaseruri.torr.torrfm.objectholder.SongInfo;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by ayaseruri on 15/12/16.
 */
public class FavouriteSongsDialog extends SweetSheet implements FavouriteSongsModel.IFavouriteSongsChanged {
    private MusicListAdaptar musicListAdaptar;
    private Context mContext;
    private FavouriteSongsModel favouriteSongsModel;
    private IFavouriteSongsItemAction iFavouriteSongsItemAction;
    private RecyclerView mRecyclerView;
    private TextView title;

    public FavouriteSongsDialog(Context context, FrameLayout root, IFavouriteSongsItemAction iFavouriteSongsItemAction) {
        super(root);
        this.mContext = context;
        this.iFavouriteSongsItemAction = iFavouriteSongsItemAction;
        init();
    }

    private void init() {
        favouriteSongsModel = new FavouriteSongsModel();
        favouriteSongsModel.addIFavouriteSongsChanged(this);

        View view = LayoutInflater.from(mContext).inflate(R.layout.music_list_dialog, null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.music_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        title = (TextView)view.findViewById(R.id.dialog_title);
        title.setText("我的喜欢(0)");

        CustomDelegate customDelegate = new CustomDelegate(false, CustomDelegate.AnimationType.DuangLayoutAnimation);
        customDelegate.setCustomView(view);
        this.setDelegate(customDelegate);
        this.setBackgroundEffect(new DimEffect(40));

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
        if(null == musicListAdaptar){
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
            mRecyclerView.setAdapter(musicListAdaptar);
        }
        title.setText("我的喜欢(" + songInfos.size() + ")");
        musicListAdaptar.notifyDataSetChanged();
    }

    public interface IFavouriteSongsItemAction {
        void onFavouriteSongsItemClick(List<SongInfo> songInfos, int postion);
        void onFavouriteSongsItemDelete(List<SongInfo> songInfos, int postion);
    }
}
