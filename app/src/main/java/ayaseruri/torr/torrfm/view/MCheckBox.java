package ayaseruri.torr.torrfm.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;

/**
 * Created by ayaseruri on 15/12/16.
 */
public class MCheckBox extends CheckBox {

    private OnCheckedChangeListener onCheckedChangeListener;

    public MCheckBox(Context context) {
        super(context);
    }

    public MCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.onCheckedChangeListener = listener;
        super.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    public void justChangeCheckState(boolean isChecked) {
        super.setOnCheckedChangeListener(null);
        this.setChecked(isChecked);
        super.setOnCheckedChangeListener(onCheckedChangeListener);
    }
}
