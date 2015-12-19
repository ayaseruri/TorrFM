package ayaseruri.torr.torrfm.fragment;


import android.support.v4.app.Fragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;

import java.util.List;

import ayaseruri.torr.torrfm.R;
import ayaseruri.torr.torrfm.global.Constant;
import ayaseruri.torr.torrfm.network.ApiService;
import ayaseruri.torr.torrfm.network.RetrofitClient;
import ayaseruri.torr.torrfm.objectholder.SongInfo;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@EFragment(R.layout.fragment_search)
public class SearchFragment extends Fragment {

    ApiService apiService;

    @AfterViews
    void init(){
        apiService = RetrofitClient.apiService;
        search(null);
    }

    public void search(String key){
        apiService.searchMusic(key).subscribeOn(Schedulers.from(Constant.executor))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<SongInfo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<SongInfo> songInfos) {

                    }
                });
    }
}
