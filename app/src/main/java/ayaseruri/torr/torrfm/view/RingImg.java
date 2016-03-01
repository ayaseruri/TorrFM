package ayaseruri.torr.torrfm.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by wufeiyang on 16/3/1.
 */
public class RingImg extends ImageView {

    private RectF mOvalRf;
    private Paint mOvalPint;
    private float mOvalInner;
    private float mOvalOutter;

    public RingImg(Context context) {
        super(context);
        init();
    }

    public RingImg(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        mOvalPint = new Paint();
        mOvalRf = new RectF();
        mOvalPint.setAntiAlias(true);
        mOvalPint.setStyle(Paint.Style.STROKE);
        mOvalPint.setColor(Color.parseColor("#FE0D1D"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        float strokeWidth = Math.max(mOvalOutter - mOvalInner, 0f);
        mOvalPint.setStrokeWidth(strokeWidth);
        mOvalRf.left = centerX - mOvalInner - strokeWidth / 2;
        mOvalRf.top = centerY - mOvalInner - strokeWidth / 2;
        mOvalRf.right = centerX + mOvalInner + strokeWidth / 2;
        mOvalRf.bottom = centerY + mOvalInner + strokeWidth / 2;
        canvas.drawOval(mOvalRf, mOvalPint);
    }

    public void setmOvalInner(float mOvalInner) {
        this.mOvalInner = mOvalInner;
    }

    public void setmOvalOutter(float mOvalOutter) {
        this.mOvalOutter = mOvalOutter;
    }
}

