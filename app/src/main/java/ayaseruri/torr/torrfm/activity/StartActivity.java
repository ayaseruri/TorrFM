package ayaseruri.torr.torrfm.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import ayaseruri.torr.torrfm.R;
import ayaseruri.torr.torrfm.global.Constant;
import ayaseruri.torr.torrfm.network.RetrofitClient;
import ayaseruri.torr.torrfm.objectholder.OneSentenceInfo;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

@EActivity(R.layout.activity_start)
public class StartActivity extends AppCompatActivity {
    private static final int DURATION = 3000;
    @ViewById(R.id.logo)
    ImageView logo;
    @ViewById(R.id.one_sentence)
    TextView oneSentence;

    @AfterViews
    void init() {
        YoYo.with(Techniques.DropOut).duration(DURATION).withListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
//                startMain();
            }
        }).playOn(logo);

        RetrofitClient.apiService.getOneSentence("http://api.hitokoto.us/rand?&cat=a")
                .subscribeOn(Schedulers.from(Constant.executor))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<OneSentenceInfo>() {
                    @Override
                    public void call(OneSentenceInfo oneSentenceInfo) {
                        oneSentence.setText(oneSentenceInfo.getHitokoto());
                    }
                });
        startMain();
    }

    @UiThread
    void startMain(){
        Intent intent = new Intent(this, MainActivity_.class);
        startActivity(intent);
        finish();
    }
}
