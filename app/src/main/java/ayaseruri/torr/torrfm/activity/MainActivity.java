package ayaseruri.torr.torrfm.activity;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.commit451.nativestackblur.NativeStackBlur;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import ayaseruri.torr.torrfm.R;
import ayaseruri.torr.torrfm.adaptar.MainViewPagerAdaptar;
import ayaseruri.torr.torrfm.fragment.MainFragment;
import ayaseruri.torr.torrfm.fragment.MainFragment_;
import ayaseruri.torr.torrfm.view.MViewPager;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    private List<Fragment> fragments;
    private MainFragment mainFragment;

    @ViewById(R.id.toolbar)
    Toolbar toolbar;
    @ViewById(R.id.main_view_pager)
    MViewPager mainViewPager;
    @ViewById(R.id.bg)
    ImageView bg;
    @ViewById(R.id.title)
    TextView title;
    @ViewById(R.id.subTile)
    TextView subTitle;

    @AfterViews
    void init(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mainFragment = MainFragment_.builder().build();
        fragments = new ArrayList<>();
        fragments.add(mainFragment);

        MainViewPagerAdaptar mainViewPagerAdaptar = new MainViewPagerAdaptar(getSupportFragmentManager(), fragments);
        mainViewPager.setAdapter(mainViewPagerAdaptar);
    }

    public void setMainBg(final Bitmap bitmap){
        final Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.music_bg_fade_in);
        final Animation fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.music_bg_fade_out);

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

        bg.setAnimation(fadeOutAnimation);
    }

    public void setTitle(final String titleStr, final String subTitleStr){
        YoYo.with(Techniques.RotateOutUpLeft).duration(200).withListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (subTitle.getVisibility() == View.GONE) {
                    subTitle.setVisibility(View.VISIBLE);
                }
                YoYo.with(Techniques.RotateOutUpLeft).duration(200).withListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        subTitle.setText(subTitleStr);
                        YoYo.with(Techniques.RotateInDownLeft).playOn(subTitle);
                    }
                }).playOn(subTitle);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                title.setText(titleStr);
                YoYo.with(Techniques.RotateInDownLeft).playOn(title);
            }
        }).playOn(title);

    }

    public void hideToolBar(float precentage){
        int height = toolbar.getHeight();
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)toolbar.getLayoutParams();
        lp.setMargins(0, (int)(-1 * height * precentage), 0, (int)(height * precentage));
        toolbar.setLayoutParams(lp);
    }
}