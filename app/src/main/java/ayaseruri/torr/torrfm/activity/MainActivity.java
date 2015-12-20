package ayaseruri.torr.torrfm.activity;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.commit451.nativestackblur.NativeStackBlur;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eftimoff.viewpagertransformers.DepthPageTransformer;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ayaseruri.torr.torrfm.R;
import ayaseruri.torr.torrfm.adaptar.MainViewPagerAdaptar;
import ayaseruri.torr.torrfm.fragment.MainFragment;
import ayaseruri.torr.torrfm.fragment.MainFragment_;
import ayaseruri.torr.torrfm.fragment.SearchFragment;
import ayaseruri.torr.torrfm.fragment.SearchFragment_;
import ayaseruri.torr.torrfm.global.MApplication;
import ayaseruri.torr.torrfm.objectholder.SongInfo;
import ayaseruri.torr.torrfm.view.MViewPager;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity{

    private final static long switchPagerDuration = 300;

    private List<Fragment> fragments;
    private MainFragment mainFragment;

    private SearchFragment searchFragment;

    private MaterialMenuDrawable materialMenu;

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
    @ViewById(R.id.search_edit)
    EditText searchET;
    @App
    MApplication mApplication;

    @AfterViews
    void init(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mainFragment = MainFragment_.builder().build();
        searchFragment = SearchFragment_.builder().build();

        fragments = new ArrayList<>();
        fragments.add(mainFragment);
        fragments.add(searchFragment);

//        mainViewPager.setPadding(0, 0, 0, mApplication.getNavigationBarHeight());
        MainViewPagerAdaptar mainViewPagerAdaptar = new MainViewPagerAdaptar(getSupportFragmentManager(), fragments);
        mainViewPager.setPageTransformer(true, new DepthPageTransformer());
        mainViewPager.setAdapter(mainViewPagerAdaptar);

        materialMenu = new MaterialMenuDrawable(this, Color.WHITE, MaterialMenuDrawable.Stroke.EXTRA_THIN);
        toolbar.setNavigationIcon(materialMenu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MaterialMenuDrawable.IconState.BURGER == materialMenu.getIconState() && mainViewPager.getCurrentItem() == 0) {
                    mainFragment.openDrawer();
                } else if (MaterialMenuDrawable.IconState.ARROW == materialMenu.getIconState() && mainViewPager.getCurrentItem() == 1) {
                    restoreFromSearch();
                }
            }
        });
    }

    @Click(R.id.search_icon)
    void onSearch(){
        if(0 == mainViewPager.getCurrentItem()){
            switchPager(true, switchPagerDuration);
            materialMenu.animateIconState(MaterialMenuDrawable.IconState.ARROW);
            YoYo.with(Techniques.FadeOutLeft).duration(switchPagerDuration).playOn(title);
            YoYo.with(Techniques.FadeOutLeft).duration(switchPagerDuration).playOn(subTitle);
            if (searchET.getVisibility() == View.GONE) {
                searchET.setVisibility(View.VISIBLE);
            }
            YoYo.with(Techniques.FadeIn).duration(switchPagerDuration).withListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    searchET.setFocusable(true);
                    searchET.setFocusableInTouchMode(true);
                    searchET.requestFocus();
                }
            }).playOn(searchET);
        }else {
            if("".equals(searchET.getText().toString())){
                YoYo.with(Techniques.Shake).playOn(searchET);
                SuperToast.create(this
                        , "搜索文字不能为空"
                        , SuperToast.Duration.LONG
                        , Style.getStyle(Style.RED
                        , SuperToast.Animations.FADE)).show();
            }else {
                searchFragment.search(searchET.getText().toString(), true);
            }
        }
    }

    private void restoreFromSearch(){
        materialMenu.animateIconState(MaterialMenuDrawable.IconState.BURGER);
        switchPager(false, switchPagerDuration);
        YoYo.with(Techniques.FadeInLeft).duration(switchPagerDuration).playOn(title);
        YoYo.with(Techniques.FadeInLeft).duration(switchPagerDuration).playOn(subTitle);
        YoYo.with(Techniques.FadeOut).duration(switchPagerDuration).withListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                searchET.setVisibility(View.GONE);
            }
        }).playOn(searchET);
    }

    private void switchPager(final boolean forward, long duration){
        if((0 == mainViewPager.getCurrentItem() && forward) || (1 == mainViewPager.getCurrentItem() && !forward)){
            ValueAnimator animator = ValueAnimator.ofInt(0, mainViewPager.getWidth());
            animator.addListener(new android.animation.Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(android.animation.Animator animation) {

                }

                @Override
                public void onAnimationEnd(android.animation.Animator animation) {
                    mainViewPager.endFakeDrag();
                }

                @Override
                public void onAnimationCancel(android.animation.Animator animation) {
                    mainViewPager.endFakeDrag();
                }

                @Override
                public void onAnimationRepeat(android.animation.Animator animation) {

                }
            });
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                private int oldDragPosition = 0;

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int dragPosition = (Integer) animation.getAnimatedValue();
                    int dragOffset = dragPosition - oldDragPosition;
                    oldDragPosition = dragPosition;
                    mainViewPager.fakeDragBy(dragOffset * (forward ? -1 : 1));
                }
            });
            animator.setInterpolator(new AccelerateInterpolator());
            animator.setDuration(duration);
            mainViewPager.beginFakeDrag();
            animator.start();
        }
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
        YoYo.with(Techniques.FadeOutLeft).withListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (subTitle.getVisibility() == View.GONE) {
                    subTitle.setVisibility(View.VISIBLE);
                }
                YoYo.with(Techniques.FadeOutLeft).withListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        subTitle.setText(subTitleStr);
                        YoYo.with(Techniques.FadeInLeft).playOn(subTitle);
                    }
                }).playOn(subTitle);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                title.setText(titleStr);
                YoYo.with(Techniques.FadeInLeft).playOn(title);
            }
        }).playOn(title);

    }

    public void hideToolBar(float precentage){
        int height = toolbar.getHeight();
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)toolbar.getLayoutParams();
        lp.setMargins(0, (int) (-1 * height * precentage), 0, (int) (height * precentage));
        toolbar.setLayoutParams(lp);
    }

    public void onSearchItemClick(SongInfo songInfo){
        restoreFromSearch();
        mainFragment.onSearchItemClick(Arrays.asList(songInfo));
    }

    @Override
    public void onBackPressed() {
        if(0 == mainViewPager.getCurrentItem()){
            super.onBackPressed();
        }else {
            restoreFromSearch();
        }
    }
}