package ayaseruri.torr.torrfm.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.ArrayList;

import ayaseruri.torr.torrfm.R;
import ayaseruri.torr.torrfm.model.LrcModel;
import ayaseruri.torr.torrfm.objectholder.LrcInfo;

/**
 * Created by ayaseruri on 15/12/13.
 */
public class LrcView extends TextView {

    private Paint currentPaint;
    private Paint notCurrentPaint;
    private int currentColor, noCurrentColor;
    private float currentSize, noCurrentSize, lineSpace;
    private LrcModel lrcModel;

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

        ArrayList<LrcInfo> lrcs = lrcModel.getLrcInfos();
        if (null != lrcs && lrcs.size() > 0) {
            float centerVertical = getHeight() / 2;
            float baseLineY = centerVertical - currentSize / 2 - lineSpace;
            for (int i = lrcModel.getIndexCurrent() - 1; i >= 0; i--) {
                canvas.drawText(lrcs.get(i).getJp(), getWidth() / 2
                        , baseLineY, notCurrentPaint);
                baseLineY = baseLineY - noCurrentSize - lineSpace;
                if (baseLineY < 0) {
                    break;
                }
            }
            canvas.drawText(lrcs.get(lrcModel.getIndexCurrent()).getJp(), getWidth() / 2
                    , centerVertical + currentSize / 2, currentPaint);

            baseLineY = centerVertical + currentSize / 2 + lineSpace + noCurrentSize;
            for (int i = lrcModel.getIndexCurrent() + 1; i < lrcs.size(); i++) {
                canvas.drawText(lrcs.get(i).getJp(), getWidth() / 2
                        , baseLineY, notCurrentPaint);
                baseLineY = baseLineY + noCurrentSize + lineSpace;
                if (baseLineY > getHeight()) {
                    break;
                }
            }
        }
    }

    public void setLrcModel(LrcModel lrcModel) {
        this.lrcModel = lrcModel;
    }
}
