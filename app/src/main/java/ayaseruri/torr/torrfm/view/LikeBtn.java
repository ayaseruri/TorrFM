package ayaseruri.torr.torrfm.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.mingle.SimpleAnimationListener;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EView;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import ayaseruri.torr.torrfm.R;

/**
 * Created by wufeiyang on 16/3/1.
 */
@EViewGroup(R.layout.like_btn)
public class LikeBtn extends FrameLayout implements View.OnClickListener{

    private static final int HEART_SCALE_SMALL_TIME = 800;

    private boolean islike;
    private OnClickListener mOnClickListener;
    private AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator();
    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();

    @ViewById(R.id.heart_img)
    ImageView heartImg;
    @ViewById(R.id.start_img)
    ImageView starImg;
    @ViewById(R.id.ring_img)
    RingImg ringImg;

    public LikeBtn(Context context) {
        super(context);
    }

    public LikeBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @AfterViews
    void init(){
        super.setClickable(true);
        super.setOnClickListener(this);
    }

    public void setIslike(boolean islike, boolean animation) {
        this.islike = islike;
    }

    @Override
    public void onClick(View v) {
        super.setOnClickListener(null);
        if(null != mOnClickListener){
            mOnClickListener.onClick(v);
        }
        islike = !islike;
        if(islike){
            ObjectAnimator heartScaleX = ObjectAnimator.ofFloat(heartImg, "scaleX", 0.05f);
            ObjectAnimator heartScaleY = ObjectAnimator.ofFloat(heartImg, "scaleY", 0.05f);
            heartScaleX.setDuration(HEART_SCALE_SMALL_TIME);
            heartScaleY.setDuration(HEART_SCALE_SMALL_TIME);
            heartScaleX.setInterpolator(accelerateInterpolator);
            heartScaleY.setInterpolator(accelerateInterpolator);
            heartScaleY.addListener(new SimpleAnimationListener(){
                @Override
                public void onAnimationEnd(Animator animation) {
                    heartImg.setVisibility(GONE);
                    secondPart(heartImg.getScaleY() * heartImg.getHeight());
                }
            });

            ObjectAnimator heartRotateAnimator = ObjectAnimator.ofFloat(heartImg, "rotation", 0f, 360f);
            heartRotateAnimator.setDuration(HEART_SCALE_SMALL_TIME);
            heartRotateAnimator.setInterpolator(accelerateInterpolator);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(heartScaleX).with(heartScaleY).with(heartRotateAnimator);
            animatorSet.start();
        }else {
            heartImg.setColorFilter(Color.parseColor("#C0C0C0"));
            YoYo.with(Techniques.Tada).duration(HEART_SCALE_SMALL_TIME).withListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    LikeBtn.super.setOnClickListener(LikeBtn.this);
                }
            }).playOn(this);
        }
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    void secondPart(final float startSize){
        ringImg.setVisibility(VISIBLE);
        ringImg.setmOvalInner(0);

        ValueAnimator outterAnimator = ObjectAnimator.ofFloat(startSize, Math.min(heartImg.getWidth(), heartImg.getHeight())/2);
        outterAnimator.setDuration(HEART_SCALE_SMALL_TIME);
        outterAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ringImg.setmOvalOutter((float)animation.getAnimatedValue());
                ringImg.invalidate();
            }
        });
        outterAnimator.setInterpolator(decelerateInterpolator);



        ValueAnimator innerAnimator = ObjectAnimator.ofFloat(0, Math.min(heartImg.getWidth(), heartImg.getHeight())/2);
        innerAnimator.setDuration(HEART_SCALE_SMALL_TIME);
        innerAnimator.addListener(new SimpleAnimationListener(){
            @Override
            public void onAnimationEnd(Animator animation) {
                ringImg.setVisibility(GONE);
            }
        });
        innerAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                starImg.setScaleX((float)animation.getAnimatedValue()/(Math.min(heartImg.getWidth(), heartImg.getHeight())/2));
                starImg.setScaleY(starImg.getScaleX());

                if(starImg.getVisibility() == GONE){
                    starImg.setVisibility(VISIBLE);
                }

                ringImg.setmOvalInner((float)animation.getAnimatedValue());
                ringImg.invalidate();
            }
        });
        innerAnimator.setInterpolator(accelerateInterpolator);


        ObjectAnimator startRotateAnimator = ObjectAnimator.ofFloat(starImg, "rotation", 0f, 270f);
        startRotateAnimator.setDuration(HEART_SCALE_SMALL_TIME + 250);

        ObjectAnimator heartRotateAnimator = ObjectAnimator.ofFloat(heartImg, "rotation", 0f, 360f);
        heartRotateAnimator.setDuration(HEART_SCALE_SMALL_TIME + 500);
        heartRotateAnimator.addListener(new SimpleAnimationListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                heartImg.setVisibility(VISIBLE);
                heartImg.setColorFilter(Color.parseColor("#FE0D1D"));
            }
        });
        heartRotateAnimator.setInterpolator(accelerateInterpolator);

        ObjectAnimator heartScaleX = ObjectAnimator.ofFloat(heartImg, "scaleX", 1f);
        ObjectAnimator heartScaleY = ObjectAnimator.ofFloat(heartImg, "scaleY", 1f);
        heartScaleY.addListener(new SimpleAnimationListener(){
            @Override
            public void onAnimationEnd(Animator animation) {
                LikeBtn.super.setOnClickListener(LikeBtn.this);
            }
        });
        heartScaleX.setDuration(HEART_SCALE_SMALL_TIME);
        heartScaleY.setDuration(heartScaleX.getDuration());
        heartScaleX.setInterpolator(accelerateInterpolator);
        heartScaleY.setInterpolator(accelerateInterpolator);

        ObjectAnimator starScaleX = ObjectAnimator.ofFloat(starImg, "scaleX", 0.1f);
        ObjectAnimator startScaleY = ObjectAnimator.ofFloat(starImg, "scaleY", 0.1f);
        starScaleX.setDuration(250);
        startScaleY.setDuration(starScaleX.getDuration());
        starScaleX.setInterpolator(decelerateInterpolator);
        startScaleY.setInterpolator(decelerateInterpolator);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(outterAnimator);
        animatorSet.play(innerAnimator)
                .with(heartRotateAnimator)
                .with(startRotateAnimator)
                .before(starScaleX)
                .before(startScaleY)
                .after(100);
        animatorSet.play(heartScaleX)
                .with(heartScaleY)
                .after(100 * 6);
        animatorSet.start();
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        this.mOnClickListener = l;
    }
}
