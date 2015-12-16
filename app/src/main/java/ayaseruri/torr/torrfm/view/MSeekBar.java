package ayaseruri.torr.torrfm.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

/**
 * Created by ayaseruri on 15/12/12.
 */
public class MSeekBar extends SeekBar {
    private boolean isSetProgressEnable = true;

    public MSeekBar(Context context) {
        super(context);
    }

    public MSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setOnSeekBarChangeListener(final OnSeekBarChangeListener l) {
        super.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                l.onProgressChanged(seekBar, progress, fromUser);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSetProgressEnable = false;
                l.onStartTrackingTouch(seekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSetProgressEnable = true;
                l.onStopTrackingTouch(seekBar);
            }
        });
    }

    @Override
    public synchronized void setProgress(int progress) {
        if (isSetProgressEnable) {
            super.setProgress(progress);
        }
    }
}
