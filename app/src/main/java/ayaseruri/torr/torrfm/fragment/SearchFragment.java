package ayaseruri.torr.torrfm.fragment;


import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import ayaseruri.torr.torrfm.R;
import ayaseruri.torr.torrfm.activity.MainActivity;
import ayaseruri.torr.torrfm.adaptar.SearchListAdaptar;
import ayaseruri.torr.torrfm.global.Constant;
import ayaseruri.torr.torrfm.global.MApplication;
import ayaseruri.torr.torrfm.network.ApiService;
import ayaseruri.torr.torrfm.network.RetrofitClient;
import ayaseruri.torr.torrfm.objectholder.SongInfo;
import cn.pedant.SweetAlert.SweetAlertDialog;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@EFragment(R.layout.fragment_search)
public class SearchFragment extends Fragment {

    private ApiService apiService;
    private LinearLayoutManager linearLayoutManager;

    @ViewById(R.id.search_recycler)
    RecyclerView searchRecycler;
    @App
    MApplication mApplication;

    @AfterViews
    void init(){
        int extraPadding = 0;
        extraPadding = ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) ? mApplication.getStatusBarHeight() : 0)
                + searchRecycler.getPaddingTop();
        searchRecycler.setPadding(0, extraPadding, 0, 0);

        apiService = RetrofitClient.apiService;
        linearLayoutManager = new LinearLayoutManager(getActivity());
        searchRecycler.setLayoutManager(linearLayoutManager);
        search("我", false);
    }

    public void search(String key, final boolean showProgress){
        final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        if(showProgress){
            sweetAlertDialog.setTitleText("稍等片刻，音乐马上就来");
        }

        apiService.searchMusic("http://danmu.fm/x/?search/" + key).subscribeOn(Schedulers.from(Constant.executor))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<SongInfo>>() {
                    @Override
                    public void onCompleted() {
                        sweetAlertDialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        sweetAlertDialog.setTitleText("搜索信息获取失败");
                    }

                    @Override
                    public void onNext(List<SongInfo> songInfos) {
                        afterSearchResult(songInfos);
                    }

                    @Override
                    public void onStart() {
                        if (showProgress) {
                            sweetAlertDialog.show();
                        }
                    }
                });
    }

    private void afterSearchResult(List<SongInfo> songInfos){
        if(null == songInfos){
            SuperToast.create(getActivity()
                    , "什么都没有找到"
                    , SuperToast.Duration.LONG
                    , Style.getStyle(Style.RED, SuperToast.Animations.FADE)).show();
        }else {
            SearchListAdaptar searchListAdaptar = new SearchListAdaptar(getActivity(), songInfos, new SearchListAdaptar.ItemClick() {
                @Override
                public void onSearchItemClick(int postion, SongInfo songInfo) {
                    ((MainActivity)getActivity()).onSearchItemClick(songInfo);
                }
            });

            searchRecycler.setAdapter(searchListAdaptar);
        }
    }
}
