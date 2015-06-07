package org.ema.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.makeramen.roundedimageview.RoundedImageView;

import org.ema.lolcompanion.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TimerButton extends RoundedImageView {
    public static final int DELAY = 200;
    protected Timer timer;
    //protected long clickedTimestamp;
    protected boolean triggered;

    public TimerButton(Context context) {
        super(context);
        this.timer = null;
        //this.clickedTimestamp = 0;
        this.triggered = false;
    }

    public TimerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.timer = null;
        //this.clickedTimestamp = 0;
        this.triggered = false;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimerButton);

        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i)
        {
            int attr = a.getIndex(i);
            switch (attr)
            {
                case R.styleable.TimerButton_onKeyLongPress: {
                    if (context.isRestricted()) {
                        throw new IllegalStateException("The "+getClass().getCanonicalName()+":onKeyLongPress attribute cannot "
                                + "be used within a restricted context");
                    }

                    final String handlerName = a.getString(attr);
                    if (handlerName != null) {
                        setOnLongClickListener(new OnLongClickListener() {
                            private Method mHandler;

                            @Override
                            public boolean onLongClick(final View p_v) {
                                boolean result = false;
                                if (mHandler == null) {
                                    try {
                                        Class[] args = new Class[2];
                                        args[0] = String.class;
                                        args[1] = boolean.class;
                                        mHandler = getContext().getClass().getMethod(handlerName, args);
                                    } catch (NoSuchMethodException e) {
                                        int id = getId();
                                        String idText = id == NO_ID ? "" : " with id '"
                                                + getContext().getResources().getResourceEntryName(
                                                id) + "'";
                                        throw new IllegalStateException("Could not find a method " +
                                                handlerName + "(View) in the activity "
                                                + getContext().getClass() + " for onKeyLongPress handler"
                                                + " on view " + TimerButton.this.getClass() + idText, e);
                                    }
                                }

                                try {
                                    Object[] params = new Object[2];
                                    int id = getId();
                                    params[0] = getContext().getResources().getResourceEntryName(id);
                                    params[1] = false;
                                    mHandler.invoke(getContext(), params);
                                    result = true;
                                } catch (IllegalAccessException e) {
                                    throw new IllegalStateException("Could not execute non "
                                            + "public method of the activity", e);
                                } catch (InvocationTargetException e) {
                                    throw new IllegalStateException("Could not execute "
                                            + "method of the activity", e);
                                }
                                return result;
                            }
                        });
                    }
                    break;
                }
                default:
                    break;
            }
        }
        a.recycle();
    }

    public TimerButton(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.timer = null;
        //this.clickedTimestamp = 0;
        this.triggered = false;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimerButton);

        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i)
        {
            int attr = a.getIndex(i);
            switch (attr)
            {
                case R.styleable.TimerButton_onKeyLongPress: {
                    if (context.isRestricted()) {
                        throw new IllegalStateException("The "+getClass().getCanonicalName()+":onKeyLongPress attribute cannot "
                                + "be used within a restricted context");
                    }

                    final String handlerName = a.getString(attr);
                    if (handlerName != null) {
                        setOnLongClickListener(new OnLongClickListener() {
                            private Method mHandler;

                            @Override
                            public boolean onLongClick(final View p_v) {
                                boolean result = false;
                                if (mHandler == null) {
                                    try {
                                        mHandler = getContext().getClass().getMethod(handlerName, View.class);
                                    } catch (NoSuchMethodException e) {
                                        int id = getId();
                                        String idText = id == NO_ID ? "" : " with id '"
                                                + getContext().getResources().getResourceEntryName(
                                                id) + "'";
                                        throw new IllegalStateException("Could not find a method " +
                                                handlerName + "(View) in the activity "
                                                + getContext().getClass() + " for onKeyLongPress handler"
                                                + " on view " + TimerButton.this.getClass() + idText, e);
                                    }
                                }

                                try {
                                    mHandler.invoke(getContext(), TimerButton.this);
                                    result = true;
                                } catch (IllegalAccessException e) {
                                    throw new IllegalStateException("Could not execute non "
                                            + "public method of the activity", e);
                                } catch (InvocationTargetException e) {
                                    throw new IllegalStateException("Could not execute "
                                            + "method of the activity", e);
                                }
                                return result;
                            }
                        });
                    }
                    break;
                }
                default:
                    break;
            }
        }
        a.recycle();
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public long getCurrentTimestamp(){
        return  Long.parseLong(this.getTimer().getTimerTextView().getText().toString()) * 1000;
    }

    public void timerDelay(long delayToRetrench){
        Log.v("Websocket","ATTTEENNNNNTIONNNNN");
        String test = this.getTimer().getTimerTextView().getText().toString();
        Log.v("Websocket","Et voici une chaine vide:" + test);
        long currentTimestamp = Long.parseLong(test) * 1000;
        Log.v("Websocket","TU NE VERRA JAMAIS CE LOG");

        if (currentTimestamp != 0) {
            if(this.getTimer() != null) {
                this.getTimer().cancel();
            }
            this.setTimer(new Timer(currentTimestamp - delayToRetrench, 1000, this.getTimer().getTimerTextView(), this));
            this.getTimer().start();
        }
    }

    /*
    public long getClickedTimestamp() {
        return this.clickedTimestamp;
    }

    public void setClickedTimestamp(long clickedTimestamp) {
        this.clickedTimestamp = clickedTimestamp;
    }
    */

    public boolean isTriggered() {
        return triggered;
    }

    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }
}
