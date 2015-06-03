package org.ema.utils;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

public class TimerButton extends RoundedImageView {

    protected Timer timer;

    public TimerButton(Context context) {
        super(context);
        timer = null;
    }

    public TimerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        timer = null;
    }

    public TimerButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }
}
