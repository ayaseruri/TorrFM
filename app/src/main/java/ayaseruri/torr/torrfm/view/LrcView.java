package ayaseruri.torr.torrfm.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.List;

import ayaseruri.torr.torrfm.R;

/**
 * Created by ayaseruri on 15/12/13.
 */
public class LrcView extends TextView {

    private Paint currentPaint;
    private Paint notCurrentPaint;
    private int crrentIndex, currentColor, noCurrentColor;
    private float currentSize, noCurrentSize, lineSpace;
    private List<String> lrcs;

    public LrcView(Context context) {
        super(context);
    }

    public LrcView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LrcView);
        currentColor = array.getColor(R.styleable.LrcView_currentColor
                , getResources().getColor(android.R.color.white));
        noCurrentColor = array.getColor(R.styleable.LrcView_noCurrentColor
                , getResources().getColor(R.color.colorAccent));
        currentSize = array.getDimension(R.styleable.LrcView_currentSize, 12.0f);
        noCurrentSize = array.getDimension(R.styleable.LrcView_noCurrentSize, 12.0f);
        lineSpace = array.getDimension(R.styleable.LrcView_lineSpace, 12.0f);
        array.recycle();

        crrentIndex = 0;

        currentPaint = new Paint();
        currentPaint.setAntiAlias(true);
        currentPaint.setTextSize(currentSize);
        currentPaint.setTextAlign(Paint.Align.CENTER);
        currentPaint.setColor(currentColor);

        notCurrentPaint = new Paint();
        notCurrentPaint.setAntiAlias(true);
        notCurrentPaint.setTextSize(noCurrentSize);
        notCurrentPaint.setTextAlign(Paint.Align.CENTER);
        notCurrentPaint.setColor(noCurrentColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(null != lrcs && lrcs.size() > 0){
            float centerVertical = getHeight()/2;
            float baseLineY = centerVertical - currentSize/2 - lineSpace;
            for(int i = crrentIndex - 1; i >= 0; i--){
                canvas.drawText(lrcs.get(i), getWidth() / 2
                        , baseLineY, notCurrentPaint);
                baseLineY = centerVertical - noCurrentSize/2 - lineSpace;
                if(baseLineY < 0){
                    break;
                }
            }
            canvas.drawText(lrcs.get(crrentIndex), getWidth() / 2
                    , centerVertical + currentSize/2, currentPaint);

            baseLineY = centerVertical + currentSize/2 + lineSpace + noCurrentSize;
            for(int i = crrentIndex + 1; i < lrcs.size(); i++){
                canvas.drawText(lrcs.get(i), getWidth() / 2
                        , baseLineY, notCurrentPaint);
                baseLineY = centerVertical - noCurrentSize/2 - lineSpace;
                if(baseLineY > getHeight()){
                    break;
                }
            }
        }
    }

    public void setCurrentIndex(int index){
        this.crrentIndex = index;
    }

    public void setLrcs(List<String> lrcs){
        this.lrcs = lrcs;
    }
}
