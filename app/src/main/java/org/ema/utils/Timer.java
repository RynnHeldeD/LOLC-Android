package org.ema.utils;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

public class Timer extends CountDownTimer {

    Boolean isTicking;
    Long startTime;
    Long countDownInterval;
    protected TextView timerTextView;
    protected TimerButton parent;

    public Timer(long startTime, long countDownInterval, TextView ttv, TimerButton parent) {
        super(startTime, countDownInterval);
        isTicking = false;
        timerTextView = ttv;
        this.parent = parent;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        isTicking = true;
        timerTextView.setText("" + millisUntilFinished / 1000);
    }

    public void setVisible(Boolean visible){
        if(visible)
            timerTextView.setVisibility(View.VISIBLE);
        else
            timerTextView.setVisibility(View.GONE);
    }

    @Override
    public void onFinish() {
        timerTextView.setText("");
        setVisible(false);
        isTicking = false;
        this.cancel();
        if(this.parent.getTimer() != null){
            this.parent.setTimer(null);
        }
    }

    public Boolean isTicking() {
        return isTicking;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getCountDownInterval() {
        return countDownInterval;
    }

    public void setCountDownInterval(Long countDownInterval) {
        this.countDownInterval = countDownInterval;
    }

    public TextView getTimerTextView() {
        return timerTextView;
    }

    public void setTimerTextView(TextView timerTextView) {
        this.timerTextView = timerTextView;
    }
}
